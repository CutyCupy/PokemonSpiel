package de.alexanderciupka.pokemon.pokemon;

public class Ability {

	private int id;
	private String name;
	private String description;

	public Ability(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}
}
