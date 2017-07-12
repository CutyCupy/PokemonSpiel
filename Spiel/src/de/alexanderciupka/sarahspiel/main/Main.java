package de.alexanderciupka.sarahspiel.main;

import de.alexanderciupka.sarahspiel.menu.MenuController;


public class Main {

	public static void main(String[] args) {
		 MenuController.getInstance();
		 //		readDescription();
	}

//	public static void readDescription() {
//		JsonParser parser = new JsonParser();
//		BufferedReader reader = null;
//		BufferedReader moveData = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/pokemon/moves.json")));
//		JsonArray allMoveData = null;
//		try {
//			allMoveData = parser.parse(moveData.readLine()).getAsJsonArray();
//		} catch (JsonSyntaxException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		int i = 1;
//		JsonArray newData = new JsonArray();
//		for (JsonElement element : allMoveData) {
//			try {
//				JsonObject currentJson = element.getAsJsonObject();
//				URLConnection connection = new URL("http://www.pokeapi.co/api/v2/move/" + i).openConnection();
//				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//				connection.connect();
//				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//				StringBuffer buffer = new StringBuffer();
//				int read;
//				char[] chars = new char[1024];
//				while ((read = reader.read(chars)) != -1)
//					buffer.append(chars, 0, read);
//				JsonObject currentMove = (JsonObject) parser.parse(buffer.toString());
//				for(JsonElement curDescription : currentMove.get("flavor_text_entries").getAsJsonArray()) {
//					JsonObject jo = curDescription.getAsJsonObject();
//					if(jo.get("language").getAsJsonObject().get("name").getAsString().equals("de")) {
//						currentJson.addProperty("desc", jo.get("flavor_text").getAsString());
//						break;
//					}
//				}
//				currentJson.addProperty("ailment", currentMove.get("meta").getAsJsonObject().get("ailment").getAsJsonObject().get("name").getAsString());
//				currentJson.addProperty("damage_class", currentMove.get("damage_class").getAsJsonObject().get("name").getAsString());
//				currentJson.addProperty("priority", currentMove.get("priority").getAsInt());
//				newData.add(currentJson);
//				System.out.println(currentJson);
//			} catch (JsonSyntaxException e) {
//				e.printStackTrace();
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			i++;
//		}
//		try {
//			FileWriter fw = new FileWriter(new File(Main.class.getResource("/pokemon/moves.json").getFile()));
//			for(char c : newData.toString().toCharArray()) {
//				fw.write(c);
//				fw.flush();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	public void readAllRoutes() {
//	for (File currentFile : folderFiles) {
//		if (currentFile.isFile() && currentFile.getName().endsWith(".txt")) {
//			try {
//				currentReader = new BufferedReader(new FileReader(currentFile));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			JsonObject route = parser.parse(currentReader).getAsJsonObject();
//
//			Route currentRoute = new Route();
//			String routeID = currentFile.getName().split("\\.")[0];
//			String currentLine;
//			try {
//				JsonObject routeDetails = route.get("route").getAsJsonObject();
//				currentRoute.setId(routeDetails.get("id").getAsString());
//				currentRoute.setName(routeDetails.get("name").getAsString());
//				currentRoute.setTerrain(routeDetails.get("terrain").getAsString());
//				currentRoute.setHeight(routeDetails.get("height").getAsInt());
//				currentRoute.setWidth(routeDetails.get("width").getAsInt());
//				warps = new ArrayList<Warp>();
//				characters = new ArrayList<NPC>();
//				for (int y = 0; y < currentRoute.getHeight(); y++) {
//					currentLine = currentReader.readLine();
//					String[] rowElements = currentLine.split(",");
//					for (int x = 0; x < currentRoute.getWidth(); x++) {
//						Entity currentEntity = null;
//						if(routeDetails.get(x + "." + y) == null) {
//							break;
//						}
//						String currentString = routeDetails.get(x + "." + y).getAsString();
//						if (!currentString.startsWith("W") && !currentString.startsWith("C")) {
//							switch (rowElements[x]) {
//							case "T": // Tree
//								currentEntity = new Entity(false, "tree", 0, "grassy");
//								break;
//							case "F": // Free
//								currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
//								break;
//							case "G": // Grass
//								currentEntity = new Entity(true, "grass", Entity.POKEMON_GRASS_RATE, "grassy");
//								break;
//							case "M": // Mauer - House/Center/Market
//								currentEntity = new Entity(false, "free", 0, currentRoute.getTerrain());
//								break;
//							case "P": // Center
//								currentEntity = new Entity(false, "center", 0, currentRoute.getTerrain());
//								break;
//							case "HS":
//								currentEntity = new Entity(false, "house_small", 0, currentRoute.getTerrain());
//								break;
//							case "HL":
//								currentEntity = new Entity(false, "house_large", 0, currentRoute.getTerrain());
//								break;
//							case "A":
//								currentEntity = new Entity(false, "gym", 0, currentRoute.getTerrain());
//								break;
//							case "S": //See
//								currentEntity = new Entity(true, "free", 0, "see");
//								currentEntity.setWater(true);
//								break;
//							case "SA": //Sand
//								currentEntity = new Entity(true, "free", 0, "sandy");
//								break;
//							case "B": //Bridge
//								currentEntity = new Entity(true, "bridge", 0, currentRoute.getTerrain());
//								break;
//							}
//						} else if (currentString.startsWith("W")) {
//							Warp currentWarp = new Warp(currentString, routeID);
//							if (currentString.startsWith("WD")) { // door
//								currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
//							} else {
//								currentEntity = new Entity(true, "warp", 0, currentRoute.getTerrain());
//							}
//							currentEntity.addWarp(currentWarp);
//							warps.add(currentWarp);
//						} else if (currentString.startsWith("C")) {
//							currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
//							NPC currentCharacter = new NPC(currentString);
//							currentCharacter.setCurrentPosition(x, y);
//							currentCharacter.setCurrentRoute(currentRoute);
//							characters.add(currentCharacter);
//						}
//						currentRoute.addEntity(x, y, currentEntity);
//					}
//				}
//				JsonArray warpDetails = route.get("warps").getAsJsonArray();
//				for (int y = 0; y < Math.min(warpDetails.size(), warps.size()); y++) {
//					int warpIndex = y;
//					JsonObject currentWarp = warpDetails.get(y).getAsJsonObject();
//					String warpID = currentWarp.get("id").getAsString();
//					if (!warpID.equals(warps.get(warpIndex).getWarpString())) {
//						for (int i = 0; i < warps.size(); i++) {
//							warpIndex = i;
//							if (warpID.equals(warps.get(warpIndex).getWarpString())) {
//								break;
//							}
//						}
//					}
//					warps.get(warpIndex).setNewRoute(currentWarp.get("new_route").getAsString());
//					warps.get(warpIndex).setNewPosition(
//							new Point(currentWarp.get("new_x").getAsInt(), currentWarp.get("new_y").getAsInt()));
//
//				}
//				JsonArray characterDetails = route.get("characters").getAsJsonArray();
//				for (int y = 0; y < Math.min(characterDetails.size(), characters.size()); y++) {
//					JsonObject currentChar = characterDetails.get(y).getAsJsonObject();
//					int characterIndex = y;
//					String characterID = currentChar.get("id").getAsString();
//					if (!characterID.equals(characters.get(characterIndex).getID())) {
//						for (int i = 0; i < characters.size(); i++) {
//							characterIndex = i;
//							if (characterID.equals(characters.get(characterIndex).getID())) {
//								break;
//							}
//						}
//					}
//					NPC currentCharacter = characters.get(characterIndex);
//					currentCharacter.setID(characterID);
//					currentCharacter.setCharacterImage(currentChar.get("char_sprite").getAsString(), currentChar.get("direction").getAsString().toLowerCase());
//					currentCharacter.setName(currentChar.get("name").getAsString());
//					currentCharacter.setTrainer(currentChar.get("is_trainer").getAsString());
//					if (currentChar.get("sprite") != null) {
//						currentRoute.getEntities()[currentCharacter.getCurrentPosition().y]
//								[currentCharacter.getCurrentPosition().x].setSprite(currentChar.get("sprite").getAsString());
//					}
//					if(currentChar.get("surfing") != null) {
//						currentCharacter.setSurfing(currentChar.get("surfing").getAsBoolean());
//						if(currentCharacter.isSurfing()) {
//							currentRoute.getEntities()[currentCharacter.getCurrentPosition().y]
//									[currentCharacter.getCurrentPosition().x].setTerrain("see");;
//						}
//					}
//					if(currentCharacter.isTrainer()) {
//						currentCharacter.importTeam();
//					}
//					currentCharacter.importDialogue();
//					currentRoute.addCharacterToEntity(currentCharacter.getCurrentPosition().x,
//							currentCharacter.getCurrentPosition().y, currentCharacter);
//				}
//				JsonArray encounterDetails = route.get("encounters").getAsJsonArray();
//				for(JsonElement j : encounterDetails) {
//					JsonObject currentEncounter = j.getAsJsonObject();
//					Pokemon encounter = new Pokemon(currentEncounter.get("id").getAsInt());
//					encounter.getStats().generateStats(currentEncounter.get("level").getAsShort());
//					currentRoute.addPokemon(encounter);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			currentRoute.createMap();
//			loadedRoutes.put(routeID, currentRoute);
//			originalRoutes.put(routeID, currentRoute.copy());
//			warps.clear();
//		}
//	}
//}


}
