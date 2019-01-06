package de.alexanderciupka.pokemon.characters.types;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.main.Main;

public class MovingNPC extends NPC implements Walkable {
	
	private long nextWalk;

	private long min = (long) (5000 / Main.FPS);
	private long max = (long) (15000 / Main.FPS);
	
	private boolean locked;

	public MovingNPC() {
		super();
	}
	public MovingNPC(String id) {
		super(id);
	}

	public void setMin(long m) {
		this.min = m;
	}

	public void setMax(long m) {
		this.max = m;
	}

	@Override
	public boolean move() {
		if (this.nextWalk <= 0 && !this.locked) {
			Direction dir = Direction.values()[Main.RNG.nextInt(4)];

			this.setCurrentDirection(dir);
			if (Main.RNG.nextFloat() < .75) {
				int x = this.currentPosition.x;
				int y = this.currentPosition.y;
				switch (dir) {
				case DOWN:
					y++;
					break;
				case LEFT:
					x--;
					break;
				case RIGHT:
					x++;
					break;
				case UP:
					y--;
					break;
				default:
					break;
				}

				if (this.getCurrentRoute() != null && this.getCurrentRoute().getEntity(x, y) != null
						&& this.getCurrentRoute().getEntity(x, y).getWarp() == null) {
					this.changePosition(dir, false);
				}
			}

			this.nextWalk = (Main.RNG.nextLong() % (this.max - this.min + 1)) + this.min;
			return true;
		}
		this.nextWalk--;
		return false;
	}
	
	@Override	
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		
		saveData.addProperty("min", this.min);
		saveData.addProperty("max", this.max);

		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			if(saveData.has("min")) {
				this.min = saveData.get("min").getAsInt();
			}
			if(saveData.has("max")) {
				this.max = saveData.get("max").getAsInt();
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
