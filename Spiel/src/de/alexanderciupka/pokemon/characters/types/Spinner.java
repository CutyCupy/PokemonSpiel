package de.alexanderciupka.pokemon.characters.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.enums.SpinnerSpeed;
import de.alexanderciupka.pokemon.main.Main;

public class Spinner extends NPC implements Walkable {

	private long nextSpin;
	
	private SpinnerSpeed speed;

	private int current;
	private Direction[] directions;
	private Direction lockedDirection;
	
	private boolean locked;
	
	public Spinner() {
		lockedDirection = Direction.NONE;
	}

	public Direction[] getDirections() {
		return this.directions;
	}

	public void setDirections(Direction[] d) {
		this.directions = d;
	}
	
	public void setSpeed(SpinnerSpeed speed) {
		this.speed = speed;
	}
	
	public SpinnerSpeed getSpeed() {
		return speed;
	}
	
	public void setLockedDirection(Direction dir) {
		this.lockedDirection = dir;
		setCurrentDirection(dir);
	}
	
	@Override
	public void setCurrentDirection(Direction direction) {
		if(this.lockedDirection == Direction.NONE) {
			super.setCurrentDirection(direction);
		}
		super.setCurrentDirection(this.lockedDirection);
	}

	@Override
	public boolean move() {
		if (this.nextSpin <= 0 && lockedDirection == Direction.NONE && !this.locked) {
			switch(this.speed) {
			case CONSTANT_FAST:
			case CONSTANT_SLOW:
				this.setCurrentDirection(this.directions[this.current % this.directions.length]);
				this.current = (this.current + 1) % this.directions.length;
				break;
			case RANDOM_FAST:
			case RANDOM_SLOW:
				this.setCurrentDirection(this.directions[Main.RNG.nextInt(this.directions.length)]);
				break;
			}
			this.nextSpin = (Main.RNG.nextLong() % (this.speed.getMax() - this.speed.getMin() + 1)) + this.speed.getMin();
			return true;
		} else if(this.lockedDirection == Direction.NONE && !this.locked) {
			this.nextSpin--;
		}
		return false;
	}
	
	public boolean isValidDirection(Direction dir) {
		for(Direction direction : this.directions) {
			if(dir.equals(direction)) {
				return true;
			}
		}
		return false;
	}
	
	@Override	
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		
		saveData.addProperty("spinner_type", this.speed.name());
		saveData.addProperty("min", this.speed.getMin());
		saveData.addProperty("max", this.speed.getMax());
		
		JsonArray directions = new JsonArray();
		for(Direction dir : this.directions) {
			JsonObject currentDir = new JsonObject();
			currentDir.addProperty("direction", dir.name());
			directions.add(currentDir);
		}
		saveData.add("directions", directions);
		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			saveData = saveData.get("spinner_data").getAsJsonObject();
			this.speed = SpinnerSpeed.valueOf(saveData.get("spinner_type").getAsString());
			if(saveData.has("min")) {
				this.speed.setMin(saveData.get("min").getAsInt());
			}
			if(saveData.has("max")) {
				this.speed.setMax(saveData.get("max").getAsInt());
			}
			
			this.directions = new Direction[saveData.get("directions").getAsJsonArray().size()];
			for(int i = 0; i < directions.length; i++) {
				this.directions[i] = Direction.valueOf(saveData.get("directions").getAsJsonArray().get(i).getAsJsonObject().get("direction").getAsString());
			}
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
