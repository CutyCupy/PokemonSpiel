package de.alexanderciupka.pokemon.map;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.map.entities.Entity;
import de.alexanderciupka.pokemon.map.entities.ItemEntity;
import de.alexanderciupka.pokemon.map.entities.PokemonEntity;
import de.alexanderciupka.pokemon.map.entities.SignEntity;
import de.alexanderciupka.pokemon.map.entities.TriggeredEvent;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class RouteAnalyzer {

	private static final File ROUTE_FOLDER = new File(Main.class.getResource("/routes/").getFile());
	private static final File LOGO_FOLDER = new File(Main.class.getResource("/logos/").getFile());
	private static final File ITEM_FOLDER = new File(Main.class.getResource("/items/").getFile());
	private static final File SPRITE_FOLDER = new File(Main.class.getResource("/routes/Entities/").getFile());
	private static final File TERRAIN_FOLDER = new File(Main.class.getResource("/routes/terrain/").getFile());
	private static final File POKEBALL_FOLDER = new File(Main.class.getResource("/pokeballs/").getFile());
	
	
	private File[] folderFiles;
	private BufferedReader currentReader;
	private Map<String, Route> loadedRoutes;
	private Map<String, Route> originalRoutes;
	private GameController gController;
	private HashMap<String, Image> logos;
	private HashMap<Item, BufferedImage> items; 
	private HashMap<String, Image> sprites;
	private HashMap<String, Image> terrains;
	private HashMap<Item, BufferedImage> pokeballs;

	private JsonParser parser;

	public RouteAnalyzer() {
		gController = GameController.getInstance();
		folderFiles = ROUTE_FOLDER.listFiles();
		loadedRoutes = new HashMap<String, Route>();
		originalRoutes = new HashMap<String, Route>();
		logos = new HashMap<String, Image>();
		items = new HashMap<Item, BufferedImage>();
		sprites = new HashMap<String, Image>();
		terrains = new HashMap<String, Image>();
		pokeballs = new HashMap<Item, BufferedImage>();
		parser = new JsonParser();
	}
	
	public void init() {
		readAllSprites();
		readAllTerrains();
		readAllPokeballs();
		readAllLogos();
		readAllItems();
		readAllRoutes();
	}
	
	private void readAllSprites() {
		File[] sprites = SPRITE_FOLDER.listFiles();
		for(File currentFile : sprites) {
			if(currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.sprites.put(currentFile.getName().split("\\.")[0].toLowerCase(), ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Image getSpriteByName(String name) {
		return this.sprites.get(name);
	}
	
	private void readAllTerrains() {
		File[] terrains = TERRAIN_FOLDER.listFiles();
		for(File currentFile : terrains) {
			if(currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.terrains.put(currentFile.getName().split("\\.")[0].toLowerCase(), ImageIO.read(currentFile).getScaledInstance(70, 70, Image.SCALE_SMOOTH));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Image getTerrainByName(String name) {
		return this.terrains.get(name);
	}
	
	private void readAllPokeballs() {
		File[] pokeballs = POKEBALL_FOLDER.listFiles();
		for(File currentFile : pokeballs) {
			if(currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.pokeballs.put(Item.valueOf(currentFile.getName().split("\\.")[0].toUpperCase()), ImageIO.read(currentFile));
				} catch (Exception e) {
					System.err.println(currentFile);
					continue;
				}
			}
		}
	}
	
	public BufferedImage getPokeballImage(Item i) {
		return this.pokeballs.get(i);
	}
	
	private void readAllItems() {
		File[] items = ITEM_FOLDER.listFiles();
		for(File currentFile : items) {
			if(currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.items.put(Item.valueOf(currentFile.getName().split("\\.")[0].toUpperCase()), ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public BufferedImage getItemImage(Item i) {
		return this.items.get(i);
	}
	
	private void readAllLogos() {
		File[] logos = LOGO_FOLDER.listFiles();
		for(File currentFile : logos) {
			if(currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.logos.put(currentFile.getName().split("\\.")[0], ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void readAllRoutes() {
		for (File currentFile : folderFiles) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".route")) {
				readRoute(currentFile);
			}
		}
	}

	public void readRoute(String name) {
		readRoute(new File(this.getClass().getResource("/routes/" + name + ".route").getFile()));
	}

	public void readRoute(File file) {
		for(int counter = 0; counter < 2; counter++) {
			try {
				currentReader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Route currentRoute = new Route();
			String routeID = file.getName().split("\\.")[0];
			try {
				JsonObject route = parser.parse(currentReader).getAsJsonObject();
				JsonObject routeDetails = route.get("route").getAsJsonObject();
				currentRoute.setId(routeDetails.get("id").getAsString());
				currentRoute.setName(routeDetails.get("name").getAsString());
				currentRoute.setTerrain(routeDetails.get("terrain").getAsString());
				currentRoute.setHeight(routeDetails.get("height").getAsInt());
				currentRoute.setWidth(routeDetails.get("width").getAsInt());
				currentRoute.setDark(routeDetails.get("dark") != null ? routeDetails.get("dark").getAsBoolean() : false);
				currentRoute.setType(RouteType.valueOf(routeDetails.get("type") != null ? 
						routeDetails.get("type").getAsString().toUpperCase() : "CITY"));
				try {
					currentRoute.setRain(RainType.valueOf(routeDetails.get("rain").getAsString().toUpperCase()));
				} catch(Exception e) {
					currentRoute.setRain(null);
				}
				ArrayList<Warp> warps = new ArrayList<Warp>();
				ArrayList<NPC> characters = new ArrayList<NPC>();
				ArrayList<NPC> stones = new ArrayList<NPC>();
				HashMap<String, ArrayList<Point>> events = new HashMap<String, ArrayList<Point>>();
				ArrayList<PokemonEntity> pokemons = new ArrayList<PokemonEntity>();
				ArrayList<ItemEntity> items = new ArrayList<ItemEntity>();
				ArrayList<SignEntity> signs = new ArrayList<SignEntity>();
				float nonGrassEncounterRate = 0;
				switch(currentRoute.getTerrainName().toLowerCase()) {
				case "cave":
					nonGrassEncounterRate = Entity.POKEMON_GRASS_RATE;
					break;
				default:		
					break;
				}
				
				for (int y = 0; y < currentRoute.getHeight(); y++) {
					for (int x = 0; x < currentRoute.getWidth(); x++) {
						Entity currentEntity = null;
						if (routeDetails.get(x + "." + y) == null) {
							break;
						}
						String currentString = routeDetails.get(x + "." + y).getAsString().toUpperCase();
						if (!currentString.startsWith("W") && !currentString.startsWith("C")
								&& !currentString.startsWith("PKM") && !currentString.startsWith("TRIGGERED") 
								&& !currentString.startsWith("ITEM")) {
							switch (currentString) {
							case "OOB":
								currentEntity = new Entity(currentRoute, false, "free", 0, "free");
								break;
							case "T": // Tree
								currentEntity = new Entity(currentRoute, false, "tree", 0, "grassy");
								break;
							case "SI":
								currentEntity = new SignEntity(currentRoute);
								signs.add((SignEntity) currentEntity);
								break;
							case "TA": // Table
								currentEntity = new Entity(currentRoute, false, "table", 0, currentRoute.getTerrainName());
								break;
							case "KAFFEE":
								currentEntity = new Entity(currentRoute, false, "coffee_table", 0,
										currentRoute.getTerrainName());
								break;
							case "BED":
								currentEntity = new Entity(currentRoute, false, "bed", 0, currentRoute.getTerrainName());
								break;
							case "TV":
								currentEntity = new Entity(currentRoute, false, "tv", 0, currentRoute.getTerrainName());
								break;
							case "LAPTOP":
								currentEntity = new Entity(currentRoute, false, "laptop", 0, currentRoute.getTerrainName());
								break;
							case "SPUELE":
								currentEntity = new Entity(currentRoute, false, "spuele", 0, currentRoute.getTerrainName());
								break;
							case "BS":
								currentEntity = new Entity(currentRoute, false, "bookshelf", 0,
										currentRoute.getTerrainName());
								break;
							case "STUHLR":
								currentEntity = new Entity(currentRoute, true, "chair_r", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "STUHLL":
								currentEntity = new Entity(currentRoute, true, "chair_l", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "STUHLD":
								currentEntity = new Entity(currentRoute, true, "chair_d", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "STUHLU":
								currentEntity = new Entity(currentRoute, true, "chair_u", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "SETTLER":
								currentEntity = new Entity(currentRoute, true, "settle_r", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "SETTLEL":
								currentEntity = new Entity(currentRoute, true, "settle_l", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "SETTLED":
								currentEntity = new Entity(currentRoute, true, "settle_d", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "SETTLEU":
								currentEntity = new Entity(currentRoute, true, "settle_u", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "F": // Free
							case "": // Alternative way to create a "Free" Entity
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "G": // Grass
								currentEntity = new Entity(currentRoute, true, "grass", Entity.POKEMON_GRASS_RATE,
										"grassy");
								break;
							case "GR":
								currentEntity = new Entity(currentRoute, true, "free", 0, "grassy");
								break;
							case "SN":
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate, "snow");
								break;
							case "M": // Mauer - House/Center/Market
								currentEntity = new Entity(currentRoute, false, "free", 0, currentRoute.getTerrainName());
								break;
							case "MW":
								currentEntity = new Entity(currentRoute, false, "wand", 0, currentRoute.getTerrainName());
								break;
							case "MWW":
								currentEntity = new Entity(currentRoute, false, "wand_window", 0,
										currentRoute.getTerrainName());
								break;
							case "MWWC":
								currentEntity = new Entity(currentRoute, false, "wand_window_curtain", 0,
										currentRoute.getTerrainName());
								break;
							case "P": // Center
								currentEntity = new Entity(currentRoute, false, "house_center", 0,
										currentRoute.getTerrainName());
								break;
							case "HS":
								currentEntity = new Entity(currentRoute, false, "house_small", 0,
										currentRoute.getTerrainName());
								break;
							case "HL":
								currentEntity = new Entity(currentRoute, false, "house_large", 0,
										currentRoute.getTerrainName());
								break;
							case "A":
								currentEntity = new Entity(currentRoute, false, "house_gym", 0,
										currentRoute.getTerrainName());
								break;
							case "S": // See
								currentEntity = new Entity(currentRoute, true, "free", Entity.POKEMON_GRASS_RATE, "see");
								break;
							case "SA": // Sand
								currentEntity = new Entity(currentRoute, true, "free", 0, "sandy");
								break;
							case "B": // Bridge
								currentEntity = new Entity(currentRoute, true, "bridge", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "PC":
								currentEntity = new Entity(currentRoute, false, "pc", 0, currentRoute.getTerrainName());
								break;
							case "JH":
								currentEntity = new Entity(currentRoute, false, "joyhealing0", 0,
										currentRoute.getTerrainName());
								break;
							case "PCD":
								currentEntity = new Entity(currentRoute, false, "pokecenter_desk", 0,
										currentRoute.getTerrainName());
								break;
							case "MD":
								currentEntity = new Entity(currentRoute, true, "movedown", 0,
										currentRoute.getTerrainName());
								break;
							case "MU":
								currentEntity = new Entity(currentRoute, true, "moveup", 0, currentRoute.getTerrainName());
								break;
							case "ML":
								currentEntity = new Entity(currentRoute, true, "moveleft", 0,
										currentRoute.getTerrainName());
								break;
							case "MR":
								currentEntity = new Entity(currentRoute, true, "moveright", 0,
										currentRoute.getTerrainName());
								break;
							case "MS":
								currentEntity = new Entity(currentRoute, true, "movestop", 0,
										currentRoute.getTerrainName());
								break;
							case "HWF":
								currentEntity = new Entity(currentRoute, false, "cave_front", 0, currentRoute.getTerrainName());
								break;
							case "HWL":
								currentEntity = new Entity(currentRoute, false, "cave_left", 0, currentRoute.getTerrainName());
								break;
							case "HWR":
								currentEntity = new Entity(currentRoute, false, "cave_right", 0, currentRoute.getTerrainName());
								break;
							case "HWB":
								currentEntity = new Entity(currentRoute, false, "cave_back", 0, currentRoute.getTerrainName());
								break;
							case "HWLF":
								currentEntity = new Entity(currentRoute, false, "cave_left_front_corner_outside", 0, currentRoute.getTerrainName());
								break;
							case "HWLB":
								currentEntity = new Entity(currentRoute, false, "cave_left_back_corner_outside", 0, currentRoute.getTerrainName());
								break;
							case "HWRB":
								currentEntity = new Entity(currentRoute, false, "cave_right_back_corner_outside", 0, currentRoute.getTerrainName());
								break;
							case "HWRF":
								currentEntity = new Entity(currentRoute, false, "cave_right_front_corner_outside", 0, currentRoute.getTerrainName());
								break;
							case "HWRBI":
								currentEntity = new Entity(currentRoute, false, "cave_right_back_corner_inside", 0, currentRoute.getTerrainName());
								break;
							case "HWRFI":
								currentEntity = new Entity(currentRoute, false, "cave_right_front_corner_inside", 0, currentRoute.getTerrainName());
								break;
							case "HWLBI":
								currentEntity = new Entity(currentRoute, false, "cave_left_back_corner_inside", 0, currentRoute.getTerrainName());
								break;
							case "HWLFI":
								currentEntity = new Entity(currentRoute, false, "cave_left_front_corner_inside", 0, currentRoute.getTerrainName());
								break;
							case "HM":
								currentEntity = new Entity(currentRoute, true, "cave_middle", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "RB":
								currentEntity = new Entity(currentRoute, false, "rockbig", 0,
										currentRoute.getTerrainName());
								break;
							case "RG":
								currentEntity = new Entity(currentRoute, false, "rockgroup", 0,
										currentRoute.getTerrainName());
								break;
							case "R":
								currentEntity = new Entity(currentRoute, false, "rock", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "TC":
								currentEntity = new Entity(currentRoute, false, "treecut", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "ST":
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate, currentRoute.getTerrainName());
								NPC currentStone = new NPC(currentString);
								currentStone.setCurrentPosition(x, y);
								currentStone.setCurrentRoute(currentRoute);
								currentStone.setID("strength");
								currentStone.setCharacterImage("strength", "back");
								stones.add(currentStone);
								break;
							default:
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							}
						} else if (currentString.startsWith("W")) {
							Warp currentWarp = new Warp(currentString, routeID);
							if (currentString.startsWith("WD")) { // door
								currentEntity = new Entity(currentRoute, false, false, false, true, "free", 0, "free");
							} else if (currentString.startsWith("WS")) {
								switch (currentString.substring(0, 4)) {
								case "WSUL":
									currentEntity = new Entity(currentRoute, false, true, false, false, "stair_up_left", 0,
											currentRoute.getTerrainName());
									break;
								case "WSUR":
									currentEntity = new Entity(currentRoute, true, false, false, false, "stair_up_right", 0,
											currentRoute.getTerrainName());
									break;
								case "WSDL":
									currentEntity = new Entity(currentRoute, false, true, false, false, "stair_down_left",
											0, currentRoute.getTerrainName());
									break;
								case "WSDR":
									currentEntity = new Entity(currentRoute, true, true, false, false, "stair_down_right",
											0, currentRoute.getTerrainName());
									break;
								}
							} else if(currentString.startsWith("WHE")) {
								currentEntity = new Entity(currentRoute, false, false, false, true, "cave_entrance_front", 0, 
										currentRoute.getTerrainName());
							} else if(currentString.startsWith("WL")) {
								if(currentString.startsWith("WLU")) {
									currentEntity = new Entity(currentRoute, false, false, false, true, "ladder_up", 0, 
											currentRoute.getTerrainName());
								} else {
									currentEntity = new Entity(currentRoute, false, false, true, true, "ladder_down", 0, 
											currentRoute.getTerrainName());
								}
							} else {
								currentEntity = new Entity(currentRoute, true, "warp", 0, currentRoute.getTerrainName());
							}
							currentEntity.addWarp(currentWarp);
							warps.add(currentWarp);
						} else if (currentString.startsWith("C")) {
							currentEntity = new Entity(currentRoute, true, "free", 0, currentRoute.getTerrainName());
							NPC currentCharacter = new NPC(currentString);
							currentCharacter.setCurrentPosition(x, y);
							currentCharacter.setCurrentRoute(currentRoute);
							characters.add(currentCharacter);
						} else if (currentString.startsWith("PKM")) {
							currentEntity = new PokemonEntity(currentRoute, currentRoute.getTerrainName(), currentString);
							pokemons.add((PokemonEntity) currentEntity);
						} else if (currentString.startsWith("TRIGGERED")) {
							currentEntity = new Entity(currentRoute, true, "warp", 0, currentRoute.getTerrainName());
							ArrayList<Point> temp = events.get(currentString);
							if (temp == null) {
								temp = new ArrayList<Point>();
							}
							temp.add(new Point(x, y));
							events.put(currentString, temp);
						} else if(currentString.startsWith("ITEM")) {
							currentEntity = new ItemEntity(currentRoute, currentRoute.getTerrainName(), currentString, false);
							items.add((ItemEntity) currentEntity);
						}
						currentEntity.setX(x);
						currentEntity.setY(y);
						currentRoute.addEntity(x, y, currentEntity);
					}
				}
				JsonArray warpDetails = route.get("warps").getAsJsonArray();
				for (int y = 0; y < Math.min(warpDetails.size(), warps.size()); y++) {
					int warpIndex = y;
					JsonObject currentWarp = warpDetails.get(y).getAsJsonObject();
					String warpID = currentWarp.get("id").getAsString();
					if (!warpID.equals(warps.get(warpIndex).getWarpString())) {
						for (int i = 0; i < warps.size(); i++) {
							warpIndex = i;
							if (warpID.equals(warps.get(warpIndex).getWarpString())) {
								break;
							}
						}
					}
					warps.get(warpIndex).setNewRoute(currentWarp.get("new_route").getAsString());
					warps.get(warpIndex).setNewPosition(
							new Point(currentWarp.get("new_x").getAsInt(), currentWarp.get("new_y").getAsInt()));

				}
				JsonArray characterDetails = route.get("characters").getAsJsonArray();
				for (int y = 0; y < Math.min(characterDetails.size(), characters.size()); y++) {
					JsonObject currentChar = characterDetails.get(y).getAsJsonObject();
					int characterIndex = y;
					String characterID = currentChar.get("id").getAsString();
					if (!characterID.equals(characters.get(characterIndex).getID())) {
						for (int i = 0; i < characters.size(); i++) {
							characterIndex = i;
							if (characterID.equals(characters.get(characterIndex).getID())) {
								break;
							}
						}
					}
					NPC currentCharacter = characters.get(characterIndex);
					currentCharacter.setID(characterID);
					currentCharacter.setCharacterImage(currentChar.get("char_sprite").getAsString(),
							currentChar.get("direction").getAsString().toLowerCase());
					currentCharacter.setName(currentChar.get("name").getAsString());
					currentCharacter.setTrainer(currentChar.get("is_trainer").getAsString());
					currentCharacter.setLogo(currentChar.get("logo") != null ? currentChar.get("logo").getAsString() : null);
					if (currentChar.get("sprite") != null) {
						currentRoute.getEntities()[currentCharacter.getCurrentPosition().y][currentCharacter
								.getCurrentPosition().x].setSprite(currentChar.get("sprite").getAsString());
					}
					if (currentChar.get("terrain") != null) {
						currentRoute.getEntities()[currentCharacter.getCurrentPosition().y][currentCharacter
								.getCurrentPosition().x].setTerrain(currentChar.get("terrain").getAsString());
					}
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

						Pokemon p = new Pokemon(
								gController.getInformation().getID(currentPokemon.get("name").getAsString()));
						p.getStats().generateStats(currentPokemon.get("level").getAsShort());
						entity.setPokemon(p);
						entity.setInteractionMessage(currentPokemon.get("interaction_message").getAsString());
						entity.setNoInteractionMessage(currentPokemon.get("no_interaction_message").getAsString());
						entity.importRequiredItems(currentPokemon.get("required_items"));
						currentRoute.addEntity(entity.getX(), entity.getY(), entity);
					}
				}
				
				if(route.get("items") != null) {
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
						if(currentItem.get("sprite") != null) {
							entity.setSprite(currentItem.get("sprite").getAsString().toLowerCase());
						}
						if(currentItem.get("terrain") != null) {
							entity.setTerrain(currentItem.get("terrain").getAsString().toLowerCase());
						}
						if(currentItem.get("hidden") != null) {
							entity.setHidden(currentItem.get("hidden").getAsBoolean());
						}
						currentRoute.addEntity(entity.getX(), entity.getY(), entity);
					}
				}
				
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
								currentChange.setRouteID(j.get("route") != null
										? j.get("route").getAsString() : currentRoute.getId());
								currentChange.setMove(new Point(
										j.get("target_x") != null
												? j.get("target_x").getAsInt() : -1,
										j.get("target_y") != null
												? j.get("target_y").getAsInt() : -1));
								currentChange.setDialog(j.get("dialog") != null
										? j.get("dialog").getAsString() : null);
								currentChange.setDirection(j.get("direction") != null
										? Direction.valueOf(
												j.get("direction").getAsString().toUpperCase())
										: null);
								currentChange.setPositionUpdate(j.get("update") != null
										? j.get("update").getAsBoolean() : false);
								currentChange.setFight(j.get("fight") != null
										? j.get("fight").getAsBoolean() : false);
								currentChange.setAfterFightUpdate(j.get("after_fight") != null
										? j.get("after_fight").getAsString() : null);
								currentChange.setBeforeFightUpdate(j.get("before_fight") != null
										? j.get("before_fight").getAsString() : null);
								currentChange.setNoFightUpdate(j.get("no_fight") != null
										? j.get("no_fight").getAsString() : null);
								currentChange.setSpriteUpdate(j.get("sprite_update") != null
										? j.get("sprite_update").getAsString() : null);
								currentChange.setHeal(j.get("heal") != null
										? j.get("heal").getAsBoolean() : false);
								if(j.get("item") != null) {
									for(JsonElement x : j.get("item").getAsJsonArray()) {
										JsonObject currentItem = x.getAsJsonObject();
										currentChange.addItem(Item.valueOf(currentItem.get("name").getAsString().toUpperCase()), 
												currentItem.get("amount") != null ? currentItem.get("amount").getAsInt() : 1);
										
									}
								}
								currentChange.setCamPosition(new Point(j.get("cam_x") != null
										? j.get("cam_x").getAsInt() : -1, j.get("cam_y") != null
										? j.get("cam_y").getAsInt() : -1));
								currentChange.setCamAnimation(j.get("cam_animation") != null
										? j.get("cam_animation").getAsBoolean() : false);
								currentChange.setCenterCharacter(j.get("cam_center") != null
										? j.get("cam_center").getAsBoolean() : false);
								changes.add(currentChange);
							}
							te.addChanges(changes.toArray(new Change[changes.size()]));
						}
						for (Point p : events.get(event)) {
							currentRoute.getEntities()[p.y][p.x].setEvent(te);
						}
					}
				}
				
				for(int i = 0; i < signs.size(); i++) {
					signs.get(i).setInformation("Wer stellt ein leeres Schild auf?");
				}
				
				if(route.get("signs") != null) {
					JsonArray signsData = route.get("signs").getAsJsonArray();
					for(int i = 0; i < Math.min(signsData.size(), signs.size()); i++) {
						JsonObject current = signsData.get(i).getAsJsonObject();
						if(current.get("terrain") != null) {
							signs.get(i).setTerrain(current.get("terrain").getAsString());
						}
						if(current.get("information") != null) {
							signs.get(i).setInformation(current.get("information").getAsString());
						}
					}
				}

				for (int i = 0; i < stones.size(); i++) {
					currentRoute.addCharacter(stones.get(i));
				}

				JsonArray encounterDetails = route.get("encounters").getAsJsonArray();
				for (JsonElement j : encounterDetails) {
					JsonObject currentEncounter = j.getAsJsonObject();
					SimpleEntry<Integer, Short> encounter = new SimpleEntry<Integer, Short>(currentEncounter.get("id").getAsInt(),
							currentEncounter.get("level").getAsShort());
					currentRoute.addPokemon(encounter);
				}
//				warps.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(counter == 0) {
				currentRoute.createMap();
				loadedRoutes.put(routeID, currentRoute);
			} else {
				originalRoutes.put(routeID, currentRoute);
			}
		}
	}

	public Route getRouteById(String id) {
		return loadedRoutes.get(id);
	}

	public boolean saveGame(String saveName) {
		try {
			String filePath = MenuController.SAVE_PATH + saveName.toLowerCase().replace(" ", "_");
			if (!filePath.endsWith(".ks")) {
				filePath += ".ks";
			}
			File saveFile = new File(filePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
			JsonObject data = new JsonObject();
			JsonArray charData = new JsonArray();
			charData.add(gController.getMainCharacter().getSaveData());
			for (Route currentRoute : loadedRoutes.values()) {
				for (NPC currentCharacter : currentRoute.getCharacters()) {
					charData.add(currentCharacter.getSaveData());
				}
			}
			
			JsonArray routeData = new JsonArray();
			
			for(String s : loadedRoutes.keySet()) {
				if(!loadedRoutes.get(s).equals(originalRoutes.get(s), false)) {
					routeData.add(loadedRoutes.get(s).getSaveData(originalRoutes.get(s)));
				}
			}
			
			data.add("characters", charData);
			data.add("routes", routeData);
			data.add("cam", gController.getCurrentBackground().getCamera().getSaveData());
			
			for (char c : data.toString().toCharArray()) {
				writer.write(c);
				writer.flush();
			}
			writer.close();
			JOptionPane.showMessageDialog(null, "Das Spiel wurde erfolgreich gespeichert!", "Speichern ...", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadGame(String path) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			JsonParser parser = new JsonParser();

			JsonObject data = parser.parse(reader).getAsJsonObject();
			
			JsonArray characters = data.get("characters").getAsJsonArray();
			JsonArray routes = data.get("routes").getAsJsonArray();

			gController.getMainCharacter().importSaveData(characters.get(0).getAsJsonObject());
			for(Route currentRoute : loadedRoutes.values()) {
				currentRoute.clearCharacters();
			}
			
			for(int i = 1; i < characters.size(); i++) {
				JsonObject currentJson = characters.get(i).getAsJsonObject();
				NPC character = new NPC();
				if(character.importSaveData(currentJson)) {
					loadedRoutes.get(character.getCurrentRoute().getId()).addCharacter(character);
				}
			}
			
			for(JsonElement current : routes) {
				JsonObject currentJson = current.getAsJsonObject();
				Route route = this.getRouteById(currentJson.get("id").getAsString());
				route.importSaveData(currentJson, originalRoutes.get(route.getId()));
			}
			
			for(String s : loadedRoutes.keySet()) {
				if(!loadedRoutes.get(s).equals(originalRoutes.get(s))) {
					loadedRoutes.get(s).createMap();
				}
			}
			
			gController.setCurrentRoute(gController.getMainCharacter().getCurrentRoute());

			
			gController.getCurrentBackground().getCamera().importSaveData(data.get("cam").getAsJsonObject());

			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, Route> getOriginalRoutes() {
		return originalRoutes;
	}

	public Image getLogoByName(String logo) {
		return this.logos.get(logo);
	}

}
