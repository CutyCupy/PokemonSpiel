package de.alexanderciupka.pokemon.exceptions;

import de.alexanderciupka.pokemon.map.Route;

public class InvalidRouteDataException extends Throwable {
	
	public InvalidRouteDataException(Route r, String key) {
		super(r.getName() + " has at least one missing or bad key: " + key);
	}

}
