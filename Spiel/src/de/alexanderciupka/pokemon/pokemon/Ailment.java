package de.alexanderciupka.pokemon.pokemon;

import java.awt.Color;

import de.alexanderciupka.pokemon.map.GameController;

public enum Ailment {

	NONE, BURN, FREEZE, PARALYSIS, SLEEP, POISON, HEAVY_POISON, FAINTED;

	private int inflictedTurn;

	Ailment() {
		this.inflictedTurn = GameController.getInstance().getFight() != null
				? GameController.getInstance().getFight().getTurn()
				: -1;
	}

	public static String getText(Ailment ailment) {
		switch (ailment) {
		case BURN:
			return "verbrannt";
		case FREEZE:
			return "eingefroren";
		case PARALYSIS:
			return "paralysiert";
		case POISON:
			return "vergiftet";
		case HEAVY_POISON:
			return "stark vergiftet";
		case SLEEP:
			return "eingeschlafen";
		default:
			return "";
		}
	}

	public static String getShorttext(Ailment ailment) {
		switch (ailment) {
		case BURN:
			return "BRT";
		case FREEZE:
			return "GFR";
		case PARALYSIS:
			return "PAR";
		case POISON:
		case HEAVY_POISON:
			return "GIF";
		case SLEEP:
			return "SLF";
		case FAINTED:
			return "BSG";
		default:
			return "NONE";
		}
	}

	public static Color getColor(Ailment ailment) {
		switch (ailment) {
		case BURN:
			return Type.getColor(Type.FIRE);
		case FREEZE:
			return Type.getColor(Type.ICE);
		case PARALYSIS:
			return Type.getColor(Type.ELECTRIC);
		case HEAVY_POISON:
		case POISON:
			return Type.getColor(Type.POISON);
		case SLEEP:
			return Type.getColor(Type.NORMAL);
		case FAINTED:
			return Type.getColor(Type.FIGHTING);
		default:
			return new Color(0, 0, 0);
		}
	}

	public int getInflictedTurn() {
		return this.inflictedTurn;
	}
}
