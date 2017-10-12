package de.alexanderciupka.pokemon.characters;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Item;

public class Player extends Character {

	private ArrayList<String> routeHistory;
	private PC pc;

	private int stepCounter;

	private int protectedSteps;

	HashMap<Item, Integer> items;

	public Player() {
		super();
		items = new HashMap<Item, Integer>();
		routeHistory = new ArrayList<String>();

		pc = new PC(this);

		init();
	}

	public Player(String id) {
		super(id);
		items = new HashMap<Item, Integer>();
		routeHistory = new ArrayList<String>();

		pc = new PC(this);

		init();
	}

	private void init() {
		for(Item i : Item.values()) {
			this.items.put(i, 0);
		}
	}

	@Override
	public void changePosition(Direction direction, boolean waiting) {
		super.changePosition(direction, waiting);
		onMovement();
	}

	@Override
	public void setCurrentRoute(Route currentRoute) {
		for(int i = 0; i < routeHistory.size(); i++) {
			if(routeHistory.get(i).equals(currentRoute.getId())) {
				routeHistory.remove(i);
				break;
			}
		}
		routeHistory.add(currentRoute.getId());
		super.setCurrentRoute(currentRoute);
		if(gController.getGameFrame() != null) {
			gController.getGameFrame().getBackgroundLabel().changeRoute(currentRoute);
		}
	}

	public void warpToPokemonCenter() {
		String routeID = "eigenes_zimmer";
		int x = 5;
		int y = 4;
		Direction d = Direction.DOWN;

		for(int centerIndex = this.routeHistory.size() - 1; centerIndex >= 0; centerIndex--) {
			if(this.routeHistory.get(centerIndex).equals("pokemon_center")) {
				routeID = this.routeHistory.get(centerIndex);
				x = 2;
				y = 1;
				d = Direction.UP;
				break;
			}
		}
		setCurrentPosition(x, y);
		setCurrentDirection(d);
		this.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(routeID));
		gController.setCurrentRoute(this.getCurrentRoute());
		team.restoreTeam();
		gController.getCurrentBackground().getCamera().setCharacter(this, false);
//		gController.getGameFrame().repaint();
	}

	public ArrayList<String> getRouteHistory() {
		return routeHistory;
	}

	public void addItem(Item reward) {
		SoundController.getInstance().playSound(SoundController.GET_ITEM);
		this.items.put(reward, this.items.get(reward) + 1);
		this.gController.getGameFrame().getInventoryPanel().update(this);
	}

	public boolean hasItem(Item i) {
		return items.get(i) > 0;
	}

	public boolean removeItem(Item i) {
		int value = this.items.get(i);
		if(value > 0) {
			this.items.put(i, value - 1);
			return true;
		} else if (value < 0) {
			this.items.put(i, 0);
		}
		return false;
	}

	public HashMap<Item, Integer> getItems() {
		return this.items;
	}

	public PC getPC() {
		return this.pc;
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		JsonArray routeHistory = new JsonArray();
		for(String r : this.routeHistory) {
			JsonObject currentRoute = new JsonObject();
			currentRoute.addProperty("id", r);
			routeHistory.add(currentRoute);
		}
		JsonArray items = new JsonArray();
		for(Item i : this.items.keySet()) {
			int amount = this.items.get(i);
			if(amount == 0) {
				continue;
			}
			JsonObject currentItem = new JsonObject();
			currentItem.addProperty("id", i.name());
			currentItem.addProperty("amount", amount);
			items.add(currentItem);
		}
		saveData.add("route_history", routeHistory);
		saveData.add("items", items);
		saveData.add("pc", this.getPC().getSaveData());
		saveData.addProperty("step_counter", this.stepCounter);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			this.routeHistory.clear();
			for(JsonElement j : saveData.get("route_history").getAsJsonArray()) {
				this.routeHistory.add(j.getAsJsonObject().get("id").getAsString());
			}
			init();
			for(JsonElement j : saveData.get("items").getAsJsonArray()) {
				if(j.getAsJsonObject().get("amount") != null) {
					this.items.put(Item.valueOf(j.getAsJsonObject().get("id").getAsString()),
							j.getAsJsonObject().get("amount").getAsInt());
				}
			}
			this.pc.importSaveData(saveData.get("pc").getAsJsonArray());
			this.stepCounter = saveData.get("step_counter").getAsInt();
			return true;
		}
		return false;
	}

	public boolean useItem(Item i) {
		gController.setInteractionPause(true);
		boolean result = false;
		switch (i) {
		case CUT:
		case FLASH:
		case ROCKSMASH:
		case STRENGTH:
		case SURF:
			this.currentRoute.getEntities()[getInteractionPoint().y][getInteractionPoint().x].useVM(this, i);
			break;
		case REPEL:
			if(isProtected()) {
				gController.getGameFrame().addDialogue("Es wurde bereits ein Schutz eingesetzt!");
			} else {
				this.protectedSteps = this.stepCounter + i.getValue();
				gController.getGameFrame().addDialogue("Du bist jetzt für " + i.getValue() + " Schritte vor wilden Pokemon geschützt!");
				result =  true;
			}
		case HYPERBALL:
		case MASTERBALL:
		case POKEBALL:
		case SUPERBALL:
		case HEALBALL:
		case PREMIERBALL:
			if(gController.isFighting()) {
				result = true;
				gController.getGameFrame().getFightPanel().throwBall(i);
			} else {
				gController.getGameFrame().addDialogue("Es wird keine Wirkung haben.");
			}
			break;
		default:
			gController.getGameFrame().addDialogue("Es wird keine Wirkung haben.");
			result = false;
		}
		if(result) {
			removeItem(i);
			gController.getGameFrame().setCurrentPanel(null);
//			gController.getGameFrame().repaint();
		}
		gController.waitDialogue();
		gController.setInteractionPause(false);
		return result;
	}

	public void onMovement() {
		this.stepCounter++;
		if(this.stepCounter % 8 == 0) {
			for(int i = 0; i < this.team.getAmmount(); i++) {
				this.team.getTeam()[i].afterWalkingDamage();
			}
		}
		if(this.stepCounter % 128 == 0) {
			for(int i = 0; i < this.team.getAmmount(); i++) {
				this.team.getTeam()[i].changeHappiness(1);
			}
		}
		if(this.protectedSteps > 0) {
			this.protectedSteps--;
			if(this.protectedSteps == 0) {
				gController.getGameFrame().addDialogue("Der Schutz ist ausgelaufen!");
				gController.waitDialogue();
			}
		}
	}

	public boolean isProtected() {
		return this.protectedSteps > 0;
	}
}
