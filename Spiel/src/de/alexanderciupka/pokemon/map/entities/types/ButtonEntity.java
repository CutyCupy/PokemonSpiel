package de.alexanderciupka.pokemon.map.entities.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;

public class ButtonEntity extends Entity {

	private boolean pushed;

	private JsonObject onPush;

	@Override
	public void onStepNoWarp(Character c) {
		if(!this.isPushed()) {
			onPush();
		}
		super.onStepNoWarp(c);
	}
	
	public void onPush() {
		if (this.onPush != null) {
			if (this.onPush.has("buttons")) {
				for (JsonElement e : this.onPush.get("buttons").getAsJsonArray()) {
					JsonObject buttonData = e.getAsJsonObject();
					ButtonEntity entity = (ButtonEntity) this.gController.getRouteAnalyzer()
							.getRouteById(buttonData.get("route").getAsString())
							.getEntity(buttonData.get("x").getAsInt(), buttonData.get("y").getAsInt());
					switch (buttonData.get("change").getAsString()) {
					case "ON":
						entity.setPushed(true);
						break;
					case "OFF":
						entity.setPushed(false);
						break;
					case "TOGGLE":
						entity.setPushed(!entity.isPushed());
						break;
					default:
						break;
					}
				}
			}
			if(this.onPush.has("gates")) {
				for (JsonElement e : this.onPush.get("gates").getAsJsonArray()) {
					JsonObject gateData = e.getAsJsonObject();
					GateEntity entity = (GateEntity) this.gController.getRouteAnalyzer()
							.getRouteById(gateData.get("route").getAsString())
							.getEntity(gateData.get("x").getAsInt(), gateData.get("y").getAsInt());
					switch (gateData.get("change").getAsString()) {
					case "ON":
						entity.setOpen(true);
						break;
					case "OFF":
						entity.setOpen(false);
						break;
					case "TOGGLE":
						entity.setOpen(!entity.isOpen());
						break;
					default:
						break;
					}
				}
			}
		}
		this.setPushed(true);
	}
	
	public void setPushed(boolean pushed) {
		this.pushed = pushed;
		if(this.pushed) {
			onPush();
		}
	}
	
	public boolean isPushed() {
		return this.pushed;
	}
	
	@Override
	public JsonObject getSaveData() {
		JsonObject data = super.getSaveData();
		data.addProperty("is_pushed", this.pushed);
		data.add("on_push", this.onPush);
		return data;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			if(saveData.has("is_pushed")) {
				this.pushed = saveData.get("is_pushed").getAsBoolean();
			}			
			if(saveData.has("on_push")) {
				this.onPush = saveData.get("on_push").getAsJsonObject();
			}
			return true;
		}
		return false;
	}
}
