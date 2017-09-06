package de.alexanderciupka.pokemon.pokemon;

public enum Gender {
	MALE(""),
	FEMALE("f"),
	GENDERLESS("");
	
	private String saveText;
	
	Gender(String saveText) {
		this.saveText = saveText;
	}
	
	public String getText() {
		return this.saveText;
	}
}
