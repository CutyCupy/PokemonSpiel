package de.alexanderciupka.pokemon.map.entities.types;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.gui.panels.GeneratorPanel;
import de.alexanderciupka.pokemon.map.Route;

public class GeneratorEntity extends Entity {

	private double percentage;

	public GeneratorEntity(Route parent, String terrainName) {
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
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty("percentage", percentage);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			if (saveData.get("percentage") != null) {
				this.percentage = saveData.get("percentage").getAsDouble();
			}
			return true;
		}
		return false;
	}
}
