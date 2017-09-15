package de.alexanderciupka.pokemon.map;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.NPC;
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
	private PokemonPool pokemonPool;
	private ArrayList<NPC> characters;
	private BufferedImage map;
	private RainType rain;
	private SnowType snow;

	private String terrainName;
	private RouteType type;

	public Route() {
		this.pokemonPool = new PokemonPool("DEFAULT");
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

	public void createMap() {
		map = new BufferedImage(width * 70, height * 70, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = map.getGraphics();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				try {
					g.drawImage(entities[y][x].getTerrain(), x * 70, y * 70, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				try {
					g.drawImage(entities[y][x].getSprite(), (int) (entities[y][x].getExactX() * 70),
							(int) (entities[y][x].getExactY() * 70), null);
					if (entities[y][x].hasCharacter()) {
						for (NPC npc : entities[y][x].getCharacters()) {
							g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() * 70),
									(int) (npc.getExactY() * 70), null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// saveMap();
	}

	public void updateMap(Point... updatePoint) {
		if (map != null) {
			Graphics g = map.getGraphics();
			boolean[][] repainted = new boolean[this.height][this.width];
			for (Point p : updatePoint) {
				try {
					g.drawImage(entities[p.y][p.x].getTerrain(), p.x * 70, p.y * 70, null);
					g.drawImage(entities[p.y][p.x].getSprite(), (int) (entities[p.y][p.x].getExactX() * 70),
							(int) (entities[p.y][p.x].getExactY() * 70), null);
					if (entities[p.y][p.x].hasCharacter()) {
						// for (NPC npc : entities[p.y][p.x].getCharacters()) {
						// g.drawImage(npc.getCharacterImage(), (int)
						// (npc.getExactX() * 70),
						// (int) (npc.getExactY() * 70), null);
						// }
					}
					repainted[p.y][p.x] = true;
					for (int x = Math.max(0, p.x - 4); x < Math.min(width, p.x + 4); x++) {
						for (int y = Math.max(0, p.y - 4); y < Math.min(height, p.y + 4); y++) {
							if (repainted[y][x]) {
								continue;
							}
							if (this.entities[y][x].getSpriteName().startsWith("house")
									|| this.entities[y][x].getSpriteName().startsWith("pokecenter")) {
								g.drawImage(entities[y][x].getTerrain(), x * 70, y * 70, null);
								g.drawImage(entities[y][x].getSprite(), (int) (entities[y][x].getExactX() * 70),
										(int) (entities[y][x].getExactY() * 70), null);
								if (entities[y][x].hasCharacter()) {
									// for (NPC npc :
									// entities[y][x].getCharacters()) {
									// g.drawImage(npc.getCharacterImage(),
									// (int) (npc.getExactX() * 70),
									// (int) (npc.getExactY() * 70), null);
									// }
								}
							}
							// for (NPC npc : entities[y][x].getCharacters()) {
							// g.drawImage(npc.getCharacterImage(), (int)
							// (npc.getExactX() * 70),
							// (int) (npc.getExactY() * 70), null);
							// }
							repainted[y][x] = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// for (int x = 0; x < width; x++) {
			// for (int y = 0; y < height; y++) {
			// if (this.entities[y][x].getSpriteName().startsWith("house")
			// || this.entities[y][x].getSpriteName().startsWith("pokecenter"))
			// {
			// g.drawImage(entities[y][x].getTerrain(), x * 70, y * 70, null);
			// g.drawImage(entities[y][x].getSprite(), (int)
			// (entities[y][x].getExactX() * 70),
			// (int) (entities[y][x].getExactY() * 70), null);
			// if (entities[y][x].hasCharacter()) {
			//// for (NPC npc : entities[y][x].getCharacters()) {
			//// g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() *
			// 70),
			//// (int) (npc.getExactY() * 70), null);
			//// }
			// }
			// }
			//// for (NPC npc : entities[y][x].getCharacters()) {
			//// g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() *
			// 70), (int) (npc.getExactY() * 70),
			//// null);
			////
			//// }
			// }
			// }
			// saveMap();
		}

	}

	void saveMap() {
		try {
			Graphics g = map.getGraphics();
			g.setFont(g.getFont().deriveFont(20.0f));
			// for (int y = 0; y < height; y++) {
			// for (int x = 0; x < width; x++) {
			// try {
			// if(entities[y][x].hasCharacter()) {
			// for (NPC npc : entities[y][x].getCharacters()) {
			// g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() *
			// 70),
			// (int) (npc.getExactY() * 70), null);
			// }
			// }
			// g.setColor(Color.black);
			// g.drawRect(GameFrame.GRID_SIZE * x, GameFrame.GRID_SIZE * y,
			// GameFrame.GRID_SIZE,
			// GameFrame.GRID_SIZE);
			// g.setColor(Color.red);
			// g.drawString(x + "|" + y, x * GameFrame.GRID_SIZE + 10, (int) ((y
			// + .5) * GameFrame.GRID_SIZE));
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// }
			ImageIO.write(map, "png", new File("./res/routes/" + this.id + ".png"));
			System.out.println(this.getName() + " wurde gespeichert!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getMap() {
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
				System.err.println("wrong size");
				return false;
			}
			for (int x = 0; x < this.width; x++) {
				for (int y = 0; y < this.height; y++) {
					if (!this.entities[y][x].equals(other.entities[y][x])) {
						System.err.println("wrong entities");
						return false;
					}
				}
			}
			if (this.characters.size() != other.characters.size()) {
				System.err.println("wrong character size");
				return false;
			}
			if (withCharacters) {
				for (int i = 0; i < this.characters.size(); i++) {
					boolean contains = false;
					for (int j = 0; j < other.characters.size(); j++) {
						if (!other.characters.get(j).equals(this.characters.get(i))) {
							contains = true;
							break;
						}
					}
					if (!contains) {
						System.err.println("wrong character " + this.characters.get(i).getName());
						return false;
					}
				}
			}
			System.out.println("finished");
			return (this.id.equals(other.id) && this.name.equals(other.name)
					&& this.terrainName.equals(other.terrainName) && ((this.rain == null && other.rain == null)
							|| (this.rain != null && this.rain.equals(other.rain))));
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
				} catch(Exception e) {
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
				} catch(Exception e) {
					this.setSnow(route.getSnow());
				}
			} else {
				this.setSnow(route.getSnow());
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
		System.out.println(saveData);
		return saveData;
	}
}
