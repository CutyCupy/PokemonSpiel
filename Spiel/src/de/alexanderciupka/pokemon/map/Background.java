package de.alexanderciupka.pokemon.map;

import java.awt.image.BufferedImage;
import java.util.Random;

import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class Background {

	private BufferedImage background;
	private Route currentRoute;
	private Random rng;
	private Camera cam;

	public Background(Route currentRoute) {
		this.cam = new Camera(0, 0);
		background = currentRoute.getMap();
		this.currentRoute = currentRoute;
		rng = new Random();
	}

	public Camera getCamera() {
		return this.cam;
	}

	public Pokemon chooseEncounter() {
		return currentRoute
				.getPoolById(currentRoute
						.getEntity(GameController.getInstance().getMainCharacter().getCurrentPosition().x,
								GameController.getInstance().getMainCharacter().getCurrentPosition().y)
						.getPokemonPool())
				.getEncounter();
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
}
