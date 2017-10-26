package de.alexanderciupka.pokemon.gui.overlay;

public enum RainType {


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
