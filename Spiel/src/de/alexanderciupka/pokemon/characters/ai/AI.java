package de.alexanderciupka.pokemon.characters.ai;

import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public interface AI {

	public Attack getAttack();

	public void setPosition(int position);
	
	public void updateTeam(int id, Pokemon p);

}
