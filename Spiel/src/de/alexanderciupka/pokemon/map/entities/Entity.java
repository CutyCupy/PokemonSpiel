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
import de.alexanderciupka.pokemon.exceptions.InvalidEntityDataException;
import de.alexanderciupka.pokemon.gui.overlay.DarkOverlay;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.map.RouteAnalyzer;
import de.alexanderciupka.pokemon.map.Warp;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

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

	protected GameController gController;

	// private PokemonPool pokemonPool;
	private int pokemonPool;
	private TriggeredEvent event;
	private Route parent;

	public Entity(Route currentRoute) {
		this.gController = GameController.getInstance();
		this.parent = currentRoute;
	}

	public Entity(String parentID, boolean left, boolean right, boolean top, boolean bottom, String spriteName,
			float pokemonRate, String terrainName) {
		this.gController = GameController.getInstance();
		this.parent = this.gController.getRouteAnalyzer().getRouteById(parentID);
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		try {
			this.setSprite(spriteName);
			this.setTerrain(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.pokemonRate = pokemonRate;
		new Random();
	}

	public Entity(Route parent, boolean accessible, String spriteName, float pokemonRate, String terrainName) {
		this.gController = GameController.getInstance();
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
		new Random();
	}

	public Entity(Route parent, boolean left, boolean right, boolean top, boolean bottom, String spriteName,
			float pokemonRate, String terrainName) {
		this.gController = GameController.getInstance();
		this.parent = parent;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		try {
			this.setSprite(spriteName);
			this.setTerrain(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.pokemonRate = pokemonRate;
		new Random();
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
				&& (!this.hasCharacter(c));
	}

	public void setSprite(String spriteName) {
		this.spriteName = spriteName;
		this.sprite = this.gController.getRouteAnalyzer().getSpriteByName(spriteName);
	}

	public void setTerrain(String terrainName) {
		this.terrainName = terrainName;
		try {
			this.terrain = this.gController.getRouteAnalyzer().getTerrainByName(terrainName);
			this.setWater(terrainName.equals("see"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getSprite() {
		this.sprite = this.gController.getRouteAnalyzer().getSpriteByName(this.spriteName);
		return this.sprite;
	}

	public String getSpriteName() {
		return this.spriteName;
	}

	public Image[] getCharacterSprites() {
		ArrayList<Image> result = new ArrayList<Image>();
		for (int i = 0; i < this.parent.getCharacters().size(); i++) {
			Point curPoint = this.parent.getCharacters().get(i).getCurrentPosition();
			if (curPoint.x == this.x && curPoint.y == this.y) {
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
			if (curPoint.x == this.x && curPoint.y == this.y) {
				characters.add(this.parent.getCharacters().get(i));
			}
		}
		return characters;
	}

	public boolean checkPokemon() {
		if (this.pokemonRate > 0 && this.parent.getPoolById(this.pokemonPool) != null
				&& this.parent.getPoolById(this.pokemonPool).getPokemonPool().size() > 0) {
			return true;
		}
		return false;
	}

	public float getEncounterRate() {
		return this.pokemonRate;
	}

	public void setEncounterRate(float f) {
		this.pokemonRate = f;
	}

	public boolean startWarp(Character c) {
		if (this.warp != null) {
			this.gController.getRouteAnalyzer()
					.updateHatches(this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()));
			if (this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()) == null) {
				return false;
			}
			if (this.warp.getNewRoute().toLowerCase().equals("pokemon_center")) {
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()).getEntities()[5][2].getWarp()
						.setNewPosition(c.getCurrentPosition().getLocation());
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()).getEntities()[5][2].getWarp()
						.setNewRoute(c.getCurrentRoute().getId());
			}
			if (c instanceof Player) {
				if (c.equals(this.gController.getCurrentBackground().getCamera().getCenter())) {
					this.gController.resetCharacterPositions();
					this.gController
							.setCurrentRoute(this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()));
				}
			}
			c.setCurrentRoute(this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()));
			c.setCurrentPosition(this.warp.getNewPosition());
			c.setCurrentDirection(this.warp.getNewDirection());
			if (c instanceof NPC) {
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getOldRoute()).removeCharacter(c);
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()).addCharacter((NPC) c);
				if (this.bottom) {
					this.gController.getRouteAnalyzer().getRouteById(this.warp.getOldRoute())
							.updateMap(new Point(this.x, this.y + 1));
				}
				if (this.top) {
					this.gController.getRouteAnalyzer().getRouteById(this.warp.getOldRoute())
							.updateMap(new Point(this.x, this.y - 1));
				}
				if (this.left) {
					this.gController.getRouteAnalyzer().getRouteById(this.warp.getOldRoute())
							.updateMap(new Point(this.x - 1, this.y));
				}
				if (this.right) {
					this.gController.getRouteAnalyzer().getRouteById(this.warp.getOldRoute())
							.updateMap(new Point(this.x + 1, this.y));
				}
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute())
						.updateMap(c.getCurrentPosition());
			}
			c.getCurrentRoute().getEntities()[c.getCurrentPosition().y][c.getCurrentPosition().x].onStepNoWarp(c);
			return true;
		}
		return false;
	}

	public boolean hasCharacter() {
		return !this.getCharacters().isEmpty();
	}

	public boolean hasCharacter(Character c) {
		for (NPC npc : this.getCharacters()) {
			if (!npc.equals(c.getFollower())) {
				return true;
			}
		}
		return false;
	}

	public Warp getWarp() {
		return this.warp;
	}

	public String getTerrainName() {
		return this.terrainName;
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

	public void setWater(boolean water) {
		this.water = water;
	}

	public boolean isWater() {
		return this.water;
	}

	public boolean isPC() {
		return this.spriteName.equals("pc") || this.spriteName.equals("laptop");
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getPokemonPool() {
		return this.pokemonPool;
	}

	public void setPokemonPool(int pokemonPool) {
		this.pokemonPool = pokemonPool;
	}

	public void onStep(Character c) {
		if (!this.startWarp(c)) {
			this.onStepNoWarp(c);
		}
	}

	public void onStepNoWarp(Character c) {
		if (this.event != null && c instanceof Player && !c.isEvent()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Entity.this.event.startEvent((Player) c);
				}
			}).start();
			return;
		}
		if (this.terrainName.equals("ice")) {
			c.startUncontrollableMove(c.getCurrentDirection(), false, Character.FAST);
		} else {
			if (!c.isControllable() && !c.isSpinning()) {
				c.setControllable(true);
			}
		}
		int characterIndex = this.gController.checkStartFight();
		if (!c.isEvent() && c instanceof Player && characterIndex >= 0) {
			NPC enemy = this.gController.getCurrentBackground().getCurrentRoute().getCharacters().get(characterIndex);
			if (enemy.moveTowardsMainCharacter()) {
				if (!this.gController.isFighting()) {
					this.gController.startFight(enemy);
				}
			}
		} else if (!c.isEvent() && c instanceof Player && !((Player) c).isProtected() && this.checkPokemon()) {
			if (!this.gController.isFighting()) {
				Pokemon encounter = null;
				if (this.parent.getPoolById(this.pokemonPool) != null) {
					encounter = this.parent.getPoolById(this.pokemonPool).getEncounter();
				}
				if (encounter == null) {
					encounter = this.gController.getCurrentBackground().chooseEncounter();
				}
				this.gController.startFight(encounter);
			}
		} else {
			if (this.spriteName.startsWith("move")) {
				switch (this.spriteName) {
				case "moveleft":
					c.startUncontrollableMove(Direction.LEFT, true, Character.VERY_SLOW);
					break;
				case "moveright":
					c.startUncontrollableMove(Direction.RIGHT, true, Character.VERY_SLOW);
					break;
				case "moveup":
					c.startUncontrollableMove(Direction.UP, true, Character.VERY_SLOW);
					break;
				case "movedown":
					c.startUncontrollableMove(Direction.DOWN, true, Character.VERY_SLOW);
					break;
				case "movestop":
					c.setControllable(true);
				}
			}
		}
	}

	public void onInteraction(Player c) {
		boolean flag = false;
		if (this.hasCharacter()) {
			for (NPC character : this.getCharacters()) {
				if (character.getName() == null) {
					continue;
				}
				if (character.isTrainer()) {
					if (!character.isDefeated()) {
						character.faceTowardsMainCharacter();
						this.gController.startFight(character);
						flag = true;
					}
				}
				if (!flag) {
					character.faceTowardsMainCharacter();
					this.gController.getGameFrame().addDialogue(character.getNoFightDialogue(), character);
					this.gController.waitDialogue();
					if (character.getName().equals("Joy")) {
						c.getTeam().restoreTeam();
						if (character.getCurrentRoute().getId().equals("pokemon_center")) {
							for (int i = 1; i <= c.getTeam().getAmmount() + 1; i++) {
								this.gController.getCurrentBackground().getCurrentRoute().getEntities()[0][1]
										.setSprite("joyhealing" + (i % (c.getTeam().getAmmount() + 1)));
								this.gController.getCurrentBackground().getCurrentRoute().updateMap(new Point(1, 0));
								if (i == c.getTeam().getAmmount()) {
									SoundController.getInstance().playSound(SoundController.POKECENTER_HEAL);
									this.gController.sleep(1500);
								} else {
									this.gController.sleep(750);
								}
							}
						}
						this.gController.getGameFrame().addDialogue("Deine Pokemon sind nun wieder topfit!");
						this.gController.waitDialogue();
					}
					if (character.getName().equals("Maria") && character.getCurrentRoute().getId().equals("zuhause")) {
						c.getTeam().restoreTeam();
					}
					if (character.hasRewards()) {
						c.earnRewards(character.getRewards(), true);
						character.getRewards().clear();
					}
				}
			}
		} else if (this.getSpriteName().equals("free") && !this.isAccessible(c)) {
			if (this.y - 1 >= 0) {
				if (c.getCurrentRoute().getEntities()[this.y - 1][this.x].hasCharacter()
						&& c.getCurrentRoute().getEntities()[this.y - 1][this.x].getCharacters().get(0).getName()
								.equals("Joy")) {
					c.getCurrentRoute().getEntities()[this.y - 1][this.x].onInteraction(c);
				}
			}
		} else if (this.isPC()) {
			this.gController.getGameFrame().displayPC(c);
		}

		for (Item i : Item.values()) {
			if (!i.isUsableOnPokemon()) {
				this.useVM(c, i);
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
					this.gController.getGameFrame().addDialogue("Du zerschneidest den Baum!");
					this.gController.waitDialogue();
					this.setAccessible(true);

					new Thread(new Runnable() {
						@Override
						public void run() {
							SoundController.getInstance().playSound(SoundController.CUT);
							for (int i = 1; i <= 3; i++) {
								Entity.this.setSprite("treecut" + i);
								source.getCurrentRoute().updateMap(source.getInteractionPoint());
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();
				} else {
					this.gController.getGameFrame().addDialogue("Dieser Baum könnte zerschnitten werden!");
				}
			}
			break;
		case FLASH:
			if (source.getCurrentRoute().isDark()) {
				if (hasItem) {
					result = true;
					((DarkOverlay) (this.gController.getGameFrame().getBackgroundLabel().getOverlay(DarkOverlay.class)))
							.flash();
				}
			}
			break;
		case ROCKSMASH:
			if (this.spriteName.equals("rock")) {
				if (hasItem) {
					result = true;
					this.gController.getGameFrame().addDialogue("Du hast den Felsen zertrümmert!");
					SoundController.getInstance().playSound(SoundController.ROCKSMASH);
					this.setSprite("free");
					this.setAccessible(true);
					source.getCurrentRoute().updateMap(source.getInteractionPoint());
					this.gController.waitDialogue();
				} else {
					this.gController.getGameFrame().addDialogue("Dieser Felsen könnte zertrümmert werden!");
				}
			} else {
				return false;
			}
			break;
		case STRENGTH:
			boolean hasStone = false;
			if (this.hasCharacter()) {
				for (NPC character : this.getCharacters()) {
					if (character.getID().equals("strength")) {
						hasStone = true;
						if (hasItem) {
							result = true;
							character.setCurrentDirection(source.getCurrentDirection());
							character.changePosition(source.getCurrentDirection(), true);
						} else {
							this.gController.getGameFrame().addDialogue("Dieser Felsen kann bewegt werden!");
							this.gController.waitDialogue();
						}
					}
				}
			}
			if (!hasStone) {
				return false;
			}
		case SURF:
			if (this.isWater() && !source.isSurfing()) {
				if (hasItem) {
					result = true;
					this.gController.getGameFrame().addDialogue("Du fängst an zu surfen!");
					source.setSurfing(true);
					this.gController.waitDialogue();
					source.changePosition(source.getCurrentDirection(), true);
				} else {
					this.gController.getGameFrame().addDialogue("Hier könnte man surfen!");
				}
			} else {
				return false;
			}
			break;
		default:
			return false;
		}

		this.gController.waitDialogue();
		return result;
	}

	public TriggeredEvent getEvent() {
		return this.event;
	}

	public void setEvent(TriggeredEvent event) {
		this.event = event;
	}

	public boolean isAccessible(Direction dir) {
		boolean access = false;
		switch (dir) {
		case DOWN:
			access = this.top;
		case LEFT:
			access = this.right;
		case RIGHT:
			access = this.left;
		case UP:
			access = this.bottom;
		case NONE:
		}
		return access && !this.isWater();
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
		clone.pokemonPool = this.pokemonPool;
		return clone;
	}

	public void load(JsonObject data) throws InvalidEntityDataException {
		final String[] MUST_HAVES = new String[] { "terrain", "sprite", "accessible", "encounter_rate" };
		if (RouteAnalyzer.getWrongMember(data, MUST_HAVES) != null) {
			throw new InvalidEntityDataException(this, RouteAnalyzer.getWrongMember(data, MUST_HAVES));
		}

		this.setTerrain(data.get("terrain").getAsString());
		this.setSprite(data.get("sprite").getAsString());
		this.setAccessible(data.get("accessible").getAsBoolean());

		if (data.has("pool")) {
			this.setPokemonPool(data.get("pool").getAsInt());
		}

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
					this.event.importSaveData(saveData.get("event").getAsJsonObject(), entity.getEvent());
				}
			} else {
				this.event = entity.getEvent() == null ? null : entity.getEvent().clone();
			}

			return true;
		}
		return false;
	}

	public Route getRoute() {
		return this.parent;
	}
}
