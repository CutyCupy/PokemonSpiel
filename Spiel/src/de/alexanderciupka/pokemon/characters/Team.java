package de.alexanderciupka.pokemon.characters;

import com.google.gson.JsonArray;

import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;

public class Team {

	private Pokemon[] pokemon;
	private int ammount;
	private Character owner;

	public Team(Character owner) {
		this.owner = owner;
		this.pokemon = new Pokemon[6];
		this.ammount = 0;
	}

	public Team(Pokemon[] team, Character owner) {
		this.owner = owner;
		this.pokemon = team.clone();
		for (Pokemon p : team) {
			if (p != null) {
				this.ammount++;
			} else {
				break;
			}
		}
		this.sort();
	}

	public Character getOwner() {
		return this.owner;
	}

	public boolean addPokemon(Pokemon newPokemon) {
		for (int i = 0; i < this.pokemon.length; i++) {
			this.ammount = i + 1;
			if (this.pokemon[i] == null) {
				this.pokemon[i] = newPokemon;
				this.sort();
				return true;
			}
		}
		return false;
	}

	public int getAmmount() {
		return this.ammount;
	}

	public void swapPokemon(int firstPokemon, int secondPokemon) {
		Pokemon swap = this.pokemon[firstPokemon];
		this.pokemon[firstPokemon] = this.pokemon[secondPokemon];
		this.pokemon[secondPokemon] = swap;
		this.sort();
	}

	public Pokemon getFirstFightPokemon() {
		for (int i = 0; i < this.ammount; i++) {
			if (this.pokemon[i].getStats().getCurrentHP() > 0) {
				return this.pokemon[i];
			}
		}
		return null;
	}

	public int getIndex(Pokemon p) {
		for (int i = 0; i < this.ammount; i++) {
			if (p.equals(this.pokemon[i])) {
				return i;
			}
		}
		return -1;
	}

	public boolean isAnyPokemonAlive() {
		for (Pokemon p : this.pokemon) {
			if (p.getStats().getCurrentHP() > 0) {
				return true;
			}
		}
		return false;
	}

	public void restoreTeam() {
		for (int i = 0; i < this.ammount; i++) {
			this.pokemon[i].getStats().restoreFullHP();
			this.pokemon[i].setAilment(Ailment.NONE);
			this.pokemon[i].restoreMoves();
		}
	}

	public Pokemon[] getTeam() {
		return this.pokemon;
	}

	public JsonArray getSaveData() {
		JsonArray data = new JsonArray();
		for (Pokemon p : this.pokemon) {
			if (p != null) {
				data.add(p.getSaveData());
			}
		}
		return data;
	}

	public boolean importSaveData(JsonArray saveData) {
		this.pokemon = new Pokemon[6];
		for (int p = 0; p < Math.min(saveData.size(), this.pokemon.length); p++) {
			this.pokemon[p] = Pokemon.importSaveData(saveData.get(p).getAsJsonObject());
		}
		this.sort();
		return true;
	}

	public Pokemon replacePokemon(int index, Pokemon pokemon) {
		if (index >= 0 && index < this.pokemon.length) {
			Pokemon old = this.pokemon[index];
			this.pokemon[index] = pokemon;
			this.sort();
			return old;
		}
		return null;
	}

	public void sort() {
		for (int j = 0; j < this.pokemon.length - 1; j++) {
			for (int i = 0; i < this.pokemon.length - 1; i++) {
				if (this.pokemon[i] == null) {
					this.pokemon[i] = this.pokemon[i + 1];
					this.pokemon[i + 1] = null;
				}
			}
		}
		this.ammount = 0;
		for (Pokemon p : this.getTeam()) {
			if (p == null) {
				break;
			}
			if (this.owner instanceof NPC) {
				if (this.owner.getName().contains("Arenaleiter")) {
					for (Stat s : Stat.values()) {
						p.getStats().setDV(s, 15);
					}
				} else {
					for (Stat s : Stat.values()) {
						p.getStats().setDV(s, 0);
					}
				}
			}
			this.ammount++;
		}
	}
}
