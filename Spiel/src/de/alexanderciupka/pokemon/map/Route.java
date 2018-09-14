package de.alexanderciupka.pokemon.map;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.characters.types.Walkable;
import de.alexanderciupka.pokemon.exceptions.InvalidEntityDataException;
import de.alexanderciupka.pokemon.exceptions.InvalidRouteDataException;
import de.alexanderciupka.pokemon.fighting.Weather;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.gui.overlay.FogType;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.gui.overlay.SnowType;
import de.alexanderciupka.pokemon.map.entities.Change;
import de.alexanderciupka.pokemon.map.entities.Entity;
import de.alexanderciupka.pokemon.map.entities.ItemEntity;
import de.alexanderciupka.pokemon.map.entities.PokemonEntity;
import de.alexanderciupka.pokemon.map.entities.QuestionEntity;
import de.alexanderciupka.pokemon.map.entities.QuestionType;
import de.alexanderciupka.pokemon.map.entities.SignEntity;
import de.alexanderciupka.pokemon.map.entities.TriggeredEvent;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.PokemonPool;

public class Route {

	private String id;
	private String name;
	private int width;
	private int height;
	private boolean dark;
	private Entity[][] entities;
	private HashMap<String, ArrayList<Point>> buildings;
	private ArrayList<NPC> characters;
	private BufferedImage tempMap;
	private BufferedImage map;
	private RainType rain;
	private SnowType snow;
	private FogType fog;

	private HashMap<Integer, PokemonPool> pools;

	private RouteType type;

	private boolean wait;
	private Thread spinnerThread;

