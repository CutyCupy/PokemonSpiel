package de.alexanderciupka.pokemon.map;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class Background {

	private BufferedImage background;
	private Route currentRoute;
	private Random rng;
	private BufferedImage terrain;

	public Background(Route currentRoute) {
		background = currentRoute.getMap();
		this.currentRoute = currentRoute;
		rng = new Random();
		try {
			this.terrain = ImageIO.read(new File(this.getClass().getResource("/routes/terrain/" + currentRoute.getTerrainName()  + ".png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pokemon chooseEncounter() {
		return currentRoute.getPokemonPool().get(rng.nextInt(currentRoute.getPokemonPool().size()));
	}

	public boolean checkEncounter(Point point) {
		return currentRoute.getEntities()[point.y][point.x].checkPokemon();
	}

//	public boolean checkPositionAccessible(Point point, de.alexanderciupka.sarahspiel.pokemon.Character character) {
//		if(point.y >= 0 && point.x >= 0 && point.y < currentRoute.getHeight() && point.x < currentRoute.getWidth()) {
//			character.setSurfing(currentRoute.getEntities()[point.y][point.x].isWater());
//			return currentRoute.getEntities()[point.y][point.x].isAccessible(character);
//		}
//		return false;
//	}

	public int getHeight() {
		return background.getHeight();
	}

	public int getWidth() {
		return background.getWidth();
	}

	public BufferedImage getBackground() {
		return this.background;
	}

	public Route getCurrentRoute() {
		return this.currentRoute;
	}

	public void setCurrentRoute(Route route) {
		background = route.getMap();
		this.currentRoute = route;
	}

	public BufferedImage getCurrentTerrain() {
		return this.terrain;
	}
}
