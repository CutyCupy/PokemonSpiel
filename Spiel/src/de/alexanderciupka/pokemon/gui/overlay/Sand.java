package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;

import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.main.Main;

public class Sand {

	private double x;
	private double y;
	private Dimension size;

	private double speed;

	private static final double WIND = 4;

	public static Color[] COLORS = new Color[] {new Color(174, 92, 25), new Color(145, 88, 20), new Color(255, 222, 173), new Color(255, 239, 213)};
	
	private Color color;

	public Sand() {
		reset();
	}

	public void reset() {
		this.x = Main.RNG.nextInt(GameFrame.FRAME_SIZE);
		this.y = Main.RNG.nextInt(50) - 55;
		this.size = new Dimension(4, 4);
		this.speed = (Main.RNG.nextDouble() + 0.5) * 3;
		this.color = COLORS[Main.RNG.nextInt(COLORS.length)];
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
	
	public Color getColor() {
		return this.color;
	}


}
