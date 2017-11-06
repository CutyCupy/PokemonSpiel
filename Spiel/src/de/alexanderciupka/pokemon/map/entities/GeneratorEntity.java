package de.alexanderciupka.pokemon.map.entities;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.gui.panels.GeneratorPanel;
import de.alexanderciupka.pokemon.map.Route;

public class GeneratorEntity extends Entity {

	private double percentage;

	public GeneratorEntity(Route parent , String terrainName) {
		super(parent, false, "generator", 0, terrainName);
		percentage = 0;
	}

	public double getPercentage() {
		return this.percentage;
	}

	public boolean isDone() {
		return this.percentage >= 100;
	}

	public void setPercentage(double p) {
		this.percentage = p;
	}

	@Override
	public void onInteraction(Player c) {
		if (this.isDone()) {
			this.gController.getGameFrame().addDialogue("Dieser Generator ist fertig!");
			this.gController.getGameFrame().getDialogue().waitText();
		} else {
			this.gController.getGameFrame().addDialogue("Du fängst an, an dem Generator zu arbeiten!");
			this.gController.getGameFrame().getDialogue().waitText();
			GeneratorPanel gp = new GeneratorPanel();
			this.gController.getGameFrame().setCurrentPanel(gp);
			gp.start(this);
		}
	}
	
	@Override
	public JsonObject getSaveData(Entity entity) {
		GeneratorEntity origin = (GeneratorEntity) entity;
		JsonObject saveData = super.getSaveData(entity);
		if(this.percentage != origin.percentage) {
			saveData.addProperty("percentage", percentage);
		}
		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if(super.importSaveData(saveData, entity) && entity instanceof ItemEntity) {
			GeneratorEntity other = (GeneratorEntity) entity;
			if(saveData.get("percentage") != null) {
				this.percentage = saveData.get("percentage").getAsDouble();
			} else {
				this.percentage = other.percentage;
			}
			return true;
		}
		return false;
	}

}
