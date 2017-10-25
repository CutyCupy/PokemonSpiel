package de.alexanderciupka.pokemon.map.entities;

public enum QuestionType {

	SINGLE_CHOICE("Es ist EINE Antwortmöglichkeit richtig!"),
	MULTIPLE_CHOICE("Es können mehrere Antwortmöglichkeiten richtig sein!"),
	SOUND_GAME("Erkenne am Songausschnitt Künstler und Songtitel!");


	private String information;

	QuestionType(String info) {
		this.information = info;
	}

	public String getInformation() {
		return this.information;
	}
}
