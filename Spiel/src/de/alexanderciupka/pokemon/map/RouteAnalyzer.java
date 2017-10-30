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
import java.util.HashSet;
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
import de.alexanderciupka.pokemon.gui.overlay.SnowType;
import de.alexanderciupka.pokemon.map.entities.Change;
import de.alexanderciupka.pokemon.map.entities.Entity;
import de.alexanderciupka.pokemon.map.entities.GeneratorEntity;
import de.alexanderciupka.pokemon.map.entities.HatchEntity;
import de.alexanderciupka.pokemon.map.entities.ItemEntity;
import de.alexanderciupka.pokemon.map.entities.PokemonEntity;
import de.alexanderciupka.pokemon.map.entities.QuestionEntity;
import de.alexanderciupka.pokemon.map.entities.QuestionType;
import de.alexanderciupka.pokemon.map.entities.SignEntity;
import de.alexanderciupka.pokemon.map.entities.TriggeredEvent;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.PokemonPool;

public class RouteAnalyzer {

	public static final File ROUTE_FOLDER = new File(Main.class.getResource("/routes/").getFile());
	public static final File LOGO_FOLDER = new File(Main.class.getResource("/logos/").getFile());
	public static final File ITEM_FOLDER = new File(Main.class.getResource("/items/").getFile());
	public static final File SPRITE_FOLDER = new File(Main.class.getResource("/routes/Entities/").getFile());
	public static final File TERRAIN_FOLDER = new File(Main.class.getResource("/routes/terrain/").getFile());
	public static final File POKEBALL_FOLDER = new File(Main.class.getResource("/pokeballs/").getFile());
	public static final File ANIMATION_FOLDER = new File(Main.class.getResource("/animations/").getFile());

	private File[] folderFiles;
	private BufferedReader currentReader;
	private Map<String, Route> loadedRoutes;
	private Map<String, Route> originalRoutes;
	private GameController gController;
	private HashMap<String, BufferedImage> logos;
	private HashMap<Item, BufferedImage> items;
	private HashMap<String, BufferedImage> sprites;
	private HashMap<String, BufferedImage> terrains;
	private HashMap<Item, BufferedImage> pokeballs;
	private HashMap<String, BufferedImage> animations;

	ArrayList<HatchEntity> hatches;

	private JsonParser parser;

	public RouteAnalyzer() {
		gController = GameController.getInstance();
		folderFiles = ROUTE_FOLDER.listFiles();
		loadedRoutes = new HashMap<String, Route>();
		originalRoutes = new HashMap<String, Route>();
		logos = new HashMap<String, BufferedImage>();
		items = new HashMap<Item, BufferedImage>();
		sprites = new HashMap<String, BufferedImage>();
		terrains = new HashMap<String, BufferedImage>();
		pokeballs = new HashMap<Item, BufferedImage>();
		animations = new HashMap<String, BufferedImage>();
		parser = new JsonParser();
	}

	public void init() {
		readAllSprites();
		readAllTerrains();
		readAllPokeballs();
		readAllLogos();
		readAllItems();
		readAllRoutes();
		readAllAnimations();

		// for (String s : loadedRoutes.keySet()) {
		// System.out.println("Loaded: " + loadedRoutes.get(s).getName());
		// }

		HashMap<Integer, HashSet<String>> locations = getAllPokemonLocations();
		for (int i = 1; i < 650; i++) {
			if (!locations.get(i).isEmpty())
				System.out.println(gController.getInformation().getName(i) + " - " + locations.get(i));
		}

	}

