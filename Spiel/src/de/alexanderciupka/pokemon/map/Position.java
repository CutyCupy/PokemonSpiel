package de.alexanderciupka.pokemon.map;

public class Position {
	
	private Route route;
	private int x;
	private int y;
	
	
	public Position(Route route, int x, int y) {
		this.route = route;
		this.x = x;
		this.y = y;
	}


	public Route getRoute() {
		return route;
	}


	public void setRoute(Route route) {
		this.route = route;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}
	
	

}
