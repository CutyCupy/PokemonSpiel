package de.alexanderciupka.sarahspiel.pokemon;

import com.google.gson.JsonArray;

public class Team {

	private Pokemon[] pokemon;
	private int ammount;

	public Team() {
		this.pokemon = new Pokemon[6];
		this.ammount = 0;
	}

	public Team(Pokemon[] team) {
		this.pokemon = team.clone();
		for(Pokemon p : team) {
			if(p != null) {
				ammount++;
			} else {
				break;
			}
		}
	}

	public boolean addPokemon(Pokemon newPokemon) {
		if(ammount < 6) {
			pokemon[ammount] = newPokemon;
			ammount++;
			return true;
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
		return true;
	}
}
