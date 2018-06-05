package de.alexanderciupka.pokemon.pokemon;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
