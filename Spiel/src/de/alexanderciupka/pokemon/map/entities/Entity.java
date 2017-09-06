package de.alexanderciupka.pokemon.map.entities;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.gui.overlay.DarkOverlay;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.map.Warp;
import de.alexanderciupka.pokemon.pokemon.Item;

public class Entity {

	private boolean left;
	private boolean right;
	private boolean top;
	private boolean bottom;

	protected Image sprite;
	private float pokemonRate;
	private Warp warp;
	private Image terrain;

	private String terrainName;
	protected String spriteName;

	private boolean water;

	public static final float POKEMON_GRASS_RATE = 0.1f;

	private int x;
	private int y;

	private double exactX;
	private double exactY;

	private Random rng;
	protected GameController gController;

	private TriggeredEvent event;
	private Route parent;

	public Entity(String parentID, boolean left, boolean right, boolean top, boolean bottom, String spriteName,
			float pokemonRate, String terrainName) {
		gController = GameController.getInstance();
		this.parent = gController.getRouteAnalyzer().getRouteById(parentID);
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		try {
			setSprite(spriteName);
			setTerrain(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.pokemonRate = pokemonRate;
		rng = new Random();
	}

	public Entity(Route parent, boolean accessible, String spriteName, float pokemonRate, String terrainName) {
		gController = GameController.getInstance();
		this.parent = parent;
		this.left = accessible;
		this.right = accessible;
		this.top = accessible;
		this.bottom = accessible;
		try {
			this.setSprite(spriteName);
			this.setTerrain(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.pokemonRate = pokemonRate;
		rng = new Random();
	}

	public Entity(Route parent, boolean left, boolean right, boolean top, boolean bottom, String spriteName,
			float pokemonRate, String terrainName) {
		gController = GameController.getInstance();
		this.parent = parent;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		try {
			setSprite(spriteName);
			setTerrain(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.pokemonRate = pokemonRate;
		rng = new Random();
	}

	public boolean isAccessible(Character c) {
		if (c.ignoreCollisions) {
			return true;
		}
		boolean accessible = false;
		switch (c.getCurrentDirection()) {
		case DOWN:
			accessible = this.top;
			break;
		case LEFT:
			accessible = this.right;
			break;
		case RIGHT:
			accessible = this.left;
			break;
		case UP:
			accessible = this.bottom;
			break;
		default:
			accessible = false;
			break;
		}
		return ((accessible && !this.isWater()) || (this.isWater() && c.isSurfing() && accessible))
				&& !this.hasCharacter();
	}

	public void setSprite(String spriteName) {
		this.spriteName = spriteName;
		if (spriteName.equals("grass")) {
			setTerrain("grassy");
		}
		this.sprite = gController.getRouteAnalyzer().getSpriteByName(spriteName);
	}

	public void setTerrain(String terrainName) {
		this.terrainName = terrainName;
		try {
			this.terrain = gController.getRouteAnalyzer().getTerrainByName(terrainName);
			this.setWater(terrainName.equals("see"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getSprite() {
		return this.sprite;
	}

	public String getSpriteName() {
		return this.spriteName;
	}

	public Image[] getCharacterSprites() {
		ArrayList<Image> result = new ArrayList<Image>();
		for (int i = 0; i < this.parent.getCharacters().size(); i++) {
			Point curPoint = this.parent.getCharacters().get(i).getCurrentPosition();
			if (curPoint.x == x && curPoint.y == y) {
				result.add(this.parent.getCharacters().get(i).getCharacterImage());
			}
		}
		return result.toArray(new Image[result.size()]);
	}

	public Image getTerrain() {
		return this.terrain;
	}

	public void addWarp(Warp warp) {
		this.warp = warp;
	}

	public ArrayList<NPC> getCharacters() {
		ArrayList<NPC> characters = new ArrayList<NPC>();
		for (int i = 0; i < this.parent.getCharacters().size(); i++) {
			Point curPoint = this.parent.getCharacters().get(i).getCurrentPosition();
			if (curPoint.x == x && curPoint.y == y) {
				characters.add(this.parent.getCharacters().get(i));
			}
		}
		return characters;
	}

	public boolean checkPokemon() {
		if (pokemonRate > 0 && gController.getCurrentBackground().getCurrentRoute().getPokemonPool().size() != 0) {
			if (rng.nextFloat() <= pokemonRate) {
				gController.getGameFrame().getBackgroundLabel().startEncounter();
				return true;
			}
		}
		return false;
	}

	public boolean startWarp(Character c) {
		if (warp != null) {
			if (gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()) == null) {
				return false;
			}
			if (warp.getNewRoute().toLowerCase().equals("pokemon_center")) {
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[4][2].getWarp()
						.setNewPosition(c.getCurrentPosition().getLocation());
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[4][2].getWarp()
						.setNewRoute(c.getCurrentRoute().getId());
			}
			if (c instanceof Player) {
				gController.resetCharacterPositions();
				gController.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()));
//				gController.getGameFrame().getBackgroundLabel()
//						.changeRoute(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()));
			}
			c.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()));
			c.setCurrentPosition(warp.getNewPosition());
			if (this.spriteName.contains("stair")) {
				switch (c.getCurrentDirection()) {
				case LEFT:
					c.setCurrentDirection(Direction.RIGHT);
					break;
				case RIGHT:
					c.setCurrentDirection(Direction.LEFT);
					break;
				default:
					break;
				}
			}
			if (c instanceof NPC) {
				gController.getRouteAnalyzer().getRouteById(warp.getOldRoute()).removeCharacter(c);
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).addCharacter((NPC) c);
				if (bottom) {
					gController.getRouteAnalyzer().getRouteById(warp.getOldRoute())
							.updateMap(new Point(this.x, this.y + 1));
				}
				if (top) {
					gController.getRouteAnalyzer().getRouteById(warp.getOldRoute())
							.updateMap(new Point(this.x, this.y - 1));
				}
				if (left) {
					gController.getRouteAnalyzer().getRouteById(warp.getOldRoute())
							.updateMap(new Point(this.x - 1, this.y));
				}
				if (right) {
					gController.getRouteAnalyzer().getRouteById(warp.getOldRoute())
							.updateMap(new Point(this.x + 1, this.y));
				}
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).updateMap(c.getCurrentPosition());
			}
			gController.getGameFrame().repaint();
			c.getCurrentRoute().getEntities()[c.getCurrentPosition().y][c.getCurrentPosition().x].onStepNoWarp(c);
			return true;
		}
		return false;
	}

	public boolean hasCharacter() {
		return !this.getCharacters().isEmpty();
	}

	public Warp getWarp() {
		return this.warp;
	}

	public String getTerrainName() {
		return terrainName;
	}

	public void setAccessible(boolean accessible) {
		this.left = accessible;
		this.right = accessible;
		this.top = accessible;
		this.bottom = accessible;
	}

	public void setAccessible(boolean left, boolean right, boolean top, boolean bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	private void setWater(boolean water) {
		this.water = water;
	}

	public boolean isWater() {
		return this.water;
	}

	public boolean isPC() {
		return spriteName.equals("pc") || spriteName.equals("laptop");
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		this.exactX = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		this.exactY = y;
	}

	public double getExactX() {
		return exactX;
	}

	public double getExactY() {
		return exactY;
	}

	public void onStep(Character c) {
		if (!startWarp(c)) {
			onStepNoWarp(c);
		}
	}

	public void onStepNoWarp(Character c) {
		if (this.event != null && c instanceof Player) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					event.startEvent((Player) c);
				}
			}).start();
			return;
		}
		int characterIndex = gController.checkStartFight();
		if (c instanceof Player && characterIndex >= 0) {
			NPC enemy = gController.getCurrentBackground().getCurrentRoute().getCharacters().get(characterIndex);
			if (enemy.moveTowardsMainCharacter()) {
				if (!this.gController.isFighting()) {
					gController.startFight(enemy);
				}
			}
		} else if (c instanceof Player && !((Player) c).isProtected() && checkPokemon()) {
			if (!this.gController.isFighting()) {
				gController.startFight(gController.getCurrentBackground().chooseEncounter());
			}
		} else {
			if (this.spriteName.startsWith("move")) {
				switch (spriteName) {
				case "moveleft":
					c.startUncontrollableMove(Direction.LEFT);
					break;
				case "moveright":
					c.startUncontrollableMove(Direction.RIGHT);
					break;
				case "moveup":
					c.startUncontrollableMove(Direction.UP);
					break;
				case "movedown":
					c.startUncontrollableMove(Direction.DOWN);
					break;
				case "movestop":
					c.setControllable(true);
				}
			}
		}
	}

