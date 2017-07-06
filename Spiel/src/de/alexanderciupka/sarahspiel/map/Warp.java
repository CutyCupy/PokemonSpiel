package de.alexanderciupka.sarahspiel.map;

import java.awt.Point;

import de.alexanderciupka.sarahspiel.pokemon.Direction;

public class Warp {

	private String warpString;
	private String oldRoute;
	private String newRoute;

	private Point newPosition;

	public Warp(String warpString, String oldRoute) {
		this.warpString = warpString;
		this.oldRoute = oldRoute;
	}

	public Warp(String warpString, String oldRoute, String newRoute, Point newPosition) {
		this.warpString = warpString;
		this.oldRoute = oldRoute;
		this.newRoute = oldRoute;
		this.newPosition = newPosition;
	}

	public String getOldRoute() {
		return oldRoute;
	}

	public void setOldRoute(String oldRoute) {
		this.oldRoute = oldRoute;
	}

	public String getNewRoute() {
		return newRoute;
	}

	public void setNewRoute(String newRoute) {
		this.newRoute = newRoute;
	}

	public Point getNewPosition() {
		return newPosition;
	}

	public void setNewPosition(Point newPosition) {
		this.newPosition = newPosition;
	}

	public String getWarpString() {
		return this.warpString;
	}
}
