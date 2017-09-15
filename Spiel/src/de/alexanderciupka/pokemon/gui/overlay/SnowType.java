package de.alexanderciupka.pokemon.gui.overlay;

public enum SnowType {
	
	NIZZLE(100, .1, .5, 1, 15),
	BLIZZARD(2000, 15, 10, 13, 0);
	private int snowflakes;
	private double wind;
	private double minSpeed;
	private double maxSpeed;
	private int maxOffset;
	
	SnowType(int snowflakes, double wind, double minSpeed, double maxSpeed, int maxOffset) {
		this.snowflakes = snowflakes;
		this.wind = wind;
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
		this.maxOffset = maxOffset;
	}
	
	public int getSnowflakes() {
		return this.snowflakes;
	}

	public double getWind() {
		return wind;
	}

	public double getMinSpeed() {
		return minSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}
	
	public int getMaxOffset() {
		return maxOffset;
	}
}