	public void onInteraction(Player c) {
		boolean flag = false;
		if (hasCharacter()) {
			for (NPC character : getCharacters()) {
				if (character.getName() == null) {
					continue;
				}
				if (character.isTrainer()) {
					if (!character.isDefeated()) {
						character.faceTowardsMainCharacter();
						gController.startFight(character);
						flag = true;
					}
				}
				if (!flag) {
					character.faceTowardsMainCharacter();
					gController.getGameFrame().addDialogue(character.getName() + ": " + character.getNoFightDialogue());
					gController.waitDialogue();
					if (character.getName().equals("Joy")) {
						c.getTeam().restoreTeam();
						if (character.getCurrentRoute().getId().equals("pokemon_center")) {
							for (int i = 1; i <= c.getTeam().getAmmount() + 1; i++) {
								gController.getCurrentBackground().getCurrentRoute().getEntities()[0][1]
										.setSprite("joyhealing" + (i % (c.getTeam().getAmmount() + 1)));
								gController.getCurrentBackground().getCurrentRoute().updateMap(new Point(1, 0));
								gController.getGameFrame().repaint();
								gController.sleep(i == c.getTeam().getAmmount() ? 1500 : 750);
							}
						}
						gController.getGameFrame().addDialogue("Deine Pokemon sind nun wieder topfit!");
						gController.waitDialogue();
					}
					if (character.getName().equals("Maria")
							&& gController.getCurrentBackground().getCurrentRoute().getName().equals("zuhause")) {
						c.getTeam().restoreTeam();
					}
				}
			}
		} else if (getSpriteName().equals("free") && !isAccessible(c)) {
			if (y - 1 >= 0) {
				if (c.getCurrentRoute().getEntities()[y - 1][x].hasCharacter()
						&& c.getCurrentRoute().getEntities()[y - 1][x].getCharacters().get(0).getName().equals("Joy")) {
					c.getCurrentRoute().getEntities()[y - 1][x].onInteraction(c);
				}
			}
		} else if (isPC()) {
			gController.getGameFrame().displayPC(c);
		}

		for (Item i : Item.values()) {
			if (!i.isUsableOnPokemon()) {
				useVM(c, i);
			}
		}

	}

