package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import de.alexanderciupka.pokemon.gui.GameFrame;

public class Raindrop {

	private double x;
	private double y;
	private Dimension size;

	private double speed;

	private static final double WIND = .5;

	public static Color COLOR = new Color(153, 204, 255, 148);

	private Random rng;

	public Raindrop() {
		rng = new Random();
		reset();
		this.y = rng.nextInt(GameFrame.FRAME_SIZE);
	}

	public void reset() {
		this.x = rng.nextInt(GameFrame.FRAME_SIZE);
		this.y = rng.nextInt(50) - 55;
		this.size = new Dimension(2, rng.nextInt(10) + 5);
		this.speed = (rng.nextDouble() + 0.5) * 3;
	}

	public void fall() {
		this.y += speed;
		this.x += WIND;
		while(this.x < 0) {
			this.x += GameFrame.FRAME_SIZE;
		}
		this.x %= GameFrame.FRAME_SIZE;
		if(this.y > GameFrame.FRAME_SIZE) {
			reset();
		}
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Dimension getSize() {
		return size;
	}

	public double getSpeed() {
		return speed;
	}

	public void offset(double x, double y) {
		this.x += x;
		this.y += y;
		while(this.x < 0) {
			this.x += GameFrame.FRAME_SIZE;
		}
		this.x %= GameFrame.FRAME_SIZE;
		if(this.y > GameFrame.FRAME_SIZE) {
			reset();
		}
	}


}
