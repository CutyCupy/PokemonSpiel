package de.alexanderciupka.pokemon.characters.types;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.PC;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.pokemon.Pokedex;

public class Player extends Character {

	private ArrayList<String> routeHistory;
	private PC pc;
	private Pokedex pokedex;

	public Player() {
		super();
		this.routeHistory = new ArrayList<String>();

		this.pc = new PC(this);

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					NPC follower = Player.this.getFollower();
//					boolean newFollower = follower == null && Player.this.getCurrentRoute() != null;
//					if (newFollower && Player.this.getCurrentRoute() != null) {
//						follower = new NPC("playermon");
//						follower.setCurrentPosition(Player.this.getOldPosition());
//						follower.setCurrentRoute(Player.this.getCurrentRoute());
//						follower.setName(Player.this.getTeam().getTeam()[0].getName());
//						follower.setCurrentDirection(Player.this.getCurrentDirection());
//						follower.setShowName(false);
//					}
//					if (Player.this.getTeam().getTeam()[0] != null
//							&& Player.this.getTeam().getTeam()[0].getAilment() != Ailment.FAINTED) {
//						Pokemon p = Player.this.getTeam().getTeam()[0];
//						follower.setCharacterImage(p.getId() + (p.isShiny() ? "s" : ""));
//						follower.setDialogue(NPC.DIALOGUE_NO_ACTION, p.getName());
//						follower.setTextColor(Type.getColor(Player.this.getTeam().getTeam()[0].getTypes()[0]));
//					}
//
//					if (newFollower) {
//						follower.getCurrentRoute().addCharacter(follower);
//						Player.this.setFollower(follower);
//					}
//
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
		this.pokedex = new Pokedex();
	}

	public Player(String id) {
		super(id);
		this.routeHistory = new ArrayList<String>();

		this.pc = new PC(this);
		this.pokedex = new Pokedex();
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

	public void earnRewards(HashMap<Integer, Integer> rewards) {
		this.earnRewards(rewards, false);
	}

	public void earnRewards(HashMap<Integer, Integer> rewards, boolean withText) {
		for (Integer i : rewards.keySet()) {
			this.addItem(i, rewards.get(i));
			if (withText) {
				this.gController.getGameFrame()
						.addDialogue(this.getName() + "hat " + this.gController.getInformation().getItemData(Items.ITEM_NAME, i) + " (x" + rewards.get(i) + ") erhalten!");
				this.gController.getGameFrame().getDialogue().waitText();
			}
		}
	}

	public PC getPC() {
		return this.pc;
	}
	
	public Pokedex getPokedex() {
		return this.pokedex;
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
		saveData.add("route_history", routeHistory);
		saveData.add("pc", this.getPC().getSaveData());
		saveData.add("pokedex", this.getPokedex().getSaveData());
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			this.routeHistory.clear();
			for (JsonElement j : saveData.get("route_history").getAsJsonArray()) {
				this.routeHistory.add(j.getAsJsonObject().get("id").getAsString());
			}
			this.pc.importSaveData(saveData.get("pc").getAsJsonArray());
			this.pokedex.importSaveData(saveData.get("pokedex").getAsJsonObject());
			return true;
		}
		return false;
	}

	@Override
	public void addItem(Integer reward, int amount) {
		super.addItem(reward, amount);
		this.gController.getGameFrame().getInventoryPanel().update(this);
	}

	public int getSpeed() {
		return this.speed;
	}
	
	public boolean isMoving() {
		return this.moving;
	}
}
