package de.alexanderciupka.pokemon.map;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.characters.types.Player;

public class Camera {

	private double x;
	private double y;
	private boolean moving;

	private de.alexanderciupka.pokemon.characters.types.Character centered;
	private GameController gController;

	public Camera(int x, int y) {
		this.x = x + 0.5;
		this.y = y + 0.5;

		gController = GameController.getInstance();
	}

	public void setCharacter(de.alexanderciupka.pokemon.characters.types.Character c, boolean animated) {
		if (animated) {
			moveTowards(c.getExactX(), c.getExactY());
		}
		this.centered = c;
	}

	public double getX() {
		if (centered != null && !moving) {
			this.x = centered.getExactX() + 0.5;
		}
		return this.x;
	}

	public double getY() {
		if (centered != null && !moving) {
			this.y = centered.getExactY() + 0.5;
		}
		return this.y;
	}

	public void moveTowards(double x, double y) {
		moveTowards(x, y, true);
	}

	public void moveTowards(double x, double y, boolean animated) {
		moving = true;
		x += .5;
		y += .5;
		if (getX() != x || getY() != y) {
			centered = null;
		}
		getX();
		getY();
		if (animated) {
			double xDelta = x - this.x;
			double yDelta = y - this.y;

			double pathLength = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));

			double xChange = xDelta / (pathLength * 10);
			double yChange = yDelta / (pathLength * 10);
			for (double i = 0; i < pathLength; i += .1) {
				this.x += xChange;
				this.y += yChange;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		this.x = x;
		this.y = y;
		moving = false;
	}

	public Route getRoute() {
		if(centered != null) {
			return centered.getCurrentRoute();
		} else {
			return gController.getCurrentBackground().getCurrentRoute();
		}
	}

	public JsonObject getSaveData() {
		JsonObject saveData = new JsonObject();
		while (moving) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (centered != null) {
			boolean isMain = centered instanceof Player;
			saveData.addProperty("isMain", isMain);
			if (!isMain) {
				saveData.add("character", ((NPC) centered).getSaveData());
			}
		} else {
			saveData.addProperty("x", this.x);
			saveData.addProperty("y", this.y);
		}
		return saveData;
	}

	public void importSaveData(JsonObject saveData) {
		if (saveData.get("character") != null) {
			de.alexanderciupka.pokemon.characters.types.NPC c = new NPC();
			c.importSaveData(saveData.get("character").getAsJsonObject());
			for (NPC npc : c.getCurrentRoute().getCharacters()) {
				if (npc.equals(c)) {
					this.centered = npc;
					break;
				}
			}
		} else if (saveData.get("isMain").getAsBoolean()) {
			this.centered = gController.getMainCharacter();
		} else {
			this.x = saveData.get("x").getAsDouble();
			this.y = saveData.get("y").getAsDouble();
		}
	}

	public de.alexanderciupka.pokemon.characters.types.Character getCenter() {
		return this.centered;
	}

}