	private void readAllSprites() {
		File[] sprites = SPRITE_FOLDER.listFiles();
		for (File currentFile : sprites) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.sprites.put(currentFile.getName().split("\\.")[0].toLowerCase(), ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BufferedImage getSpriteByName(String name) {
		return this.sprites.get(name);
	}

	private void readAllTerrains() {
		File[] terrains = TERRAIN_FOLDER.listFiles();
		for (File currentFile : terrains) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.terrains.put(currentFile.getName().split("\\.")[0].toLowerCase(), ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BufferedImage getTerrainByName(String name) {
		return this.terrains.get(name);
	}

	private void readAllPokeballs() {
		File[] pokeballs = POKEBALL_FOLDER.listFiles();
		for (File currentFile : pokeballs) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.pokeballs.put(Item.valueOf(currentFile.getName().split("\\.")[0].toUpperCase()),
							ImageIO.read(currentFile));
				} catch (Exception e) {
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
		for (File currentFile : items) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.items.put(Item.valueOf(currentFile.getName().split("\\.")[0].toUpperCase()),
							ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BufferedImage getItemImage(Item i) {
		return this.items.get(i);
	}

	private void readAllAnimations() {
		File[] animations = ANIMATION_FOLDER.listFiles();
		for (File currentFile : animations) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.animations.put(currentFile.getName().split("\\.")[0].toUpperCase(), ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BufferedImage getAnimationImage(String animation) {
		return this.animations.get(animation.toUpperCase());
	}

	private void readAllLogos() {
		File[] logos = LOGO_FOLDER.listFiles();
		for (File currentFile : logos) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.logos.put(currentFile.getName().split("\\.")[0], ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void readAllRoutes() {
		hatches = new ArrayList<>();
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
		for (int counter = 0; counter < 2; counter++) {
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
				currentRoute.setId(routeID);
				currentRoute.setName(routeDetails.get("name").getAsString());
				currentRoute.setTerrain(routeDetails.get("terrain").getAsString());
				currentRoute.setHeight(routeDetails.get("height").getAsInt());
				currentRoute.setWidth(routeDetails.get("width").getAsInt());
				currentRoute
						.setDark(routeDetails.get("dark") != null ? routeDetails.get("dark").getAsBoolean() : false);
				currentRoute.setType(RouteType.valueOf(routeDetails.get("type") != null
						? routeDetails.get("type").getAsString().toUpperCase() : "CITY"));
				try {
					currentRoute.setRain(RainType.valueOf(routeDetails.get("rain").getAsString().toUpperCase()));
				} catch (Exception e) {
					currentRoute.setRain(null);
				}
				try {
					currentRoute.setSnow(SnowType.valueOf(routeDetails.get("snow").getAsString().toUpperCase()));
				} catch (Exception e) {
					currentRoute.setSnow(null);
				}
				ArrayList<Warp> warps = new ArrayList<Warp>();
				// ArrayList<NPC> characters = new ArrayList<NPC>();
				ArrayList<NPC> stones = new ArrayList<NPC>();
				HashMap<String, ArrayList<Point>> events = new HashMap<String, ArrayList<Point>>();
				HashMap<String, ArrayList<Entity>> pokemonPools = new HashMap<>();
				ArrayList<PokemonEntity> pokemons = new ArrayList<PokemonEntity>();
				ArrayList<ItemEntity> items = new ArrayList<ItemEntity>();
				ArrayList<SignEntity> signs = new ArrayList<SignEntity>();
				ArrayList<QuestionEntity> quizzes = new ArrayList<>();
				float nonGrassEncounterRate = 0;
				switch (currentRoute.getTerrainName().toLowerCase()) {
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
						if (!currentString.startsWith("W") && !currentString.startsWith("PKM")
								&& !currentString.startsWith("TRIGGERED") && !currentString.startsWith("ITEM")
								&& !currentString.startsWith("GRASS") && !currentString.startsWith("SEE")
								&& !currentString.startsWith("CAVE") && !currentString.startsWith("QUIZ")) {
							switch (currentString) {
							case "OOB":
								currentEntity = new Entity(currentRoute, false, "free", 0, "free");
								break;
							case "T": // Tree
								currentEntity = new Entity(currentRoute, false, "tree", 0, "grassy");
								break;
							case "TS":
								currentEntity = new Entity(currentRoute, false, "snow_tree", 0, "snow");
								break;
							case "SI":
								currentEntity = new SignEntity(currentRoute);
								signs.add((SignEntity) currentEntity);
								break;
							case "TA": // Table
								currentEntity = new Entity(currentRoute, false, "table", 0,
										currentRoute.getTerrainName());
								break;
							case "KAFFEE":
								currentEntity = new Entity(currentRoute, false, "coffee_table", 0,
										currentRoute.getTerrainName());
								break;
							case "BED":
								currentEntity = new Entity(currentRoute, false, "bed", 0,
										currentRoute.getTerrainName());
								break;
							case "TV":
								currentEntity = new Entity(currentRoute, false, "tv", 0, currentRoute.getTerrainName());
								break;
							case "LAPTOP":
								currentEntity = new Entity(currentRoute, false, "laptop", 0,
										currentRoute.getTerrainName());
								break;
							case "SPUELE":
								currentEntity = new Entity(currentRoute, false, "spuele", 0,
										currentRoute.getTerrainName());
								break;
							case "FR":
								currentEntity = new Entity(currentRoute, false, "fridge", 0, currentRoute.getTerrainName());
								break;
							case "SV":
								currentEntity = new Entity(currentRoute, false, "server", 0, currentRoute.getTerrainName());
								break;
							case "V":
								currentEntity = new Entity(currentRoute, false, "vitrine", 0, currentRoute.getTerrainName());
								break;
							case "BS":
								currentEntity = new Entity(currentRoute, false, "bookshelf", 0,
										currentRoute.getTerrainName());
								break;
							case "TL":
								currentEntity = new Entity(currentRoute, false, "toilette", 0, currentRoute.getTerrainName());
								break;
							case "BT":
								currentEntity = new Entity(currentRoute, false, "bathtub", 0, currentRoute.getTerrainName());
								break;
							case "STUHLR":
								currentEntity = new Entity(currentRoute, true, "chair_r", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "STUHLL":
								currentEntity = new Entity(currentRoute, true, "chair_l", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "STUHLD":
								currentEntity = new Entity(currentRoute, true, "chair_d", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "STUHLU":
								currentEntity = new Entity(currentRoute, true, "chair_u", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								currentRoute.addBuilding("chair_u", new Point(x, y));
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
							case "": // Alternative way to create a "Free"
										// Entity
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "GR":
								currentEntity = new Entity(currentRoute, true, "free", 0, "grassy");
								break;
							case "SN":
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate, "snow");
								break;
							case "ICE":
								currentEntity = new Entity(currentRoute, true, "free", 0, "ice");
								break;
							case "M": // Mauer - House/Center/Market
								currentEntity = new Entity(currentRoute, false, "free", 0,
										currentRoute.getTerrainName());
								break;
							case "MW":
								currentEntity = new Entity(currentRoute, false, "wand", 0,
										currentRoute.getTerrainName());
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
								currentEntity = new Entity(currentRoute, true, "house_center", 0,
										currentRoute.getTerrainName());
								break;
							case "HS":
								currentEntity = new Entity(currentRoute, true, "house_small", 0,
										currentRoute.getTerrainName());
								break;
							case "HL":
								currentEntity = new Entity(currentRoute, true, "house_large", 0,
										currentRoute.getTerrainName());
								break;
							case "A":
								currentEntity = new Entity(currentRoute, true, "house_gym", 0,
										currentRoute.getTerrainName());
								break;
							case "SA": // Sand
								currentEntity = new Entity(currentRoute, true, "free", 0, "sandy");
								break;
							case "STO":
								currentEntity = new Entity(currentRoute, true, "free", 0, "stone");
								break;
							case "B": // Bridge
								currentEntity = new Entity(currentRoute, true, "bridge", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "LKWR":
								currentEntity = new Entity(currentRoute, false, "lkw_right", nonGrassEncounterRate,
										"stone");
								currentRoute.addBuilding("lkw_right", new Point(x, y));
								break;
							case "LKWL":
								currentEntity = new Entity(currentRoute, false, "lkw_left", nonGrassEncounterRate,
										"stone");
								currentRoute.addBuilding("lkw_left", new Point(x, y));
								break;
							case "PC":
								currentEntity = new Entity(currentRoute, false, "pc", 0, currentRoute.getTerrainName());
								break;
							case "STATUE":
								currentEntity = new Entity(currentRoute, false, "statue", 0, currentRoute.getTerrainName());
								break;
							case "JH":
								currentEntity = new Entity(currentRoute, false, "joyhealing0", 0,
										currentRoute.getTerrainName());
								break;
							case "PCD":
								currentEntity = new Entity(currentRoute, false, "pokecenter_desk", 0,
										currentRoute.getTerrainName());
								currentRoute.addBuilding("pokecenter_desk", new Point(x, y));
								break;
							case "MD":
								currentEntity = new Entity(currentRoute, true, "movedown", 0,
										currentRoute.getTerrainName());
								break;
							case "MU":
								currentEntity = new Entity(currentRoute, true, "moveup", 0,
										currentRoute.getTerrainName());
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
								currentEntity = new Entity(currentRoute, false, "cave_front", 0,
										currentRoute.getTerrainName());
								break;
							case "HWL":
								currentEntity = new Entity(currentRoute, false, "cave_left", 0,
										currentRoute.getTerrainName());
								break;
							case "HWR":
								currentEntity = new Entity(currentRoute, false, "cave_right", 0,
										currentRoute.getTerrainName());
								break;
							case "HWB":
								currentEntity = new Entity(currentRoute, false, "cave_back", 0,
										currentRoute.getTerrainName());
								break;
							case "HWLF":
								currentEntity = new Entity(currentRoute, false, "cave_left_front_corner_outside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWLB":
								currentEntity = new Entity(currentRoute, false, "cave_left_back_corner_outside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWRB":
								currentEntity = new Entity(currentRoute, false, "cave_right_back_corner_outside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWRF":
								currentEntity = new Entity(currentRoute, false, "cave_right_front_corner_outside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWRBI":
								currentEntity = new Entity(currentRoute, false, "cave_right_back_corner_inside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWRFI":
								currentEntity = new Entity(currentRoute, false, "cave_right_front_corner_inside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWLBI":
								currentEntity = new Entity(currentRoute, false, "cave_left_back_corner_inside", 0,
										currentRoute.getTerrainName());
								break;
							case "HWLFI":
								currentEntity = new Entity(currentRoute, false, "cave_left_front_corner_inside", 0,
										currentRoute.getTerrainName());
								break;
							case "HM":
								currentEntity = new Entity(currentRoute, true, "cave_middle", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "SCF":
								currentEntity = new Entity(currentRoute, false, false, true, true, "stairway_front",
										nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "ZLU":
								currentEntity = new Entity(currentRoute, false, "zaun_links_unten",
										nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "ZLO":
								currentEntity = new Entity(currentRoute, false, "zaun_links_oben",
										nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "ZRU":
								currentEntity = new Entity(currentRoute, false, "zaun_rechts_unten",
										nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "ZRO":
								currentEntity = new Entity(currentRoute, false, "zaun_rechts_oben",
										nonGrassEncounterRate, currentRoute.getTerrainName());
								break;
							case "ZF":
								currentEntity = new Entity(currentRoute, false, "zaun_front", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "ZL":
								currentEntity = new Entity(currentRoute, false, "zaun_links", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "ZR":
								currentEntity = new Entity(currentRoute, false, "zaun_rechts", nonGrassEncounterRate,
										currentRoute.getTerrainName());
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
								currentEntity = new Entity(currentRoute, false, "rock", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "IR":
								currentEntity = new Entity(currentRoute, false, "icerock", nonGrassEncounterRate,
										"snow");
								break;
							case "IP":
								currentEntity = new Entity(currentRoute, false, "icepillar", nonGrassEncounterRate,
										"snow");
								break;
							case "IRB":
								currentEntity = new Entity(currentRoute, false, "icerockbig", nonGrassEncounterRate,
										"snow");
								break;
							case "TC":
								currentEntity = new Entity(currentRoute, false, "treecut", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							case "ST":
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								NPC currentStone = new NPC(currentString);
								currentStone.setCurrentPosition(x, y);
								currentStone.setCurrentRoute(currentRoute);
								currentStone.setID("strength");
								currentStone.setCharacterImage("strength", "back");
								stones.add(currentStone);
								break;
							case "GENERATOR":
								currentEntity = new GeneratorEntity(currentRoute, currentRoute.getTerrainName());
								break;
							case "HOOK":
								currentEntity = new Entity(currentRoute, false, "hook", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							default:
								currentEntity = new Entity(currentRoute, true, "free", nonGrassEncounterRate,
										currentRoute.getTerrainName());
								break;
							}
						} else if (currentString.startsWith("W")) {
							Warp currentWarp = new Warp(currentString, routeID);
							if (currentString.startsWith("WD")) { // door
								currentEntity = new Entity(currentRoute, false, false, false, true, "free", 0, "free");
							} else if (currentString.startsWith("WS")) {
								switch (currentString.substring(0, 4)) {
								case "WSUL":
									currentEntity = new Entity(currentRoute, false, true, false, false, "stair_up_left",
											0, currentRoute.getTerrainName());
									break;
								case "WSUR":
									currentEntity = new Entity(currentRoute, true, false, false, false,
											"stair_up_right", 0, currentRoute.getTerrainName());
									break;
								case "WSDL":
									currentEntity = new Entity(currentRoute, false, true, false, false,
											"stair_down_left", 0, currentRoute.getTerrainName());
									break;
								case "WSDR":
									currentEntity = new Entity(currentRoute, true, true, false, false,
											"stair_down_right", 0, currentRoute.getTerrainName());
									break;
								}
							} else if (currentString.startsWith("WHE")) {
								String str = "";
								for (char c : currentString.toCharArray()) {
									try {
										Integer.parseInt(String.valueOf(c));
										break;
									} catch (Exception e) {
										str += c;
									}
								}
								switch (str) {
								case "WHER":
									currentEntity = new Entity(currentRoute, false, true, false, false,
											"cave_entrance_right", 0, currentRoute.getTerrainName());
									break;
								case "WHEL":
									currentEntity = new Entity(currentRoute, true, false, false, false,
											"cave_entrance_left", 0, currentRoute.getTerrainName());
									break;
								case "WHEB":
									currentEntity = new Entity(currentRoute, false, false, true, false,
											"cave_entrance_back", 0, currentRoute.getTerrainName());
									break;
								case "WHEF":
								default:
									currentEntity = new Entity(currentRoute, false, false, false, true,
											"cave_entrance_front", 0, currentRoute.getTerrainName());
									break;
								}
							} else if (currentString.startsWith("WL")) {
								if (currentString.startsWith("WLU")) {
									currentEntity = new Entity(currentRoute, false, false, false, true, "ladder_up", 0,
											currentRoute.getTerrainName());
								} else {
									currentEntity = new Entity(currentRoute, false, false, true, true, "ladder_down", 0,
											currentRoute.getTerrainName());
								}
							} else if (currentString.startsWith("WH")) {
								currentEntity = new HatchEntity(currentRoute, currentString,
										currentRoute.getTerrainName());
								hatches.add((HatchEntity) currentEntity);
							} else {
								currentEntity = new Entity(currentRoute, true, "warp", 0,
										currentRoute.getTerrainName());
							}
							currentEntity.addWarp(currentWarp);
							warps.add(currentWarp);
						} else if (currentString.startsWith("PKM")) {
							currentEntity = new PokemonEntity(currentRoute, currentRoute.getTerrainName(),
									currentString);
							pokemons.add((PokemonEntity) currentEntity);
						} else if (currentString.startsWith("TRIGGERED")) {
							currentEntity = new Entity(currentRoute, true, "warp", 0, currentRoute.getTerrainName());
							ArrayList<Point> temp = events.get(currentString);
							if (temp == null) {
								temp = new ArrayList<Point>();
							}
							temp.add(new Point(x, y));
							events.put(currentString, temp);
						} else if (currentString.startsWith("ITEM")) {
							currentEntity = new ItemEntity(currentRoute, currentRoute.getTerrainName(), currentString,
									false);
							items.add((ItemEntity) currentEntity);
						} else if (currentString.startsWith("GRASS")) {
							if(currentString.contains("SNOW")) {
								currentEntity = new Entity(currentRoute, true, "snow_grass", Entity.POKEMON_GRASS_RATE,
										"snow");
							} else {
								currentEntity = new Entity(currentRoute, true, "grass", Entity.POKEMON_GRASS_RATE,
										"grassy");
							}
							ArrayList<Entity> temp = pokemonPools.get(currentString);
							if (temp == null) {
								temp = new ArrayList<Entity>();
							}
							temp.add(currentEntity);
							pokemonPools.put(currentString, temp);
						} else if (currentString.startsWith("SEE")) {
							currentEntity = new Entity(currentRoute, true, "free", Entity.POKEMON_GRASS_RATE, "see");
							ArrayList<Entity> temp = pokemonPools.get(currentString);
							if (temp == null) {
								temp = new ArrayList<Entity>();
							}
							temp.add(currentEntity);
							pokemonPools.put(currentString, temp);
						} else if (currentString.startsWith("CAVE")) {
							currentEntity = new Entity(currentRoute, true, "free", Entity.POKEMON_GRASS_RATE, "cave");
							ArrayList<Entity> temp = pokemonPools.get(currentString);
							if (temp == null) {
								temp = new ArrayList<Entity>();
							}
							temp.add(currentEntity);
							pokemonPools.put(currentString, temp);
						} else if(currentString.startsWith("QUIZ")) {
							currentEntity = new QuestionEntity(currentString, currentRoute, currentRoute.getTerrainName());
							quizzes.add((QuestionEntity) currentEntity);
						}
						currentEntity.setX(x);
						currentEntity.setY(y);
						currentRoute.addEntity(x, y, currentEntity);
					}
				}
				JsonArray warpDetails = route.get("warps").getAsJsonArray();
				for (int y = 0; y < Math.min(warps.size(), warpDetails.size()); y++) {
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
					warps.get(warpIndex).setNewDirection(currentWarp.get("direction") != null
							? currentWarp.get("direction").getAsString() : Direction.NONE.name());

				}
				JsonArray characterDetails = route.get("characters").getAsJsonArray();
				for (int y = 0; y < characterDetails.size(); y++) {
					JsonObject currentChar = characterDetails.get(y).getAsJsonObject();
					NPC currentCharacter = new NPC(currentChar.get("id").getAsString());
					currentCharacter.setCurrentPosition(currentChar.get("x").getAsInt(),
							currentChar.get("y").getAsInt());
					currentCharacter.setCurrentRoute(currentRoute);
					currentCharacter.setCharacterImage(currentChar.get("char_sprite").getAsString(),
							currentChar.get("direction").getAsString().toLowerCase());
					currentCharacter.setName(currentChar.get("name").getAsString());
					currentCharacter.setTrainer(currentChar.get("is_trainer").getAsString());
					currentCharacter
							.setLogo(currentChar.get("logo") != null ? currentChar.get("logo").getAsString() : null);
					if (currentChar.get("surfing") != null) {
						currentCharacter.setSurfing(currentChar.get("surfing").getAsBoolean());
						if (currentCharacter.isSurfing()) {
							currentRoute.getEntities()[currentCharacter.getCurrentPosition().y][currentCharacter
									.getCurrentPosition().x].setTerrain("see");
						}
					}
					currentCharacter.setAggro(
							currentChar.get("aggro") != null ? currentChar.get("aggro").getAsBoolean() : true);
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

				if (route.get("hatches") != null) {
					JsonArray hatchesDetails = route.get("hatches").getAsJsonArray();
					ArrayList<HatchEntity> currentHatches = new ArrayList<>();
					for (HatchEntity h : hatches) {
						if (h.getRoute().equals(currentRoute)) {
							currentHatches.add(h);
						}
					}
					for (int i = 0; i < Math.min(hatchesDetails.size(), currentHatches.size()); i++) {
						JsonObject currentHatch = hatchesDetails.get(i).getAsJsonObject();
						int hatchIndex = i;
						String hatchID = currentHatch.get("entity_id").getAsString();
						if (!hatchID.equals(currentHatches.get(hatchIndex).getId())) {
							for (int j = 0; j < currentHatches.size(); j++) {
								hatchIndex = j;
								if (hatchID.equals(currentHatches.get(hatchIndex).getId())) {
									break;
								}
							}
						}
						HatchEntity entity = currentHatches.get(hatchIndex);

						System.out.println(routeID + " - " + currentHatch.get("minimum"));

						for (JsonElement g : currentHatch.get("generators").getAsJsonArray()) {
							JsonObject generator = g.getAsJsonObject();
							entity.addGenerator(
									generator.get("route") != null ? generator.get("route").getAsString() : routeID,
									new Point(generator.get("x").getAsInt(), generator.get("y").getAsInt()));
						}

						entity.setMinimum(
								currentHatch.get("minimum") != null ? currentHatch.get("minimum").getAsInt() : 0);

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
//								System.out.println(current);
								JsonObject j = current.getAsJsonObject();
								Change currentChange = new Change();
								currentChange.setParticipant(j.get("character").getAsString());
								currentChange.setRouteID(
										j.get("route") != null ? j.get("route").getAsString() : currentRoute.getId());
								currentChange.setMove(
										new Point(j.get("target_x") != null ? j.get("target_x").getAsInt() : -1,
												j.get("target_y") != null ? j.get("target_y").getAsInt() : -1));
								currentChange.setDialog(j.get("dialog") != null ? j.get("dialog").getAsString() : null);
								currentChange.setDirection(j.get("direction") != null
										? Direction.valueOf(j.get("direction").getAsString().toUpperCase()) : null);
								currentChange.setPositionUpdate(
										j.get("update") != null ? j.get("update").getAsBoolean() : false);
								currentChange.setFight(j.get("fight") != null ? j.get("fight").getAsBoolean() : false);
								currentChange.setAfterFightUpdate(
										j.get("after_fight") != null ? j.get("after_fight").getAsString() : null);
								currentChange.setBeforeFightUpdate(
										j.get("before_fight") != null ? j.get("before_fight").getAsString() : null);
								currentChange.setNoFightUpdate(
										j.get("no_fight") != null ? j.get("no_fight").getAsString() : null);
								currentChange.setSpriteUpdate(
										j.get("sprite_update") != null ? j.get("sprite_update").getAsString() : null);
								currentChange.setHeal(j.get("heal") != null ? j.get("heal").getAsBoolean() : false);
								currentChange.setRemove(j.get("remove") != null ? j.get("remove").getAsBoolean() : false);
								if (j.get("item") != null && j.get("item").isJsonArray()) {
									for (JsonElement x : j.get("item").getAsJsonArray()) {
										JsonObject currentItem = x.getAsJsonObject();
										currentChange.addItem(
												Item.valueOf(currentItem.get("name").getAsString().toUpperCase()),
												currentItem.get("amount") != null ? currentItem.get("amount").getAsInt()
														: 1);

									}
								}
								currentChange.setCamPosition(
										new Point(j.get("cam_x") != null ? j.get("cam_x").getAsInt() : -1,
												j.get("cam_y") != null ? j.get("cam_y").getAsInt() : -1));
								currentChange.setCamAnimation(
										j.get("cam_animation") != null ? j.get("cam_animation").getAsBoolean() : false);
								currentChange.setCenterCharacter(
										j.get("cam_center") != null ? j.get("cam_center").getAsBoolean() : false);
								currentChange.setSound(j.get("sound") != null ? j.get("sound").getAsString() : null);
								currentChange.setWait(j.get("wait") != null ? j.get("wait").getAsBoolean() : true);
								currentChange.setDelay(j.get("delay") != null ? j.get("delay").getAsLong() : 0);
								currentChange.setUnknown(j.get("unknown") != null ? j.get("unknown").getAsBoolean() : false);
								changes.add(currentChange);
							}
							te.addChanges(changes.toArray(new Change[changes.size()]));
						}
						for (Point p : events.get(event)) {
							currentRoute.getEntities()[p.y][p.x].setEvent(te);
						}
					}
				}

				for (int i = 0; i < signs.size(); i++) {
					signs.get(i).setInformation("Wer stellt ein leeres Schild auf?");
				}

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

				for (int i = 0; i < stones.size(); i++) {
					currentRoute.addCharacter(stones.get(i));
				}

				if (route.get("encounters") != null) {
					JsonObject encounterDetails = route.get("encounters").getAsJsonObject();
					if (encounterDetails.get("DEFAULT") != null) {
						PokemonPool pool = new PokemonPool("DEFAULT");
						for (JsonElement j : encounterDetails.get("DEFAULT").getAsJsonArray()) {
							JsonObject currentEncounter = j.getAsJsonObject();
							int ammount = currentEncounter.get("ammount") != null
									? currentEncounter.get("ammount").getAsInt() : 1;
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

				if (route.get("buildings") != null) {
					JsonArray buildings = route.get("buildings").getAsJsonArray();
					for (JsonElement je : buildings) {
						JsonObject building = je.getAsJsonObject();
						currentRoute.addBuilding(building.get("building").getAsString(),
								new Point(building.get("x").getAsInt(), building.get("y").getAsInt()));
					}
				}

				if(route.get("quizzes") != null) {
					JsonObject quizData = route.get("quizzes").getAsJsonObject();
					for(QuestionEntity q : quizzes) {
						if(quizData.get(q.getID().toUpperCase()) != null) {
							JsonObject currentQuiz = quizData.get(q.getID().toUpperCase()).getAsJsonObject();
							q.setQuestion(currentQuiz.get("question") == null ? "" : currentQuiz.get("question").getAsString());
							q.addOptions(currentQuiz.get("options").getAsString().split("\\+"));
							q.addSolutions(currentQuiz.get("solutions").getAsString().split("\\+"));
							q.setSource(currentQuiz.get("source") != null ? currentQuiz.get("source").getAsString() :
									"nothing");
							q.setNPC(currentRoute.getNPC(currentQuiz.get("npc").getAsString()));
							q.setType(QuestionType.valueOf(currentQuiz.get("type").getAsString().toUpperCase()));
							for(JsonElement e : currentQuiz.get("entities").getAsJsonArray()) {
								JsonObject entity = e.getAsJsonObject();
								q.addGates(currentRoute.getEntity(entity.get("x").getAsInt(), entity.get("y").getAsInt()));
							}
						}
					}
				}

				// warps.clear();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (counter == 0) {
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

			for (String s : loadedRoutes.keySet()) {
				if (!loadedRoutes.get(s).equals(originalRoutes.get(s), false)) {
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
			SoundController.getInstance().playSound(SoundController.SAVE);
			JOptionPane.showMessageDialog(null, "Das Spiel wurde erfolgreich gespeichert!", "Speichern ...",
					JOptionPane.INFORMATION_MESSAGE);
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
			for (Route currentRoute : loadedRoutes.values()) {
				currentRoute.clearCharacters();
			}

			for (int i = 1; i < characters.size(); i++) {
				JsonObject currentJson = characters.get(i).getAsJsonObject();
				NPC character = new NPC();
				if (character.importSaveData(currentJson)) {
					loadedRoutes.get(character.getCurrentRoute().getId()).addCharacter(character);
				}
			}

			for (JsonElement current : routes) {
				JsonObject currentJson = current.getAsJsonObject();
				Route route = this.getRouteById(currentJson.get("id").getAsString());
				route.importSaveData(currentJson, originalRoutes.get(route.getId()));
			}

			for (String s : loadedRoutes.keySet()) {
				// if(!loadedRoutes.get(s).equals(originalRoutes.get(s))) {
				loadedRoutes.get(s).createMap();
				// }
			}

			gController.setCurrentRoute(gController.getMainCharacter().getCurrentRoute());

			gController.getCurrentBackground().getCamera().importSaveData(data.get("cam").getAsJsonObject());

			return true;
		} catch (Exception e) {
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

	public HashMap<Integer, HashSet<String>> getAllPokemonLocations() {
		HashMap<Integer, HashSet<String>> allLocations = new HashMap<>();
		for (int i = 1; i < 650; i++) {
			allLocations.put(i, new HashSet<String>());
		}
		for (String s : loadedRoutes.keySet()) {
			Route r = loadedRoutes.get(s);
			ArrayList<String> checkedPools = new ArrayList<String>();
			for (int x = 0; x < r.getWidth(); x++) {
				for (int y = 0; y < r.getHeight(); y++) {
					Entity e = r.getEntities()[y][x];
					if (e.getPokemonPool() != null && !checkedPools.contains(e.getPokemonPool().getId())) {
						checkedPools.add(e.getPokemonPool().getId());
						for (SimpleEntry<Integer, Short> id : e.getPokemonPool().getPokemonPool()) {
							HashSet<String> temp = allLocations.get(id.getKey());
							temp.add(s);
							allLocations.put(id.getKey(), temp);
						}
					}
				}
			}
		}
		return allLocations;
	}

	public void updateHatches(GeneratorEntity source) {
		for (HatchEntity hatch : hatches) {
			if (hatch.containsGenerator(source)) {
				hatch.getRoute().updateMap(new Point(hatch.getX(), hatch.getY()));
			}
		}
	}

	public void updateHatches(Route route) {
		for (Entity[] rows : route.getEntities()) {
			for (Entity e : rows) {
				if (e instanceof HatchEntity) {
					route.updateMap(new Point(e.getX(), e.getY()));
				}
			}
		}
	}

}
