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
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.characters.types.Spinner;
import de.alexanderciupka.pokemon.characters.types.Walkable;
import de.alexanderciupka.pokemon.fighting.Weather;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.gui.overlay.FogType;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.gui.overlay.SnowType;
import de.alexanderciupka.pokemon.map.entities.Change;
import de.alexanderciupka.pokemon.map.entities.TriggeredEvent;
import de.alexanderciupka.pokemon.map.entities.types.Entity;
import de.alexanderciupka.pokemon.map.entities.types.WaterEntity;
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

	public Route() {
		this.pools = new HashMap<>();
		this.buildings = new HashMap<>();
		this.characters = new ArrayList<NPC>();
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
		entity.setX(x);
		entity.setY(y);
		this.entities[x][y] = entity;
	}

	public void addEntities(int row, ArrayList<Entity> rowEntities) {
		this.createEntities();
		for (int x = 0; x < this.width; x++) {
			this.entities[row][x] = rowEntities.get(x);
		}
	}

	public void moveCharacters() {
		Player p = GameController.getInstance().getMainCharacter();
		if (p != null && Route.this.equals(p.getCurrentRoute(), false)
				&& !GameController.getInstance().getInteractionPause()) {

			for (NPC npc : Route.this.characters) {
				if (npc instanceof Walkable) {
					if (npc instanceof Spinner) {
						Spinner spinner = (Spinner) npc;
						if (p.getSpeed() == Player.FAST && p.isMoving()) {
							Point delta = new Point((int) Math.round(npc.getCurrentPosition().x - p.getExactX()),
									(int) Math.round(npc.getCurrentPosition().y - p.getExactY()));
							if (Math.abs(delta.x) < Math.abs(delta.y)) {
								if (Math.abs(delta.x) <= 1
										&& spinner.isValidDirection(delta.y < 0 ? Direction.DOWN : Direction.UP)
										&& Math.abs(delta.y) <= npc.getRange()) {
									spinner.setLockedDirection((delta.y < 0 ? Direction.DOWN : Direction.UP));
								} else {
									spinner.setLockedDirection(Direction.NONE);
								}
							} else {
								if (Math.abs(delta.y) <= 1
										&& spinner.isValidDirection(delta.x < 0 ? Direction.RIGHT : Direction.LEFT)
										&& Math.abs(delta.x) <= npc.getRange()) {
									spinner.setLockedDirection(delta.x < 0 ? Direction.RIGHT : Direction.LEFT);
								} else {
									spinner.setLockedDirection(Direction.NONE);
								}
							}
						} else {
							spinner.setLockedDirection(Direction.NONE);
						}
					}
					((Walkable) npc).move();
				}
			}
		}
	}

	public void addCharacter(NPC character) {
		this.characters.add(character);
	}

	public boolean removeCharacter(de.alexanderciupka.pokemon.characters.Character c) {
		for (int i = 0; i < this.characters.size(); i++) {
			if (c.equals(this.characters.get(i))) {
				this.characters.remove(i);
				return true;
			}
		}
		return false;
	}

	private void createEntities() {
		if (this.entities == null) {
			this.entities = new Entity[this.width][this.height];
		} else if (this.invalidSize()) {
			Entity[][] updated = new Entity[this.width][this.height];
			for (int x = 0; x < Math.min(this.entities.length, this.width); x++) {
				for (int y = 0; y < Math.min(this.entities[x].length, this.height); y++) {
					updated[x][y] = this.entities[x][y];
				}
			}
			this.entities = updated;
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
		this.saveMap();
		this.map = this.tempMap;
	}

	public BufferedImage generateMap(int startX, int startY, int w, int h) {
		BufferedImage map = new BufferedImage(w * GameFrame.GRID_SIZE, h * GameFrame.GRID_SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = map.getGraphics();
		Player mc = GameController.getInstance().getMainCharacter();
		ArrayList<de.alexanderciupka.pokemon.characters.Character> chars = new ArrayList<>(this.characters);
		if (mc != null && this.equals(mc.getCurrentRoute(), false)) {
			chars.add(mc);
		}
		for (int x = startX; x <= startX + w; x++) {
			for (int y = startY; y <= startY + h; y++) {
				if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
					continue;
				}
				if (this.getEntity(x, y) != null) {
					try {
						g.drawImage(this.getEntity(x, y).getTerrain(), (x - startX) * GameFrame.GRID_SIZE,
								(y - startY) * GameFrame.GRID_SIZE, null);
						g.drawImage(this.getEntity(x, y).getSprite(), (x - startX) * GameFrame.GRID_SIZE,
								(y - startY) * GameFrame.GRID_SIZE, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (de.alexanderciupka.pokemon.characters.Character c : chars) {
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

	public Entity getEntity(int x, int y) {
		if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
			return this.entities[x][y];
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
					if (this.getEntity(x, y) == null ? other.getEntity(x, y) == null
							: !this.getEntity(x, y).equals(other.getEntity(x, y))) {
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

	public void loadRouteData(JsonObject data) {
		if (data.has("name")) {
			this.setName(data.get("name").getAsString());
		}
		if (data.has("properties")) {
			loadProperties(data.get("properties").getAsJsonObject());
		}
		this.createEntities();
		if (data.has("entities")) {
			loadEntities(data.get("entities").getAsJsonObject());
		}

	}

	private void loadProperties(JsonObject data) {
		if (data.has("dark")) {
			this.setDark(data.get("dark").getAsBoolean());
		}
		if (data.has("type")) {
			this.setType(RouteType.valueOf(data.get("type").getAsString().toUpperCase()));
		}
		if (data.has("rain")) {
			this.setRain(RainType.valueOf(data.get("rain").getAsString().toUpperCase()));
		}
		if (data.has("snow")) {
			this.setSnow(SnowType.valueOf(data.get("snow").getAsString().toUpperCase()));
		}
		if (data.has("fog")) {
			this.setFog(FogType.valueOf(data.get("fog").getAsString().toUpperCase()));
		}
	}

	private void loadEntities(JsonObject data) {

		for (Entry<String, JsonElement> entries : data.entrySet()) {
			String coords = entries.getKey();
			int x = Integer.parseInt(coords.substring(0, coords.indexOf("."))) + 1;
			int y = Integer.parseInt(coords.substring(coords.indexOf(".") + 1, coords.length())) + 1;

			this.setHeight(Math.max(y, this.getHeight()));
			this.setWidth(Math.max(x, this.getWidth()));
		}

		this.createEntities();

		for (int y = 0; y < this.getHeight(); y++) {
			for (int x = 0; x < this.getWidth(); x++) {
				String coordinates = x + "." + y;
				if (!data.has(coordinates)) {
					continue;
				}

				JsonObject entity = data.get(coordinates).getAsJsonObject();

				Entity currentEntity = this.getEntity(x, y);
				if (currentEntity == null) {
					try {
						currentEntity = (Entity) Class.forName(
								"de.alexanderciupka.pokemon.map.entities.types." + entity.get("type").getAsString())
								.newInstance();
					} catch (Exception e1) {
						currentEntity = new Entity();
					}
				}
				try {
					currentEntity.setParent(this);
					currentEntity.importSaveData(data.get(coordinates).getAsJsonObject());
					currentEntity.setX(x);
					currentEntity.setY(y);
					this.addEntity(x, y, currentEntity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadCharacters(JsonArray data) {
		for (JsonElement e : data) {
			JsonObject currentChar = e.getAsJsonObject();
			NPC npc = null;
			try {
				npc = (NPC) Class
						.forName("de.alexanderciupka.pokemon.characters.types." + currentChar.get("type").getAsString())
						.newInstance();
			} catch (Exception e1) {
				System.out.println(currentChar);
				e1.printStackTrace();
				continue;
			}
			npc.setCurrentRoute(this);
			npc.importSaveData(currentChar);
			// npc.loadData(currentChar);
			npc.setSurfing(this.getEntity((int) npc.getExactX(), (int) npc.getExactY()) instanceof WaterEntity);
			this.addCharacter(npc);
		}
	}

	private void loadEvents(JsonArray data) {
		// TODO: Review all the keys if needed
		final String[] MUST_HAVE = new String[] { "character", "route", "target_x", "target_y", "direction", "update",
				"fight", "heal", "remove", "cam_x", "cam_y", "cam_animation", "cam_center", "wait", "delay", "unknown",
				"pause" };
		for (JsonElement e : data) {
			TriggeredEvent te = new TriggeredEvent("");
			if (!e.isJsonObject()
					|| RouteAnalyzer.getWrongMember(e.getAsJsonObject(), new String[] { "loc", "sequences" }) != null) {
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
				this.getEntity(j.getAsJsonObject().get("x").getAsInt(), j.getAsJsonObject().get("y").getAsInt())
						.setEvent(te);
			}
		}
	}

	private void loadEncounters(JsonObject data) {
		for (Entry<String, JsonElement> s : data.entrySet()) {
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

	// private void loadQuizzes(JsonObject data) {
	// if (data.get("quizzes").isJsonArray()) {
	// for (JsonElement e : data.get("quizzes").getAsJsonArray()) {
	// JsonObject currentQuiz = e.getAsJsonObject();
	// QuestionEntity q = QuestionEntity
	// .convert(this.getEntity(currentQuiz.get("x").getAsInt(),
	// currentQuiz.get("y").getAsInt()));
	// q.setQuestion(currentQuiz.get("question") == null ? "" :
	// currentQuiz.get("question").getAsString());
	// q.addOptions(currentQuiz.get("options").getAsString().split("\\+"));
	// q.addSolutions(currentQuiz.get("solutions").getAsString().split("\\+"));
	// q.setSource(currentQuiz.get("source") != null ?
	// currentQuiz.get("source").getAsString() : "nothing");
	// q.setNPC(this.getNPC(currentQuiz.get("npc").getAsString()));
	// q.setType(QuestionType.valueOf(currentQuiz.get("type").getAsString().toUpperCase()));
	// for (JsonElement j : currentQuiz.get("gates").getAsJsonArray()) {
	// JsonObject gates = j.getAsJsonObject();
	// q.addGates(GameController.getInstance().getRouteAnalyzer()
	// .getRouteById(gates.get("route").getAsString())
	// .getEntity(gates.get("x").getAsInt(), gates.get("y").getAsInt()));
	// }
	// this.addEntity(q.getX(), q.getY(), q);
	// }
	// }
	// }

	public boolean importSaveData(JsonObject saveData) {
		if (this.id.equals(saveData.get("id").getAsString())) {
			loadRouteData(saveData.get("route").getAsJsonObject());
			loadCharacters(saveData.get("characters").getAsJsonArray());
			loadEncounters(saveData.get("encounters").getAsJsonObject());
			loadEvents(saveData.get("events").getAsJsonArray());
			this.createMap();
			return true;
		}
		return false;
	}

	public JsonObject getSaveData() {
		JsonObject saveData = new JsonObject();
		saveData.addProperty("id", this.id);
		saveData.add("route", getRouteData());
		saveData.add("events", getEventsData());
		saveData.add("characters", getCharactersData());
		saveData.add("encounters", getEncountersData());
		return saveData;
	}

	private JsonObject getRouteData() {
		JsonObject routeData = new JsonObject();
		routeData.addProperty("name", this.name);
		JsonObject entities = null;
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if (entities == null) {
					entities = new JsonObject();
				}
				entities.add(x + "." + y, this.getEntity(x, y).getSaveData());
			}
		}

		routeData.add("entities", entities);

		JsonObject properties = getPropertiesData();
		routeData.add("properties", properties);
		return routeData;
	}

	private JsonArray getEventsData() {
		JsonArray eventsData = new JsonArray();
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				Entity e = getEntity(x, y);
				if (e != null && e.getEvent() != null) {
					eventsData.add(e.getEvent().getSaveData());
				}
			}
		}
		return eventsData;
	}

	private JsonArray getCharactersData() {
		JsonArray saveData = new JsonArray();
		for (NPC npc : this.characters) {
			saveData.add(npc.getSaveData());
		}
		return saveData;
	}

	private JsonObject getPropertiesData() {
		JsonObject properties = new JsonObject();
		properties.addProperty("rain", this.rain != null ? this.rain.name() : RainType.CLEAR.toString());
		properties.addProperty("snow", this.snow != null ? this.snow.name() : SnowType.CLEAR.toString());
		properties.addProperty("fog", this.fog != null ? this.fog.name() : FogType.CLEAR.toString());
		return properties;
	}

	private JsonObject getEncountersData() {
		JsonObject encounters = new JsonObject();
		for (Integer pool : this.pools.keySet()) {
			encounters.add(String.valueOf(pool), this.pools.get(pool).getSaveData());
		}
		return encounters;
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

	public void reset() {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				Entity entity = this.getEntity(x, y);
				if (entity != null) {
					entity.reset();
				}
			}
		}
		for (NPC c : this.getCharacters()) {
			c.resetPosition();
		}
	}

}
