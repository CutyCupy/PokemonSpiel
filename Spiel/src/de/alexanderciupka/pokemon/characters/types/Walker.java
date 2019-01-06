package de.alexanderciupka.pokemon.characters.types;

import java.awt.Point;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.Path;

public class Walker extends NPC implements Walkable {

	private Path path;
	private boolean locked;

	public Walker() {
		super();
	}
	public Walker(String id) {
		super(id);
	}

	@Override
	public boolean move() {
		if(!this.moving && !this.locked) {
			Point delta = new Point(this.currentPosition.x - this.path.getNextPoint().x, this.currentPosition.y - this.path.getNextPoint().y);
			
			if(delta.x == 0 && delta.y == 0) {
				path.shift();
				return false;
			}
			
			if(delta.x < 0) {
				this.setCurrentDirection(Direction.RIGHT);
			} else if(delta.x > 0) {
				this.setCurrentDirection(Direction.LEFT);
			} else if(delta.y < 0) {
				this.setCurrentDirection(Direction.DOWN);
			} else if(delta.y > 0) {
				this.setCurrentDirection(Direction.UP);
			}
			this.changePosition(this.getCurrentDirection(), false);
			return true;
		}
		return false;
	}
	
	
	
	
	@Override	
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		
		saveData.add("path", path.getSaveData());

		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			path = new Path();
			this.path.importSaveData(saveData.get("walker_data").getAsJsonObject().get("path").getAsJsonArray());
			return true;
		}
		return false;
	}
	
	@Override
	public void onDefeat(Player p) {
		super.onDefeat(p);
		this.lock();
	}
	
	@Override
	public void lock() {
		this.locked = true;
	}
	
	@Override
	public void resetPosition() {
		super.resetPosition();
		this.locked = false;
	}
}
