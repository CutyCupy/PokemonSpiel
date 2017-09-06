package de.alexanderciupka.pokemon.pokemon;

public enum Stat {
	
	ATTACK("den", "Angriff"), DEFENSE("die", "Verteidigung"), 
	SPECIALATTACK("den", "Spezialangriff"), SPECIALDEFENSE("die", "Spezialverteidigung"), 
	SPEED("die", "Initiative"), HP("die", "KP"), ACCURACY("die", "Genauigkeit"), EVASION("der", "Fluchtwert");
	
	private String text;
	private String article;
	
	Stat(String article, String text) {
		this.text = text;
		this.article = article;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getArticle() {
		return this.article;
	}
	
}
