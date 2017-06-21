package de.alexanderciupka.sarahspiel.pokemon;

public class Box {

	private Pokemon[] pokemons;
	private int number;
	private PC pc;
	private String name;

	public static final int LIMIT = 30;

	public Box(int number, PC pc) {
		this.number = number;
		this.name = "Box " + this.number;
		this.pokemons = new Pokemon[LIMIT];
		this.pc = pc;
	}

	public boolean isFull() {
		for(Pokemon p : this.pokemons) {
			if(p == null) {
				return false;
			}
		}
		return true;
 	}

	public boolean isEmpty() {
		for(Pokemon p : this.pokemons) {
			if(p != null) {
				return false;
			}
		}
		return true;
	}

	public boolean addPokemon(Pokemon p) {
		for(int i = 0; i < this.pokemons.length; i++) {
			if(this.pokemons[i] == null) {
				this.pokemons[i] = p;
				pc.onUpdate();
				return true;
			}
		}
		return false;
	}

	public boolean addPokemon(Pokemon p, int index) {
		if(index >= 0 && index < this.pokemons.length) {
			if(this.pokemons[index] == null) {
				this.pokemons[index] = p;
				pc.onUpdate();
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pokemon[] getPokemons() {
		return pokemons;
	}

	public int getNumber() {
		return number;
	}

	public PC getPc() {
		return pc;
	}

	public int getAmount() {
		int counter = 0;
		for(Pokemon p : this.pokemons) {
			if(p != null) {
				counter++;
			}
		}
		return counter;
	}

}