	public boolean useVM(Player source, Item vm) {
		boolean result = false;
		boolean hasItem = source.hasItem(vm);
		switch (vm) {
		case CUT:
			if (this.spriteName.equals("treecut")) {
				if (hasItem) {
					result = true;
					gController.getGameFrame().addDialogue("Du zerschneidest den Baum!");
					gController.waitDialogue();
					this.setAccessible(true);

					new Thread(new Runnable() {
						@Override
						public void run() {
							for (int i = 1; i <= 3; i++) {
								setSprite("treecut" + i);
								source.getCurrentRoute().updateMap(source.getInteractionPoint());
								gController.getGameFrame().repaint();
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();
				} else {
					gController.getGameFrame().addDialogue("Dieser Baum k�nnte zerschnitten werden!");
				}
			}
			break;
		case FLASH:
			if (source.getCurrentRoute().isDark()) {
				if (hasItem) {
					result = true;
					((DarkOverlay) (gController.getGameFrame().getBackgroundLabel().getOverlay(DarkOverlay.class)))
							.flash();
				} else {
					gController.getGameFrame().addDialogue("Ein Hilfsmitte könnte die Höhle erleuchten!");
				}
			}
			break;
		case ROCKSMASH:
			if (this.spriteName.equals("rock")) {
				if (hasItem) {
					result = true;
					gController.getGameFrame().addDialogue("Du hast den Felsen zertrümmert!");
					this.setSprite("free");
					this.setAccessible(true);
					source.getCurrentRoute().updateMap(source.getInteractionPoint());
					gController.waitDialogue();
				} else {
					gController.getGameFrame().addDialogue("Dieser Felsen könnte zertr�mmert werden!");
				}
				gController.getGameFrame().repaint();
			} else {
				return false;
			}
			break;
		case STRENGTH:
			boolean hasStone = false;
			if (hasCharacter()) {
				for (NPC character : getCharacters()) {
					if (character.getID().equals("strength")) {
						hasStone = true;
						if (hasItem) {
							result = true;
							character.setCurrentDirection(source.getCurrentDirection());
							character.changePosition(source.getCurrentDirection(), true);
						} else {
							gController.getGameFrame().addDialogue("Dieser Felsen kann bewegt werden!");
							gController.waitDialogue();
						}
					}
				}
			}
			if (!hasStone) {
				return false;
			}
		case SURF:
			if (isWater() && !source.isSurfing()) {
				if (hasItem) {
					result = true;
					gController.getGameFrame().addDialogue("Du f�ngst an zu surfen!");
					source.setSurfing(true);
					gController.waitDialogue();
					source.changePosition(source.getCurrentDirection(), true);
				} else {
					gController.getGameFrame().addDialogue("Hier k�nnte man surfen!");
				}
			} else {
				return false;
			}
			break;
		default:
			return false;
		}

		gController.waitDialogue();
		return result;
	}

	public void setExactX(double x) {
		this.exactX = x;
	}

	public void setExactY(double y) {
		this.exactY = y;
	}

	public TriggeredEvent getEvent() {
		return event;
	}

	public void setEvent(TriggeredEvent event) {
		this.event = event;
	}

	public boolean isAccessible(Direction dir) {
		switch (dir) {
		case DOWN:
			return this.top;
		case LEFT:
			return this.right;
		case RIGHT:
			return this.left;
		case UP:
			return this.bottom;
		case NONE:
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Entity) {
			Entity other = (Entity) obj;
			return (other.left == this.left && other.right == this.right && other.bottom == this.bottom
					&& other.top == this.top && other.pokemonRate == this.pokemonRate
					&& ((this.warp == null && other.warp == null)
							|| (this.warp != null && this.warp.equals(other.warp)))
					&& this.terrainName.equals(other.terrainName) && this.spriteName.equals(other.spriteName)
					&& this.water == other.water && this.x == other.x && this.y == other.y
					&& ((this.event == null && other.event == null)
							|| (this.event != null && this.event.equals(other.event)))
					&& this.parent.getId().equals(other.parent.getId()));
		}
		return false;
	}

	@Override
	public Entity clone() {
		Entity clone = new Entity(this.parent.getId(), this.left, this.right, this.top, this.bottom, this.spriteName,
				this.pokemonRate, this.terrainName);
		if (this.warp != null) {
			clone.addWarp(this.warp.clone());
		}
		clone.water = this.water;
		clone.event = this.event != null ? this.event.clone() : null;
		clone.x = this.x;
		clone.y = this.y;
		return clone;
	}

	public JsonObject getSaveData(Entity entity) {
		JsonObject saveData = new JsonObject();

		saveData.addProperty("x", this.x);
		saveData.addProperty("y", this.y);

		if (this.left != entity.left) {
			saveData.addProperty("left", this.left);
		}
		if (this.right != entity.right) {
			saveData.addProperty("right", this.right);
		}
		if (this.top != entity.top) {
			saveData.addProperty("top", this.top);
		}
		if (this.bottom != entity.bottom) {
			saveData.addProperty("bottom", this.bottom);
		}
		if (!this.terrainName.equals(entity.terrainName)) {
			saveData.addProperty("terrain", this.terrainName);
		}
		if (!this.spriteName.equals(entity.spriteName)) {
			saveData.addProperty("sprite", this.spriteName);
		}
		if (this.water != entity.water) {
			saveData.addProperty("water", this.water);
		}
		if (this.event != entity.getEvent()
				&& (this.event == null || entity.getEvent() == null || !this.event.equals(entity.getEvent()))) {
			saveData.add("event", this.event == null ? null : this.event.getSaveData(entity.getEvent()));
		}
		return saveData;
	}

	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if (this.x == saveData.get("x").getAsInt() && this.y == saveData.get("y").getAsInt()) {
			if (saveData.get("left") != null) {
				this.left = saveData.get("left").getAsBoolean();
			} else {
				this.left = entity.left;
			}
			if (saveData.get("right") != null) {
				this.right = saveData.get("right").getAsBoolean();
			} else {
				this.right = entity.right;
			}
			if (saveData.get("top") != null) {
				this.top = saveData.get("top").getAsBoolean();
			} else {
				this.top = entity.top;
			}
			if (saveData.get("bottom") != null) {
				this.bottom = saveData.get("bottom").getAsBoolean();
			} else {
				this.bottom = entity.bottom;
			}
			if (saveData.get("terrain") != null) {
				this.terrainName = saveData.get("terrain").getAsString();
			} else {
				this.terrainName = entity.terrainName;
			}
			if (saveData.get("sprite") != null) {
				this.spriteName = saveData.get("sprite").getAsString();
			} else {
				this.spriteName = entity.spriteName;
			}
			if (saveData.get("water") != null) {
				this.water = saveData.get("water").getAsBoolean();
			} else {
				this.water = entity.water;
			}
			if (saveData.get("event") != null) {
				if (this.event != null) {
					event.importSaveData(saveData.get("event").getAsJsonObject(), entity.getEvent());
				}
			} else {
				this.event = entity.getEvent() == null ? null : entity.getEvent().clone();
			}

			return true;
		}
		return false;
	}

}
