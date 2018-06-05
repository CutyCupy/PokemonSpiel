package de.alexanderciupka.pokemon.characters;

import java.util.Random;

public class RandomWalker extends NPC implements Walkable {

	private long nextWalk;
	private boolean walk;

	private long min = 5000;
	private long max = 15000;

	private Random rng;

	public RandomWalker() {
		super();

		this.rng = new Random();
	}

	public RandomWalker(String id) {
		super(id);
		this.rng = new Random();
	}

	public void setMin(long m) {
		this.min = m;
	}

	public void setMax(long m) {
		this.max = m;
	}

	@Override
	public boolean move() {
		if (System.currentTimeMillis() >= this.nextWalk) {
			Direction dir = Direction.values()[this.rng.nextInt(4)];

			System.out.println(dir + " - " + this.walk);

			this.setCurrentDirection(dir);
			if (this.walk) {
				int x = this.currentPosition.x;
				int y = this.currentPosition.y;
				switch (dir) {
				case DOWN:
					y++;
					break;
				case LEFT:
					x--;
					break;
				case RIGHT:
					x++;
					break;
				case UP:
					y--;
					break;
				default:
					break;
				}

				if (this.getCurrentRoute() != null && this.getCurrentRoute().getEntity(x, y) != null
						&& this.getCurrentRoute().getEntity(x, y).getWarp() == null) {
					this.changePosition(dir, false);
				}
			}

			this.walk = this.rng.nextFloat() < .75;
			this.nextWalk = System.currentTimeMillis() + (this.rng.nextLong() % (this.max - this.min)) + this.min;

		}
		return false;
	}

}
