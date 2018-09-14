package de.alexanderciupka.pokemon.pokemon;

import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Pokedex {
	
	private HashSet<Integer> seen;
	private HashSet<Integer> caught;
	
	public Pokedex() {
		this.seen = new HashSet<>();
		this.caught = new HashSet<>();
	}
	
	
	public HashSet<Integer> getSeen() {
		return this.seen;
	}
	
	public HashSet<Integer> getCaught() {
		return this.seen;
	}
	
	public boolean addToSeen(int id) {
		return this.seen.add(id);
	}
	
	public boolean addToCaught(int id) {
		return this.caught.add(id);
	}
	
	public boolean isCaught(int id) {
		return this.caught.contains(id);
	}
	
	
	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		JsonArray seen = new JsonArray();
		JsonArray caught = new JsonArray();
		for(Integer i : this.seen) {
			JsonObject j = new JsonObject();
			j.addProperty("id", i);
			seen.add(j);
		}
		
		for(Integer i : this.caught) {
			JsonObject j = new JsonObject();
			j.addProperty("id", i);
			caught.add(j);
		}
		
		data.add("seen", seen);
		data.add("caught", caught);
		
		return data;
	}

	public boolean importSaveData(JsonObject saveData) {
		this.seen = new HashSet<>();
		this.caught = new HashSet<>();
		for(JsonElement e : saveData.get("seen").getAsJsonArray()) {
			this.seen.add(e.getAsJsonObject().get("id").getAsInt());
		}
		for(JsonElement e : saveData.get("caught").getAsJsonArray()) {
			this.caught.add(e.getAsJsonObject().get("id").getAsInt());
		}
		return true;
	}

}
