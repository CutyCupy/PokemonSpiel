package de.alexanderciupka.sarahspiel.pokemon;

import java.util.ArrayList;

public class PC {

	private ArrayList<Box> boxes;

	public static final int INITIAL_BOXES = 12;
	public static final int NEW_BOXES = 6;

	public PC() {
		boxes = new ArrayList<Box>(INITIAL_BOXES);
		for(int i = 1; i <= INITIAL_BOXES; i++) {
			boxes.add(new Box(i, this));
		}
	}

	public Box[] getBoxes() {
		return this.boxes.toArray(new Box[this.boxes.size()]);
	}


	public void onUpdate() {
		for(Box b : this.boxes) {
			if(b.isEmpty()) {
				return;
			}
		}
		int boxSize = boxes.size();
		for(int i = boxSize; i < NEW_BOXES + boxSize; i++) {
			this.boxes.add(new Box(i,this));
		}
	}

	public Box addPokemon(Pokemon p) {
		for(int i = 0; i < this.boxes.size(); i++) {
			if(this.boxes.get(i).addPokemon(p)) {
				return this.boxes.get(i);
			}
		}
		return null;
	}

	public Box addPokemon(Pokemon p, int box) {
		for(int i = 0; i < this.boxes.size(); i++) {
			if(this.boxes.get(i).getNumber() == box) {
				if(this.boxes.get(i).addPokemon(p)) {
					return this.boxes.get(i);
				}
			}
		}
		return null;
	}


}
