package de.alexanderciupka.pokemon.characters.types;

import java.util.Random;

import de.alexanderciupka.pokemon.characters.Direction;

public class Spinner extends NPC implements Walkable {

	private long nextSpin;

	private int current;
	private Direction[] directions;

	private long min = 1500;
	private long max = 5000;

	private Random rng;

	public Spinner() {
		super();

		this.rng = new Random();
	}

	public Spinner(String id) {
		super(id);
		this.rng = new Random();
	}

	private int increase() {
		if (this.directions != null && this.directions.length > 0) {
			this.current = (this.current + 1) % this.directions.length;
			return this.current;
		}
		return -1;
	}

	public Direction[] getDirections() {
		return this.directions;
	}

	public void setDirections(Direction[] d) {
		this.directions = d;
	}

	public void setMin(long m) {
		this.min = m;
	}

	public void setMax(long m) {
		this.max = m;
	}

	@Override
	public boolean move() {
		if (System.currentTimeMillis() >= this.nextSpin) {
			if (this.increase() != -1) {
				this.setCurrentDirection(this.directions[this.current]);
			} else {
				this.setCurrentDirection(Direction.values()[this.rng.nextInt(4)]);
			}
			this.nextSpin = System.currentTimeMillis() + (this.rng.nextLong() % (this.max - this.min)) + this.min;
			return true;
		}
		return false;
	}

}
