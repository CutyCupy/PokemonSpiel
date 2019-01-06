package de.alexanderciupka.pokemonspiel.routecreation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFileChooser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.RouteAnalyzer;
import de.alexanderciupka.pokemon.map.entities.types.Entity;
import de.alexanderciupka.pokemonspiel.routecreation.frames.CharacterFrame;
import de.alexanderciupka.pokemonspiel.routecreation.frames.EntitiesFrame;
import de.alexanderciupka.pokemonspiel.routecreation.frames.OtherRoutesFrame;
import de.alexanderciupka.pokemonspiel.routecreation.frames.RouteDataFrame;
import de.alexanderciupka.pokemonspiel.routecreation.frames.SpriteFrame;
import de.alexanderciupka.pokemonspiel.routecreation.frames.TerrainFrame;

public class RouteCreatorController {

	private static RouteCreatorController instance;

	private EntitiesFrame entities;
	private RouteDataFrame routeData;
	private CharacterFrame characters;
	private SpriteFrame sprites;
	private TerrainFrame terrains;
	private OtherRoutesFrame otherRoutes;

	private JsonObject data;

	private BufferedImage map;

	private ArrayList<Point> updatedPoints;

	private RouteCreatorController() {

	}

	public static RouteCreatorController getInstance() {
		if (instance == null) {
			instance = new RouteCreatorController();
			instance.start();
		}
		return instance;
	}

	private void start() {
		initializeEntities();

		this.updatedPoints = new ArrayList<>();

		sprites = new SpriteFrame();
		terrains = new TerrainFrame();
		characters = new CharacterFrame();
		otherRoutes = new OtherRoutesFrame();
		routeData = new RouteDataFrame(this.data);
		entities = new EntitiesFrame();

		for (int x = 0; x < routeData.getRouteSize().getWidth(); x++) {
			for (int y = 0; y < routeData.getRouteSize().getHeight(); y++) {
				this.setEntity(x, y,
						this.data.getAsJsonObject("route").getAsJsonObject("entities").has(x + "." + y) ? this.data
								.getAsJsonObject("route").getAsJsonObject("entities").getAsJsonObject(x + "." + y)
								: new JsonObject());
			}
		}

	}

	public EntitiesFrame getEntities() {
		return entities;
	}

	public SpriteFrame getSprites() {
		return sprites;
	}

	public TerrainFrame getTerrains() {
		return terrains;
	}

	public CharacterFrame getCharacters() {
		return characters;
	}

	public void initializeEntities() {
		if (this.data == null) {
			this.data = new JsonObject();
			this.data.addProperty("id", "");
			this.data.add("events", new JsonArray());
			this.data.add("characters", new JsonArray());
			this.data.add("encounters", new JsonObject());

			JsonObject route = new JsonObject();
			route.addProperty("name", "");

			JsonObject properties = new JsonObject();
			properties.addProperty("dark", false);
			properties.addProperty("type", "ARENA");
			properties.addProperty("rain", "CLEAR");
			properties.addProperty("snow", "CLEAR");
			properties.addProperty("fog", "CLEAR");

			route.add("properties", properties);
			route.add("entities", new JsonObject());

			this.data.add("route", route);
		}
	}

	public void clearEntities() {
		this.data = null;
		initializeEntities();
	}

	public void setEntity(int x, int y, JsonObject entity) {
		this.data.getAsJsonObject("route").getAsJsonObject("entities").add(x + "." + y, entity);
		updatedPoints.add(new Point(x, y));
	}

	public JsonObject getEntity(int x, int y) {
		initializeEntities();
		return this.data.getAsJsonObject("route").getAsJsonObject("entities").has(x + "." + y)
				? this.data.getAsJsonObject("route").getAsJsonObject("entities").getAsJsonObject(x + "." + y)
				: new JsonObject();
	}

