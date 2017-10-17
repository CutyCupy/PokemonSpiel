package de.alexanderciupka.pokemon.map;

import java.awt.Point;

import de.alexanderciupka.pokemon.characters.Direction;

public class Warp {

	private String warpString;
	private String oldRoute;
	private String newRoute;

	private Point newPosition;
	private Direction newDirection;
	
	public Warp(String warpString, String oldRoute) {
		this.warpString = warpString;
		this.oldRoute = oldRoute;
	}

	public Warp(String warpString, String oldRoute, String newRoute, Point newPosition, Direction newDirection) {
		this.warpString = warpString;
		this.oldRoute = oldRoute;
		this.newRoute = oldRoute;
		this.newPosition = newPosition;
		this.newDirection = newDirection;
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
	
	public void setNewDirection(Direction d) {
		this.newDirection = d;
	}
	
	public void setNewDirection(String d) {
		try {
			this.newDirection = Direction.valueOf(d.toUpperCase());
		} catch(Exception e) {
			this.newDirection = Direction.NONE;
		}
	}
	
	public Direction getNewDirection() {
		return this.newDirection;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Warp) {
			Warp other = (Warp) obj;
			return (this.warpString.equals(other.warpString) && 
					this.oldRoute.equals(other.oldRoute) && this.newRoute.equals(other.newRoute) && 
					this.newPosition.equals(other.newPosition) && this.newDirection.equals(other.newDirection));
		}
		return false;
	}
	
	@Override
	public Warp clone() {
		return new Warp(this.warpString, this.oldRoute, this.newRoute, new Point(this.newPosition), this.newDirection);
	}
}
