package de.alexanderciupka.pokemon.fighting;

public enum Screen {
	LICHTSCHILD, REFLEKTOR, AURORASCHLEIER;

	int turns = 5;

	String setScreen() {
		this.turns = 5;
		switch (Screen.valueOf(this.name())) {
		case LICHTSCHILD:
			return "Lichtschild stärkt Pokemon, die auf deiner Seite kämpfen, gegen Spezialangriffe!";
		case REFLEKTOR:
			return "Reflektor stärkt Pokemon, die auf deiner Seite kämpfen, gegen Angriffe!";
		case AURORASCHLEIER:
			return "Auroraschleier stärkt Pokemon, die auf deiner Seite kämpfen, gegen alle Angriffe!";
		}
		return null;
	}

	void increase() {
		this.turns = 8;
	}

	String onStop() {
		switch (Screen.valueOf(this.name())) {
		case LICHTSCHILD:
			return "Das Lichtschild verschwindet!";
		case REFLEKTOR:
			return "Der Reflektor verschwindet!";
		case AURORASCHLEIER:
			return "Der Auroraschleier verschwindet!";
		}
		return null;
	}

	int getTurns() {
		return this.turns;
	}

	void setTurns(int turns) {
		this.turns = turns;
	}
}
