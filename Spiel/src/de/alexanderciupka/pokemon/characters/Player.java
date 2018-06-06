package de.alexanderciupka.pokemon.characters;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Type;

public class Player extends Character {

	private ArrayList<String> routeHistory;
	private PC pc;

	private int stepCounter;

	private int protectedSteps;

	private HashMap<Item, Integer> items;

	public Player() {
		super();
		this.items = new HashMap<Item, Integer>();
		this.routeHistory = new ArrayList<String>();

		this.pc = new PC(this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					NPC follower = Player.this.getFollower();
					boolean newFollower = follower == null && Player.this.getCurrentRoute() != null;
					if (newFollower && Player.this.getCurrentRoute() != null) {
						follower = new NPC("playermon");
						follower.setCurrentRoute(Player.this.getCurrentRoute());
						follower.setCurrentPosition(Player.this.getOldPosition());
						follower.setName(Player.this.getTeam().getTeam()[0].getName());
						follower.setCurrentDirection(Player.this.getCurrentDirection());
						follower.setShowName(false);
					}
					if (Player.this.getTeam().getTeam()[0] != null
							&& Player.this.getTeam().getTeam()[0].getAilment() != Ailment.FAINTED) {
						Pokemon p = Player.this.getTeam().getTeam()[0];
						follower.setCharacterImage(p.getId() + (p.isShiny() ? "s" : ""));
						follower.setNoFightDialogue(p.getName());
						follower.setTextColor(Type.getColor(Player.this.getTeam().getTeam()[0].getTypes()[0]));
					}

					if (newFollower) {
						follower.getCurrentRoute().addCharacter(follower);
						Player.this.setFollower(follower);
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		this.init();
	}

	public Player(String id) {
		super(id);
		this.items = new HashMap<Item, Integer>();
		this.routeHistory = new ArrayList<String>();

		this.pc = new PC(this);

		this.init();
	}

	private void init() {
		for (Item i : Item.values()) {
			this.items.put(i, 0);
		}
	}

	@Override
	public void changePosition(Direction direction, boolean waiting) {
		super.changePosition(direction, waiting);
		this.onMovement();
	}

	@Override
	public void setCurrentRoute(Route currentRoute) {
		for (int i = 0; i < this.routeHistory.size(); i++) {
			if (this.routeHistory.get(i).equals(currentRoute.getId())) {
				this.routeHistory.remove(i);
				break;
			}
		}
		this.routeHistory.add(currentRoute.getId());
		super.setCurrentRoute(currentRoute);
		if (this.gController.getGameFrame() != null
				&& this.equals(this.gController.getCurrentBackground().getCamera().getCenter())) {
			this.gController.getGameFrame().getBackgroundLabel().changeRoute(currentRoute);
		}
	}

	public void warpToPokemonCenter() {
		String routeID = "eigenes_zimmer";
		int x = 5;
		int y = 4;
		Direction d = Direction.DOWN;

		for (int centerIndex = this.routeHistory.size() - 1; centerIndex >= 0; centerIndex--) {
			if (this.routeHistory.get(centerIndex).equals("pokemon_center")) {
				routeID = this.routeHistory.get(centerIndex);
				x = 2;
				y = 1;
				d = Direction.UP;
				break;
			}
		}
		this.setCurrentPosition(x, y);
		this.setCurrentDirection(d);
		this.setCurrentRoute(this.gController.getRouteAnalyzer().getRouteById(routeID));
		this.gController.setCurrentRoute(this.getCurrentRoute());
		this.team.restoreTeam();
		this.gController.getCurrentBackground().getCamera().setCharacter(this, false);
	}

	public ArrayList<String> getRouteHistory() {
		return this.routeHistory;
	}

	public void addItem(Item reward) {
		this.addItem(reward, 1);
	}

	public void addItem(Item reward, int amount) {
		if (amount > 0) {
			SoundController.getInstance().playSound(SoundController.GET_ITEM);
			this.items.put(reward, this.items.get(reward) + amount);
			this.gController.getGameFrame().getInventoryPanel().update(this);
		}
	}

	public void earnRewards(HashMap<Item, Integer> rewards) {
		this.earnRewards(rewards, false);
	}

	public void earnRewards(HashMap<Item, Integer> rewards, boolean withText) {
		for (Item i : rewards.keySet()) {
			this.addItem(i, rewards.get(i));
			if (withText) {
				this.gController.getGameFrame()
						.addDialogue("Du hast " + i.getName() + " (x" + rewards.get(i) + ") erhalten!");
				this.gController.getGameFrame().getDialogue().waitText();
			}
		}
	}

	public boolean hasItem(Item i) {
		return this.items.get(i) > 0;
	}

	public boolean removeItem(Item i) {
		int value = this.items.get(i);
		if (value > 0) {
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
		for (String r : this.routeHistory) {
			JsonObject currentRoute = new JsonObject();
			currentRoute.addProperty("id", r);
			routeHistory.add(currentRoute);
		}
		JsonArray items = new JsonArray();
		for (Item i : this.items.keySet()) {
			int amount = this.items.get(i);
			if (amount == 0) {
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
		if (super.importSaveData(saveData)) {
			this.routeHistory.clear();
			for (JsonElement j : saveData.get("route_history").getAsJsonArray()) {
				this.routeHistory.add(j.getAsJsonObject().get("id").getAsString());
			}
			this.init();
			for (JsonElement j : saveData.get("items").getAsJsonArray()) {
				if (j.getAsJsonObject().get("amount") != null) {
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
		this.gController.setInteractionPause(true);
		boolean result = false;
		switch (i) {
		case CUT:
		case FLASH:
		case ROCKSMASH:
		case STRENGTH:
		case SURF:
			this.currentRoute.getEntities()[this.getInteractionPoint().y][this.getInteractionPoint().x].useVM(this, i);
			break;
		case REPEL:
			if (this.isProtected()) {
				this.gController.getGameFrame().addDialogue("Es wurde bereits ein Schutz eingesetzt!");
			} else {
				this.protectedSteps = this.stepCounter + i.getValue();
				this.gController.getGameFrame()
						.addDialogue("Du bist jetzt für " + i.getValue() + " Schritte vor wilden Pokemon geschützt!");
				result = true;
			}
		case HYPERBALL:
		case MASTERBALL:
		case POKEBALL:
		case SUPERBALL:
		case HEALBALL:
		case PREMIERBALL:
			if (this.gController.isFighting()) {
				result = true;
				this.gController.getGameFrame().getFightPanel().throwBall(i);
			} else {
				this.gController.getGameFrame().addDialogue("Es wird keine Wirkung haben.");
			}
			break;
		default:
			this.gController.getGameFrame().addDialogue("Es wird keine Wirkung haben.");
			result = false;
		}
		if (result) {
			this.removeItem(i);
			this.gController.getGameFrame().setCurrentPanel(null);
		}
		this.gController.waitDialogue();
		this.gController.setInteractionPause(false);
		return result;
	}

	public void onMovement() {
		this.stepCounter++;
		if (this.stepCounter % 8 == 0) {
			for (int i = 0; i < this.team.getAmmount(); i++) {
				this.team.getTeam()[i].afterWalkingDamage();
			}
		}
		if (this.stepCounter % 128 == 0) {
			for (int i = 0; i < this.team.getAmmount(); i++) {
				this.team.getTeam()[i].changeHappiness(1);
			}
		}
		if (this.protectedSteps > 0) {
			this.protectedSteps--;
			if (this.protectedSteps == 0) {
				this.gController.getGameFrame().addDialogue("Der Schutz ist ausgelaufen!");
				this.gController.waitDialogue();
			}
		}
	}

	public boolean isProtected() {
		return this.protectedSteps > 0;
	}
}
