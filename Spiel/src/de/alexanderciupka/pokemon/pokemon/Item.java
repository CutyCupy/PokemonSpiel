package de.alexanderciupka.pokemon.pokemon;

public enum Item {
	CUT("Zerschneider", "Ermöglicht das Zerschneiden von kleinen Bäumen.", false),
	SURF("Surfer", "Ermöglicht das Surfen über Wasser.", false),
	ROCKSMASH("Zertrümmerer", "Ermöglicht das Zertrümmern von kleinen Steinen.", false),
	STRENGTH("Stärke", "Ermöglicht das Verschieben von großen Felsen.", false), 
	FLASH("Taschenlampe", "Leuchtet Höhlen aus.", false),
	BIKE("Fahrrad", "Das solltest du schon wissen, was man damit machen kann...", false),
	GOODROD("Superangel", "Eine gute Angel.", false),
	PROROD("Profiangel", "Eine sehr gute Angel.", false),
	ROD("Angel", "Eine Angel.", false),
	BACKSTAGEPASS("SF9 VIP Pass", "Ein VIP Pass für das Konzert von SF9 in Düsseldorf.", false),

	POTION("Trank", "Regeneriert @value KP eines Pokemons.", true, 20),
	SUPERPOTION("Supertrank", "Regeneriert @value KP eines Pokemons.", true, 60),
	HYPERPOTION("Hypertrank", "Regeneriert @value KP eines Pokemons.", true, 120),
	FULLHEAL("Top-Trank", "Regeneriert alle KP eines Pokemons.", true, Integer.MAX_VALUE),
	FULLRESTORE("Top-Genesung", "Regeneriert alle KP eines Pokemons und heilt die Statusprobleme.", true, Integer.MAX_VALUE / 2), 
	REVIVE("Beleber", "Belebt ein besiegtes Pokemon wieder und stellt dabei @value% der KP wieder her.", true, 50),
	MAXREVIVE("Top-Beleber", "Belebt ein besiegtes Pokemon wieder und stellt dabei die @value% KP wieder her.", true, 100),
	PARAHEAL("Paraheiler", "Heilt die Paralyse eines Pokemons.", true, Ailment.PARALYSIS),
	FREEZEHEAL("Eisheiler", "Heilt die Erfrierung eines Pokemons.", true, Ailment.FREEZE),
	POISONHEAL("Gegengift", "Heilt die Vergiftung eines Pokemons.", true, Ailment.POISON), 
	SLEEPHEAL("Aufwecker", "Weckt ein Pokemon auf.", true, Ailment.SLEEP),
	BURNHEAL("Feuerheiler", "Heilt die Verbrennung eines Pokemons.", true, Ailment.BURN),
	HYPERHEAL("Hyperheiler", "Heilt jede Statusveränderung eines Pokemons.", true),
	ZWIEBACKNUTELLA("Zwiebacknutella", "Heilt jede Statusveränderung eines Pokemons.", true),

	ELIXIR("Elixier", "Füllt die AP aller Attacken eines Pokémon um @value Punkte auf.", true, 10),
	MAXELIXIR("Top-Elixier", "Füllt die AP aller Attacken eines Pokémon komplett auf.", true),

	REPEL("Schutz", "Schützt den Anwender für @value Schritte vor wilden Pokemon", false, 150),
	SUPERREPEL("Superschutz", "Schützt den Anwender für @value Schritte vor wilden Pokemon", false, 200),
	MAXREPEL("Topschutz", "Schützt den Anwender für @value Schritte vor wilden Pokemon", false, 250),

	ESCAPEROPE("Fluchtseil", "Ermöglicht das Fliehen aus einer Höhle.", false),

	POKEBALL("Pokeball", "Ermöglicht das Einfangen von Pokemon.", false, 255),
	SUPERBALL("Superball", "Ermöglicht das Einfangen von Pokemon. Besser als der Pokeball.", false, 200),
	HYPERBALL("Hyperball", "Ermöglicht das Einfangen von Pokemon. Besser als der Superball.", false, 150),
	MASTERBALL("Meisterball", "Der beste Pokeball, der je entwickelt wurde. Fängt garantiert jedes Pokemon.", false, 1),
	HEALBALL("Heilball", "Ermöglicht das Einfangen von Pokémon. Ist das Pokémon gefangen, so wird es komplett geheilt.", false, 255),
	PREMIERBALL("Premierball", "Ermöglicht das Einfangen von Pokemon. Diesen Ball kann man nirgendswo kaufen.", false, 200),

	RARECANDY("Sonderbonbon", "Ein Bonbon, der aus irgendeinem Grund einem Pokemon ein Level-UP gibt.", true),
	CALCIUM("Kalzium", "Ein Getränk, das @article @stat leicht erhöht.", true, Stat.SPECIALATTACK),
	CARBON("Carbon", "Ein Getränk, das @article @stat leicht erhöht.", true, Stat.SPEED),
	PROTEIN("Protein", "Ein Getränk, das @article @stat leicht erhöht.", true, Stat.ATTACK),
	HPUP("KP-Up", "Ein Getränk, das @article @stat leicht erhöht.", true, Stat.HP),
	IRON("Eisen", "Ein Getränk, das @article @stat leicht erhöht.", true, Stat.DEFENSE),
	ZINC("Zink", "Ein Getränk, das @article @stat leicht erhöht.", true, Stat.SPECIALDEFENSE),

	DAWNSTONE("Funkelstein", "Ein funkelnder Stein, der bei manchen Pokemon eine Entwicklung auslöst.", true),
	FIRESTONE("Feuerstein", "Ein Stein mit einer Flamme im Inneren. Löst bei manchen Pokemon eine Entwicklung aus.", true),
	SUNSTONE("Sonnenstein", "Ein Stein, der die Form einer Sonne hat. Löst bei manchen Pokemon eine Entwicklung aus.", true),
	LEAFSTONE("Blattstein", "Ein Stein mit einem Blatt im Inneren. Löst bei manchen Pokemon eine Entwicklung aus.", true),
	WATERSTONE("Wasserstein", "Ein Stein mit Wasser im Inneren. Löst bei manchen Pokemon eine Entwicklung aus.", true),
	MOONSTONE("Mondstein", "Ein Stein aus einem seltenen Material. Löst bei manchen Pokemon eine Entwicklung auslöst.", true),
	DUSKSTONE("Finsterstein", "Ein finsterer Stein, der bei manchen Pokemon eine Entwicklung auslöst.", true),
	THUNDERSTONE("Donnerstein", "Ein Stein mit einem Blitz im Inneren. Löst bei manchen Pokemon eine Entwicklung aus.", true),
	SHINYSTONE("Leuchtstein", "Ein leuchtender Stein, der bei manchen Pokemon eine Entwicklung auslöst.", true),

	NONE("Nichts", "Nichts", false);


	private String name;
	private String description;
	private boolean usableOnPokemon;

	private int value;
	private Ailment ailment;
	private Stat increase;


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

	Item(String name, String description, boolean usableOnPokemon, Stat increase) {
		this.name = name;
		this.description = description.replace("@stat", increase.getText());
		this.description = this.description.replace("@article", increase.getArticle());
		this.usableOnPokemon = usableOnPokemon;
		this.increase = increase;
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

	public Stat getIncrease() {
		return this.increase;
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