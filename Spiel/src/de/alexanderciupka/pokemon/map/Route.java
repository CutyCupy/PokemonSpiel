package de.alexanderciupka.pokemon.map;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.exceptions.InvalidEntityDataException;
import de.alexanderciupka.pokemon.exceptions.InvalidRouteDataException;
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
import de.alexanderciupka.pokemon.map.entities.TriggeredEvent;
import de.alexanderciupka.pokemon.pokemon.Item;
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

	private String terrainName;
	private RouteType type;

	private boolean wait;

	public Route() {
		this.pools = new HashMap<>();
		this.buildings = new HashMap<>();
		this.characters = new ArrayList<NPC>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTerrain(String terrainName) {
		this.terrainName = terrainName;
	}

	public String getTerrainName() {
		return this.terrainName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Entity[][] getEntities() {
		return entities;
	}

	public boolean isDark() {
		return dark;
	}

	public void setDark(boolean dark) {
		this.dark = dark;
	}

	public void addEntity(int x, int y, Entity entity) {
		createEntities();
		entities[y][x] = entity;
	}

	public void addEntities(int row, ArrayList<Entity> rowEntities) {
		createEntities();
		for (int x = 0; x < width; x++) {
			entities[row][x] = rowEntities.get(x);
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
		if (entities == null || invalidSize()) {
			this.entities = new Entity[height][width];
		}
	}

	private boolean invalidSize() {
		if (this.entities.length == height) {
			for (int i = 0; i < height; i++) {
				if (this.entities[i].length != width) {
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
		tempMap = new BufferedImage(width * 70, height * 70, BufferedImage.TYPE_4BYTE_ABGR);
		map = new BufferedImage(width * 70, height * 70, BufferedImage.TYPE_4BYTE_ABGR);
		Point[] points = new Point[this.width * this.height];
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				points[x + y * this.width] = new Point(x, y);
			}
		}
		updateMap(points);
		// saveMap();
		map = tempMap;
	}

	public void updateMap(Point... updatePoint) {
		while (wait) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		wait = true;
		if (tempMap != null) {
			Player mc = GameController.getInstance().getMainCharacter();
			Graphics g = tempMap.getGraphics();

			for (Point p : updatePoint) {
				if (!(p.x >= 0 && p.x < width && p.y >= 0 && p.y < height)) {
					continue;
				}
				try {
					g.drawImage(entities[p.y][p.x].getTerrain(), p.x * 70, p.y * 70, null);
					g.drawImage(entities[p.y][p.x].getSprite(), (int) (entities[p.y][p.x].getExactX() * 70),
							(int) (entities[p.y][p.x].getExactY() * 70), null);
					if (mc != null && this.equals(mc.getCurrentRoute(), false) && p.equals(mc.getCurrentPosition())) {
						g.drawImage(mc.getCharacterImage(), (int) (mc.getExactX() * 70), (int) (mc.getExactY() * 70),
								null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (NPC npc : this.characters) {
				g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() * 70), (int) (npc.getExactY() * 70), null);
			}
			for (String building : this.buildings.keySet()) {
				BufferedImage currentBuilding = GameController.getInstance().getRouteAnalyzer()
						.getSpriteByName(building);
				for (Point p : this.buildings.get(building)) {
					g.drawImage(currentBuilding, p.x * 70, p.y * 70, null);
				}
			}

			ArrayList<Character> character = new ArrayList<>(this.characters);
			if (mc != null && this.equals(mc.getCurrentRoute(), false)) {
				character.add(mc);
			}

			for (int i = 0; i < character.size(); i++) {
				Character c = character.get(i);
				boolean repaint = true;
				for (String building : this.buildings.keySet()) {
					if (!repaint) {
						break;
					}
					BufferedImage currentBuilding = GameController.getInstance().getRouteAnalyzer()
							.getSpriteByName(building);
					int buildingWidth = currentBuilding.getWidth() - (currentBuilding.getWidth() % GameFrame.GRID_SIZE);
					int buildingHeight = currentBuilding.getHeight()
							- (currentBuilding.getHeight() % GameFrame.GRID_SIZE);

					for (Point p : this.buildings.get(building)) {
						if (!repaint) {
							break;
						}

						if (((c.getExactX() * 70 + c.getCharacterImage().getWidth(null) > p.x * 70
								&& c.getExactX() * 70 < p.x * 70 + buildingWidth)
								&& (c.getExactY() * 70 + c.getCharacterImage().getHeight(null) > p.y * 70
										&& c.getExactY() * 70 < p.y * 70 + buildingHeight))) {
							repaint = false;
						}
					}
				}
				if (repaint) {
					g.drawImage(c.getCharacterImage(), (int) (c.getExactX() * 70), (int) (c.getExactY() * 70), null);
				}
			}
			if (mc != null && this.equals(mc.getCurrentRoute(), false)
					&& (this.entities[mc.getCurrentPosition().y][mc.getCurrentPosition().x].getWarp() != null
							|| (mc.getOldPosition().x < this.width && mc.getOldPosition().y < this.height
									&& this.entities[mc.getOldPosition().y][mc.getOldPosition().x]
											.getWarp() != null))) {
				g.drawImage(mc.getCharacterImage(), (int) (mc.getExactX() * 70), (int) (mc.getExactY() * 70), null);
			}
			map = tempMap;
		}
		wait = false;
	}

	public Entity getEntity(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return entities[y][x];
		}
		return null;
	}

	void saveMap() {
		try {
			while (wait) {
				Thread.sleep(1);
			}
			ImageIO.write(map, "png", new File("./res/routes/" + this.id + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getMap() {
		while (wait) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.map;
	}

	public NPC getNPC(String id) {
		for (int i = 0; i < characters.size(); i++) {
			if (characters.get(i).getID().equals(id)) {
				return characters.get(i);
			}
		}
		return null;
	}

	public NPC getNPCByName(String name) {
		for (int i = 0; i < characters.size(); i++) {
			if (characters.get(i).getName() != null
					&& name.toLowerCase().equals(characters.get(i).getName().toLowerCase())) {
				return characters.get(i);
			}
		}
		return null;
	}

	public RainType getRain() {
		return rain;
	}

	public void setRain(RainType rain) {
		this.rain = rain;
	}

	public SnowType getSnow() {
		return snow;
	}

	public void setSnow(SnowType snow) {
		this.snow = snow;
	}

	public RouteType getType() {
		return type;
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
					if (!this.entities[y][x].equals(other.entities[y][x])) {
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
			return this.id.equals(other.id) && this.name.equals(other.name)
					&& this.terrainName.equals(other.terrainName) && this.rain == other.rain && this.snow == other.snow
					&& this.fog == other.fog;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return equals(obj, true);
	}

	public void loadRouteData(JsonObject data) throws InvalidRouteDataException {
		final String[] MUST_HAVES = new String[] { "name", "height", "width", "dark", "type", "rain", "snow",
				"entities" };

		if (RouteAnalyzer.getWrongMember(data, MUST_HAVES) != null) {
			throw new InvalidRouteDataException(this, RouteAnalyzer.getWrongMember(data, MUST_HAVES));
		}

		setName(data.get("name").getAsString());
		setDark(data.get("dark").getAsBoolean());
		setType(RouteType.valueOf(data.get("type").getAsString().toUpperCase()));
		setRain(RainType.valueOf(data.get("rain").getAsString().toUpperCase()));
		setSnow(SnowType.valueOf(data.get("snow").getAsString().toUpperCase()));

		JsonObject entities = data.get("entities").getAsJsonObject();

		this.setHeight(0);
		this.setWidth(0);
		for (Entry<String, JsonElement> entry : entities.entrySet()) {
			setWidth(Math.max(Integer.valueOf(entry.getKey().substring(0, entry.getKey().indexOf("."))) + 1,
					getWidth()));
			setHeight(Math.max(Integer.valueOf(entry.getKey().substring(entry.getKey().indexOf(".") + 1)) + 1,
					getHeight()));
		}
		createEntities();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
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
		if(data.get("warps").isJsonArray()) {
			JsonArray warps = data.get("warps").getAsJsonArray();
			final String[] MUST_HAVES = new String[] {"id", "new_route", "new_x", "new_y", "x", "y", "direction"};
			for (JsonElement e : warps) {
				JsonObject warp = e.getAsJsonObject();
				if(RouteAnalyzer.getWrongMember(warp, MUST_HAVES) != null) {
					continue;
				}
				
				Warp w = new Warp(warp.get("id").getAsString(), this.id);
				w.setNewRoute(warp.get("new_route").getAsString());
				w.setNewPosition(new Point(warp.get("new_x").getAsInt(), warp.get("new_y").getAsInt()));
				w.setNewDirection(warp.get("direction").getAsString());
				
				getEntity(warp.get("x").getAsInt(), warp.get("y").getAsInt()).addWarp(w);
			}
		}
	}
	
	private void loadCharacters(JsonObject data) {
		if(data.get("characters").isJsonArray()) {
			JsonArray characterDetails = data.get("characters").getAsJsonArray();
			final String[] MUST_HAVES = new String[] {"id", "x", "y", "char_sprite", "name", "direction", 
					"is_trainer"};
			for(JsonElement e : characterDetails) {
				JsonObject characterData = e.getAsJsonObject();
				
			}
		}
		for (int y = 0; y < characterDetails.size(); y++) {
			JsonObject currentChar = characterDetails.get(y).getAsJsonObject();
			NPC currentCharacter = new NPC(currentChar.get("id").getAsString());
			currentCharacter.setCurrentPosition(currentChar.get("x").getAsInt(), currentChar.get("y").getAsInt());
			currentCharacter.setCurrentRoute(currentRoute);
			currentCharacter.setCharacterImage(currentChar.get("char_sprite").getAsString(),
					currentChar.get("direction").getAsString().toLowerCase());
			currentCharacter.setName(currentChar.get("name").getAsString());
			currentCharacter.setTrainer(currentChar.get("is_trainer").getAsString());
			currentCharacter.setLogo(currentChar.get("logo") != null ? currentChar.get("logo").getAsString() : null);
			if (currentChar.get("surfing") != null) {
				currentCharacter.setSurfing(currentChar.get("surfing").getAsBoolean());
				if (currentCharacter.isSurfing()) {
					currentRoute.getEntities()[currentCharacter.getCurrentPosition().y][currentCharacter
							.getCurrentPosition().x].setTerrain("see");
				}
			}
			currentCharacter
					.setAggro(currentChar.get("aggro") != null ? currentChar.get("aggro").getAsBoolean() : true);
			if (currentCharacter.isTrainer()) {
				currentCharacter.importTeam();
			}
			currentCharacter.importDialogue();
			currentRoute.addCharacter(currentCharacter);
		}
	}
	
	private void loadPokemons(JsonObject data) {
		if (route.get("pokemons") != null) {
			JsonArray pokemonDetails = route.get("pokemons").getAsJsonArray();
			for (int i = 0; i < Math.min(pokemons.size(), pokemonDetails.size()); i++) {
				JsonObject currentPokemon = pokemonDetails.get(i).getAsJsonObject();
				int pokemonIndex = i;
				String pokemonID = currentPokemon.get("entity_id").getAsString();
				if (!pokemonID.equals(pokemons.get(pokemonIndex).getId())) {
					for (int j = 0; j < pokemons.size(); j++) {
						pokemonIndex = j;
						if (pokemonID.equals(pokemons.get(pokemonIndex).getId())) {
							break;
						}
					}
				}
				PokemonEntity entity = pokemons.get(pokemonIndex);

				Pokemon p = new Pokemon(gController.getInformation().getID(currentPokemon.get("name").getAsString()));
				p.getStats().generateStats(currentPokemon.get("level").getAsShort());
				entity.setPokemon(p);
				entity.setInteractionMessage(currentPokemon.get("interaction_message").getAsString());
				entity.setNoInteractionMessage(currentPokemon.get("no_interaction_message").getAsString());
				entity.importRequiredItems(currentPokemon.get("required_items"));
				currentRoute.addEntity(entity.getX(), entity.getY(), entity);
			}
		}
	}
	
	private void loadItems(JsonObject data) {
		if (route.get("items") != null) {
			JsonArray itemDetails = route.get("items").getAsJsonArray();
			for (int i = 0; i < Math.min(items.size(), itemDetails.size()); i++) {
				JsonObject currentItem = itemDetails.get(i).getAsJsonObject();
				int itemIndex = i;
				String itemID = currentItem.get("entity_id").getAsString();
				if (!itemID.equals(items.get(itemIndex).getId())) {
					for (int j = 0; j < items.size(); j++) {
						itemIndex = j;
						if (itemID.equals(items.get(itemIndex).getId())) {
							break;
						}
					}
				}
				ItemEntity entity = items.get(itemIndex);

				entity.setItem(Item.valueOf(currentItem.get("name").getAsString().toUpperCase()));
				if (currentItem.get("sprite") != null) {
					entity.setSprite(currentItem.get("sprite").getAsString().toLowerCase());
				}
				if (currentItem.get("terrain") != null) {
					entity.setTerrain(currentItem.get("terrain").getAsString().toLowerCase());
				}
				if (currentItem.get("hidden") != null) {
					entity.setHidden(currentItem.get("hidden").getAsBoolean());
				}
				currentRoute.addEntity(entity.getX(), entity.getY(), entity);
			}
		}
	}
	
	private void loadEvents(JsonObject data) {
		if (route.get("events") != null) {
			JsonObject eventDetails = route.get("events").getAsJsonObject();
			for (String event : events.keySet()) {
				JsonArray currentEvent = eventDetails.get(event.toLowerCase()).getAsJsonArray();
				TriggeredEvent te = new TriggeredEvent(event);
				for (JsonElement step : currentEvent) {
					JsonArray currentStep = step.getAsJsonArray();
					ArrayList<Change> changes = new ArrayList<Change>();
					for (JsonElement current : currentStep) {
						JsonObject j = current.getAsJsonObject();
						Change currentChange = new Change();
						currentChange.setParticipant(j.get("character").getAsString());
						currentChange.setRouteID(j.get("route") != null ? j.get("route").getAsString() : "");
						currentChange.setMove(new Point(j.get("target_x") != null ? j.get("target_x").getAsInt() : -1,
								j.get("target_y") != null ? j.get("target_y").getAsInt() : -1));
						currentChange.setDialog(j.get("dialog") != null ? j.get("dialog").getAsString() : null);
						currentChange.setDirection(j.get("direction") != null
								? Direction.valueOf(j.get("direction").getAsString().toUpperCase()) : null);
						currentChange
								.setPositionUpdate(j.get("update") != null ? j.get("update").getAsBoolean() : false);
						currentChange.setFight(j.get("fight") != null ? j.get("fight").getAsBoolean() : false);
						currentChange.setAfterFightUpdate(
								j.get("after_fight") != null ? j.get("after_fight").getAsString() : null);
						currentChange.setBeforeFightUpdate(
								j.get("before_fight") != null ? j.get("before_fight").getAsString() : null);
						currentChange
								.setNoFightUpdate(j.get("no_fight") != null ? j.get("no_fight").getAsString() : null);
						currentChange.setSpriteUpdate(
								j.get("sprite_update") != null ? j.get("sprite_update").getAsString() : null);
						currentChange.setHeal(j.get("heal") != null ? j.get("heal").getAsBoolean() : false);
						currentChange.setRemove(j.get("remove") != null ? j.get("remove").getAsBoolean() : false);
						if (j.get("item") != null && j.get("item").isJsonArray()) {
							for (JsonElement x : j.get("item").getAsJsonArray()) {
								JsonObject currentItem = x.getAsJsonObject();
								currentChange.addItem(Item.valueOf(currentItem.get("name").getAsString().toUpperCase()),
										currentItem.get("amount") != null ? currentItem.get("amount").getAsInt() : 1);

							}
						}
						currentChange.setCamPosition(new Point(j.get("cam_x") != null ? j.get("cam_x").getAsInt() : -1,
								j.get("cam_y") != null ? j.get("cam_y").getAsInt() : -1));
						currentChange.setCamAnimation(
								j.get("cam_animation") != null ? j.get("cam_animation").getAsBoolean() : false);
						currentChange.setCenterCharacter(
								j.get("cam_center") != null ? j.get("cam_center").getAsBoolean() : false);
						currentChange.setSound(j.get("sound") != null ? j.get("sound").getAsString() : null);
						currentChange.setWait(j.get("wait") != null ? j.get("wait").getAsBoolean() : true);
						currentChange.setDelay(j.get("delay") != null ? j.get("delay").getAsLong() : 0);
						currentChange.setUnknown(j.get("unknown") != null ? j.get("unknown").getAsBoolean() : false);
						currentChange.setPause(j.get("pause") != null ? j.get("pause").getAsBoolean() : false);
						changes.add(currentChange);
					}
					te.addChanges(changes.toArray(new Change[changes.size()]));
				}
				for (Point p : events.get(event)) {
					currentRoute.getEntities()[p.y][p.x].setEvent(te);
				}
			}
		}
	}
	
	private void loadSigns(JsonObject data) {
		if (route.get("signs") != null) {
			JsonArray signsData = route.get("signs").getAsJsonArray();
			for (int i = 0; i < Math.min(signsData.size(), signs.size()); i++) {
				JsonObject current = signsData.get(i).getAsJsonObject();
				if (current.get("terrain") != null) {
					signs.get(i).setTerrain(current.get("terrain").getAsString());
				}
				if (current.get("information") != null) {
					signs.get(i).setInformation(current.get("information").getAsString());
				}
			}
		}
	}
	
	private void loadEncounters(JsonObject data) {
		if (route.get("encounters") != null) {
			JsonObject encounterDetails = route.get("encounters").getAsJsonObject();
			if (encounterDetails.get("DEFAULT") != null) {
				PokemonPool pool = new PokemonPool("DEFAULT");
				for (JsonElement j : encounterDetails.get("DEFAULT").getAsJsonArray()) {
					JsonObject currentEncounter = j.getAsJsonObject();
					int ammount = currentEncounter.get("ammount") != null ? currentEncounter.get("ammount").getAsInt()
							: 1;
					for (int i = 0; i < ammount; i++) {
						pool.addPokemon(currentEncounter.get("id").getAsInt(),
								currentEncounter.get("level").getAsShort());
					}
				}
			}
			for (String s : pokemonPools.keySet()) {
				PokemonPool pool = new PokemonPool(s);
				if (encounterDetails.get(s) != null) {
					for (JsonElement j : encounterDetails.get(s).getAsJsonArray()) {
						JsonObject currentEncounter = j.getAsJsonObject();
						int ammount = currentEncounter.get("ammount") != null
								? currentEncounter.get("ammount").getAsInt() : 1;
						for (int i = 0; i < ammount; i++) {
							pool.addPokemon(currentEncounter.get("id").getAsInt(),
									currentEncounter.get("level").getAsShort());
						}
					}
				} else {
					pool.addPokemon(25, (short) 1);
				}
				for (int i = 0; i < pokemonPools.get(s).size(); i++) {
					pokemonPools.get(s).get(i).setPokemonPool(pool);
				}
			}
		}
	}
	
	private void loadQuizzes(JsonObject data) {
		if (route.get("quizzes") != null) {
			JsonObject quizData = route.get("quizzes").getAsJsonObject();
			for (QuestionEntity q : quizzes) {
				if (quizData.get(q.getID().toUpperCase()) != null) {
					JsonObject currentQuiz = quizData.get(q.getID().toUpperCase()).getAsJsonObject();
					q.setQuestion(currentQuiz.get("question") == null ? "" : currentQuiz.get("question").getAsString());
					q.addOptions(currentQuiz.get("options").getAsString().split("\\+"));
					q.addSolutions(currentQuiz.get("solutions").getAsString().split("\\+"));
					q.setSource(
							currentQuiz.get("source") != null ? currentQuiz.get("source").getAsString() : "nothing");
					q.setNPC(currentRoute.getNPC(currentQuiz.get("npc").getAsString()));
					q.setType(QuestionType.valueOf(currentQuiz.get("type").getAsString().toUpperCase()));
					for (JsonElement e : currentQuiz.get("entities").getAsJsonArray()) {
						JsonObject entity = e.getAsJsonObject();
						q.addGates(currentRoute.getEntity(entity.get("x").getAsInt(), entity.get("y").getAsInt()));
					}
				}
			}
		}
	}

	public void loadExtras(JsonObject data) throws InvalidRouteDataException {
		if(data.has("warps")) {
			loadWarps(data);
		}
		if(data.has("characters")) {
			loadCharacters(data);
		}
		if(data.has("pokemons")) {
			loadPokemons(data);
		}
		if(data.has("items")) {
			loadItems(data);
		}
		if(data.has("events")) {
			loadEvents(data);
		}
		if(data.has("signs")) {
			loadSigns(data);
		}
		if(data.has("encounters")) {
			loadEncounters(data);
		}
		if(data.has("quizzes")) {
			loadQuizzes(data);
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

			createEntities();
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
			saveData.addProperty("rain", rain.name());
		}
		if (this.snow == null ? oldRoute.snow != null : !this.snow.equals(oldRoute.snow)) {
			saveData.addProperty("snow", this.snow != null ? snow.name() : null);
		}
		if (this.fog == null ? oldRoute.fog != null : !this.fog.equals(oldRoute.fog)) {
			saveData.addProperty("fog", this.fog != null ? fog.name() : null);
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
		boolean result = !this.pools.containsKey(id);
		this.pools.put(id, pool);
		return result;
	}

	public PokemonPool getPoolById(int id) {
		return this.pools.get(id);
	}

}
