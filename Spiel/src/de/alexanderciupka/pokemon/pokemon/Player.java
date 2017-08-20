package de.alexanderciupka.pokemon.pokemon;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.map.Route;

public class Player extends Character {

	private ArrayList<String> routeHistory;
	private PC pc;

	ArrayList<Item> items;

	public Player() {
		super();
		items = new ArrayList<Item>();
		routeHistory = new ArrayList<String>();

		pc = new PC(this);
	}

	public Player(String id) {
		super(id);
		items = new ArrayList<Item>();
		routeHistory = new ArrayList<String>();

		pc = new PC(this);
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
	}

	public void warpToPokemonCenter() {
		String routeID = "eigenes_zimmer";
		int x = 5;
		int y = 4;
		Direction d = Direction.RIGHT;
		
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
	}

	public ArrayList<String> getRouteHistory() {
		return routeHistory;
	}

	public void addItem(Item reward) {
		this.items.add(reward);
	}

	public boolean hasItem(Item i) {
		return items.contains(i);
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
		for(Item i : this.items) {
			JsonObject currentItem = new JsonObject();
			currentItem.addProperty("id", i.name());
			items.add(currentItem);
		}
		saveData.add("route_history", routeHistory);
		saveData.add("items", items);
		saveData.add("pc", this.getPC().getSaveData());
		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			this.routeHistory.clear();
			for(JsonElement j : saveData.get("route_history").getAsJsonArray()) {
				this.routeHistory.add(j.getAsJsonObject().get("id").getAsString());
			}
			this.items.clear();
			for(JsonElement j : saveData.get("items").getAsJsonArray()) {
				this.items.add(Item.valueOf(j.getAsJsonObject().get("id").getAsString()));
			}
			this.pc.importSaveData(saveData.get("pc").getAsJsonArray());
			return true;
		}
		return false;
	}
}
