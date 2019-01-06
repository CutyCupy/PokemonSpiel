package de.alexanderciupka.pokemon.exceptions;

import de.alexanderciupka.pokemon.map.entities.types.Entity;

public class InvalidEntityDataException extends Throwable {

	public InvalidEntityDataException(Entity e, String key) {
		super(e.getX() + "." + e.getY() + " on Route " + e.getRoute().getName() + " has at least one "
				+ "missing or bad key: " + key);
	}
	
}