	public Route() {
		this.pools = new HashMap<>();
		this.buildings = new HashMap<>();
		this.characters = new ArrayList<NPC>();

		this.spinnerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Player p = GameController.getInstance().getMainCharacter();
					if (p != null && Route.this.equals(p.getCurrentRoute(), false)) {
						while (GameController.getInstance().getInteractionPause()) {
							Thread.yield();
						}
						for (NPC npc : Route.this.characters) {
							// TODO: Spinner should turn, when player runs next
							// to them
						}

						for (NPC npc : Route.this.characters) {
							if (npc instanceof Walkable) {
								((Walkable) npc).move();
							}
						}
					}
					Thread.yield();
				}
			}
		});
		this.spinnerThread.setDaemon(true);

		this.spinnerThread.start();

	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		this.spinnerThread.setName("SPINNER_" + name.toUpperCase());
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Entity[][] getEntities() {
		return this.entities;
	}

	public boolean isDark() {
		return this.dark;
	}

	public void setDark(boolean dark) {
		this.dark = dark;
	}

	public void addEntity(int x, int y, Entity entity) {
		this.createEntities();
		this.entities[y][x] = entity;
	}

	public void addEntities(int row, ArrayList<Entity> rowEntities) {
		this.createEntities();
		for (int x = 0; x < this.width; x++) {
			this.entities[row][x] = rowEntities.get(x);
		}
	}

	public void addCharacter(NPC character) {
		this.characters.add(character);
	}

	public boolean removeCharacter(de.alexanderciupka.pokemon.characters.types.Character c) {
		for (int i = 0; i < this.characters.size(); i++) {
			if (c.equals(this.characters.get(i))) {
				this.characters.remove(i);
				return true;
			}
		}
		return false;
	}

	private void createEntities() {
		if (this.entities == null || this.invalidSize()) {
			this.entities = new Entity[this.height][this.width];
		}
	}

	private boolean invalidSize() {
		if (this.entities.length == this.height) {
			for (int i = 0; i < this.height; i++) {
				if (this.entities[i].length != this.width) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public ArrayList<NPC> getCharacters() {
		return this.characters;
	}

	public void clearCharacters() {
		this.characters.clear();
	}

	public HashMap<String, ArrayList<Point>> getBuildings() {
		return this.buildings;
	}

	public void addBuilding(String building, Point location) {
		ArrayList<Point> locations = this.buildings.get(building);
		if (locations == null) {
			locations = new ArrayList<Point>();
		}
		locations.add(location);
		this.buildings.put(building, locations);
	}

	public void createMap() {
		this.tempMap = new BufferedImage(this.width * GameFrame.GRID_SIZE, this.height * GameFrame.GRID_SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		this.map = new BufferedImage(this.width * GameFrame.GRID_SIZE, this.height * GameFrame.GRID_SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		Point[] points = new Point[this.width * this.height];
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				points[x + y * this.width] = new Point(x, y);
			}
		}
		this.updateMap(points);
		this.saveMap();
		this.map = this.tempMap;
	}

	public BufferedImage generateMap(int startX, int startY, int w, int h) {
		BufferedImage map = new BufferedImage(w * GameFrame.GRID_SIZE, h * GameFrame.GRID_SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = map.getGraphics();
		Player mc = GameController.getInstance().getMainCharacter();
		ArrayList<de.alexanderciupka.pokemon.characters.types.Character> chars = new ArrayList<>(this.characters);
		if (mc != null && this.equals(mc.getCurrentRoute(), false)) {
			chars.add(mc);
		}
		for (int x = startX; x <= startX + w; x++) {
			for (int y = startY; y <= startY + h; y++) {
				if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
					continue;
				}
				if (this.entities[y][x] != null) {
					try {
						g.drawImage(this.entities[y][x].getTerrain(), (x - startX) * GameFrame.GRID_SIZE,
								(y - startY) * GameFrame.GRID_SIZE, null);
						g.drawImage(this.entities[y][x].getSprite(), (x - startX) * GameFrame.GRID_SIZE,
								(y - startY) * GameFrame.GRID_SIZE, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (de.alexanderciupka.pokemon.characters.types.Character c : chars) {
					int deltaX = c.getCharacterImage().getWidth(null) / GameFrame.GRID_SIZE;
					int deltaY = c.getCharacterImage().getHeight(null) / GameFrame.GRID_SIZE;
					if (c.getCurrentPosition().x >= x - deltaX && c.getCurrentPosition().x <= x + deltaX
							&& c.getCurrentPosition().y >= y - deltaY && c.getCurrentPosition().y <= y + deltaY
							|| c.getOldPosition().x >= x - deltaX && c.getOldPosition().x <= x + deltaX
									&& c.getOldPosition().y >= y - deltaY && c.getOldPosition().y <= y + deltaY) {
						double xPos = (c.getExactX() - startX) * GameFrame.GRID_SIZE
								- (c.getCharacterImage().getWidth(null) - GameFrame.GRID_SIZE) / 2.0;
						double yPos = (c.getExactY() - startY) * GameFrame.GRID_SIZE
								- (c.getCharacterImage().getHeight(null) - GameFrame.GRID_SIZE);
						g.drawImage(c.getCharacterImage(), (int) Math.round(xPos), (int) Math.round(yPos), null);
					}
				}
			}
		}
		return map;
	}

	public void updateMap(Point... updatePoint) {
		// while (this.wait) {
		// Thread.yield();
		// }
		// this.wait = true;
		// if (this.tempMap != null) {
		// Player mc = GameController.getInstance().getMainCharacter();
		// Graphics g = this.tempMap.getGraphics();
		//
		// for (Point p : updatePoint) {
		// if (!(p.x >= 0 && p.x < this.width && p.y >= 0 && p.y < this.height))
		// {
		// continue;
		// }
		// try {
		// g.drawImage(this.entities[p.y][p.x].getTerrain(), p.x *
		// GameFrame.GRID_SIZE,
		// p.y * GameFrame.GRID_SIZE, null);
		// g.drawImage(this.entities[p.y][p.x].getSprite(), p.x *
		// GameFrame.GRID_SIZE,
		// p.y * GameFrame.GRID_SIZE, null);
		// if (mc != null && this.equals(mc.getCurrentRoute(), false) &&
		// p.equals(mc.getCurrentPosition())) {
		// g.drawImage(mc.getCharacterImage(), (int) (mc.getExactX() *
		// GameFrame.GRID_SIZE),
		// (int) (mc.getExactY() * GameFrame.GRID_SIZE), null);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// for (NPC npc : this.characters) {
		// if (npc.getCurrentPosition().equals(p)) {
		// g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() *
		// GameFrame.GRID_SIZE),
		// (int) (npc.getExactY() * GameFrame.GRID_SIZE), null);
		// }
		// }
		// if (mc != null && this.equals(mc.getCurrentRoute(), false)) {
		// if (mc.getCurrentPosition().equals(p)) {
		// g.drawImage(mc.getCharacterImage(), (int) (mc.getExactX() *
		// GameFrame.GRID_SIZE),
		// (int) (mc.getExactY() * GameFrame.GRID_SIZE), null);
		// }
		// }
		// }
		// // for (String building : this.buildings.keySet()) {
		// // BufferedImage currentBuilding =
		// // GameController.getInstance().getRouteAnalyzer()
		// // .getSpriteByName(building);
		// // for (Point p : this.buildings.get(building)) {
		// // g.drawImage(currentBuilding, p.x * 70, p.y * 70, null);
		// // }
		// // }
		// //
		// // ArrayList<Character> character = new ArrayList<>(this.characters);
		// // if (mc != null && this.equals(mc.getCurrentRoute(), false)) {
		// // character.add(mc);
		// // }
		// //
		// // for (int i = 0; i < character.size(); i++) {
		// // Character c = character.get(i);
		// // boolean repaint = true;
		// // for (String building : this.buildings.keySet()) {
		// // if (!repaint) {
		// // break;
		// // }
		// // BufferedImage currentBuilding =
		// // GameController.getInstance().getRouteAnalyzer()
		// // .getSpriteByName(building);
		// // int buildingWidth = currentBuilding.getWidth() -
		// (currentBuilding.getWidth()
		// // % GameFrame.GRID_SIZE);
		// // int buildingHeight = currentBuilding.getHeight()
		// // - (currentBuilding.getHeight() % GameFrame.GRID_SIZE);
		// //
		// // for (Point p : this.buildings.get(building)) {
		// // if (!repaint) {
		// // break;
		// // }
		// //
		// // if (((c.getExactX() * 70 + c.getCharacterImage().getWidth(null) >
		// p.x * 70
		// // && c.getExactX() * 70 < p.x * 70 + buildingWidth)
		// // && (c.getExactY() * 70 + c.getCharacterImage().getHeight(null) >
		// p.y * 70
		// // && c.getExactY() * 70 < p.y * 70 + buildingHeight))) {
		// // repaint = false;
		// // }
		// // }
		// // }
		// // if (repaint) {
		// // g.drawImage(c.getCharacterImage(), (int) (c.getExactX() * 70),
		// (int)
		// // (c.getExactY() * 70), null);
		// // }
		// // }
		// // if (mc != null && this.equals(mc.getCurrentRoute(), false)
		// // &&
		// //
		// (this.entities[mc.getCurrentPosition().y][mc.getCurrentPosition().x].getWarp()
		// // != null
		// // || (mc.getOldPosition().x < this.width && mc.getOldPosition().y <
		// this.height
		// // && this.entities[mc.getOldPosition().y][mc.getOldPosition().x]
		// // .getWarp() != null))) {
		// // g.drawImage(mc.getCharacterImage(), (int) (mc.getExactX() * 70),
		// (int)
		// // (mc.getExactY() * 70), null);
		// // }
		// this.map = this.tempMap;
		// }
		// this.wait = false;
	}

	public Entity getEntity(int x, int y) {
		if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
			return this.entities[y][x];
		}
		return null;
	}

	void saveMap() {
		try {
			while (this.wait) {
				Thread.sleep(1);
			}
			ImageIO.write(this.map, "png", new File("./res/routes/" + this.id + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getMap() {
		while (this.wait) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.map;
	}

	public NPC getNPC(String id) {
		for (int i = 0; i < this.characters.size(); i++) {
			if (this.characters.get(i).getID().equals(id)) {
				return this.characters.get(i);
			}
		}
		return null;
	}

	public NPC getNPCByName(String name) {
		for (int i = 0; i < this.characters.size(); i++) {
			if (this.characters.get(i).getName() != null
					&& name.toLowerCase().equals(this.characters.get(i).getName().toLowerCase())) {
				return this.characters.get(i);
			}
		}
		return null;
	}

	public RainType getRain() {
		return this.rain;
	}

	public void setRain(RainType rain) {
		this.rain = rain;
	}

	public SnowType getSnow() {
		return this.snow;
	}

	public void setSnow(SnowType snow) {
		this.snow = snow;
	}

	public RouteType getType() {
		return this.type;
	}

	public void setType(RouteType type) {
		this.type = type;
	}

	public boolean equals(Object obj, boolean withCharacters) {
		if (obj instanceof Route) {
			Route other = (Route) obj;
			if (this.height != other.height || this.width != other.width) {
				return false;
			}
			for (int x = 0; x < this.width; x++) {
				for (int y = 0; y < this.height; y++) {
					if (this.entities[y][x] == null ? other.entities[y][x] == null
							: !this.entities[y][x].equals(other.entities[y][x])) {
						return false;
					}
				}
			}
			if (this.characters.size() != other.characters.size()) {
				return false;
			}
			if (withCharacters) {
				for (int i = 0; i < this.characters.size(); i++) {
					boolean contains = false;
					for (int j = 0; j < other.characters.size(); j++) {
						if (other.characters.get(j).equals(this.characters.get(i))) {
							contains = true;
							break;
						}
					}
					if (!contains) {
						return false;
					}
				}
			}
			return this.id.equals(other.id) && this.name.equals(other.name) && this.rain == other.rain
					&& this.snow == other.snow && this.fog == other.fog;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return this.equals(obj, true);
	}

	public void loadRouteData(JsonObject data) throws InvalidRouteDataException {
		final String[] MUST_HAVES = new String[] { "name", "dark", "type", "rain", "snow", "entities" };

		if (RouteAnalyzer.getWrongMember(data, MUST_HAVES) != null) {
			throw new InvalidRouteDataException(this, RouteAnalyzer.getWrongMember(data, MUST_HAVES));
		}

		this.setName(data.get("name").getAsString());
		this.setDark(data.get("dark").getAsBoolean());
		this.setType(RouteType.valueOf(data.get("type").getAsString().toUpperCase()));
		this.setRain(RainType.valueOf(data.get("rain").getAsString().toUpperCase()));
		this.setSnow(SnowType.valueOf(data.get("snow").getAsString().toUpperCase()));

		JsonObject entities = data.get("entities").getAsJsonObject();

		this.setHeight(0);
		this.setWidth(0);
		for (Entry<String, JsonElement> entry : entities.entrySet()) {
			this.setWidth(Math.max(Integer.valueOf(entry.getKey().substring(0, entry.getKey().indexOf("."))) + 1,
					this.getWidth()));
			this.setHeight(Math.max(Integer.valueOf(entry.getKey().substring(entry.getKey().indexOf(".") + 1)) + 1,
					this.getHeight()));
		}
		this.createEntities();
		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getWidth(); x++) {
				String coordinates = x + "." + y;
				if (entities.get(coordinates) == null) {
					continue;
				}

				Entity currentEntity = new Entity(this);
				try {
					currentEntity.load(entities.get(coordinates).getAsJsonObject());
					currentEntity.setX(x);
					currentEntity.setY(y);
					this.addEntity(x, y, currentEntity);
				} catch (InvalidEntityDataException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadWarps(JsonObject data) {
		if (data.get("warps").isJsonArray()) {
			final String[] MUST_HAVES = new String[] { "id", "new_route", "new_x", "new_y", "x", "y", "direction" };
			for (JsonElement e : data.get("warps").getAsJsonArray()) {
				JsonObject warp = e.getAsJsonObject();
				if (RouteAnalyzer.getWrongMember(warp, MUST_HAVES) != null) {
					continue;
				}

				Warp w = new Warp(warp.get("id").getAsString(), this.id);
				w.setNewRoute(warp.get("new_route").getAsString());
				w.setNewPosition(new Point(warp.get("new_x").getAsInt(), warp.get("new_y").getAsInt()));
				w.setNewDirection(warp.get("direction").getAsString());

				this.getEntity(warp.get("x").getAsInt(), warp.get("y").getAsInt()).addWarp(w);
			}
		}
	}

	private void loadCharacters(JsonObject data) {
		if (data.get("characters").isJsonArray()) {
			final String[] MUST_HAVES = new String[] { "id", "x", "y", "char_sprite", "name", "direction",
					"is_trainer" };
			for (JsonElement e : data.get("characters").getAsJsonArray()) {
				JsonObject currentChar = e.getAsJsonObject();
				if (RouteAnalyzer.getWrongMember(currentChar, MUST_HAVES) != null) {
					continue;
				}
				NPC character = new NPC(currentChar.get("id").getAsString());
				character.setCurrentPosition(currentChar.get("x").getAsInt(), currentChar.get("y").getAsInt());
				character.setCurrentRoute(this);
				character.setCharacterImage(currentChar.get("char_sprite").getAsString(),
						currentChar.get("direction").getAsString().toLowerCase());
				character.setName(currentChar.get("name").getAsString());
				// character.setTrainer(currentChar.get("is_trainer").getAsString());
				character.setLogo(currentChar.has("logo") ? currentChar.get("logo").getAsString() : null);
				// character.setAggro(currentChar.has("aggro") ?
				// currentChar.get("aggro").getAsBoolean() : true);
				character.setRange(currentChar.has("range") ? currentChar.get("range").getAsInt() : 4);
				character
						.setSurfing(this.getEntity((int) character.getExactX(), (int) character.getExactY()).isWater());
				character.importTeam();
				character.importDialogue();
				this.addCharacter(character);
			}
		}
	}

	private void loadPokemons(JsonObject data) {
		if (data.get("pokemons").isJsonArray()) {
			final String[] MUST_HAVES = new String[] { "x", "y", "name", "level", "shiny", "interaction_message",
					"no_interaction_message", "required_items" };
			for (JsonElement e : data.get("pokemons").getAsJsonArray()) {
				JsonObject currentPokemon = e.getAsJsonObject();
				if (RouteAnalyzer.getWrongMember(currentPokemon, MUST_HAVES) != null) {
					continue;
				}
				PokemonEntity pokemon = PokemonEntity.convert(
						this.getEntity(currentPokemon.get("x").getAsInt(), currentPokemon.get("y").getAsInt()));
				Pokemon p = new Pokemon(
						GameController.getInstance().getInformation().getID(currentPokemon.get("name").getAsString()));
				p.getStats().generateStats(currentPokemon.get("level").getAsShort());
				pokemon.setPokemon(p);
				pokemon.setInteractionMessage(currentPokemon.get("interaction_message").getAsString());
				pokemon.setNoInteractionMessage(currentPokemon.get("no_interaction_message").getAsString());
				pokemon.importRequiredItems(currentPokemon.get("required_items"));
				this.addEntity(pokemon.getX(), pokemon.getY(), pokemon);
			}
		}
	}

	private void loadItems(JsonObject data) {
		if (data.get("items").isJsonArray()) {
			final String[] MUST_HAVES = new String[] { "x", "y", "id", "hidden" };
			for (JsonElement e : data.get("items").getAsJsonArray()) {
				JsonObject currentItem = e.getAsJsonObject();
				if (RouteAnalyzer.getWrongMember(currentItem, MUST_HAVES) != null) {
					continue;
				}
				ItemEntity entity = ItemEntity
						.convert(this.getEntity(currentItem.get("x").getAsInt(), currentItem.get("y").getAsInt()));
				entity.setItem(currentItem.get("id").getAsInt());
				entity.setHidden(currentItem.get("hidden").getAsBoolean());
				this.addEntity(entity.getX(), entity.getY(), entity);
			}
		}
	}

	private void loadEvents(JsonObject data) {
		if (data.get("events").isJsonArray()) {
			final String[] MUST_HAVE = new String[] { "character", "route", "target_x", "target_y", "direction",
					"update", "fight", "heal", "remove", "cam_x", "cam_y", "cam_animation", "cam_center", "wait",
					"delay", "unknown", "pause" };
			for (JsonElement e : data.get("events").getAsJsonArray()) {
				TriggeredEvent te = new TriggeredEvent("");
				if (!e.isJsonObject() || RouteAnalyzer.getWrongMember(e.getAsJsonObject(),
						new String[] { "loc", "sequences" }) != null) {
					continue;
				}
				for (JsonElement sequence : e.getAsJsonObject().get("sequences").getAsJsonArray()) {
					ArrayList<Change> changes = new ArrayList<Change>();
					for (JsonElement current : sequence.getAsJsonArray()) {
						JsonObject move = current.getAsJsonObject();
						if (RouteAnalyzer.getWrongMember(move, MUST_HAVE) != null) {
							continue;
						}
						Change currentChange = new Change();
						currentChange.load(move);
						changes.add(currentChange);
					}
					te.addChanges(changes.toArray(new Change[changes.size()]));
				}
				for (JsonElement j : e.getAsJsonObject().get("loc").getAsJsonArray()) {
					this.getEntities()[j.getAsJsonObject().get("x").getAsInt()][j.getAsJsonObject().get("y").getAsInt()]
							.setEvent(te);
				}
			}
		}
	}

	private void loadSigns(JsonObject data) {
		if (data.get("signs").isJsonArray()) {
			for (JsonElement e : data.get("signs").getAsJsonArray()) {
				JsonObject current = e.getAsJsonObject();
				if (current.has("information")) {
					SignEntity sign = SignEntity
							.convert(this.getEntity(current.get("x").getAsInt(), current.get("y").getAsInt()));
					sign.setInformation(current.get("information").getAsString());
					this.addEntity(sign.getX(), sign.getY(), sign);
				}
			}
		}
	}

	private void loadEncounters(JsonObject data) {
		if (data.get("encounters").isJsonObject()) {
			JsonObject allPools = data.get("encounters").getAsJsonObject();
			for (Entry<String, JsonElement> s : allPools.entrySet()) {
				PokemonPool current = new PokemonPool(Integer.valueOf(s.getKey()));
				for (JsonElement e : s.getValue().getAsJsonArray()) {
					JsonObject encounter = e.getAsJsonObject();
					int minLevel = encounter.get("min_level").getAsInt();
					int maxLevel = encounter.get("max_level").getAsInt();
					for (int i = 0; i < encounter.get("amount").getAsInt(); i++) {
						current.addPokemon(encounter.get("id").getAsInt(),
								(short) ThreadLocalRandom.current().nextInt(minLevel, maxLevel + 1));
					}
				}
				this.addPool(current.getId(), current);
			}
		}
	}

	private void loadQuizzes(JsonObject data) {
		if (data.get("quizzes").isJsonArray()) {
			for (JsonElement e : data.get("quizzes").getAsJsonArray()) {
				JsonObject currentQuiz = e.getAsJsonObject();
				QuestionEntity q = QuestionEntity
						.convert(this.getEntity(currentQuiz.get("x").getAsInt(), currentQuiz.get("y").getAsInt()));
				q.setQuestion(currentQuiz.get("question") == null ? "" : currentQuiz.get("question").getAsString());
				q.addOptions(currentQuiz.get("options").getAsString().split("\\+"));
				q.addSolutions(currentQuiz.get("solutions").getAsString().split("\\+"));
				q.setSource(currentQuiz.get("source") != null ? currentQuiz.get("source").getAsString() : "nothing");
				q.setNPC(this.getNPC(currentQuiz.get("npc").getAsString()));
				q.setType(QuestionType.valueOf(currentQuiz.get("type").getAsString().toUpperCase()));
				for (JsonElement j : currentQuiz.get("gates").getAsJsonArray()) {
					JsonObject gates = j.getAsJsonObject();
					q.addGates(GameController.getInstance().getRouteAnalyzer()
							.getRouteById(gates.get("route").getAsString())
							.getEntity(gates.get("x").getAsInt(), gates.get("y").getAsInt()));
				}
				this.addEntity(q.getX(), q.getY(), q);
			}
		}
	}

	public void loadExtras(JsonObject data) throws InvalidRouteDataException {
		if (data.has("warps")) {
			this.loadWarps(data);
		}
		if (data.has("characters")) {
			this.loadCharacters(data);
		}
		if (data.has("pokemons")) {
			this.loadPokemons(data);
		}
		if (data.has("items")) {
			this.loadItems(data);
		}
		if (data.has("events")) {
			this.loadEvents(data);
		}
		if (data.has("signs")) {
			this.loadSigns(data);
		}
		if (data.has("encounters")) {
			this.loadEncounters(data);
		}
		if (data.has("quizzes")) {
			this.loadQuizzes(data);
		}

	}

	public boolean importSaveData(JsonObject saveData, Route route) {
		if (this.id.equals(saveData.get("id").getAsString())) {
			if (saveData.get("name") != null) {
				this.name = saveData.get("name").getAsString();
			} else {
				this.name = route.name;
			}
			JsonElement rain = saveData.get("rain");
			if (rain != null && rain instanceof JsonNull) {
				this.rain = null;
			} else if (rain != null) {
				try {
					this.setRain(RainType.valueOf(rain.getAsString().toUpperCase()));
				} catch (Exception e) {
					this.setRain(route.getRain());
				}
			} else {
				this.setRain(route.getRain());
			}

			JsonElement snow = saveData.get("snow");
			if (snow != null && snow instanceof JsonNull) {
				this.snow = null;
			} else if (snow != null) {
				try {
					this.setSnow(SnowType.valueOf(snow.getAsString().toUpperCase()));
				} catch (Exception e) {
					this.setSnow(route.getSnow());
				}
			} else {
				this.setSnow(route.getSnow());
			}

			JsonElement fog = saveData.get("fog");
			if (fog != null && fog instanceof JsonNull) {
				this.fog = null;
			} else if (fog != null) {
				try {
					this.setFog(FogType.valueOf(fog.getAsString().toUpperCase()));
				} catch (Exception e) {
					this.setFog(route.getFog());
				}
			} else {
				this.setFog(route.getFog());
			}

			this.createEntities();
			if (saveData.get("entities") != null) {
				for (JsonElement j : saveData.get("entities").getAsJsonArray()) {
					JsonObject currentEntity = j.getAsJsonObject();
					int x = currentEntity.get("x").getAsInt();
					int y = currentEntity.get("y").getAsInt();
					this.entities[y][x].importSaveData(currentEntity, route.getEntities()[y][x]);
				}
			} else {
				for (int x = 0; x < this.width; x++) {
					for (int y = 0; y < this.height; y++) {
						this.entities[y][x] = route.getEntities()[y][x].clone();
						if (route.getId().equals("wald_von_hamburg")) {
							this.entities[y][x].getSpriteName();
						}
					}
				}
			}
			this.createMap();
			return true;
		}
		return false;
	}

	public JsonObject getSaveData(Route oldRoute) {
		if (this.equals(oldRoute, false)) {
			return null;
		}
		JsonObject saveData = new JsonObject();
		saveData.addProperty("id", this.id);
		if (!this.name.equals(oldRoute.name)) {
			saveData.addProperty("name", this.name);
		}
		JsonArray entities = null;
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (!this.entities[y][x].equals(oldRoute.entities[y][x])) {
					if (entities == null) {
						entities = new JsonArray();
					}
					entities.add(this.entities[y][x].getSaveData(oldRoute.entities[y][x]));
				}
			}
		}
		if (entities != null) {
			saveData.add("entities", entities);
		}
		if (this.rain == null ? oldRoute.rain != null : !this.rain.equals(oldRoute.rain)) {
			saveData.addProperty("rain", this.rain.name());
		}
		if (this.snow == null ? oldRoute.snow != null : !this.snow.equals(oldRoute.snow)) {
			saveData.addProperty("snow", this.snow != null ? this.snow.name() : null);
		}
		if (this.fog == null ? oldRoute.fog != null : !this.fog.equals(oldRoute.fog)) {
			saveData.addProperty("fog", this.fog != null ? this.fog.name() : null);
		}
		return saveData;
	}

	public void setFog(FogType fog) {
		this.fog = fog;
	}

	public FogType getFog() {
		return this.fog;
	}

	public boolean addPool(int id, PokemonPool pool) {
		System.out.println(id);
		System.out.println(pool);
		boolean result = !this.pools.containsKey(id);
		this.pools.put(id, pool);
		return result;
	}

	public PokemonPool getPoolById(int id) {
		return this.pools.get(id);
	}

	public Weather getWeather() {
		if (!RainType.CLEAR.equals(this.rain)) {
			return Weather.RAIN;
		} else if (!SnowType.CLEAR.equals(this.snow)) {
			return Weather.HAIL;
		} else if (this.fog != null) {
			return Weather.FOG;
		}
		return Weather.NONE;
		// TODO: Sandstorm + Sun?
	}

}
