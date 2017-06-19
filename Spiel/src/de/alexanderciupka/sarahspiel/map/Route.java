package de.alexanderciupka.sarahspiel.map;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import de.alexanderciupka.sarahspiel.pokemon.Character;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

public class Route {

	private String id;
	private String name;
	private int width;
	private int height;
	private Entity[][] entities;
	private ArrayList<Pokemon> pokemonPool;
	private ArrayList<Character> characters;
	private BufferedImage map;
	private String terrainName;

	public Route() {
		this.pokemonPool = new ArrayList<Pokemon>();
		this.characters = new ArrayList<Character>();
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

	public String getTerrain() {
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

	public void addCharacterToEntity(int x, int y, Character character) {
		entities[y][x].addCharacter(character);
		this.characters.add(character);
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
	
	public ArrayList<Character> getCharacters() {
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
					g.drawImage(entities[y][x].getSprite(), x * 70, y * 70, null);
					if(entities[y][x].hasCharacter()) {
						g.drawImage(entities[y][x].getCharacterSprite(), x * 70, y * 70, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
//		saveMap();
	}
	
	public void updateMap(Point... updatePoint) {
		if(map != null) {
			Graphics g = map.getGraphics();
			for(Point p : updatePoint) {
				try {
					g.drawImage(entities[p.y][p.x].getTerrain(), p.x * 70, p.y * 70, null);
					g.drawImage(entities[p.y][p.x].getSprite(), p.x * 70, p.y * 70, null);
					if(entities[p.y][p.x].hasCharacter()) {
						g.drawImage(entities[p.y][p.x].getCharacterSprite(), (int) (entities[p.y][p.x].getCharacter().getExactX() * 70), (int) (entities[p.y][p.x].getCharacter().getExactY() * 70), null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
	
	public BufferedImage getMap() {
		return this.map;
	}

}
