package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Image;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.types.Player;

public class WaterEntity extends Entity {
	
	private boolean diveable;
	
	public void setDiveable(boolean diveable) {
		this.diveable = diveable;
	}
	
	public boolean isDiveable() {
		return diveable;
	}
	
	@Override
	public void onInteraction(Player c) {
		super.onInteraction(c);
		//TODO: Ask if the player wants to surf and check if he is able to.
		c.setSurfing(true);
		c.changePosition(c.getCurrentDirection(), true);
	}
	
	@Override
	public boolean isAccessible(Character c) {
		return super.isAccessible(c) && c.isSurfing();
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			if(saveData.has("is_diveable")) {
				this.diveable = saveData.get("is_diveable").getAsBoolean();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public Image getTerrain() {
		return this.gController.getRouteAnalyzer().getTerrainByName("see");
	}
	
	@Override
	public JsonObject getSaveData() {
		JsonObject data = super.getSaveData();
		data.addProperty("is_diveable", this.diveable);
		return data;
	}

}
