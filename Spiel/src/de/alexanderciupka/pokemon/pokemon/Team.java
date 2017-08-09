package de.alexanderciupka.pokemon.pokemon;

import com.google.gson.JsonArray;

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
		for(Pokemon p : team) {
			if(p != null) {
				ammount++;
			} else {
				break;
			}
		}
		sort();
	}

	public Character getOwner() {
		return this.owner;
	}

	public boolean addPokemon(Pokemon newPokemon) {
		for(int i = 0; i < this.pokemon.length; i++) {
			this.ammount = i+1;
			if(pokemon[i] == null) {
				pokemon[i] = newPokemon;
				sort();
				return true;
			}
		}
		return false;
	}

	public int getAmmount() {
		return this.ammount;
	}

	public void swapPokemon(int firstPokemon, int secondPokemon) {
		Pokemon swap = pokemon[firstPokemon];
		pokemon[firstPokemon] = pokemon[secondPokemon];
		pokemon[secondPokemon] = swap;
		sort();
	}

	public Pokemon getFirstFightPokemon() {
		for(int i = 0; i < ammount; i++) {
			if(pokemon[i].getStats().getCurrentHP() > 0) {
				return pokemon[i];
			}
		}
		return null;
	}

	public int getIndex(Pokemon p) {
		for(int i = 0; i < ammount; i++) {
			if(p.equals(pokemon[i])) {
				return i;
			}
		}
		return -1;
	}

	public boolean isAnyPokemonAlive() {
		for(Pokemon p : pokemon) {
			if(p.getStats().getCurrentHP() > 0) {
				return true;
			}
		}
		return false;
	}

	public void restoreTeam() {
		for(int i = 0; i < ammount; i++) {
			pokemon[i].getStats().restoreFullHP();
			pokemon[i].setAilment(Ailment.NONE);
			pokemon[i].restoreMoves();
		}
	}

	public Pokemon[] getTeam() {
		return this.pokemon;
	}

	public JsonArray getSaveData() {
		JsonArray data = new JsonArray();
		for(Pokemon p : this.pokemon) {
			if(p != null) {
				data.add(p.getSaveData());
			}
		}
		return data;
	}

	public boolean importSaveData(JsonArray saveData) {
		this.pokemon = new Pokemon[6];
		for(int p = 0; p < Math.min(saveData.size(), this.pokemon.length); p++) {
			pokemon[p] = Pokemon.importSaveData(saveData.get(p).getAsJsonObject());
		}
		sort();
		return true;
	}

	public Pokemon replacePokemon(int index, Pokemon pokemon) {
		if(index >= 0 && index < this.pokemon.length) {
			Pokemon old = this.pokemon[index];
			this.pokemon[index] = pokemon;
			sort();
			return old;
		}
		return null;
	}

	public void sort() {
		for(int j = 0; j < this.pokemon.length - 1; j++) {
			for(int i = 0; i < this.pokemon.length - 1; i++) {
				if(pokemon[i] == null) {
					pokemon[i] = pokemon[i+1];
					pokemon[i+1] = null;
				}
			}
		}
		this.ammount = 0;
		for(Pokemon p : this.getTeam()) {
			if(p == null) {
				break;
			}
			if(owner instanceof NPC) {
				if(owner.getName().contains("Arenaleiter")) {
					for(String stat : Stats.STAT_SAVE_NAMES) {
						p.getStats().setDV(stat, 15);
					}
				} else {
					for(String stat : Stats.STAT_SAVE_NAMES) {
						p.getStats().setDV(stat, 0);
					}
				}
			}
			ammount++;
		}
	}
}
