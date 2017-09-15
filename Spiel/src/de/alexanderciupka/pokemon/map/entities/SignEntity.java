package de.alexanderciupka.pokemon.map.entities;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.map.Route;

public class SignEntity extends Entity {

	private String information;
	
	public SignEntity(Route parent) {
		super(parent, false, "sign", 0, parent.getTerrainName());
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}
	
	@Override
	public void onInteraction(Player c) {
		if(c.getCurrentDirection() == Direction.UP) {
			gController.getGameFrame().addDialogue(information);
			gController.waitDialogue();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj) && obj instanceof SignEntity) {
			SignEntity other = (SignEntity) obj;
			return other.information.equals(this.information);
		}
		return false;
	}
	
	@Override
	public JsonObject getSaveData(Entity entity) {
		JsonObject saveData = super.getSaveData(entity);
		SignEntity other = (SignEntity) entity;
		if(!other.information.equals(this.information)) {
			saveData.addProperty("information", this.information);
		}
		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if(super.importSaveData(saveData, entity) && entity instanceof SignEntity) {
			SignEntity other = (SignEntity) entity;
			if(saveData.get("information") != null) {
				this.information = saveData.get("information").getAsString();
			} else {
				this.information = other.information;
			}
		}
		return false;
	}

}
