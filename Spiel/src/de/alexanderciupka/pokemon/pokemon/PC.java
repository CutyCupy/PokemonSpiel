package de.alexanderciupka.pokemon.pokemon;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PC {

	private ArrayList<Box> boxes;
	private Character owner;

	public static final int INITIAL_BOXES = 12;
	public static final int NEW_BOXES = 6;

	public PC(Character character) {
		this.setOwner(character);
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
		for(int i = boxSize + 1; i <= NEW_BOXES + boxSize; i++) {
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

	public Character getOwner() {
		return owner;
	}

	public void setOwner(Character owner) {
		this.owner = owner;
	}

	public void swap(Box first, int firstIndex, Box second, int secondIndex) {
		Pokemon temp = first.getPokemons()[firstIndex];
		first.getPokemons()[firstIndex] = second.getPokemons()[secondIndex];
		second.getPokemons()[secondIndex] = temp;
		onUpdate();
	}

	public JsonArray getSaveData() {
		JsonArray data = new JsonArray();
		for(Box b : this.boxes) {
			JsonObject boxData = new JsonObject();
			boxData.addProperty("name", b.getName());
			boxData.addProperty("id", b.getNumber());
			for(int i = 0; i < b.getPokemons().length; i++) {
				if(b.getPokemons()[i] != null) {
					boxData.add(String.valueOf(i), b.getPokemons()[i].getSaveData());
				}
			}
			data.add(boxData);
		}
		return data;
	}

	public void importSaveData(JsonArray data) {
		reset();


		for(int i = 0; i < data.size(); i++) {
			JsonObject currentBox = data.get(i).getAsJsonObject();
			this.boxes.get(i).setName(currentBox.get("name").getAsString());
			this.boxes.get(i).setNumber(currentBox.get("id").getAsInt());
			for(int j = 0; j < this.boxes.get(i).getPokemons().length; j++) {
				if(currentBox.get(String.valueOf(j)) != null) {
					this.boxes.get(i).addPokemon(Pokemon.importSaveData(currentBox.get(String.valueOf(j)).getAsJsonObject()), j);
				}
			}
		}
	}

	private void reset() {
		for(int i = 0; i < this.boxes.size(); i++) {
			for(int j = 0; j < this.boxes.get(i).getPokemons().length; j++) {
				this.boxes.get(i).getPokemons()[j] = null;
			}
		}
	}
}