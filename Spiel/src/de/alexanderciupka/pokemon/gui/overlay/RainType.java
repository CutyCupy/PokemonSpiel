package de.alexanderciupka.pokemon.gui.overlay;

public enum RainType {

	CLEAR(0),
	NIZZLE(100),
	HEAVY(1000),
	STORM(1000);

	private int raindrops;


	RainType(int raindrops) {
		this.raindrops = raindrops;
	}

	public int getRaindrops() {
		return this.raindrops;
	}

}
