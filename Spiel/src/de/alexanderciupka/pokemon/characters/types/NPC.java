package de.alexanderciupka.pokemon.characters.types;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class NPC extends Character {

	public static final String DIALOGUE_BEFORE_ACTION = "before_action";
	public static final String DIALOGUE_NO_ACTION = "no_action";
	public static final String DIALOGUE_ON_ACTION = "on_action";
	public static final String DIALOGUE_ON_EXIT = "on_exit";
	public static final String DIALOGUE_AFTER_ACTION = "after_action";

	public static final int NO_DOUBLE = 0;
	public static final int DOUBLE = 1;
	public static final int FOLLOWER_DOUBLE = 2;

	protected HashMap<String, String> dialogues;

	private String logo;

	private boolean showName = true;

	private Color textColor = Color.BLUE;

	private int fightingStyle;

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
		this.dialogues = new HashMap<>();
	}

	public NPC(String currentString) {
		super(currentString);
		this.dialogues = new HashMap<>();
	}

	public void resetPosition() {
		if (!this.currentPosition.equals(this.originalPosition)) {
			this.oldPosition = new Point(this.currentPosition);
			this.setCurrentPosition(this.originalPosition);
		}
		if (this.currentDirection != this.originalDirection) {
			this.setCurrentDirection(this.originalDirection);
		}
	}

	public void loadData(JsonObject data) {
		this.setCurrentPosition(data.get("x").getAsInt(), data.get("y").getAsInt());
		this.setCharacterImage(data.get("char_sprite").getAsString(),
				data.get("direction").getAsString().toLowerCase());
		this.setName(data.get("name").getAsString());
		this.setLogo(data.has("logo") ? data.get("logo").getAsString() : null);
		this.setRange(
				data.has("range") ? data.get("range").getAsInt() : (GameFrame.FRAME_SIZE / GameFrame.GRID_SIZE) / 2);

	}

	public void faceTowardsCharacter(de.alexanderciupka.pokemon.characters.Character c) {
		if(this instanceof Walkable) {
			((Walkable) this).lock();
		}
		switch (c.getCurrentDirection()) {
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
	}

	public String getDialogue(String type) {
		return this.dialogues.get(type);
	}

	public void setDialogue(String type, String dialogue) {
		this.dialogues.put(type, dialogue);
	}

	public boolean moveTowardsCharacter(de.alexanderciupka.pokemon.characters.Character c) {
		int mainX = c.getCurrentPosition().x;
		int mainY = c.getCurrentPosition().y;
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
			for (int i = 1; i < this.range; i++) {
				if (this.currentPosition.x + (i * x) == mainX && this.currentPosition.y + (i * y) == mainY) {
					break;
				} else if (!this.currentRoute.getEntity(this.currentPosition.x + (i * x),
						this.currentPosition.y + (i * y)).isAccessible(this)) {
					return false;
				}
			}
			if(this instanceof Walkable) {
				((Walkable) this).lock();
			}
			switch (this.getCurrentDirection()) {
			case DOWN:
				c.setCurrentDirection(Direction.UP);
				break;
			case LEFT:
				c.setCurrentDirection(Direction.RIGHT);
				break;
			case RIGHT:
				c.setCurrentDirection(Direction.LEFT);
				break;
			case UP:
				c.setCurrentDirection(Direction.DOWN);
				break;
			default:
				break;
			}
			if (c instanceof Player) {
				this.gController.getGameFrame().getBackgroundLabel().spotted(this);
			}
			while (!(this.currentPosition.x + x == mainX && this.currentPosition.y + y == mainY)) {
				this.changePosition(this.getCurrentDirection(), true);
			}
			return true;
		}
		return false;
	}

	public void importTeam() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(
					"/characters/teams/" + this.currentRoute.getId() + "/" + this.getFileName() + ".txt")));

			JsonArray team = new JsonParser().parse(new JsonReader(reader)).getAsJsonArray();
			for (JsonElement e : team) {
				try {
					this.team.addPokemon(Pokemon.importSaveData(e.getAsJsonObject()));
				} catch (Exception ex) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void importDialogue() {
	// try {
	// BufferedReader reader = new BufferedReader(new
	// InputStreamReader(this.getClass().getResourceAsStream(
	// "/characters/dialoge/" + this.currentRoute.getId() + "/" +
	// this.getFileName() + ".char")));
	// JsonObject dialogue = new JsonParser().parse(new
	// JsonReader(reader)).getAsJsonObject();
	// // if (!(dialogue.get("reward") == null)) {
	// // this.importRewards(dialogue.get("reward").getAsString());
	// // }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// private void importRewards(String rewards) {
	// for (String s : rewards.split("\\+")) {
	// Item current = null;
	// try {
	// current = Item.valueOf(s.toUpperCase());
	// } catch (Exception e) {
	// continue;
	// }
	// this.rewards.put(current, this.rewards.get(current) == null ? 1 :
	// this.rewards.get(current) + 1);
	// }
	// }

	private String getFileName() {
		return this.name.toLowerCase().replace(" ", "_").replace("ä", "ae").replace("ö", "oe").replace("ü", "ue");
	}

	// public HashMap<Item, Integer> getRewards() {
	// return this.rewards;
	// }

	// public boolean hasRewards() {
	// for (Item i : this.rewards.keySet()) {
	// if (this.rewards.get(i) != null && this.rewards.get(i) > 0) {
	// return true;
	// }
	// }
	// return false;
	// }

	// public void removeReward(Item reward, Integer amount) {
	// if (this.rewards.get(reward) != null) {
	// int delta = this.rewards.get(reward) - amount;
	// if (delta <= 0) {
	// this.rewards.remove(reward);
	// } else {
	// this.rewards.put(reward, delta);
	// }
	// }
	// }

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void onInteraction(Player p) {
		this.faceTowardsCharacter(p);
		if (!this.isDefeated()) {
			this.gController.getGameFrame().addDialogue(this.getDialogue(NPC.DIALOGUE_BEFORE_ACTION), this);
			switch (this.fightingStyle) {
			case NO_DOUBLE:
				this.gController.startFight(this);
				break;
			case DOUBLE:
				this.gController.startFight(this, this);
				break;
			case FOLLOWER_DOUBLE:
				this.gController.startFight(this, this.getFollower());
			}
		} else {
			System.out.println(this.getName());
			this.gController.getGameFrame().addDialogue(this.getDialogue(DIALOGUE_NO_ACTION), this);
		}
	}

	public int getFightingStyle() {
		return this.fightingStyle;
	}

	public boolean checkStartFight(Player p) {
		if (this.isTrainer() && this.isAggro()) {
			if (!this.isDefeated()) {
				double mainX = p.getExactX();
				double mainY = p.getExactY();
				if (mainX == this.currentPosition.x || mainY == this.currentPosition.y) {
					int x = 0;
					int y = 0;
					switch (this.currentDirection) {
					case DOWN:
						if (mainY - this.currentPosition.y < 7 && mainY - this.currentPosition.y > 0) {
							y = 1;
							break;
						}
						return false;
					case LEFT:
						if (this.currentPosition.x - mainX < 7 && this.currentPosition.x - mainX > 0) {
							x = -1;
							break;
						}
						return false;
					case RIGHT:
						if (mainX - this.currentPosition.x < 7 && mainX - this.currentPosition.x > 0) {
							x = 1;
							break;
						}
						return false;
					case UP:
						if (this.currentPosition.y - mainY < 7 && this.currentPosition.y - mainY > 0) {
							y = -1;
							break;
						}
						return false;
					default:
						return false;
					}
					for (int i = 1; i <= range; i++) {
						if (this.getCurrentRoute()
								.getEntity(this.currentPosition.x + (i * x), this.currentPosition.y + (i * y))
								.isAccessible(this)) {
							if (mainX == this.currentPosition.x + (i * x)
									&& mainY == this.currentPosition.y + (i * y)) {
								return true;
							} else if (!this.currentRoute
									.getEntity(this.currentPosition.x + (i * x), this.currentPosition.y + (i * y))
									.isAccessible(this)) {
								return false;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void onDefeat(Player p) {
		this.gController.getGameFrame().getFightPanel().addText(this.getDialogue(DIALOGUE_ON_ACTION));
		if (this.money > 0) {
			this.gController.getGameFrame().getFightPanel()
					.addText(p.getName() + " erhält " + this.money + " Cupydollar!");
		}
	}

	public void afterFight(Player p) {
		this.gController.getGameFrame().addDialogue(this.getDialogue(DIALOGUE_AFTER_ACTION), this);
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty(NPC.DIALOGUE_NO_ACTION, this.dialogues.get(NPC.DIALOGUE_NO_ACTION));
		if (this.isTrainer()) {
			saveData.addProperty(NPC.DIALOGUE_BEFORE_ACTION, this.dialogues.get(NPC.DIALOGUE_BEFORE_ACTION));
			saveData.addProperty(NPC.DIALOGUE_AFTER_ACTION, this.dialogues.get(NPC.DIALOGUE_AFTER_ACTION));
			saveData.addProperty(NPC.DIALOGUE_ON_EXIT, this.dialogues.get(NPC.DIALOGUE_ON_EXIT));
			saveData.get("trainer_data").getAsJsonObject().addProperty("fighting_style", this.fightingStyle);
		}
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			this.dialogues.put(NPC.DIALOGUE_NO_ACTION, saveData.get(NPC.DIALOGUE_NO_ACTION).getAsString());
			if (this.isTrainer()) {
				this.dialogues.put(NPC.DIALOGUE_BEFORE_ACTION, saveData.get(NPC.DIALOGUE_BEFORE_ACTION).getAsString());
				this.dialogues.put(NPC.DIALOGUE_AFTER_ACTION, saveData.get(NPC.DIALOGUE_AFTER_ACTION).getAsString());
				this.dialogues.put(NPC.DIALOGUE_ON_EXIT, saveData.get(NPC.DIALOGUE_ON_EXIT).getAsString());
				this.fightingStyle = saveData.get("trainer_data").getAsJsonObject().get("fighting_style").getAsInt();
			}
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return this.id + " - " + this.name;
	}

	public int getRange() {
		return this.range;
	}
}
