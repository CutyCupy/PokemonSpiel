package de.alexanderciupka.pokemon.pokemon;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Random;

public class PokemonPool {
	
	private Random rng;
	private ArrayList<SimpleEntry<Integer, Short>> pokemonPool;
	private String id;
	
	
	public PokemonPool(String id) {
		this.rng = new Random();
		this.pokemonPool = new ArrayList<>();
		this.id = id;
	}
	
	public String getId() {
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
}
