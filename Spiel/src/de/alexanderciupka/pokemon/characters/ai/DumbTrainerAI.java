package de.alexanderciupka.pokemon.characters.ai;

import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class DumbTrainerAI implements AI {
	
	private int position;

	@Override
	public Attack getAttack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateTeam(int position, Pokemon p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}
}
