package de.alexanderciupka.pokemon.fighting;

import java.util.ArrayList;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class Field {

	private Weather weather;
	private int weatherTurns;
	private ArrayList<Hazard> leftHazards;
	private ArrayList<Hazard> rightHazards;

	public Field(Weather w) {
		this.weather = w;
		this.leftHazards = new ArrayList<>();
		this.rightHazards = new ArrayList<>();
	}

	public boolean addHazard(Hazard h, boolean left) {
		if (this.countHazards(h, left) < h.getMax()) {
			(left ? this.leftHazards : this.rightHazards).add(h);
		}
		return false;
	}

	private int countHazards(Hazard h, boolean left) {
		int counter = 0;
		for (Hazard hazard : left ? this.leftHazards : this.rightHazards) {
			if (h.equals(hazard)) {
				counter++;
			}
		}
		while (counter > h.getMax()) {
			(left ? this.leftHazards : this.rightHazards).remove(h);
			counter--;
		}
		return counter;
	}

	public void cleanHazards(Hazard h, boolean left) {
		while ((left ? this.leftHazards : this.rightHazards).contains(h)) {
			(left ? this.leftHazards : this.rightHazards).remove(h);
		}
	}

	public boolean cleanHazards(boolean left) {
		boolean result = false;
		for (Hazard h : Hazard.values()) {
			this.cleanHazards(h, left);
			result = true;
		}
		return result;
	}

	public String setWeather(Weather w) {
		this.weather = w;
		this.weatherTurns = 0;
		return w.startWeather();
	}

	public Weather getWeather() {
		return this.weather;
	}

	public String onEntrance(Pokemon p) {

		return null;
	}

	public void endOfTurn(Pokemon first, Pokemon second) {
		this.weather.onEndOfTurn(first);
		this.weather.onEndOfTurn(second);
		this.weatherTurns++;
		if (this.weatherTurns > 5) {
			GameController.getInstance().getGameFrame().getFightPanel().addText(this.setWeather(Weather.NONE));
		}
	}
}
