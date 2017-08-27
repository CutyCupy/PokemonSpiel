package de.alexanderciupka.pokemon.pokemon;

public enum Item {
	CUT("Zerschneider", "Ermöglicht das Zerschneiden von kleinen Bäumen.", false), 
	SURF("Surfer", "Ermöglicht das Surfen über Wasser.", false), 
	ROCKSMASH("Zertrümmerer", "Ermöglicht das Zertrümmern von kleinen Steinen.", false), 
	STRENGTH("Stärke", "Ermöglicht das Verschieben von großen Felsen.", false), 
	FLASH("Blitz", "Leuchtet Höhlen aus.", false), 
	POTION("Trank", "Regeneriert @value KP eines Pokemons.", true, 20),
	SUPERPOTION("Supertrank", "Regeneriert @value KP eines Pokemons.", true, 60),
	HYPERPOTION("Hypertrank", "Regeneriert @value KP eines Pokemons.", true, 120),
	FULLHEAL("Top-Trank", "Regeneriert alle KP eines Pokemons.", true, Integer.MAX_VALUE),
	FULLRESTORE("Top-Genesung", "Regeneriert alle KP eines Pokemons und heilt die Statusprobleme.", true, Integer.MAX_VALUE / 2),
	PARAHEAL("Para-Heiler", "Heilt die Paralyse eines Pokemons.", true, Ailment.PARALYSIS),
	FREEZEHEAL("Enteiser", "Heilt die Erfrierung eines Pokemons.", true, Ailment.FREEZE),
	POISONHEAL("Gegengift", "Heilt die Vergiftung eines Pokemons.", true, Ailment.POISON),
	SLEEPHEAL("Wecker", "Weckt ein Pokemon auf.", true, Ailment.SLEEP),
	BURNHEAL("Feuerlöscher", "Heilt die Verbrennung eines Pokemons.", true, Ailment.BURN),
	REPEL("Schutz", "Schützt den Anwender für @value Schritte vor wilden Pokemon", false, 150),
	POKEBALL("Pokeball", "Ermöglicht das Einfangen von Pokemon.", false, 255),
	SUPERBALL("Superball", "Ermöglicht das Einfangen von Pokemon. Besser als der Pokeball.", false, 200),
	HYPERBALL("Hyperball", "Ermöglicht das Einfangen von Pokemon. Besser als der Superball.", false, 150),
	MASTERBALL("Meisterball", "Der beste Pokeball, der je entwickelt wurde. Fängt garantiert jedes Pokemon.", false, 1),
	RARECANDY("Sonderbonbon", "Ein Bonbon, der aus irgendeinem Grund einem Pokemon ein Level-UP gibt.", true),
	NONE("Nichts", "Nichts", false);
	
	
	private String name;
	private String description;
	private boolean usableOnPokemon;
	
	private int value;
	private Ailment ailment;
	

	Item(String name, String description, boolean usableOnPokemon) {
		this.name = name;
		this.description = description;
		this.usableOnPokemon = usableOnPokemon;
	}
	
	Item(String name, String description, boolean usableOnPokemon, int value) {
		this.name = name;
		this.description = description.replace("@value", String.valueOf(value));
		this.usableOnPokemon = usableOnPokemon;
		this.value = value;
	}
	
	Item(String name, String description, boolean usableOnPokemon, Ailment ailment) {
		this.name = name;
		this.description = description.replace("@value", String.valueOf(value));
		this.usableOnPokemon = usableOnPokemon;
		this.ailment = ailment;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isUsableOnPokemon() {
		return usableOnPokemon;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public Ailment getAilment() {
		return this.ailment;
	}

	public static Item getItemByName(String text) {
		for(Item i : Item.values()) {
			if(i.getName().equals(text)) {
				return i;
			}
		}
		return Item.NONE;
	}
}