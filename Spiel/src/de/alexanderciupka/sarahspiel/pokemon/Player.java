package de.alexanderciupka.sarahspiel.pokemon;

import java.util.ArrayList;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.map.Route;

public class Player extends Character {

	private ArrayList<String> routeHistory;

	ArrayList<Item> items;

	public Player() {
		super();
		items = new ArrayList<Item>();
		routeHistory = new ArrayList<String>();
	}

	public Player(String id) {
		super(id);
		items = new ArrayList<Item>();
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

	public ArrayList<String> getRouteHistory() {
		return routeHistory;
	}

	public boolean canCut() {
		return items.contains(Item.CUT);
	}

	public boolean canRocksmash() {
		return items.contains(Item.ROCKSMASH);
	}

	public boolean canSurf() {
		return items.contains(Item.SURF);
	}

	public boolean canStrength() {
		return items.contains(Item.STRENGTH);
	}

	public void addItem(Item reward) {
		this.items.add(reward);
	}

	public boolean hasItem(Item i) {
		return items.contains(i);
	}
}
