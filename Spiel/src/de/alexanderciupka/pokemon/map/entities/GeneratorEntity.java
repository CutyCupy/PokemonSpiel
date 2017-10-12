package de.alexanderciupka.pokemon.map.entities;

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

}
