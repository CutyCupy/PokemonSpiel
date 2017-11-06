package de.alexanderciupka.pokemon.map;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.gui.overlay.FogType;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.gui.overlay.SnowType;
import de.alexanderciupka.pokemon.map.entities.Entity;
import de.alexanderciupka.pokemon.pokemon.PokemonPool;

public class Route {

	private String id;
	private String name;
	private int width;
	private int height;
	private boolean dark;
	private Entity[][] entities;
	private HashMap<String, ArrayList<Point>> buildings;
	private PokemonPool pokemonPool;
	private ArrayList<NPC> characters;
	private BufferedImage tempMap;
	private BufferedImage map;
	private RainType rain;
	private SnowType snow;
	private FogType fog;

	private String terrainName;
	private RouteType type;

	private boolean wait;

	public Route() {
		this.pokemonPool = new PokemonPool("DEFAULT");
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
		if (entities == null) {
			this.entities = new Entity[height][width];
		}
	}

	public void setDefaultPokemonPool(PokemonPool defaultPool) {
		this.pokemonPool = defaultPool;
	}

	public PokemonPool getPokemonPool() {
		return this.pokemonPool;
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
//		 saveMap();
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

	public boolean importSaveData(JsonObject saveData, Route route) {
		if (this.id.equals(saveData.get("id").getAsString())) {
			if (saveData.get("name") != null) {
				this.name = saveData.get("name").getAsString();
			} else {
				this.name = route.name;
			}
			this.pokemonPool = route.pokemonPool;

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
}
