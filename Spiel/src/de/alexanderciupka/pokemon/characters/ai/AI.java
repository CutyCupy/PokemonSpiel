package de.alexanderciupka.pokemon.characters.ai;

import de.alexanderciupka.pokemon.characters.Character;

public abstract class AI implements IAI {
	
	protected Character trainer;
	
	public AI(Character c) {
		this.trainer = c;
	}
	
	public Character getTrainer() {
		return this.trainer;
	}

}
