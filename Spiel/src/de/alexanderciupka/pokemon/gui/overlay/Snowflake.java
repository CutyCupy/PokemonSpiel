package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import de.alexanderciupka.pokemon.gui.GameFrame;

public class Snowflake {
	
	private double x;
	private double y;
	private Dimension size;
	
	private double speed;

	public static Color COLOR = Color.WHITE;
	
	private Random rng;
	
	private double currentRadiant;
	
	private SnowType snowflakeType;
	
	public Snowflake(SnowType type) {
		rng = new Random();
		this.snowflakeType = type;
		reset();
		this.y = rng.nextInt(GameFrame.FRAME_SIZE);
	}
	
	public void reset() {
		this.x = rng.nextInt(GameFrame.FRAME_SIZE);
		this.y = rng.nextInt(50) - 55;
		this.size = new Dimension(5, 5);
		this.speed = (rng.nextDouble() * (snowflakeType.getMaxSpeed() - snowflakeType.getMinSpeed()))
				+ snowflakeType.getMinSpeed();
		this.currentRadiant = (rng.nextDouble() * Math.PI * 2);
	}
	
	public void fall() {
		this.y += speed;
		this.x += snowflakeType.getWind();
		while(this.x < 0) {
			this.x += GameFrame.FRAME_SIZE;
		}
		this.x %= GameFrame.FRAME_SIZE;
		if(this.y > GameFrame.FRAME_SIZE) {
			reset();
		}
		currentRadiant += .05 * speed * rng.nextDouble();
		currentRadiant %= Math.PI * 2;
	}

	public double getX() {
		return x + Math.sin(currentRadiant) * snowflakeType.getMaxOffset();
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
