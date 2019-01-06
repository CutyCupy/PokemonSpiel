package de.alexanderciupka.pokemon.characters.ai;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class BestAI extends AI {
	
	public BestAI(Character c) {
		super(c);
	}

	private int position;

	@Override
	public Attack getAttack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateTeam(int id, Pokemon p) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}
	
	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public Pokemon getNextPokemon() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
