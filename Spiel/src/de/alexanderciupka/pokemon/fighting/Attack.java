/**
 * @author CutyCupy
 */
package de.alexanderciupka.pokemon.fighting;

import java.util.ArrayList;

import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

/**
 * Stores all the data for an attack in a pokemon fight.
 * 
 * @author CutyCupy
 */
public class Attack {

	private Pokemon source;
	private Move move;
	private Pokemon[] targets;

	private Integer item;
	private Pokemon swap;

	public Attack() {
	}

	public Attack(Pokemon source, Move move, int... targets) {
		this.source = source;
		this.move = move;
		setTargets(targets);
	}

	public Attack(Pokemon source, Integer item, Pokemon target) {
		this.source = source;
		this.item = item;
		this.targets = new Pokemon[] { target };
	}

	public Attack(Pokemon source, Pokemon swap) {
		this.source = source;
		this.swap = swap;
	}

	public Pokemon getSource() {
		return this.source;
	}

	public void setSource(Pokemon source) {
		this.source = source;
	}

	public Move getMove() {
		return this.move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public Pokemon[] getTargets() {
		return this.targets;
	}

	public void setTargets(int... targets) {
		ArrayList<Pokemon> temp = new ArrayList<>();
		for (int t : targets) {
			temp.add(GameController.getInstance().getFight().getPokemon(t));
		}
		this.targets = temp.toArray(new Pokemon[temp.size()]);
	}

	public void setTargets(Pokemon... targets) {
		this.targets = targets;
	}

	public Integer getItem() {
		return this.item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Pokemon getSwap() {
		return this.swap;
	}

	public void setSwap(Pokemon swap) {
		this.swap = swap;
	}

	public void updateTargets(Pokemon oldTarget, Pokemon newTarget) {
		if (this.targets != null) {
			for (int i = 0; i < targets.length; i++) {
				if (oldTarget.equals(targets[i])) {
					targets[i] = newTarget;
				}
			}
		}
	}

	public int getPriority() {
		if (this.move != null) {
			switch (this.source.getAbility().getId()) {
			case Abilities.STROLCH:
				switch (this.move.getDamageClass()) {
				case NO_DAMAGE:
					return this.move.getPriority() + 1;
				default:
					break;
				}
				break;
			}
			return this.move.getPriority();
		}
		return 6;
	}

	@Override
	public String toString() {
		String string = "[source:" + source + ",";
		if (move != null) {
			string += "move:" + move + ",targets:";
			for (Pokemon t : this.targets) {
				string += t + ",";
			}
		} else if (item != null) {
			string += "item:" + item + ",targets:";
			for (Pokemon t : this.targets) {
				string += t + ",";
			}
		} else {
			string += "swap:" + this.swap + ",";
		}
		string += "priority:" + this.getPriority() + "]";
		return string;
	}

}
