package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Image;
import java.awt.Point;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.Weather;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.map.Warp;
import de.alexanderciupka.pokemon.map.entities.TriggeredEvent;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.PokemonInformation;

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

	public static final float POKEMON_GRASS_RATE = 0.1f;

	private int x;
	private int y;

	protected GameController gController;

	private int pokemonPool;
	private TriggeredEvent event;
	private Route parent;

	public Entity() {
		this.gController = GameController.getInstance();
	}

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

	public void setParent(Route route) {
		this.parent = route;
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
		return accessible && !this.hasCharacter(c);
	}

	public void setSprite(String spriteName) {
		this.spriteName = spriteName;
		this.sprite = this.gController.getRouteAnalyzer().getSpriteByName(spriteName);
	}

	public void setTerrain(String terrainName) {
		this.terrainName = terrainName;
		try {
			this.terrain = this.gController.getRouteAnalyzer().getTerrainByName(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getSprite() {
		// this.sprite =
		// this.gController.getRouteAnalyzer().getSpriteByName(this.spriteName);
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
		float rate = this.pokemonRate;
		switch (this.gController.getMainCharacter().getTeam().getTeam()[0].getAbility().getId()) {
		case Abilities.ERLEUCHTUNG:
		case Abilities.AUSWEGLOS:
			rate *= 2;
			break;
		case Abilities.RASANZ:
		case Abilities.PULVERRAUCH:
		case Abilities.DUFTNOTE:
			rate *= .5;
			break;
		case Abilities.SCHNEEMANTEL:
			if (this.parent.getWeather() == Weather.HAIL) {
				rate *= .5;
			}
		}
		if (Main.RNG.nextFloat() < rate && this.parent.getPoolById(this.pokemonPool) != null
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
			if (this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()) == null) {
				return false;
			}
			// if
			// (this.warp.getNewRoute().toLowerCase().equals("pokemon_center"))
			// {
			// this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()).getEntities()[5][2].getWarp()
			// .setNewPosition(c.getCurrentPosition().getLocation());
			// this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()).getEntities()[5][2].getWarp()
			// .setNewRoute(c.getCurrentRoute().getId());
			// }
			if (c instanceof Player) {
				if (c.equals(this.gController.getCurrentBackground().getCamera().getCenter())) {
					this.gController
							.setCurrentRoute(this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()));
				}
				if (c.getCurrentRoute() != null) {
					c.getCurrentRoute().reset();
				}
			}
			c.setCurrentRoute(this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()));
			c.setCurrentPosition(this.warp.getNewPosition());
			c.setCurrentDirection(this.warp.getNewDirection());
			if (c instanceof NPC) {
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getOldRoute()).removeCharacter(c);
				this.gController.getRouteAnalyzer().getRouteById(this.warp.getNewRoute()).addCharacter((NPC) c);
			}
			c.getCurrentRoute().getEntity(c.getCurrentPosition().x, c.getCurrentPosition().y).onStepNoWarp(c);
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
		if (!(this instanceof WaterEntity)) {
			c.setSurfing(false);
		}
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
		if (!(this instanceof IceEntity)) {
			if (!c.isControllable() && !c.isSpinning()) {
				c.setControllable(true);
			}
		}
		SimpleEntry<NPC, NPC> fight = this.gController.checkStartFight(c);
		if (!c.isEvent() && fight == null && c instanceof Player && !((Player) c).isProtected() && this.checkPokemon()) {
			System.out.println("pokemon");
			if (!this.gController.isFighting()) {
				Pokemon encounter = null;
				Pokemon encounter2 = null;
				if (this.parent.getPoolById(this.pokemonPool) != null) {
					encounter = this.parent.getPoolById(this.pokemonPool).getEncounter();
					encounter2 = this.parent.getPoolById(this.pokemonPool).getEncounter();
				}
				if (encounter == null) {
					encounter = this.gController.getCurrentBackground().chooseEncounter();
					encounter2 = this.gController.getCurrentBackground().chooseEncounter();
				}
				this.gController.startFight(encounter, encounter2);
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
				character.onInteraction(c);
				this.gController.waitDialogue();
				// if (character.isTrainer()) {
				// if (!character.isDefeated()) {
				// character.faceTowardsMainCharacter();
				// this.gController.startFight(character);
				// flag = true;
				// }
				// }
				// if (!flag) {
				// character.faceTowardsCharacter(c);
				// this.gController.getGameFrame().addDialogue(character.getDialogue(NPC.DIALOGUE_NO_FIGHT),
				// character);
				// this.gController.waitDialogue();
				// if (character.getName().equals("Joy")) {
				// c.getTeam().restoreTeam();
				// if
				// (character.getCurrentRoute().getId().equals("pokemon_center"))
				// {
				// for (int i = 1; i <= c.getTeam().getAmmount() + 1; i++) {
				// this.gController.getCurrentBackground().getCurrentRoute().getEntities()[0][1]
				// .setSprite("joyhealing" + (i % (c.getTeam().getAmmount() +
				// 1)));
				// this.gController.getCurrentBackground().getCurrentRoute().updateMap(new
				// Point(1, 0));
				// if (i == c.getTeam().getAmmount()) {
				// SoundController.getInstance().playSound(SoundController.POKECENTER_HEAL);
				// this.gController.sleep(1500);
				// } else {
				// this.gController.sleep(750);
				// }
				// }
				// }
				// this.gController.getGameFrame().addDialogue("Deine Pokemon
				// sind nun wieder topfit!");
				// this.gController.waitDialogue();
				// }
				// if (character.getName().equals("Maria") &&
				// character.getCurrentRoute().getId().equals("zuhause")) {
				// c.getTeam().restoreTeam();
				// }
				// if (character.hasRewards()) {
				// c.earnRewards(character.getRewards(), true);
				// character.getRewards().clear();
				// }
				// }
			}
		} else if (this.getSpriteName().equals("free") && !this.isAccessible(c)) {
			if (this.y - 1 >= 0) {
				if (c.getCurrentRoute().getEntity(this.x, this.y - 1).hasCharacter() && c.getCurrentRoute()
						.getEntity(this.x, this.y - 1).getCharacters().get(0).getName().equals("Joy")) {
					c.getCurrentRoute().getEntity(this.x, this.y - 1).onInteraction(c);
				}
			}
		} else if (this.isPC()) {
			this.gController.getGameFrame().displayPC(c);
		}

		for (Integer item : c.getItems().keySet()) {
			if (this.gController.getInformation().getItemData(Items.ITEM_NAME, item).toString().contains("hm")) {
				this.useVM(c, item);
			}
		}

	}

	public boolean useVM(Player source, Integer vm) {
		PokemonInformation info = this.gController.getInformation();
		boolean result = false;
		boolean canUse = false;

		for (Pokemon p : source.getTeam().getTeam()) {
			if (p != null) {
				for (Move m : p.getMoves()) {
					if (m != null) {
						if (m.getId() == Integer.valueOf(info.getItemData(Items.ITEM_MACHINE, vm).toString())) {
							canUse = true;
						}
					}
				}
			}
		}
		switch (vm) {
		case Items.CUTTER:
			if (this.spriteName.equals("treecut")) {
				if (canUse) {
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
		// case FLASH:
		// if (source.getCurrentRoute().isDark()) {
		// if (hasItem) {
		// result = true;
		// ((DarkOverlay)
		// (this.gController.getGameFrame().getBackgroundLabel().getOverlay(DarkOverlay.class)))
		// .flash();
		// }
		// }
		// break;
		case Items.ROCKSMASH:
			if (this.spriteName.equals("rock")) {
				if (canUse) {
					result = true;
					this.gController.getGameFrame().addDialogue("Du hast den Felsen zertrümmert!");
					SoundController.getInstance().playSound(SoundController.ROCKSMASH);
					this.setSprite("free");
					this.setAccessible(true);
					this.gController.waitDialogue();
				} else {
					this.gController.getGameFrame().addDialogue("Dieser Felsen könnte zertrümmert werden!");
				}
			} else {
				return false;
			}
			break;
		case Items.STRENGTH:
			boolean hasStone = false;
			if (this.hasCharacter()) {
				for (NPC character : this.getCharacters()) {
					if (character.getID().equals("strength")) {
						hasStone = true;
						if (canUse) {
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
		case Items.SURFBOARD:
			// if (this.isWater() && !source.isSurfing()) {
			// if (canUse) {
			// result = true;
			// this.gController.getGameFrame().addDialogue("Du fängst an zu
			// surfen!");
			// source.setSurfing(true);
			// this.gController.waitDialogue();
			// source.changePosition(source.getCurrentDirection(), true);
			// } else {
			// this.gController.getGameFrame().addDialogue("Hier könnte man
			// surfen!");
			// }
			// } else {
			// return false;
			// }
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
		return access;
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
					&& this.x == other.x && this.y == other.y
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
		clone.event = this.event != null ? this.event.clone() : null;
		clone.x = this.x;
		clone.y = this.y;
		clone.pokemonPool = this.pokemonPool;
		return clone;
	}

	public JsonObject getSaveData() {
		JsonObject saveData = new JsonObject();

		saveData.addProperty("terrain", this.getTerrainName());
		saveData.addProperty("sprite", this.getSpriteName());
		saveData.addProperty("encounter_rate", this.getEncounterRate());
		saveData.addProperty("pool", this.pokemonPool);
		saveData.addProperty("type", this.getClass().getSimpleName());

		JsonObject accessibility = new JsonObject();
		accessibility.addProperty("left", left);
		accessibility.addProperty("right", right);
		accessibility.addProperty("top", top);
		accessibility.addProperty("bottom", bottom);
		saveData.add("accessibility", accessibility);

		if (warp != null) {
			JsonObject warpData = new JsonObject();
			warpData.addProperty("id", this.warp.getWarpString());
			warpData.addProperty("new_route", this.warp.getNewRoute());
			warpData.addProperty("new_x", this.warp.getNewPosition().x);
			warpData.addProperty("new_y", this.warp.getNewPosition().y);
			warpData.addProperty("direction_change", this.warp.getNewDirection().toString());
			saveData.add("warp", warpData);
		}

		return saveData;
	}

	public boolean importSaveData(JsonObject saveData) {
		this.setTerrain(saveData.get("terrain").getAsString());
		this.setSprite(saveData.get("sprite").getAsString());
		this.setEncounterRate(saveData.get("encounter_rate").getAsFloat());
		if (saveData.has("pool")) {
			this.setPokemonPool(saveData.get("pool").getAsInt());
		} else {
			this.setPokemonPool(0);
		}

		this.setAccessible(saveData.get("accessibility").getAsJsonObject().get("left").getAsBoolean(),
				saveData.get("accessibility").getAsJsonObject().get("right").getAsBoolean(),
				saveData.get("accessibility").getAsJsonObject().get("top").getAsBoolean(),
				saveData.get("accessibility").getAsJsonObject().get("bottom").getAsBoolean());

		if (saveData.has("warp")) {
			JsonObject w = saveData.get("warp").getAsJsonObject();
			this.addWarp(new Warp(w.get("id").getAsString(), this.parent.getName(), w.get("new_route").getAsString(),
					new Point(w.get("new_x").getAsInt(), w.get("new_y").getAsInt()),
					Direction.valueOf(w.get("direction_change").getAsString().toUpperCase())));
		}

		return true;
	}

	public Route getRoute() {
		return this.parent;
	}

	public void reset() {
		// Do nothing - needs to be overwritten by subclasses
	}
}
