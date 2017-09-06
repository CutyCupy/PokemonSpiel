package de.alexanderciupka.pokemon.map.entities;

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

}
