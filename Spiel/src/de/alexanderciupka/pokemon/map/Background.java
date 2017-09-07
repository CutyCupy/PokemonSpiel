package de.alexanderciupka.pokemon.map;

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
	private Camera cam;

	public Background(Route currentRoute) {
		this.cam = new Camera(0, 0);
		background = currentRoute.getMap();
		this.currentRoute = currentRoute;
		rng = new Random();
		try {
			this.terrain = ImageIO.read(new File(this.getClass().getResource("/routes/terrain/" + currentRoute.getTerrainName()  + ".png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Camera getCamera() {
		return this.cam;
	}

	public Pokemon chooseEncounter() {
		return currentRoute.getPokemonPool().getEncounter();
	}

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
