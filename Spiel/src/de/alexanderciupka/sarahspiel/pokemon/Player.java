package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Point;
import java.util.ArrayList;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.map.Route;

public class Player extends Character {

	private ArrayList<String> routeHistory;
	
	private boolean cut;
	private boolean rocksmash;
	private boolean surf;
	private boolean strength;

	public Player() {
		super();
		routeHistory = new ArrayList<String>();
	}

	public Player(String id) {
		super(id);
		routeHistory = new ArrayList<String>();
	}

	@Override
	public void setCurrentRoute(Route currentRoute) {
		routeHistory.add(currentRoute.getId());
		super.setCurrentRoute(currentRoute);
	}

	public void warpToPokemonCenter() {
		this.setCurrentRoute(this.getLastPokemonCenter());
		gController.setCurrentRoute(this.getCurrentRoute());
		team.restoreTeam();
	}

	public Route getLastPokemonCenter() {
		String routeID = "zuhause";
		int x = 2;
		int y = 0;
		Direction d = Direction.DOWN;
		for(int centerIndex = this.routeHistory.size() - 1; centerIndex >= 0; centerIndex--) {
			if(this.routeHistory.get(centerIndex).equals("pokemon_center")) {
				routeID = this.routeHistory.get(centerIndex);
				x = 2;
				y = 1;
				d = Direction.UP;
				break;
			}
		}
		setCurrentPosition(x, y);
		setCurrentDirection(d);
		return GameController.getInstance().getRouteAnalyzer().getRouteById(routeID);
	}

	public Point getInteractionPoint() {
		switch (currentDirection) {
		case DOWN:
			return new Point(currentPosition.x, currentPosition.y + 1);
		case UP:
			return new Point(currentPosition.x, currentPosition.y - 1);
		case LEFT:
			return new Point(currentPosition.x - 1, currentPosition.y);
		case RIGHT:
			return new Point(currentPosition.x + 1, currentPosition.y);
		}
		return null;
	}

	public ArrayList<String> getRouteHistory() {
		return routeHistory;
	}

	public boolean canCut() {
		return cut;
	}

	public void setCut(boolean cut) {
		this.cut = cut;
	}

	public boolean canRocksmash() {
		return rocksmash;
	}

	public void setRocksmash(boolean rocksmash) {
		this.rocksmash = rocksmash;
	}

	public boolean canSurf() {
		return surf;
	}

	public void setSurf(boolean surf) {
		this.surf = surf;
	}

	public boolean canStrength() {
		return strength;
	}

	public void setStrength(boolean strength) {
		this.strength = strength;
	}
}
