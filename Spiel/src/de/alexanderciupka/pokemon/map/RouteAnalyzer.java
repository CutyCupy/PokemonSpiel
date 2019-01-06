package de.alexanderciupka.pokemon.map;

import java.awt.Image;
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
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.entities.types.Entity;
import de.alexanderciupka.pokemon.map.entities.types.HatchEntity;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.painting.Painting;

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
	private HashMap<Integer, BufferedImage> items;
	private HashMap<String, BufferedImage> sprites;
	private HashMap<String, BufferedImage> terrains;
	private HashMap<Integer, BufferedImage> pokeballs;
	private HashMap<String, BufferedImage> animations;

	ArrayList<HatchEntity> hatches;

	private JsonParser parser;

	public RouteAnalyzer() {
		this.gController = GameController.getInstance();
		this.folderFiles = ROUTE_FOLDER.listFiles();
		this.loadedRoutes = new HashMap<String, Route>();
		this.originalRoutes = new HashMap<String, Route>();
		this.logos = new HashMap<String, BufferedImage>();
		this.items = new HashMap<Integer, BufferedImage>();
		this.sprites = new HashMap<String, BufferedImage>();
		this.terrains = new HashMap<String, BufferedImage>();
		this.pokeballs = new HashMap<Integer, BufferedImage>();
		this.animations = new HashMap<String, BufferedImage>();
		this.parser = new JsonParser();
	}

	public void init() {
		this.readAllSprites();
		this.readAllTerrains();
		this.readAllLogos();
		this.readAllItems();
		this.readAllRoutes();
		this.readAllAnimations();

	}

	private void readAllSprites() {
		System.out.println("sprites");
		File[] sprites = SPRITE_FOLDER.listFiles();
		for (File currentFile : sprites) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.sprites.put(currentFile.getName().split("\\.")[0].toLowerCase(),
							Painting.toBufferedImage(ImageIO.read(currentFile)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Set<String> getSpriteNames() {
		return this.sprites.keySet();
	}

	public BufferedImage getSpriteByName(String name) {
		return this.sprites.get(name);
	}

	public Set<String> getTerrainNames() {
		return this.terrains.keySet();
	}

	private void readAllTerrains() {
		File[] terrains = TERRAIN_FOLDER.listFiles();
		for (File currentFile : terrains) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.terrains.put(currentFile.getName().split("\\.")[0].toLowerCase(),
							Painting.toBufferedImage(ImageIO.read(currentFile)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BufferedImage getTerrainByName(String name) {
		return this.terrains.get(name);
	}

	private void readAllItems() {
		File[] items = ITEM_FOLDER.listFiles();
		for (File currentFile : items) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".png")) {
				try {
					this.items.put(Integer.parseInt(currentFile.getName().split("\\.")[0]), ImageIO.read(currentFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public BufferedImage getItemImage(Integer i) {
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
		this.hatches = new ArrayList<>();
		for (File currentFile : this.folderFiles) {
			if (currentFile.isFile() && currentFile.getName().endsWith(".route")) {
				this.readRoute(currentFile);
			}
		}
	}

	public void readRoute(String name) {
		this.readRoute(new File(this.getClass().getResource("/routes/" + name + ".route").getFile()));
	}

	public void readRoute(File file) {
		for (int counter = 0; counter < 2; counter++) {
			try {
				this.currentReader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				return;
			}
			Route currentRoute = new Route();
			String routeID = file.getName().split("\\.")[0];
			try {
				JsonObject route = this.parser.parse(this.currentReader).getAsJsonObject();
				currentRoute.setId(routeID);
				currentRoute.importSaveData(route);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (counter == 0) {
				currentRoute.createMap();
				this.loadedRoutes.put(routeID, currentRoute);
			} else {
				this.originalRoutes.put(routeID, currentRoute);
			}
		}
	}

	public Route getRouteById(String id, boolean original) {
		if (original) {
			return this.originalRoutes.get(id);
		}
		return this.loadedRoutes.get(id);
	}

	public Route getRouteById(String id) {
		return getRouteById(id, false);
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
			data.add("main_character", this.gController.getMainCharacter().getSaveData());
			JsonArray charData = new JsonArray();
			for (Route currentRoute : this.loadedRoutes.values()) {
				for (NPC currentCharacter : currentRoute.getCharacters()) {
					charData.add(currentCharacter.getSaveData());
				}
			}

			JsonArray routeData = new JsonArray();

			for (String s : this.loadedRoutes.keySet()) {
				// if
				// (!this.loadedRoutes.get(s).equals(this.originalRoutes.get(s),
				// false)) {
				routeData.add(this.loadedRoutes.get(s).getSaveData());
				// }
			}

			data.add("characters", charData);
			data.add("routes", routeData);
			data.add("cam", this.gController.getCurrentBackground().getCamera().getSaveData());

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

			System.out.println(
					this.gController.getMainCharacter().importSaveData(data.get("main_character").getAsJsonObject()));
			for (Route currentRoute : this.loadedRoutes.values()) {
				currentRoute.clearCharacters();
			}

			for (JsonElement current : routes) {
				JsonObject currentJson = current.getAsJsonObject();
				Route route = this.getRouteById(currentJson.get("id").getAsString(), true);
				route.importSaveData(currentJson);
			}

			for (int i = 1; i < characters.size(); i++) {
				JsonObject currentJson = characters.get(i).getAsJsonObject();
				NPC character = new NPC();
				if (character.importSaveData(currentJson)) {
					this.loadedRoutes.get(character.getCurrentRoute().getId()).addCharacter(character);
				}
			}

			for (String s : this.loadedRoutes.keySet()) {
				this.loadedRoutes.get(s).createMap();
			}

			this.gController.setCurrentRoute(this.gController.getMainCharacter().getCurrentRoute());

			this.gController.getCurrentBackground().getCamera().importSaveData(data.get("cam").getAsJsonObject());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, Route> getOriginalRoutes() {
		return this.originalRoutes;
	}

	public Image getLogoByName(String logo) {
		return this.logos.get(logo);
	}

	public HashMap<Integer, HashSet<String>> getAllPokemonLocations() {
		HashMap<Integer, HashSet<String>> allLocations = new HashMap<>();
		for (int i = 1; i < 650; i++) {
			allLocations.put(i, new HashSet<String>());
		}
		for (String s : this.loadedRoutes.keySet()) {
			Route r = this.loadedRoutes.get(s);
			ArrayList<Integer> checkedPools = new ArrayList<Integer>();
			for (int x = 0; x < r.getWidth(); x++) {
				for (int y = 0; y < r.getHeight(); y++) {
					Entity e = r.getEntity(x, y);
					if (!checkedPools.contains(e.getPokemonPool())) {
						checkedPools.add(e.getPokemonPool());
						for (SimpleEntry<Integer, Short> id : e.getRoute().getPoolById(e.getPokemonPool())
								.getPokemonPool()) {
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

	public static String getWrongMember(JsonObject data, String[] members) {
		for (String member : members) {
			if (!data.has(member)) {
				return member;
			}
		}
		return null;
	}

}
