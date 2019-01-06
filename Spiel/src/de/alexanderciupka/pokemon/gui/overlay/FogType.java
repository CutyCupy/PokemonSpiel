package de.alexanderciupka.pokemon.gui.overlay;

public enum FogType {
	
	CLEAR(0),
	MIST(75),
	HEAVY(150);
	
	
	private int alpha;
	
	FogType(int alpha) {
		this.alpha = alpha;
	}
	
	public int getAlpha() {
		return this.alpha;
	}
}
