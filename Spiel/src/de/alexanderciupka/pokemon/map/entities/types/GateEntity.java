package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Image;

import com.google.gson.JsonObject;

public class GateEntity extends Entity {

	private boolean open;

	public void setOpen(boolean open) {
		this.open = open;
		this.setAccessible(open);
	}
	
	@Override
	public Image getSprite() {
		if(this.open) {
			return gController.getRouteAnalyzer().getSpriteByName("free");
		}
		return super.getSprite();
	}

	public boolean isOpen() {
		return this.open;
	}
	
	@Override
	public JsonObject getSaveData() {
		JsonObject data = super.getSaveData();
		data.addProperty("is_open", open);
		return data;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			if(saveData.has("is_open")) {
				this.open = saveData.get("is_open").getAsBoolean();
			}
			return true;
		}
		return false;
	}
}
