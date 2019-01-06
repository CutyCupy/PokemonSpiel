package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Image;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;

public class MoveEntity extends Entity {

	private Direction direction;

	public Direction getDirection() {
		return this.direction;
	}

	public void setDirection(Direction dir) {
		this.direction = dir;
	}

	@Override
	public void onStep(Character c) {
		super.onStep(c);
		switch (this.direction) {
		case LEFT:
			c.startUncontrollableMove(Direction.LEFT, true, Character.VERY_SLOW);
			break;
		case RIGHT:
			c.startUncontrollableMove(Direction.RIGHT, true, Character.VERY_SLOW);
			break;
		case UP:
			c.startUncontrollableMove(Direction.UP, true, Character.VERY_SLOW);
			break;
		case DOWN:
			c.startUncontrollableMove(Direction.DOWN, true, Character.VERY_SLOW);
			break;
		case NONE:
			c.setControllable(true);
		}
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			if (saveData.has("direction")) {
				this.direction = Direction.valueOf(saveData.get("direction").getAsString().toUpperCase());
			}
			return true;
		}
		return false;
	}
	
	@Override
	public Image getSprite() {
		if(this.direction != null) {
			return this.gController.getRouteAnalyzer().getSpriteByName("move" + this.direction.name().toLowerCase());
		}
		return super.getSprite();
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject data = super.getSaveData();
		data.addProperty("direction", this.direction.name());
		return data;
	}

}