	public BufferedImage getMap() {
		if (this.map == null) {
			this.map = new BufferedImage((int) routeData.getRouteSize().getWidth() * GameFrame.GRID_SIZE,
					(int) routeData.getRouteSize().getHeight() * GameFrame.GRID_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						Graphics g = map.getGraphics();
						for (; !updatedPoints.isEmpty();) {
							Point p = updatedPoints.remove(0);
							if (p == null) {
								continue;
							}
							if (getEntity(p.x, p.y).entrySet().size() > 0) {
								Entity currentEntity = null;
								try {
									currentEntity = (Entity) Class
											.forName("de.alexanderciupka.pokemon.map.entities.types."
													+ getEntity(p.x, p.y).get("type").getAsString())
											.newInstance();
								} catch (Exception e1) {
									e1.printStackTrace();
									currentEntity = new Entity();
								}
								try {
									currentEntity.importSaveData(getEntity(p.x, p.y));
									currentEntity.setX(p.x);
									currentEntity.setY(p.y);
								} catch (Exception e) {
									e.printStackTrace();
									continue;
								}
								g.drawImage(currentEntity.getTerrain(), p.x * GameFrame.GRID_SIZE,
										p.y * GameFrame.GRID_SIZE, null);
								g.drawImage(currentEntity.getSprite(), p.x * GameFrame.GRID_SIZE,
										p.y * GameFrame.GRID_SIZE, null);
								for (int x = 0; x < (Math.max(currentEntity.getSprite().getWidth(null),
										currentEntity.getTerrain().getWidth(null))) / GameFrame.GRID_SIZE; x++) {
									for (int y = 0; y < (Math.max(currentEntity.getSprite().getHeight(null),
											currentEntity.getTerrain().getHeight(null))) / GameFrame.GRID_SIZE; y++) {
										if (entities != null) {
											entities.updateBackground(p.x + x, p.y + y);
										}
									}
								}
								// for
								// (de.alexanderciupka.pokemon.characters.Character
								// c :
								// chars) {
								// int deltaX =
								// c.getCharacterImage().getWidth(null) /
								// GameFrame.GRID_SIZE;
								// int deltaY =
								// c.getCharacterImage().getHeight(null) /
								// GameFrame.GRID_SIZE;
								// if (c.getCurrentPosition().p.x >= p.x -
								// deltaX &&
								// c.getCurrentPosition().p.x <= p.x + deltaX
								// && c.getCurrentPosition().p.y >= p.y - deltaY
								// &&
								// c.getCurrentPosition().p.y <= p.y + deltaY
								// || c.getOldPosition().p.x >= p.x - deltaX &&
								// c.getOldPosition().p.x <= p.x + deltaX
								// && c.getOldPosition().p.y >= p.y - deltaY &&
								// c.getOldPosition().p.y <= p.y + deltaY) {
								// double xPos = (c.getExactX() - startX) *
								// GameFrame.GRID_SIZE
								// - (c.getCharacterImage().getWidth(null) -
								// GameFrame.GRID_SIZE) / 2.0;
								// double yPos = (c.getExactY() - startY) *
								// GameFrame.GRID_SIZE
								// - (c.getCharacterImage().getHeight(null)
								// -
								// GameFrame.GRID_SIZE);
								// g.drawImage(c.getCharacterImage(), (int)
								// Math.round(xPos), (int) Math.round(yPos),
								// null);
								// }
							} else {
								g.setColor(entities.getBackground());
								g.fillRect(p.x * GameFrame.GRID_SIZE, p.y * GameFrame.GRID_SIZE, GameFrame.GRID_SIZE,
										GameFrame.GRID_SIZE);
							}
							if (entities != null) {
								entities.updateBackground(p.x, p.y);
							}
						}
						for (int i = 0; i < routeData.getRouteSize().getWidth(); i++) {
							for (int j = 0; j < routeData.getRouteSize().getHeight(); j++) {
								if (getEntity(i, j) != null) {
									Entity currentEntity = null;
									try {
										currentEntity = (Entity) Class
												.forName("de.alexanderciupka.pokemon.map.entities.types."
														+ getEntity(i, j).get("type").getAsString())
												.newInstance();
									} catch (Exception e1) {
										currentEntity = new Entity();
									}
									try {
										currentEntity.importSaveData(getEntity(i, j));
									} catch (Exception e) {
										continue;
									}
									if (currentEntity.getSprite().getWidth(null) > GameFrame.GRID_SIZE
											|| currentEntity.getSprite().getHeight(null) > GameFrame.GRID_SIZE) {
										g.drawImage(currentEntity.getTerrain(), i * GameFrame.GRID_SIZE,
												j * GameFrame.GRID_SIZE, null);
										g.drawImage(currentEntity.getSprite(), i * GameFrame.GRID_SIZE,
												j * GameFrame.GRID_SIZE, null);
										for (int x = 0; x < (Math.max(currentEntity.getSprite().getWidth(null),
												currentEntity.getTerrain().getWidth(null)))
												/ GameFrame.GRID_SIZE; x++) {
											for (int y = 0; y < (Math.max(currentEntity.getSprite().getHeight(null),
													currentEntity.getTerrain().getHeight(null)))
													/ GameFrame.GRID_SIZE; y++) {
												if (entities != null) {
													entities.updateBackground(i + x, j + y);
												}
											}
										}
									}
								}
							}
						}
						while (updatedPoints.isEmpty()) {
							Thread.yield();
						}
					}
				}
			}).start();

			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			// while (true) {
			// long start = System.currentTimeMillis();
			// for (int x = 0; x < routeData.getRouteSize().getWidth(); x++) {
			// for (int y = 0; y < routeData.getRouteSize().getHeight(); y++) {
			// if (entities != null) {
			// entities.updateBackground(x, y);
			// }
			// }
			// }
			// System.out.println("finished");
			// try {
			// Thread.sleep((long) Math.max((1000 / Main.FPS) -
			// (System.currentTimeMillis() - start), 0));
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			// }).start();
		}
		return this.map;
	}

	public void show(boolean b) {
		sprites.setVisible(b);
		terrains.setVisible(b);
		characters.setVisible(b);
		otherRoutes.setVisible(b);
		routeData.setVisible(b);
		entities.setVisible(b);
	}

	public JsonObject getData() {
		return this.data;
	}

	public void setData(JsonObject data) {
		this.data = data;
	}

	public void save() {
		JFileChooser chooser = new JFileChooser(RouteAnalyzer.ROUTE_FOLDER);
		chooser.setCurrentDirectory(new java.io.File("./res/routes/"));
	    chooser.setDialogTitle("Ordner für die Datei auswählen!");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showDialog(this.entities, "Ordner auswählen") == JFileChooser.APPROVE_OPTION) {
			File selectedFile = new File(chooser.getSelectedFile().getPath() + "/" + this.data.get("id").getAsString() + ".route");
			JsonObject data = this.getData();
			Set<Entry<String, JsonElement>> entities = data.getAsJsonObject("route").getAsJsonObject("entities")
					.entrySet();
			for (Entry<String, JsonElement> entity : entities) {
				System.out.println(entity);
				if (entity.getValue().getAsJsonObject().entrySet().size() == 0
						|| Integer.parseInt(entity.getKey().split("\\.")[0]) >= this.routeData.getRouteSize().getWidth()
						|| Integer.parseInt(entity.getKey().split("\\.")[1]) >= this.routeData.getRouteSize()
								.getHeight()) {
					data.getAsJsonObject("route").getAsJsonObject("entities").remove(entity.getKey());
				}
			}
			
			System.out.println(data);

			try {
				FileWriter writer = new FileWriter(selectedFile);
				for (char c : data.toString().toCharArray()) {
					writer.write(c);
					writer.flush();
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.entities.dispose();
			this.otherRoutes.dispose();
			this.routeData.dispose();
			this.sprites.dispose();
			this.terrains.dispose();
		}
	}

}
