package de.alexanderciupka.sarahspiel.map;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import de.alexanderciupka.sarahspiel.pokemon.NPC;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

public class Route {

	private String id;
	private String name;
	private int width;
	private int height;
	private Entity[][] entities;
	private ArrayList<Pokemon> pokemonPool;
	private ArrayList<NPC> characters;
	private BufferedImage map;
	private boolean moving;

	private String terrainName;

	public Route() {
		this.pokemonPool = new ArrayList<Pokemon>();
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

	public void addCharacterToEntity(int x, int y, NPC character) {
		entities[y][x].addCharacter(character);
		this.characters.add(character);
	}

	public void removeCharacter(String characterId) {
		int toRemove = 0;
		boolean breaked = false;
		for(int i = 0; i < this.height * this.width; i++) {
			int x = i % width;
			int y = i / width;
			for(int j = 0; j < this.entities[y][x].getCharacters().size(); j++) {
				if(this.entities[y][x].getCharacters().get(j).getID().equals(characterId)) {
					this.entities[y][x].removeCharacter(characterId);
					breaked = true;
					break;
				}
				toRemove++;
			}
			if(breaked) break;
		}
		characters.remove(toRemove);
	}

	private void createEntities() {
		if (entities == null) {
			this.entities = new Entity[height][width];
		}
	}

	public void addPokemon(Pokemon newPokemon) {
		this.pokemonPool.add(newPokemon);
	}

	public ArrayList<Pokemon> getPokemonPool() {
		return this.pokemonPool;
	}

	public ArrayList<NPC> getCharacters() {
		return this.characters;
	}

	public Pokemon getEncounter() {
		Random rng = new Random();
		return pokemonPool.get(rng.nextInt(pokemonPool.size()));
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
					g.drawImage(entities[y][x].getSprite(), (int) (entities[y][x].getExactX() * 70), (int) (entities[y][x].getExactY() * 70), null);
					if(entities[y][x].hasCharacter()) {
						for(NPC npc : entities[y][x].getCharacters()) {
							g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() * 70), (int) (npc.getExactY() * 70), null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		saveMap();
	}

	public void updateMap(Point... updatePoint) {
		if(map != null) {
			Graphics g = map.getGraphics();
			for(Point p : updatePoint) {
				try {
					g.drawImage(entities[p.y][p.x].getTerrain(), p.x * 70, p.y * 70, null);
					g.drawImage(entities[p.y][p.x].getSprite(),(int) (entities[p.y][p.x].getExactX() * 70), (int) (entities[p.y][p.x].getExactY() * 70), null);
					if(entities[p.y][p.x].hasCharacter()) {
						for(NPC npc : entities[p.y][p.x].getCharacters()) {
							g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() * 70), (int) (npc.getExactY() * 70), null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			for(int x = 0; x < width; x++) {
//				for(int y = 0; y < height; y++) {
//					if(this.entities[y][x].getSpriteName().startsWith("house") || this.entities[y][x].getSpriteName().startsWith("pokecenter")) {
//						g.drawImage(entities[y][x].getTerrain(), x * 70, y * 70, null);
//						g.drawImage(entities[y][x].getSprite(),(int) (entities[y][x].getExactX() * 70), (int) (entities[y][x].getExactY() * 70), null);
//						if(entities[y][x].hasCharacter()) {
//							for(NPC npc : entities[y][x].getCharacters()) {
//								g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() * 70), (int) (npc.getExactY() * 70), null);
//							}
//						}
//					}
//					for(NPC npc : entities[y][x].getCharacters()) {
//						g.drawImage(npc.getCharacterImage(), (int) (npc.getExactX() * 70), (int) (npc.getExactY() * 70), null);
//
//					}
//				}
//			}
//			saveMap();
		}

	}

	private void saveMap() {
		try {
			ImageIO.write(map, "png", new File("./res/routes/" + this.id + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public Route copy() {
//		Route result = new Route();
//		result.setId(this.id);
//		result.setName(this.name);
//		result.setWidth(this.width);
//		result.setHeight(this.height);
//		result.createEntities();
//		for(int x = 0; x < this.width; x++) {
//			for(int y = 0; y < this.height; y++) {
//				result.addEntity(x, y, this.getEntities()[y][x].copy());
//			}
//		}
//		result.pokemonPool = (ArrayList<Pokemon>) pokemonPool.clone();
//		result.characters = (ArrayList<NPC>) characters.clone();
//		result.map = deepCopy(map);
//	}

//	static BufferedImage deepCopy(BufferedImage bi) {
//		 ColorModel cm = bi.getColorModel();
//		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
//		 WritableRaster raster = bi.copyData(null);
//		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
//		}

	public BufferedImage getMap() {
		return this.map;
	}

	public NPC getNPC(String id) {
		for(int i = 0; i < characters.size(); i++) {
			if(characters.get(i).getID().equals(id)) {
				return characters.get(i);
			}
		}
		return null;
	}

	public NPC getNPCByName(String name) {
		for(int i = 0; i < characters.size(); i++) {
			if(characters.get(i).getName() != null && name.toLowerCase().equals(characters.get(i).getName().toLowerCase())) {
				return characters.get(i);
			}
		}
		return null;
	}
}
