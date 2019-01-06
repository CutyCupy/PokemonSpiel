package de.alexanderciupka.pokemon.characters.ai;

import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public interface IAI {
	
	

	public Attack getAttack();

	public void setPosition(int position);
	
	public int getPosition();
	
	public void updateTeam(int id, Pokemon p);
	
	public Pokemon getNextPokemon();

}
