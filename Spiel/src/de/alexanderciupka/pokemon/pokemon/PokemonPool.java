package de.alexanderciupka.pokemon.pokemon;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;

public class PokemonPool {
	
	private Random rng;
	private ArrayList<SimpleEntry<Integer, Short>> pokemonPool;
	private int id;
	
	
	public PokemonPool(int id) {
		this.rng = new Random();
		this.pokemonPool = new ArrayList<>();
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Pokemon getEncounter() {
		if(pokemonPool.size() == 0) {
			return null;
		}
		SimpleEntry<Integer, Short> encounter = pokemonPool.get(rng.nextInt(pokemonPool.size()));
		Pokemon first = GameController.getInstance().getMainCharacter().getTeam().getTeam()[0];
		switch(first.getAbility().getId()) {
		case Abilities.MAGNETFALLE:
			if(Main.RNG.nextFloat() < .5) {
				ArrayList<SimpleEntry<Integer, Short>> steels = new ArrayList<>();
				for(SimpleEntry<Integer, Short> p : this.pokemonPool) {
					Type[] t = GameController.getInstance().getInformation().getTypes(p.getKey());
					if(t[0] == Type.STEEL || t[1] == Type.STEEL) {
						steels.add(p);
					}
				}
				if(!steels.isEmpty()) {
					encounter = steels.get(Main.RNG.nextInt(steels.size()));
				}
			}
			break;
		case Abilities.ERZWINGER:
		case Abilities.ÜBEREIFER:
		case Abilities.MUNTERKEIT:
		case Abilities.BEDROHER:
		case Abilities.ADLERAUGE:
			if(Main.RNG.nextFloat() < .5) {
				ArrayList<SimpleEntry<Integer, Short>> stronger = new ArrayList<>();
				for(SimpleEntry<Integer, Short> p : this.pokemonPool) {
					if(p.getValue() > first.getStats().getLevel()) {
						stronger.add(p);
					}
				}
				if(!stronger.isEmpty()) {
					encounter = stronger.get(Main.RNG.nextInt(stronger.size()));
				}
			}
		}
		Pokemon result = new Pokemon(encounter.getKey());
		result.getStats().generateStats(encounter.getValue());
		return result;
	}
	
	public void addPokemon(Integer id,  short level) {
		this.pokemonPool.add(new SimpleEntry<Integer, Short>(id, level));
	}
	
	public ArrayList<SimpleEntry<Integer, Short>> getPokemonPool() {
		return this.pokemonPool;
	}
	
	public void deletePokemon(Integer id) {
		for(int i = 0; i < this.pokemonPool.size(); i++) {
			if(this.pokemonPool.get(i).getKey() == id) {
				this.pokemonPool.remove(i);
				i--;
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PokemonPool) {
			PokemonPool other = (PokemonPool) obj;
			if(this.id != other.id) {
				return false;
			}
			ArrayList<SimpleEntry<Integer, Short>> otherPool = new ArrayList<>(other.pokemonPool);
			for(SimpleEntry<Integer, Short> current : this.pokemonPool) {
				if(otherPool.contains(current)) {
					otherPool.remove(current);
				} else {
					return false;
				}
			}
			return otherPool.isEmpty();
		}
		return false;
	}
	
	public JsonObject getSaveData(PokemonPool pool) {
		JsonObject saveData = new JsonObject();
		saveData.addProperty("id", this.id);
		JsonArray pokemon = new JsonArray();
		for(SimpleEntry<Integer, Short> current : this.pokemonPool) {
			JsonObject currentPokemon = new JsonObject();
			currentPokemon.addProperty("id", current.getKey());
			currentPokemon.addProperty("level", current.getValue());
			pokemon.add(currentPokemon);
		}
		saveData.add("pokemon", pokemon);
		return saveData;
	}
}
