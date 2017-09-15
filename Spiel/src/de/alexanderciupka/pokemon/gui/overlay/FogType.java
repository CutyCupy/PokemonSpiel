package de.alexanderciupka.pokemon.gui.overlay;

public enum FogType {
	
	HEAVY(150),
	MIST(75);
	
	
	private int alpha;
	
	FogType(int alpha) {
		this.alpha = alpha;
	}
	
	public int getAlpha() {
		return this.alpha;
	}
}
