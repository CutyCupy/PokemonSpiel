package de.alexanderciupka.pokemon.fighting;

import java.util.ArrayList;
import java.util.HashMap;

import de.alexanderciupka.pokemon.constants.Moves;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;

public class Field {

	private Weather weather;
	private ArrayList<Hazard> leftHazards;
	private ArrayList<Hazard> rightHazards;

	private ArrayList<Screen> leftScreens;
	private ArrayList<Screen> rightScreens;

	private int trickRoomTurns;

	public Field(Weather w) {
		this.weather = w;
		this.leftHazards = new ArrayList<>();
		this.rightHazards = new ArrayList<>();
		this.leftScreens = new ArrayList<>();
		this.rightScreens = new ArrayList<>();

		this.trickRoomTurns = 0;
	}

	public boolean addHazard(Hazard h, boolean left) {
		if (this.countHazards(h, left) < h.getMax()) {
			(left ? this.leftHazards : this.rightHazards).add(h);
			return true;
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

	public String setWeather(Weather w, Pokemon reason) {
		this.weather = w;
		// TODO: Check for weather item
		return w.startWeather();
	}

	public Weather getWeather() {
		return this.weather;
	}

	public void onEntrance(Pokemon p, boolean left) {
		for (Hazard h : Hazard.values()) {
			if (h.onEntry(this.countHazards(h, left), p)) {
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat die " + h.name() + " entfernt!");
				this.cleanHazards(h, left);
			}
		}
	}

	public boolean addScreen(Screen s, boolean left) {
		if ((left ? this.leftScreens : this.rightScreens).contains(s)
				|| (left ? this.leftScreens : this.rightScreens).contains(Screen.AURORASCHLEIER)) {
			return false;
		}
		switch (s) {
		case AURORASCHLEIER:
			if (!(left ? this.leftScreens : this.rightScreens).isEmpty()) {
				return false;
			}
			break;
		default:
			break;
		}
		(left ? this.leftScreens : this.rightScreens).add(s);
		return true;
	}

	public HashMap<Stat, Double> updateFightStats(Pokemon p, boolean left) {
		HashMap<Stat, Double> result = p.getStats().getFightStats();
		for (Screen s : (left ? this.leftScreens : this.rightScreens)) {
			switch (s) {
			case AURORASCHLEIER:
				result.put(Stat.DEFENSE, result.get(Stat.DEFENSE) * 1.5);
				result.put(Stat.SPECIALDEFENSE, result.get(Stat.SPECIALDEFENSE) * 1.5);
				break;
			case LICHTSCHILD:
				result.put(Stat.SPECIALDEFENSE, result.get(Stat.SPECIALDEFENSE) * 1.5);
				break;
			case REFLEKTOR:
				result.put(Stat.DEFENSE, result.get(Stat.DEFENSE) * 1.5);
				break;
			default:
				break;
			}
		}

		return result;
	}

	public void endOfTurn(Pokemon... participants) {
		for (Pokemon p : participants) {
			this.weather.onEndOfTurn(p);
		}
		this.increaseTurn();
		if (this.weather.getTurns() <= 0) {
			GameController.getInstance().getGameFrame().getFightPanel().addText(this.setWeather(Weather.NONE, null));
		}

		for (int i = 0; i < this.leftScreens.size(); i++) {
			if (this.leftScreens.get(i).getTurns() <= 0) {
				GameController.getInstance().getGameFrame().getFightPanel().addText(this.leftScreens.get(i).onStop());
				this.leftScreens.remove(i);
				i--;
			}
		}
		for (int i = 0; i < this.rightScreens.size(); i++) {
			if (this.rightScreens.get(i).getTurns() <= 0) {
				GameController.getInstance().getGameFrame().getFightPanel().addText(this.rightScreens.get(i).onStop());
				this.rightScreens.remove(i);
				i--;
			}
		}
		if (this.trickRoomTurns == 0) {
			this.stopTrickRoom();
		}
	}

	private void stopTrickRoom() {
		GameController.getInstance().getGameFrame().getFightPanel().addText("Der "
				+ GameController.getInstance().getInformation().getMoveNameById(Moves.BIZARRORAUM) + " hört auf!");
		this.trickRoomTurns = -1;
	}

	private void increaseTurn() {
		this.weather.setTurns(this.weather.getTurns() - 1);
		for (Screen s : this.leftScreens) {
			s.setTurns(s.getTurns() - 1);
		}
		for (Screen s : this.rightScreens) {
			s.setTurns(s.getTurns() - 1);
		}
		this.trickRoomTurns--;
	}

	public boolean isTrickRoom() {
		return this.trickRoomTurns > 0;
	}

	public void addTrickRoom() {
		if (this.isTrickRoom()) {
			this.stopTrickRoom();
		} else {
			this.trickRoomTurns = 5;
			GameController.getInstance().getGameFrame().getFightPanel().addText("Die Dimension wurde gedreht und ein "
					+ GameController.getInstance().getInformation().getMoveNameById(Moves.BIZARRORAUM) + " entstand!");
		}
	}
}
