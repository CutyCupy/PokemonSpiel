package de.alexanderciupka.pokemon.characters.enums;

import de.alexanderciupka.pokemon.main.Main;

public enum SpinnerSpeed {
	RANDOM_SLOW(3000, 5000),
	RANDOM_FAST(1000, 2500),
	CONSTANT_SLOW(3000, 5000),
	CONSTANT_FAST(1000, 2500);
	
	
	//TODO: Update Delay
	
	
	private int min;
	private int max;
	
	SpinnerSpeed(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	public int getMax() {
		return (int) (max / Main.FPS);
	}
	
	public int getMin() {
		return (int) (min / Main.FPS);
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public void setMax(int max) {
		this.max = max;
	}
}
