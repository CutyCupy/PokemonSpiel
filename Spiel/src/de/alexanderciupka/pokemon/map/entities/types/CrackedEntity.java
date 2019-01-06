package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Image;
import java.awt.Point;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.map.Warp;

public class CrackedEntity extends Entity {
	
	private boolean cracked;
	private String uncrackedSprite;
	private String crackedSprite;
	

	
	public void setCracked(boolean cracked) {
		this.cracked = cracked;
	}
	
	public boolean isCracked() {
		return this.cracked;
	}
	
	public void setUncrackedSprite(String uncrackedSprite) {
		this.uncrackedSprite = uncrackedSprite;
	}
	
	public void setCrackedSprite(String crackedSprite) {
		this.crackedSprite = crackedSprite;
	}
	
	@Override
	public Image getSprite() {
		if(this.isCracked()) {
			super.setSprite(crackedSprite);
		} else {
				super.setSprite(uncrackedSprite);
		}
		return super.getSprite();
	}
	
	
	@Override
	public void onStep(Character c) {
		if(!isCracked()) {
			onStepNoWarp(c);
		} else {
			startWarp(c);
		}
	}
	
	@Override
	public void onStepNoWarp(Character c) {
		super.onStepNoWarp(c);
		this.cracked = true;
	}
	
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			if(saveData.has("is_cracked")) {
				this.cracked = saveData.get("is_cracked").getAsBoolean();
			}
			if(saveData.has("sprites")) {
				this.crackedSprite = saveData.get("sprites").getAsJsonObject().get("cracked").getAsString();
				this.uncrackedSprite = saveData.get("sprites").getAsJsonObject().get("uncracked").getAsString();
			}
			if(saveData.has("drop_location")) {
				Warp w = new Warp("", this.getRoute().getName());
				w.setNewDirection(Direction.NONE);
				w.setNewPosition(new Point(saveData.get("drop_location").getAsJsonObject().get("x").getAsInt(), 
						saveData.get("drop_location").getAsJsonObject().get("y").getAsInt()));
				w.setNewRoute(saveData.get("drop_location").getAsJsonObject().get("route").getAsString());
				this.addWarp(w);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public JsonObject getSaveData() {
		JsonObject data = super.getSaveData();
		data.addProperty("is_cracked", this.cracked);
		
		JsonObject sprites = new JsonObject();
		sprites.addProperty("uncracked", this.uncrackedSprite);
		sprites.addProperty("cracked", this.crackedSprite);
		data.add("sprites", sprites);
		
		JsonObject dropLocation = new JsonObject();
		dropLocation.addProperty("route", this.getWarp().getNewRoute());
		dropLocation.addProperty("x", this.getWarp().getNewPosition().x);
		dropLocation.addProperty("y", this.getWarp().getNewPosition().y);
		data.add("drop_location", dropLocation);
		
		return data;
	}
	
	@Override
	public void reset() {
		this.cracked = false;
	}

}
