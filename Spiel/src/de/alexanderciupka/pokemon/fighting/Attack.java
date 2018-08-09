/**
 * @author CutyCupy
 */
package de.alexanderciupka.pokemon.fighting;

import de.alexanderciupka.pokemon.pokemon.Item;
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

	private Item item;
	private Pokemon swap;

	public Attack(Pokemon source, Move move, Pokemon... targets) {
		this.source = source;
		this.move = move;
		this.targets = targets;
	}

	public Attack(Pokemon source, Item item) {
		this.source = source;
		this.item = item;
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

	public void setTargets(Pokemon[] targets) {
		this.targets = targets;
	}

	public Item getItem() {
		return this.item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Pokemon getSwap() {
		return this.swap;
	}

	public void setSwap(Pokemon swap) {
		this.swap = swap;
	}

	public int getPriority() {
		if (this.move != null) {
			return this.move.getPriority();
		}
		return 6;
	}

}
