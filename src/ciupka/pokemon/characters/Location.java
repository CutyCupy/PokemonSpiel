package ciupka.pokemon.characters;

import ciupka.pokemon.entities.Route;
import ciupka.pokemon.enums.MovingDirection;

public class Location {

	// Position
	private float x;
	private float y;
	private MovingDirection dir;
	
	// Route
	private Route route;

	

	public Location(float x, float y, Route route) {
		this.x = x;
		this.y = y;
		this.route = route;
	}


	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}


	public float getY() {
		return y;
	}


	public void setY(float y) {
		this.y = y;
	}


	public Route getRoute() {
		return route;
	}


	public void setRoute(Route route) {
		this.route = route;
	}
	
	public MovingDirection getDirection() {
		return this.dir;
	}
	
	public void setDirection(MovingDirection dir) {
		this.dir = dir;
	}
	
	
}
