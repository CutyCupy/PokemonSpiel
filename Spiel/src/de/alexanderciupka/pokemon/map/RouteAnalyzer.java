package de.alexanderciupka.pokemon.map;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.pokemon.Direction;
import de.alexanderciupka.pokemon.pokemon.NPC;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class RouteAnalyzer {

	/**
	 * route.txt Route display name height (multiply with 70 to get the image
	 * height) width (multiply with 70 to get the image width) Route entities
	 * split by , warps (id,newrouteID,newX,newY) characters(id,name,side)
	 * possibleEncounters (id,level,move1,move2,move3,move4)
	 */

	private File routeFolder;
	private File[] folderFiles;
	private BufferedReader currentReader;
	private Map<String, Route> loadedRoutes;
	private Map<String, Route> originalRoutes;
	private ArrayList<Warp> warps;
	private ArrayList<NPC> characters;
	private ArrayList<NPC> stones;
	private ArrayList<PokemonEntity> pokemons;
	private HashMap<String, ArrayList<Point>> events;
	private GameController gController;

	private JsonParser parser;

	public RouteAnalyzer() {
		gController = GameController.getInstance();
		routeFolder = new File(this.getClass().getResource("/routes/").getFile());
		folderFiles = routeFolder.listFiles();
		loadedRoutes = new HashMap<String, Route>();
		originalRoutes = new HashMap<String, Route>();
		parser = new JsonParser();
		readAllRoutes();
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
				warps = new ArrayList<Warp>();
				characters = new ArrayList<NPC>();
				stones = new ArrayList<NPC>();
				pokemons = new ArrayList<PokemonEntity>();
				events = new HashMap<String, ArrayList<Point>>();
				for (int y = 0; y < currentRoute.getHeight(); y++) {
					for (int x = 0; x < currentRoute.getWidth(); x++) {
						Entity currentEntity = null;
						if (routeDetails.get(x + "." + y) == null) {
							break;
						}
						String currentString = routeDetails.get(x + "." + y).getAsString().toUpperCase();
						if (!currentString.startsWith("W") && !currentString.startsWith("C")
								&& !currentString.startsWith("PKM") && !currentString.startsWith("TRIGGERED")) {
							switch (currentString) {
							case "OOB":
								currentEntity = new Entity(currentRoute, false, "free", 0, "free");
								break;
							case "T": // Tree
								currentEntity = new Entity(currentRoute, false, "tree", 0, "grassy");
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
								currentEntity = new Entity(currentRoute, true, "chair_r", 0, currentRoute.getTerrainName());
								break;
							case "STUHLL":
								currentEntity = new Entity(currentRoute, true, "chair_l", 0, currentRoute.getTerrainName());
								break;
							case "STUHLD":
								currentEntity = new Entity(currentRoute, true, "chair_d", 0, currentRoute.getTerrainName());
								break;
							case "STUHLU":
								currentEntity = new Entity(currentRoute, true, "chair_u", 0, currentRoute.getTerrainName());
								break;
							case "SETTLER":
								currentEntity = new Entity(currentRoute, true, "settle_r", 0,
										currentRoute.getTerrainName());
								break;
							case "SETTLEL":
								currentEntity = new Entity(currentRoute, true, "settle_l", 0,
										currentRoute.getTerrainName());
								break;
							case "SETTLED":
								currentEntity = new Entity(currentRoute, true, "settle_d", 0,
										currentRoute.getTerrainName());
								break;
							case "SETTLEU":
								currentEntity = new Entity(currentRoute, true, "settle_u", 0,
										currentRoute.getTerrainName());
								break;
							case "F": // Free
								currentEntity = new Entity(currentRoute, true, "free", 0, currentRoute.getTerrainName());
								break;
							case "G": // Grass
								currentEntity = new Entity(currentRoute, true, "grass", Entity.POKEMON_GRASS_RATE,
										"grassy");
								break;
							case "GR":
								currentEntity = new Entity(currentRoute, true, "free", 0, "grassy");
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
								currentEntity = new Entity(currentRoute, true, "free", 0, "see");
								break;
							case "SA": // Sand
								currentEntity = new Entity(currentRoute, true, "free", 0, "sandy");
								break;
							case "B": // Bridge
								currentEntity = new Entity(currentRoute, true, "bridge", 0, currentRoute.getTerrainName());
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
							case "RB":
								currentEntity = new Entity(currentRoute, false, "rockbig", 0,
										currentRoute.getTerrainName());
								break;
							case "RG":
								currentEntity = new Entity(currentRoute, false, "rockgroup", 0,
										currentRoute.getTerrainName());
								break;
							case "R":
								currentEntity = new Entity(currentRoute, false, "rock", 0, currentRoute.getTerrainName());
								break;
							case "TC":
								currentEntity = new Entity(currentRoute, false, "treecut", 0,
										currentRoute.getTerrainName());
								break;
							case "ST":
								currentEntity = new Entity(currentRoute, true, "free", 0, currentRoute.getTerrainName());
								NPC currentStone = new NPC(currentString);
								currentStone.setCurrentPosition(x, y);
								currentStone.setCurrentRoute(currentRoute);
								currentStone.setID("strength");
								currentStone.setCharacterImage("strength", "back");
								stones.add(currentStone);
								break;
							default:
								currentEntity = new Entity(currentRoute, true, "free", 0, currentRoute.getTerrainName());
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
							ArrayList<Point> temp = this.events.get(currentString);
							if (temp == null) {
								temp = new ArrayList<Point>();
							}
							temp.add(new Point(x, y));
							this.events.put(currentString, temp);
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
					for (int i = 0; i < Math.min(this.pokemons.size(), pokemonDetails.size()); i++) {
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

				if (route.get("events") != null) {
					JsonObject eventDetails = route.get("events").getAsJsonObject();
					for (String event : this.events.keySet()) {
						JsonArray currentEvent = eventDetails.get(event.toLowerCase()).getAsJsonArray();
						TriggeredEvent te = new TriggeredEvent(event);
						for (JsonElement step : currentEvent) {
							JsonArray currentStep = step.getAsJsonArray();
							ArrayList<Change> changes = new ArrayList<Change>();
							for (JsonElement current : currentStep) {
								Change currentChange = new Change();
								currentChange.setParticipant(current.getAsJsonObject().get("character").getAsString());
								currentChange.setRouteID(current.getAsJsonObject().get("route") != null
										? current.getAsJsonObject().get("route").getAsString() : currentRoute.getId());
								currentChange.setMove(new Point(
										current.getAsJsonObject().get("target_x") != null
												? current.getAsJsonObject().get("target_x").getAsInt() : -1,
										current.getAsJsonObject().get("target_y") != null
												? current.getAsJsonObject().get("target_y").getAsInt() : -1));
								currentChange.setDialog(current.getAsJsonObject().get("dialog") != null
										? current.getAsJsonObject().get("dialog").getAsString() : null);
								currentChange.setDirection(current.getAsJsonObject().get("direction") != null
										? Direction.valueOf(
												current.getAsJsonObject().get("direction").getAsString().toUpperCase())
										: null);
								currentChange.setPositionUpdate(current.getAsJsonObject().get("update") != null
										? current.getAsJsonObject().get("update").getAsBoolean() : false);
								currentChange.setFight(current.getAsJsonObject().get("fight") != null
										? current.getAsJsonObject().get("fight").getAsBoolean() : false);
								currentChange.setAfterFightUpdate(current.getAsJsonObject().get("after_fight") != null
										? current.getAsJsonObject().get("after_fight").getAsString() : null);
								currentChange.setBeforeFightUpdate(current.getAsJsonObject().get("before_fight") != null
										? current.getAsJsonObject().get("before_fight").getAsString() : null);
								currentChange.setNoFightUpdate(current.getAsJsonObject().get("no_fight") != null
										? current.getAsJsonObject().get("no_fight").getAsString() : null);
								currentChange.setSpriteUpdate(current.getAsJsonObject().get("sprite_update") != null
										? current.getAsJsonObject().get("sprite_update").getAsString() : null);
								currentChange.setHeal(current.getAsJsonObject().get("heal") != null
										? current.getAsJsonObject().get("heal").getAsBoolean() : false);
								changes.add(currentChange);
							}
							te.addChanges(changes.toArray(new Change[changes.size()]));
						}
						for (Point p : this.events.get(event)) {
							currentRoute.getEntities()[p.y][p.x].setEvent(te);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(counter == 0) {
				currentRoute.createMap();
				loadedRoutes.put(routeID, currentRoute);
			} else {
				originalRoutes.put(routeID, currentRoute);
			}
			warps.clear();
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
					System.out.println(s);
					routeData.add(loadedRoutes.get(s).getSaveData(originalRoutes.get(s)));
				}
			}
			
			data.add("characters", charData);
			data.add("routes", routeData);
			
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
					System.out.println(s);
					loadedRoutes.get(s).createMap();
				}
			}


			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, Route> getOriginalRoutes() {
		return originalRoutes;
	}

}
