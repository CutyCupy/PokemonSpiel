package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Image;

import de.alexanderciupka.pokemon.characters.Character;

public class IceEntity extends Entity {

	@Override
	public void onStep(Character c) {
		super.onStep(c);
		c.startUncontrollableMove(c.getCurrentDirection(), false, Character.FAST);
	}
	
	
	@Override
	public Image getTerrain() {
		return this.gController.getRouteAnalyzer().getTerrainByName("ice");
	}

}
