package de.alexanderciupka.sarahspiel.map;

import java.awt.Image;
import java.awt.Point;
import java.util.Random;

import javax.swing.ImageIcon;

import de.alexanderciupka.sarahspiel.pokemon.Character;
import de.alexanderciupka.sarahspiel.pokemon.NPC;

public class Entity {

	private boolean accessible;
	private Image sprite;
	private float pokemonRate;
	private Warp warp;
	private NPC character;
	private Image terrain;
	private boolean hasCharacter;

	private boolean water;
	private boolean pc;

	public static final float POKEMON_GRASS_RATE = 0.1f;

	private Random rng;
	private GameController gController;

	public Entity(boolean accessible, String spriteName, float pokemonRate, String terrainName) {
		this.accessible = accessible;
		gController = GameController.getInstance();
		try {
//			this.sprite = new ImageIcon(this.getClass().getResource("/routes/entities/" + spriteName + ".png"))
//					.getImage();
			setSprite(spriteName);
			setTerrain(terrainName);
//			this.terrain = new ImageIcon(this.getClass().getResource("/routes/terrain/" + terrainName + ".png"))
//					.getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.hasCharacter = false;
		this.pokemonRate = pokemonRate;
		rng = new Random();
	}

	public boolean isAccessible(Character c) {
		return ((this.accessible && !this.isWater()) || (this.isWater() && c.isSurfing() && this.accessible)) && !this.hasCharacter();
	}

	public void setSprite(String spriteName) {
		if(spriteName.equals("grass")) {
			this.terrain = new ImageIcon(this.getClass().getResource("/routes/terrain/grassy.png")).getImage();
		}
		pc = spriteName.equals("pc");
		this.sprite = new ImageIcon(this.getClass().getResource("/routes/entities/" + spriteName + ".png")).getImage();
	}

	public void setTerrain(String terrainName) {
		try {
			this.terrain = new ImageIcon(this.getClass().getResource("/routes/terrain/" + terrainName + ".png"))
					.getImage();
			this.setWater(terrainName.equals("see"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getSprite() {
		return this.sprite;
	}

	public Image getCharacterSprite() {
		return this.character.getCharacterImage();
	}

	public Image getTerrain() {
		return this.terrain;
	}

	public void addWarp(Warp warp) {
		this.warp = warp;
	}

	public void addCharacter(NPC character) {
		this.hasCharacter = true;
		this.character = character;
	}

	public boolean removeCharacter() {
		if (hasCharacter) {
			this.character = null;
			this.hasCharacter = false;
			return true;
		}
		return false;
	}

	public NPC getCharacter() {
		return this.character;
	}

	public boolean checkPokemon() {
		if (pokemonRate > 0 && gController.getCurrentBackground().getCurrentRoute().getPokemonPool().size() != 0) {
			if (rng.nextFloat() <= pokemonRate) {
				return true;
			}
		}
		return false;
	}

	public boolean startWarp() {
		if (warp != null) {
			if(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()) == null) {
				return false;
			}
			if(warp.getNewRoute().toLowerCase().equals("pokemon_center")) {
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[4][2].getWarp().setNewPosition(gController.getMainCharacter().getCurrentPosition().getLocation());
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[4][2].getWarp().setNewRoute(gController.getMainCharacter().getCurrentRoute().getId());
			}
			gController.resetCharacterPositions();
			gController.getMainCharacter().setCurrentPosition(warp.getNewPosition());
			gController.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()));
			gController.repaint();
			if(gController.getMainCharacter().getCurrentRoute().getId().equals("haus_von_alex")) {
				Entity house = gController.getRouteAnalyzer().getRouteById("bruchkoebel").getEntities()[7][42];
				if(house.getWarp() == null) {
					Warp houseWarp = new Warp("W100", "bruchkoebel");
					houseWarp.setNewPosition(new Point(3, 4));
					houseWarp.setNewRoute("verlassenes_haus");
					house.addWarp(houseWarp);
					house.accessible = true;
					gController.getGameFrame().addDialogue("Irgendwie scheint Alex nicht hier zu sein... Wo steckt er denn?! "
							+ "Huch - was liegt denn da? Eine Notiz: WICHTIG! KOMME SOFORT NACH BRUCHKOEBEL ZUM VERLASSENEN HAUS!");
					gController.waitDialogue();
				}
			} else if(gController.getMainCharacter().getCurrentRoute().getId().equals("verlassenes_haus")) {
				for(Character c : gController.getMainCharacter().getCurrentRoute().getCharacters()) {
					if(c.getName().equals("Alex")) {
						if(!c.isDefeated()) {
							gController.getGameFrame().addDialogue("Alle: Happy Birthday to You! "
									+ "Happy Birthday to You! Happy Birthday liebe SARAH! Happy Birthday to You!");
							gController.getGameFrame().addDialogue("Alex: Alles Gute zum Geburtstag Schatz!");
							gController.waitDialogue();
						}
						break;
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean hasCharacter() {
		return this.hasCharacter;
	}

	public Warp getWarp() {
		return this.warp;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	public void setWater(boolean water) {
		this.water = water;
	}

	public boolean isWater() {
		return this.water;
	}

	public boolean isPC() {
		return pc;
	}

//	public Entity copy() {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
