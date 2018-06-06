package de.alexanderciupka.pokemon.characters;

import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class Box {

	private Pokemon[] pokemons;
	private int number;
	private PC pc;
	private String name;

	private static final int LIMIT = 36;

	public Box(int number, PC pc) {
		this.number = number;
		this.name = "Box " + this.number;
		this.pokemons = new Pokemon[LIMIT];
		this.pc = pc;
	}

	public boolean isFull() {
		for (Pokemon p : this.pokemons) {
			if (p == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		for (Pokemon p : this.pokemons) {
			if (p != null) {
				return false;
			}
		}
		return true;
	}

	public boolean addPokemon(Pokemon p) {
		for (int i = 0; i < this.pokemons.length; i++) {
			if (this.pokemons[i] == null) {
				this.pokemons[i] = p;
				this.pc.onUpdate();
				return true;
			}
		}
		return false;
	}

	public boolean addPokemon(Pokemon p, int index) {
		if (index >= 0 && index < this.pokemons.length) {
			if (this.pokemons[index] == null) {
				this.pokemons[index] = p;
				this.pc.onUpdate();
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pokemon[] getPokemons() {
		return this.pokemons;
	}

	public int getNumber() {
		return this.number;
	}

	public PC getPc() {
		return this.pc;
	}

	public int getAmount() {
		int counter = 0;
		for (Pokemon p : this.pokemons) {
			if (p != null) {
				counter++;
			}
		}
		return counter;
	}

	public Box getNext() {
		for (int i = 0; i < this.pc.getBoxes().length; i++) {
			if (this.pc.getBoxes()[i].getNumber() == this.number) {
				return i != this.pc.getBoxes().length - 1 ? this.pc.getBoxes()[i + 1] : this.pc.getBoxes()[0];
			}
		}
		return null;
	}

	public Box getBefore() {
		for (int i = 0; i < this.pc.getBoxes().length; i++) {
			if (this.pc.getBoxes()[i].getNumber() == this.number) {
				return i != 0 ? this.pc.getBoxes()[i - 1] : this.pc.getBoxes()[this.pc.getBoxes().length - 1];
			}
		}
		return null;
	}

	public Pokemon replacePokemon(int index, Pokemon pokemon) {
		Pokemon old = this.pokemons[index];
		this.pokemons[index] = pokemon;
		return old;
	}

	public void setNumber(int asInt) {
		this.number = asInt;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Box) {
			Box other = (Box) obj;
			for (int i = 0; i < this.pokemons.length; i++) {
				if (this.pokemons[i] == null ? other.pokemons[i] != null
						: !this.pokemons[i].equals(other.pokemons[i])) {
					return false;
				}
			}
			return this.number == other.number && this.name.equals(other.name);
		}
		return false;
	}

}
