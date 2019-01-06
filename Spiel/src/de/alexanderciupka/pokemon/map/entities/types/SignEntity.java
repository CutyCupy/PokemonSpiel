package de.alexanderciupka.pokemon.map.entities.types;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.types.Player;

public class SignEntity extends Entity {

	private String information;
	
	public SignEntity() {
		super();
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	@Override
	public void onInteraction(Player c) {
		System.out.println("hi");
		if (c.getCurrentDirection() == Direction.UP) {
			gController.getGameFrame().addDialogue(information);
			gController.waitDialogue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj) && obj instanceof SignEntity) {
			SignEntity other = (SignEntity) obj;
			return other.information.equals(this.information);
		}
		return false;
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty("information", this.information);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			if (saveData.has("information")) {
				this.information = saveData.get("information").getAsString();
			}
		}
		return false;
	}
}
