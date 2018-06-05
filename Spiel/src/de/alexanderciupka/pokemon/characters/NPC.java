package de.alexanderciupka.pokemon.characters;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class NPC extends Character {

	private File teamFile;
	private File dialogueFile;
	private String beforeFight;
	private String noFight;
	private String onDefeat;
	private String logo;
	private HashMap<Item, Integer> rewards;

	private boolean showName = true;
	private Color textColor = Color.BLUE;

	public boolean showName() {
		return this.showName;
	}

	public void setShowName(boolean s) {
		this.showName = s;
	}

	public Color getTextColor() {
		return this.textColor;
	}

	public void setTextColor(Color c) {
		this.textColor = c;
	}

	public NPC() {
		super();
		this.rewards = new HashMap<>();
	}

	public NPC(String currentString) {
		super(currentString);
		this.rewards = new HashMap<>();
	}

	public void resetPosition() {
		if (!this.currentPosition.equals(this.originalPosition)) {
			this.oldPosition = new Point(this.currentPosition);
			this.setCurrentPosition(this.originalPosition);
			this.currentRoute.updateMap(this.oldPosition);
		}
		if (this.currentDirection != this.originalDirection) {
			this.setCurrentDirection(this.originalDirection);
		}
		this.currentRoute.updateMap(this.currentPosition);
	}

	public void faceTowardsMainCharacter() {
		switch (this.gController.getMainCharacter().getCurrentDirection()) {
		case UP:
			this.setCurrentDirection(de.alexanderciupka.pokemon.characters.Direction.DOWN);
			break;
		case RIGHT:
			this.setCurrentDirection(de.alexanderciupka.pokemon.characters.Direction.LEFT);
			break;
		case DOWN:
			this.setCurrentDirection(de.alexanderciupka.pokemon.characters.Direction.UP);
			break;
		case LEFT:
			this.setCurrentDirection(de.alexanderciupka.pokemon.characters.Direction.RIGHT);
			break;
		default:
			return;
		}
		this.currentRoute.updateMap(this.currentPosition);
	}

	public void setBeforeFightDialogue(String dialog) {
		this.beforeFight = dialog;
	}

	public void setNoFightDialogue(String dialog) {
		this.noFight = dialog;
	}

	public void setAfterFightDialog(String dialog) {
		this.onDefeat = dialog;
	}

	public String getBeforeFightDialogue() {
		return this.beforeFight;
	}

	public String getNoFightDialogue() {
		return this.noFight;
	}

	public boolean moveTowardsMainCharacter() {
		int mainX = this.gController.getMainCharacter().getCurrentPosition().x;
		int mainY = this.gController.getMainCharacter().getCurrentPosition().y;
		if (this.currentPosition.x != mainX ^ this.currentPosition.y != mainY) {
			int x = 0;
			int y = 0;
			switch (this.currentDirection) {
			case DOWN:
				if (mainX != this.currentPosition.x || this.currentPosition.y > mainY) {
					return false;
				}
				y = 1;
				break;
			case LEFT:
				if (mainY != this.currentPosition.y || this.currentPosition.x < mainX) {
					return false;
				}
				x = -1;
				break;
			case RIGHT:
				if (mainY != this.currentPosition.y || this.currentPosition.x > mainX) {
					return false;
				}
				x = 1;
				break;
			case UP:
				if (mainX != this.currentPosition.x || this.currentPosition.y < mainY) {
					return false;
				}
				y = -1;
				break;
			default:
				return false;
			}
			for (int i = 1; i < 5; i++) {
				if (this.currentPosition.x + (i * x) == mainX && this.currentPosition.y + (i * y) == mainY) {
					break;
				} else if (!this.currentRoute.getEntities()[this.currentPosition.y + (i * y)][this.currentPosition.x
						+ (i * x)].isAccessible(this)) {
					return false;
				}
			}
			switch (this.getCurrentDirection()) {
			case DOWN:
				this.gController.getMainCharacter().setCurrentDirection(Direction.UP);
				break;
			case LEFT:
				this.gController.getMainCharacter().setCurrentDirection(Direction.RIGHT);
				break;
			case RIGHT:
				this.gController.getMainCharacter().setCurrentDirection(Direction.LEFT);
				break;
			case UP:
				this.gController.getMainCharacter().setCurrentDirection(Direction.DOWN);
				break;
			default:
				break;
			}
			this.gController.getGameFrame().getBackgroundLabel().spotted(this);
			while (!(this.currentPosition.x + x == mainX && this.currentPosition.y + y == mainY)) {
				this.currentRoute.updateMap(this.currentPosition);
				this.changePosition(this.getCurrentDirection(), true);
				this.currentRoute.updateMap(this.currentPosition);
			}
			return true;
		}
		return false;
	}

	@Override
	public void setCurrentDirection(Direction direction) {
		super.setCurrentDirection(direction);
		if (this.currentRoute != null) {
			this.currentRoute.updateMap(this.currentPosition);
		}
	}

	public void importTeam() {
		if (!this.trainer) {
			return;
		}
		try {
			this.teamFile = new File(this.getClass()
					.getResource("/characters/teams/" + this.currentRoute.getId() + "/" + this.getFileName() + ".txt")
					.getFile());
			BufferedReader reader = new BufferedReader(new FileReader(this.teamFile));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String[] rowElements = currentLine.split(",");
				Pokemon currentPokemon = new Pokemon(Integer.parseInt(rowElements[0]));
				currentPokemon.getStats().generateStats(Short.parseShort(rowElements[1]));
				this.team.addPokemon(currentPokemon);
			}
			this.trainer = true;
		} catch (Exception e) {
			// new File("res/characters/teams/" + this.currentRoute.getId() + "/").mkdir();
			// File f = new File("res/characters/teams/" + this.currentRoute.getId() + "/" +
			// getFileName() + ".txt");
			// try {
			// if(f.exists()) {
			// return;
			// }
			// f.createNewFile();
			// PrintWriter writer = new PrintWriter(new FileWriter(f));
			// writer.write("25,1");
			// writer.flush();
			// importTeam();
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			this.trainer = false;
		}
	}

	public void importDialogue() {
		try {
			this.dialogueFile = new File(this.getClass()
					.getResource(
							"/characters/dialoge/" + this.currentRoute.getId() + "/" + this.getFileName() + ".char")
					.getFile());
			JsonObject dialogue = new JsonParser().parse(new BufferedReader(new FileReader(this.dialogueFile)))
					.getAsJsonObject();
			this.beforeFight = dialogue.get("before") == null ? null : dialogue.get("before").getAsString();
			this.noFight = dialogue.get("no") == null ? null : dialogue.get("no").getAsString();
			this.onDefeat = dialogue.get("on_defeat") == null ? null : dialogue.get("on_defeat").getAsString();
			this.rewards = new HashMap<>();
			if (!(dialogue.get("reward") == null)) {
				this.importRewards(dialogue.get("reward").getAsString());
			}
		} catch (Exception e) {
			new File("res/characters/dialoge/" + this.currentRoute.getId() + "/").mkdir();
			File f = new File(
					"res/characters/dialoge/" + this.currentRoute.getId() + "/" + this.getFileName() + ".char");
			try {
				f.createNewFile();
				JsonObject d = new JsonObject();
				d.addProperty("before", "");
				d.addProperty("no", "");
				d.addProperty("on_defeat", "");
				d.addProperty("reward", "");
				FileWriter fw = new FileWriter(f);
				fw.write(d.toString());
				fw.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private void importRewards(String rewards) {
		for (String s : rewards.split("\\+")) {
			Item current = null;
			try {
				current = Item.valueOf(s.toUpperCase());
			} catch (Exception e) {
				continue;
			}
			this.rewards.put(current, this.rewards.get(current) == null ? 1 : this.rewards.get(current) + 1);
		}
	}

	private String getFileName() {
		return this.name.toLowerCase().replace(" ", "_").replace("ä", "ae").replace("ö", "oe").replace("ü", "ue");
	}

	public String getOnDefeatDialogue() {
		return this.onDefeat;
	}

	public HashMap<Item, Integer> getRewards() {
		return this.rewards;
	}

	public boolean hasRewards() {
		for (Item i : this.rewards.keySet()) {
			if (this.rewards.get(i) != null && this.rewards.get(i) > 0) {
				return true;
			}
		}
		return false;
	}

	public void removeReward(Item reward, Integer amount) {
		if (this.rewards.get(reward) != null) {
			int delta = this.rewards.get(reward) - amount;
			if (delta <= 0) {
				this.rewards.remove(reward);
			} else {
				this.rewards.put(reward, delta);
			}
		}
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty("before", this.beforeFight);
		saveData.addProperty("no", this.noFight);
		saveData.addProperty("after", this.onDefeat);
		String reward = "";
		if (this.rewards != null) {
			for (Item i : this.rewards.keySet()) {
				if (this.rewards.get(i) != null && this.rewards.get(i) > 0) {
					for (int j = 0; j < this.rewards.get(i); j++) {
						if (j != 0) {
							reward += "+";
						}
						reward += i.name();
					}
				}
			}
		}
		saveData.addProperty("reward", reward);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			this.beforeFight = saveData.get("before") instanceof JsonNull ? null : saveData.get("before").getAsString();
			this.noFight = saveData.get("no") instanceof JsonNull ? null : saveData.get("no").getAsString();
			this.onDefeat = saveData.get("after") instanceof JsonNull ? null : saveData.get("after").getAsString();
			this.rewards = new HashMap<>();
			if (!(saveData.get("reward") instanceof JsonNull)) {
				this.importRewards(saveData.get("reward").getAsString());
			}
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return this.id + " - " + this.name;
	}
}
