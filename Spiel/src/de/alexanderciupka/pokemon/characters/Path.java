package de.alexanderciupka.pokemon.characters;

import java.awt.Point;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Path {
	
	private ArrayList<Point> path;
	
	public Path() {
		this.path = new ArrayList<>();
	}
	
	public void addPoint(Point p) {
		this.path.add(p);
	}
	
	public Point getNextPoint() {
		if(this.path.size() > 0) {
			return this.path.get(0);
		}
		return null;
	}
	
	public void shift() {
		this.path.add(this.path.remove(0));
	}
	
	public JsonArray getSaveData() {
		JsonArray saveData = new JsonArray();
		for(Point p : this.path) {
			JsonObject current = new JsonObject();
			current.addProperty("x", p.x);
			current.addProperty("y", p.y);
			saveData.add(current);
		}
		return saveData;
	}
	
	public boolean importSaveData(JsonArray data) {
		this.path = new ArrayList<>();
		for(JsonElement e : data) {
			JsonObject p = e.getAsJsonObject();
			this.path.add(new Point(p.get("x").getAsInt(), p.get("y").getAsInt()));
		}
		return true;
	}

}
