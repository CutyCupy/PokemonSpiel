package de.alexanderciupka.sarahspiel.map;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.alexanderciupka.sarahspiel.menu.MenuController;
import de.alexanderciupka.sarahspiel.pokemon.Character;
import de.alexanderciupka.sarahspiel.pokemon.Direction;
import de.alexanderciupka.sarahspiel.pokemon.Move;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

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
	private ArrayList<Warp> warps;
	private ArrayList<Character> characters;
	private GameController gController;

	public RouteAnalyzer() {
		gController = GameController.getInstance();
		routeFolder = new File(this.getClass().getResource("/routes/").getFile());
		folderFiles = routeFolder.listFiles();
		loadedRoutes = new HashMap<String, Route>();
		readAllRoutes();
	}

	public void readAllRoutes() {
		for (File currentFile : folderFiles) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".txt")) {
				try {
					currentReader = new BufferedReader(new FileReader(currentFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Route currentRoute = new Route();
				String routeID = currentFile.getName().split("\\.")[0];
				String currentLine;
				try {
					currentRoute.setId(routeID);
					currentRoute.setName(currentReader.readLine());
					currentRoute.setTerrain(currentReader.readLine());
					currentRoute.setHeight(Integer.parseInt(currentReader.readLine()));
					currentRoute.setWidth(Integer.parseInt(currentReader.readLine()));
					warps = new ArrayList<Warp>();
					characters = new ArrayList<Character>();
					for (int y = 0; y < currentRoute.getHeight(); y++) {
						currentLine = currentReader.readLine();
						String[] rowElements = currentLine.split(",");
						for (int x = 0; x < currentRoute.getWidth(); x++) {
							Entity currentEntity = null;
							String currentString = rowElements[x];
							if (!currentString.startsWith("W") && !currentString.startsWith("C")) {
								switch (rowElements[x]) {
								case "T": // Tree
									currentEntity = new Entity(false, "tree", 0, "grassy");
									break;
								case "F": // Free
									currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
									break;
								case "G": // Grass
									currentEntity = new Entity(true, "grass", Entity.POKEMON_GRASS_RATE, "grassy");
									break;
								case "M": // Mauer - House/Center/Market
									currentEntity = new Entity(false, "free", 0, currentRoute.getTerrain());
									break;
								case "P": // Center
									currentEntity = new Entity(false, "center", 0, currentRoute.getTerrain());
									break;
								case "HS":
									currentEntity = new Entity(false, "house_small", 0, currentRoute.getTerrain());
									break;
								case "HL":
									currentEntity = new Entity(false, "house_large", 0, currentRoute.getTerrain());
									break;
								case "A":
									currentEntity = new Entity(false, "gym", 0, currentRoute.getTerrain());
									break;
								case "S": //See
									currentEntity = new Entity(false, "free", 0, "see");
									break;
								case "SA": //Sand
									currentEntity = new Entity(true, "free", 0, "sandy");
									break;
								case "B": //Bridge
									currentEntity = new Entity(true, "bridge", 0, currentRoute.getTerrain());
									break;
								}
							} else if (currentString.startsWith("W")) {
								Warp currentWarp = new Warp(currentString, routeID);
								if (currentString.startsWith("WD")) { // door
									currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
								} else {
									currentEntity = new Entity(true, "warp", 0, currentRoute.getTerrain());
								}
								currentEntity.addWarp(currentWarp);
								warps.add(currentWarp);
							} else if (currentString.startsWith("C")) {
								currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
								Character currentCharacter = new Character(currentString);
								currentCharacter.setCurrentPosition(x, y);
								currentCharacter.setCurrentRoute(currentRoute);
								characters.add(currentCharacter);
							}
							currentRoute.addEntity(x, y, currentEntity);
						}
					}
					for (int y = 0; y < warps.size(); y++) {
						currentLine = currentReader.readLine();
						String[] rowElements = currentLine.split(",");
						int warpIndex = y;
						if (!rowElements[0].equals(warps.get(warpIndex).getWarpString())) {
							for (int i = 0; i < warps.size(); i++) {
								warpIndex = i;
								if (rowElements[0].equals(warps.get(warpIndex).getWarpString())) {
									break;
								}
							}
						}
						warps.get(warpIndex).setNewRoute(rowElements[1]);
						warps.get(warpIndex).setNewPosition(
								new Point(Integer.parseInt(rowElements[2]), Integer.parseInt(rowElements[3])));

					}
					for (int y = 0; y < characters.size(); y++) {
						currentLine = currentReader.readLine();
						String[] rowElements = currentLine.split(",");
						int characterIndex = y;
						if (!rowElements[0].equals(characters.get(characterIndex).getID())) {
							for (int i = 0; i < characters.size(); i++) {
								characterIndex = i;
								if (rowElements[0].equals(characters.get(characterIndex).getID())) {
									break;
								}
							}
						}
						Character currentCharacter = characters.get(characterIndex);
						currentCharacter.setID(rowElements[0]);
						currentCharacter.setCharacterImage(rowElements[1], rowElements[2].toLowerCase());
						currentCharacter.setName(rowElements[3]);
						currentCharacter.setTrainer(rowElements[4]);
						if (rowElements.length == 6) {
							currentRoute.getEntities()[currentCharacter.getCurrentPosition().y]
									[currentCharacter.getCurrentPosition().x].setSprite(rowElements[5]);
						}
						if(currentCharacter.isTrainer()) {
							currentCharacter.importTeam();
						}
						currentCharacter.importDialogue();
						currentRoute.addCharacterToEntity(currentCharacter.getCurrentPosition().x,
								currentCharacter.getCurrentPosition().y, currentCharacter);
					}
					while ((currentLine = currentReader.readLine()) != null) {
						// possibleEncounters (id,level)
						String[] rowElements = currentLine.split(",");
						Pokemon currentEncounter = new Pokemon(Integer.parseInt(rowElements[0]));
						currentEncounter.getStats().generateStats(Short.parseShort(rowElements[1]));
						currentRoute.addPokemon(currentEncounter);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				currentRoute.createMap();
				loadedRoutes.put(routeID, currentRoute);
				warps.clear();
			}
		}
	}

	public Route getRouteById(String id) {
		return loadedRoutes.get(id);
	}

	public boolean saveGame(String saveName) {
		try {
			String filePath = MenuController.SAVE_PATH + saveName.toLowerCase().replace(" ", "_");
			if(!filePath.endsWith(".sss")) {
				filePath += ".sss";
			}
			File saveFile = new File(filePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
			/**
			 * Line 1: routeID,xpos,ypos,direction
			 * Line 2: Pokemon1 (id,lvl,CurrentHP,HP,ATTACK,DEFENSE,SPATTACK,SPDEFENSE,SPEED,currentXP,move1,move2,move3,move4)
			 * ...
			 * Line 7: Pokemon6
			 * Line 7: RouteID,characterid,trainer,defeated,direction
			 * LastLine: bruchkoebel[7][42] isWarp
			 */
			Character mainCharacter = gController.getMainCharacter();
			String currentLine = mainCharacter.getCurrentRoute().getId() + "," + mainCharacter.getCurrentPosition().x + "," + mainCharacter.getCurrentPosition().y + "," + mainCharacter.getCurrentDirection();
			writer.write(currentLine + "\n");
			writer.flush();
			for(int i = 0; i < 6; i++) {
				Pokemon currentPokemon = mainCharacter.getTeam().getTeam()[i];
				if(currentPokemon != null) {
					currentLine = currentPokemon.getId() + "," + currentPokemon.getStats().getLevel() + "," + currentPokemon.getStats().getCurrentHP();
					for(int j = 0; j < currentPokemon.getStats().getStats().length; j++) {
						currentLine += "," + currentPokemon.getStats().getStats()[j];
					}
					currentLine += "," + currentPokemon.getStats().getCurrentXP();
					for(Move currentMove : currentPokemon.getMoves()) {
						if(currentMove != null) {
							currentLine += "," + currentMove.getName();
						} else {
							currentLine += ",null";
						}
					}
				} else {
					currentLine = "null";
				}
				writer.write(currentLine + "\n");
				writer.flush();
			}
			for(Route currentRoute : loadedRoutes.values()) {
				for(Character currentCharacter : currentRoute.getCharacters()) {
					currentLine = currentRoute.getId() + "," + currentCharacter.getID() + "," + currentCharacter.isTrainer() + "," + currentCharacter.isDefeated();
					if(currentCharacter.getCurrentDirection() == Direction.UP) {
						currentLine += ",back";
					} else if(currentCharacter.getCurrentDirection() == Direction.DOWN) {
						currentLine += ",front";
					} else {
						currentLine += "," + currentCharacter.getCurrentDirection().name().toLowerCase();
					}
					writer.write(currentLine + "\n");
					writer.flush();
				}
			}
			Entity house = loadedRoutes.get("bruchkoebel").getEntities()[7][42];
			if(house.getWarp() != null) {
				currentLine = "true";
			} else {
				currentLine = "false";
			}
			writer.write(currentLine);
			writer.flush();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean loadGame(String path) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			/**
			 * Line 1: routeID,xpos,ypos,direction
			 * Line 2: Pokemon1 (id,lvl,CurrentHP,HP,ATTACK,DEFENSE,SPATTACK,SPDEFENSE,SPEED,currentXP,move1,move2,move3,move4)
			 * ...
			 * Line 7: Pokemon6
			 * Line 7: RouteID,characterid,trainer,defeated,direction
			 * LastLine: bruchkoebel[7][42] isWarp
			 */
			String currentLine = reader.readLine();
			String[] splitLine = currentLine.split(",");
			Character mainCharacter = gController.getMainCharacter();
			mainCharacter.setCurrentRoute(getRouteById(splitLine[0]));
			mainCharacter.setCurrentPosition(Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]));
			mainCharacter.setCurrentDirection(splitLine[3]);
			for(int i = 0; i < 6; i++) {
				currentLine = reader.readLine();
				if(!currentLine.equals("null")) {
					splitLine = currentLine.split(",");
					Pokemon currentPokemon = new Pokemon(Integer.parseInt(splitLine[0]));
					short[] stats = new short[8];
					stats[0] = Short.parseShort(splitLine[1]);
					for(int j = 1; j < stats.length - 1; j++) {
						stats[j] = Short.parseShort(splitLine[j + 2]);
					}
					stats[7] = Short.parseShort(splitLine[9]);
					currentPokemon.getStats().setStats(stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6], stats[7]);
					currentPokemon.getStats().setCurrentHP(Short.parseShort(splitLine[2]));
					for(int j = 0; j < 4; j++) {
						if(!splitLine[j+10].equals("null")) {
							currentPokemon.addMove(splitLine[j+10]);
						}
					}
					mainCharacter.getTeam().addPokemon(currentPokemon);
				}
			}
			//Line 7: RouteID,characterid,trainer,defeated,direction
			while(!(currentLine = reader.readLine()).equals("true") && !currentLine.equals("false")) {
				splitLine = currentLine.split(",");
				Route currentRoute = this.loadedRoutes.get(splitLine[0]);
				if(currentRoute.getCharacters().size() > 0) {
					for(Character currentCharacter : currentRoute.getCharacters()) {
						if(currentCharacter.getID().equals(splitLine[1])) {
							currentCharacter.setTrainer(splitLine[2]);
							currentCharacter.defeated(Boolean.parseBoolean(splitLine[3]));
							currentCharacter.setCurrentDirection(splitLine[4].toLowerCase());
						}
					}
				}
			}
			//LastLine: bruchkoebel[7][42] isWarp --> true | false
			if(currentLine.equals("true")) {
				Warp houseWarp = new Warp("W100", "bruchkoebel");
				houseWarp.setNewPosition(new Point(3, 4));
				houseWarp.setNewRoute("verlassenes_haus");
				loadedRoutes.get("bruchkoebel").getEntities()[7][42].addWarp(houseWarp);
				loadedRoutes.get("bruchkoebel").getEntities()[7][42].setAccessible(true);
			} else {
				loadedRoutes.get("bruchkoebel").getEntities()[7][42].setAccessible(false);
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
