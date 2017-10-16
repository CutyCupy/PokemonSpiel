package de.alexanderciupka.pokemon.main;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;

public class Main {

	public static final double FPS = 60;

	public static void main(String[] args) throws Exception {

		MenuController.getInstance();


		Thread repainter = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = 0;
				GameController gController = null;
				GameFrame frame = null;
				while (gController == null) {
					gController = GameController.getInstance();
				}
				while (frame == null) {
					frame = gController.getGameFrame();
				}
				boolean firstDialogue = true;
				boolean wait = false;
				int waitFrames = 0;
				while (true) {
					startTime = System.currentTimeMillis();
					wait = false;
					if (!frame.getDialogue().isVisible()
							|| (gController.isFighting() && !frame.getFightPanel().getTextLabel().isVisible()) && !frame.getDialogue().isVisible()) {
						wait = true;
						firstDialogue = true;
						waitFrames = 0;
					} else {
						if (waitFrames <= 5) {
							waitFrames++;
						}
					}

					if (firstDialogue) {
						frame.repaint();
						if (waitFrames > 5) {
							firstDialogue = false;
						}
					} else {
						if (frame.getDialogue().isVisible()) {
							frame.getDialogue().repaint();
							wait = true;
						} else if (frame.getFightPanel().getTextLabel().isVisible()) {
							frame.getFightPanel().getTextLabel().repaint();
							wait = true;
						}
					}
					if (wait) {
						try {
							Thread.sleep(
									(long) Math.max(1000.0 / Main.FPS - (System.currentTimeMillis() - startTime), 0));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		repainter.setDaemon(true);
		repainter.setName("REPAINTER");
		repainter.start();

		Thread songChooser = new Thread(new Runnable() {
			@Override
			public void run() {
				SoundController sc = SoundController.getInstance();
				Random rng = new Random();
				File[] songs = new File(Main.class.getResource("/music/songs/").getFile()).listFiles();
				while (true) {
					if (!sc.isSongRunning()) {
						File song = songs[rng.nextInt(songs.length)];
						sc.playSong(song);
					}
					Thread.yield();
				}
			}
		});
		songChooser.setDaemon(true);
		songChooser.setName("SONGCHOOSER");
		songChooser.start();
		// readDescription();


	}

	public static void readDescription() {
		JsonParser parser = new JsonParser();
		JsonObject currentObject = null;
		BufferedReader currentRoute = null;
		HashMap<String, Point> charLocations = null;
		for (File f : new File(Main.class.getResource("/routes/").getFile()).listFiles()) {
			if (f.getName().endsWith(".route")) {
				try {
					currentRoute = new BufferedReader(new FileReader(f));
					String data = "";
					String currentLine = null;
					while ((currentLine = currentRoute.readLine()) != null) {
						data += currentLine;
					}
					currentObject = parser.parse(data).getAsJsonObject();

					charLocations = new HashMap<String, Point>();
					JsonObject routeDetails = currentObject.get("route").getAsJsonObject();
					for (int x = 0; x < routeDetails.get("width").getAsInt(); x++) {
						for (int y = 0; y < routeDetails.get("height").getAsInt(); y++) {
							if (routeDetails.get(x + "." + y).getAsString().startsWith("C")) {
								routeDetails.addProperty(x + "." + y, "");
							}
						}
					}

					FileWriter fw = new FileWriter(f);
					for (char c : currentObject.toString().toCharArray()) {
						fw.write(c);
						fw.flush();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// BufferedReader reader = null;
		// BufferedReader pokemonData = null;
		// try {
		// pokemonData = new BufferedReader(new FileReader(new
		// File("C:/Users/alexa/Desktop/names.json")));
		// } catch (FileNotFoundException e1) {
		// e1.printStackTrace();
		// }
		// JsonArray allPokemonData = null;
		// try {
		// allPokemonData = parser.parse(pokemonData.readLine()).getAsJsonArray();
		// } catch (JsonSyntaxException e1) {
		// e1.printStackTrace();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// HashSet<Integer> foo = new HashSet<>();
		// for(int i = 1; i < 650; i++) {
		// foo.add(i);
		// }
		// int i = 1;
		// JsonArray newData = new JsonArray();
		// for(int i = 1; i < 650; i++) {
		// System.out.println(i);
		// for (JsonElement element : allPokemonData) {
		// JsonObject currentJson = element.getAsJsonObject();
		// JsonObject currentJson = new JsonObject();
		// if(currentJson.get("is_baby") != null) {
		// newData.add(currentJson);
		// foo.remove(currentJson.get("id").getAsInt());
		// continue;
		// }
		// System.err.println(currentJson.get("id").getAsInt());
		// try {
		// URLConnection connection = new URL("http://www.pokeapi.co/api/v2/pokemon/" +
		// i).openConnection();
		// connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1;
		// WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95
		// Safari/537.11");
		// connection.connect();
		// reader = new BufferedReader(new
		// InputStreamReader(connection.getInputStream()));
		// StringBuffer buffer = new StringBuffer();
		// int read;
		// char[] chars = new char[1024];
		// while ((read = reader.read(chars)) != -1)
		// buffer.append(chars, 0, read);
		// JsonObject currentSpecies = (JsonObject) parser.parse(buffer.toString());
		//
		// currentJson.addProperty("name", currentSpecies.get("name").getAsString());
		// newData.add(currentJson);
		// System.out.println(currentJson);
		// String name = currentSpecies.get("name").getAsString();
		// try {
		// new File("C:/Users/alexa/Desktop/sprites/front/" + i +
		// ".gif").createNewFile();
		// ImageIO.write(ImageIO.read(new URL("http://www.pokestadium.com/sprites/xy/" +
		// name + ".gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/front/" + i + ".gif"));
		// ImageIO.write(ImageIO.read(new
		// URL("http://www.pokestadium.com/sprites/xy/back/" + name + ".gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/back/" + i + "s.gif"));
		// } catch(Exception e) {
		// e.printStackTrace();
		// }
		// try {
		// ImageIO.write(ImageIO.read(new
		// URL("http://www.pokestadium.com/sprites/xy/shiny/" + name + ".gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/front/" + i + "s.gif"));
		// ImageIO.write(ImageIO.read(new
		// URL("http://www.pokestadium.com/sprites/xy/shiny/back/" + name + ".gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/back/" + i + "s.gif"));
		// } catch(Exception e) {
		// e.printStackTrace();
		// }
		// try {
		// ImageIO.write(ImageIO.read(new URL("http://www.pokestadium.com/sprites/xy/" +
		// name + "-female.gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/front/" + i + "f.gif"));
		// ImageIO.write(ImageIO.read(new
		// URL("http://www.pokestadium.com/sprites/xy/back/" + name + "-female.gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/back/" + i + "f.gif"));
		// } catch(Exception e) {
		// e.printStackTrace();
		// }
		// try {
		// ImageIO.write(ImageIO.read(new
		// URL("http://www.pokestadium.com/sprites/xy/shiny/" + name + "-female.gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/front/" + i + "fs.gif"));
		// ImageIO.write(ImageIO.read(new
		// URL("http://www.pokestadium.com/sprites/xy/shiny/back/" + name +
		// "-female.gif")),
		// "gif",
		// new File("C:/Users/alexa/Desktop/sprites/back/" + i + "fs.gif"));
		// } catch(Exception e) {
		// e.printStackTrace();
		// }
		// i++;
		// break;
		//
		// currentJson.addProperty("base_happiness",
		// currentSpecies.get("base_happiness").getAsInt());
		//
		// for(JsonElement j :
		// currentSpecies.get("flavor_text_entries").getAsJsonArray()) {
		// JsonObject current = j.getAsJsonObject();
		// if(current.get("version").getAsJsonObject().get("name").getAsString().equals("alpha-sapphire")
		// &&
		// current.get("language").getAsJsonObject().get("name").getAsString().equals("de"))
		// {
		// currentJson.addProperty("description",
		// current.get("flavor_text").getAsString());
		// }
		// }
		// JsonArray eggGroups = new JsonArray();
		// for(JsonElement j : currentSpecies.get("egg_groups").getAsJsonArray()) {
		// JsonObject current = new JsonObject();
		// current.addProperty("name", j.getAsJsonObject().get("name").getAsString());
		// eggGroups.add(current);
		// }
		// currentJson.add("egg_groups", eggGroups);
		// currentJson.addProperty("has_gender_differences",
		// currentSpecies.get("has_gender_differences").getAsBoolean());
		// currentJson.addProperty("is_baby",
		// currentSpecies.get("is_baby").getAsBoolean());
		// currentJson.addProperty("female_rate",
		// (currentSpecies.get("gender_rate").getAsInt()) / 8.0);

		// while(!chains.isEmpty()) {
		// currentChain = chains.pop();
		// JsonObject currentJson = null;
		// int id =
		// Integer.valueOf(currentChain.get("species").getAsJsonObject().get("url").getAsString().split("/")[6]);
		// if(id > 649) {
		// continue;
		// }
		// currentJson = allPokemonData.get(id - 1).getAsJsonObject();
		// JsonArray evolutionData = new JsonArray();
		// if(currentChain.get("evolves_to").getAsJsonArray().size() > 0) {
		// for(JsonElement je : currentChain.get("evolves_to").getAsJsonArray()) {
		// JsonObject bla = new JsonObject();
		// JsonObject j = je.getAsJsonObject();
		// JsonArray details = j.get("evolution_details").getAsJsonArray();
		// for(int x = 0; x < details.size(); x++) {
		// JsonObject y = details.get(x).getAsJsonObject();
		// for(String s : new String[]{"item", "trigger", "known_move_type",
		// "party_type", "trade_species", "party_species", "held_item", "known_move",
		// "location"}) {
		// if(!(y.get(s) instanceof JsonNull)) {
		// y.addProperty(s, y.get(s).getAsJsonObject().get("name").getAsString());
		// }
		// }
		// }
		// bla.add("details", details);
		// bla.addProperty("id",
		// j.get("species").getAsJsonObject().get("url").getAsString().split("/")[6]);
		// evolutionData.add(bla);
		// chains.push(j);
		// }
		// }
		// currentJson.add("evolution", evolutionData);
		// System.out.println(currentJson);
		// newData.add(currentJson);
		// }

		// String target = null;
		// switch(currentMove.get("target").getAsJsonObject().get("name").getAsString())
		// {
		// case "users-field":
		// case "user-or-ally":
		// case "user":
		// case "user-and-allies":
		// target = Target.USER.name();
		// break;
		// case "opponents-field":
		// case "random-opponent":
		// case "all-other-pokemon":
		// case "selected-pokemon":
		// case "all-opponents":
		// target = Target.OPPONENT.name();
		// break;
		// case "entire-field":
		// case "all-pokemon":
		// target = Target.ALL.name();
		// break;
		// }
		// currentJson.addProperty("target", target);
		// currentJson.addProperty("crit_rate",
		// currentMove.get("meta").getAsJsonObject().get("crit_rate").getAsInt());
		// currentJson.addProperty("name", currentMove.get("name").getAsString());
		// currentJson.add("levels", currentMove.get("levels").getAsJsonArray());
		// String description = "";
		// for(JsonElement d : currentMove.get("descriptions").getAsJsonArray()) {
		// if(d.getAsJsonObject().get("language").getAsJsonObject().get("name").equals("de"))
		// {
		// description = d.getAsJsonObject().get("description").getAsString();
		// break;
		// }
		// }
		// currentJson.addProperty("description", description);
		// currentJson.addProperty("capture_rate",
		// currentMove.get("capture_rate").getAsInt());
		// currentJson.addProperty("hatch_counter",
		// currentMove.get("hatch_counter").getAsInt());
		// currentJson.addProperty("growth_rate",
		// currentMove.get("growth_rate").getAsJsonObject().get("name").getAsString());
		// currentJson.addProperty("base_experience",
		// currentMove.get("base_experience").getAsInt());
		// JsonArray stats = new JsonArray();
		// for(JsonElement curStat : currentMove.get("stats").getAsJsonArray()) {
		// JsonObject stat = curStat.getAsJsonObject();
		// JsonObject newStat = new JsonObject();
		// newStat.addProperty("name",
		// stat.get("stat").getAsJsonObject().get("name").getAsString());
		// newStat.addProperty("effort", stat.get("effort").getAsInt());
		// newStat.addProperty("base_stat", stat.get("base_stat").getAsInt());
		// stats.add(newStat);
		// }
		// currentJson.add("stats", stats);
		// currentJson.addProperty("weight", currentMove.get("weight").getAsInt());
		// currentJson.addProperty("height", currentMove.get("height").getAsString());
		// for(JsonElement curDescription :
		// currentMove.get("flavor_text_entries").getAsJsonArray()) {
		// JsonObject jo = curDescription.getAsJsonObject();
		// if(jo.get("language").getAsJsonObject().get("name").getAsString().equals("de"))
		// {
		// currentJson.addProperty("desc", jo.get("flavor_text").getAsString());
		// break;
		// }
		// }
		// currentJson.addProperty("ailment",
		// currentMove.get("meta").getAsJsonObject().get("ailment").getAsJsonObject().get("name").getAsString());
		// currentJson.addProperty("damage_class",
		// currentMove.get("damage_class").getAsJsonObject().get("name").getAsString());
		// currentJson.addProperty("priority", currentMove.get("priority").getAsInt());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// newData.add(currentJson);
		// i++;
		// }
		// System.out.println(foo);
		// try {
		// FileWriter fw = new FileWriter(new
		// File("C:/Users/alexa/Desktop/names.json"));
		// for(char c : newData.toString().toCharArray()) {
		// fw.write(c);
		// fw.flush();
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	// public void readAllRoutes() {
	// for (File currentFile : folderFiles) {
	// if (currentFile.isFile() && currentFile.getName().endsWith(".txt")) {
	// try {
	// currentReader = new BufferedReader(new FileReader(currentFile));
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// }
	// JsonObject route = parser.parse(currentReader).getAsJsonObject();
	//
	// Route currentRoute = new Route();
	// String routeID = currentFile.getName().split("\\.")[0];
	// String currentLine;
	// try {
	// JsonObject routeDetails = route.get("route").getAsJsonObject();
	// currentRoute.setId(routeDetails.get("id").getAsString());
	// currentRoute.setName(routeDetails.get("name").getAsString());
	// currentRoute.setTerrain(routeDetails.get("terrain").getAsString());
	// currentRoute.setHeight(routeDetails.get("height").getAsInt());
	// currentRoute.setWidth(routeDetails.get("width").getAsInt());
	// warps = new ArrayList<Warp>();
	// characters = new ArrayList<NPC>();
	// for (int y = 0; y < currentRoute.getHeight(); y++) {
	// currentLine = currentReader.readLine();
	// String[] rowElements = currentLine.split(",");
	// for (int x = 0; x < currentRoute.getWidth(); x++) {
	// Entity currentEntity = null;
	// if(routeDetails.get(x + "." + y) == null) {
	// break;
	// }
	// String currentString = routeDetails.get(x + "." + y).getAsString();
	// if (!currentString.startsWith("W") && !currentString.startsWith("C")) {
	// switch (rowElements[x]) {
	// case "T": // Tree
	// currentEntity = new Entity(false, "tree", 0, "grassy");
	// break;
	// case "F": // Free
	// currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
	// break;
	// case "G": // Grass
	// currentEntity = new Entity(true, "grass", Entity.POKEMON_GRASS_RATE,
	// "grassy");
	// break;
	// case "M": // Mauer - House/Center/Market
	// currentEntity = new Entity(false, "free", 0, currentRoute.getTerrain());
	// break;
	// case "P": // Center
	// currentEntity = new Entity(false, "center", 0, currentRoute.getTerrain());
	// break;
	// case "HS":
	// currentEntity = new Entity(false, "house_small", 0,
	// currentRoute.getTerrain());
	// break;
	// case "HL":
	// currentEntity = new Entity(false, "house_large", 0,
	// currentRoute.getTerrain());
	// break;
	// case "A":
	// currentEntity = new Entity(false, "gym", 0, currentRoute.getTerrain());
	// break;
	// case "S": //See
	// currentEntity = new Entity(true, "free", 0, "see");
	// currentEntity.setWater(true);
	// break;
	// case "SA": //Sand
	// currentEntity = new Entity(true, "free", 0, "sandy");
	// break;
	// case "B": //Bridge
	// currentEntity = new Entity(true, "bridge", 0, currentRoute.getTerrain());
	// break;
	// }
	// } else if (currentString.startsWith("W")) {
	// Warp currentWarp = new Warp(currentString, routeID);
	// if (currentString.startsWith("WD")) { // door
	// currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
	// } else {
	// currentEntity = new Entity(true, "warp", 0, currentRoute.getTerrain());
	// }
	// currentEntity.addWarp(currentWarp);
	// warps.add(currentWarp);
	// } else if (currentString.startsWith("C")) {
	// currentEntity = new Entity(true, "free", 0, currentRoute.getTerrain());
	// NPC currentCharacter = new NPC(currentString);
	// currentCharacter.setCurrentPosition(x, y);
	// currentCharacter.setCurrentRoute(currentRoute);
	// characters.add(currentCharacter);
	// }
	// currentRoute.addEntity(x, y, currentEntity);
	// }
	// }
	// JsonArray warpDetails = route.get("warps").getAsJsonArray();
	// for (int y = 0; y < Math.min(warpDetails.size(), warps.size()); y++) {
	// int warpIndex = y;
	// JsonObject currentWarp = warpDetails.get(y).getAsJsonObject();
	// String warpID = currentWarp.get("id").getAsString();
	// if (!warpID.equals(warps.get(warpIndex).getWarpString())) {
	// for (int i = 0; i < warps.size(); i++) {
	// warpIndex = i;
	// if (warpID.equals(warps.get(warpIndex).getWarpString())) {
	// break;
	// }
	// }
	// }
	// warps.get(warpIndex).setNewRoute(currentWarp.get("new_route").getAsString());
	// warps.get(warpIndex).setNewPosition(
	// new Point(currentWarp.get("new_x").getAsInt(),
	// currentWarp.get("new_y").getAsInt()));
	//
	// }
	// JsonArray characterDetails = route.get("characters").getAsJsonArray();
	// for (int y = 0; y < Math.min(characterDetails.size(), characters.size());
	// y++) {
	// JsonObject currentChar = characterDetails.get(y).getAsJsonObject();
	// int characterIndex = y;
	// String characterID = currentChar.get("id").getAsString();
	// if (!characterID.equals(characters.get(characterIndex).getID())) {
	// for (int i = 0; i < characters.size(); i++) {
	// characterIndex = i;
	// if (characterID.equals(characters.get(characterIndex).getID())) {
	// break;
	// }
	// }
	// }
	// NPC currentCharacter = characters.get(characterIndex);
	// currentCharacter.setID(characterID);
	// currentCharacter.setCharacterImage(currentChar.get("char_sprite").getAsString(),
	// currentChar.get("direction").getAsString().toLowerCase());
	// currentCharacter.setName(currentChar.get("name").getAsString());
	// currentCharacter.setTrainer(currentChar.get("is_trainer").getAsString());
	// if (currentChar.get("sprite") != null) {
	// currentRoute.getEntities()[currentCharacter.getCurrentPosition().y]
	// [currentCharacter.getCurrentPosition().x].setSprite(currentChar.get("sprite").getAsString());
	// }
	// if(currentChar.get("surfing") != null) {
	// currentCharacter.setSurfing(currentChar.get("surfing").getAsBoolean());
	// if(currentCharacter.isSurfing()) {
	// currentRoute.getEntities()[currentCharacter.getCurrentPosition().y]
	// [currentCharacter.getCurrentPosition().x].setTerrain("see");;
	// }
	// }
	// if(currentCharacter.isTrainer()) {
	// currentCharacter.importTeam();
	// }
	// currentCharacter.importDialogue();
	// currentRoute.addCharacterToEntity(currentCharacter.getCurrentPosition().x,
	// currentCharacter.getCurrentPosition().y, currentCharacter);
	// }
	// JsonArray encounterDetails = route.get("encounters").getAsJsonArray();
	// for(JsonElement j : encounterDetails) {
	// JsonObject currentEncounter = j.getAsJsonObject();
	// Pokemon encounter = new Pokemon(currentEncounter.get("id").getAsInt());
	// encounter.getStats().generateStats(currentEncounter.get("level").getAsShort());
	// currentRoute.addPokemon(encounter);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// currentRoute.createMap();
	// loadedRoutes.put(routeID, currentRoute);
	// originalRoutes.put(routeID, currentRoute.copy());
	// warps.clear();
	// }
	// }
	// }

}
