package de.alexanderciupka.pokemon.characters.ai;

import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class WildPokemonAI implements AI {
	
	private int position;

	@Override
	public Attack getAttack() {
		Fighting fight = GameController.getInstance().getFight();
		Attack attack = new Attack();
		attack.setSource(fight.getPokemon(position));
		attack.setMove(attack.getSource().getRandomMove());
		switch(attack.getMove().getTarget()) {
		case ALLY:
			attack.setTargets(fight.getPartner(this.position));
			break;
		case ALL_OPPONENTS:
			if(fight.isPlayer(this.position)) {
				attack.setTargets(Fighting.LEFT_OPPONENT, Fighting.RIGHT_OPPONENT);
			} else {
				attack.setTargets(Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER);
			}
			break;
		case ALL_OTHER_POKEMON:
			switch(this.position) {
			case Fighting.LEFT_PLAYER:
				attack.setTargets(Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT, Fighting.RIGHT_OPPONENT);
				break;
			case Fighting.RIGHT_PLAYER:
				attack.setTargets(Fighting.LEFT_PLAYER, Fighting.LEFT_OPPONENT, Fighting.RIGHT_OPPONENT);
				break;
			case Fighting.LEFT_OPPONENT:
				attack.setTargets(Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.RIGHT_OPPONENT);				
				break;
			case Fighting.RIGHT_OPPONENT:
				attack.setTargets(Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT);
				break;
			}
			break;
		case ALL_POKEMON:
			attack.setTargets(Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT, Fighting.RIGHT_OPPONENT);
			break;
		case ENTIRE_FIELD:
			attack.setTargets(this.position);
		case OPPONENTS_FIELD:
		case RANDOM_OPPONENT:
		case SELECTED_POKEMON_ME_FIRST:
		case SELECTED_POKEMON:
		case SPECIFIC_MOVE:
			if(fight.isPlayer(this.position)) {
				attack.setTargets(Main.RNG.nextBoolean() ? Fighting.LEFT_OPPONENT : Fighting.RIGHT_OPPONENT);
			} else {
				attack.setTargets(Main.RNG.nextBoolean() ? Fighting.LEFT_PLAYER : Fighting.RIGHT_PLAYER);
			}
			break;
		case USER:
		case USERS_FIELD:
			attack.setTargets(this.position);
			break;
		case USER_AND_ALLIES:
			attack.setTargets(this.position, fight.getPartner(this.position));
			break;
		case USER_OR_ALLY:
			attack.setTargets(Main.RNG.nextBoolean() ? this.position : fight.getPartner(this.position));
			break;
		}
		return attack;
	}

	@Override
	public void updateTeam(int id, Pokemon p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(int position) {
		this.position = position;
	}

}
