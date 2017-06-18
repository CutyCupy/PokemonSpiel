package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Color;

public enum Ailment {

	NONE, BURN, FREEZE,
	PARALYSIS, SLEEP, CONFUSION,
	POISON;




	public static String getText(Ailment ailment) {
		switch(ailment) {
		case BURN:
			return "verbrannt";
		case CONFUSION:
			return "verwirrt";
		case FREEZE:
			return "eingefroren";
		case PARALYSIS:
			return "paralysiert";
		case POISON:
			return "vergiftet";
		case SLEEP:
			return "eingeschlafen";
		default:
			return "";
		}
	}

	public static String getShorttext(Ailment ailment) {
		switch(ailment) {
		case BURN:
			return "BRT";
		case CONFUSION:
			return "VWR";
		case FREEZE:
			return "GFR";
		case PARALYSIS:
			return "PAR";
		case POISON:
			return "GIF";
		case SLEEP:
			return "SLF";
		default:
			return "NONE";
		}
	}

	public static Color getColor(Ailment ailment) {
		switch(ailment) {
		case BURN:
			return Type.getColor(Type.FIRE);
		case CONFUSION:
			return Type.getColor(Type.PSYCHO);
		case FREEZE:
			return Type.getColor(Type.ICE);
		case PARALYSIS:
			return Type.getColor(Type.ELECTRIC);
		case POISON:
			return Type.getColor(Type.POISON);
		case SLEEP:
			return Type.getColor(Type.NORMAL);
		default:
			return new Color(0, 0, 0);
		}
	}
}
