package de.alexanderciupka.pokemon.pokemon;

import de.alexanderciupka.pokemon.map.GameController;

public enum SecondaryAilment {

	HEALBLOCK(5, 5, "@pokemon kann sich wieder heilen!", "@pokemon kann sich nicht heilen!", "Heilung von @pokemon wird verhindert!"),
	TORMENT(4, 4, "@pokemon hat sich vom Folterknecht befreit!", "@pokemon kann diese Attacke nicht einsetzen!", "@pokemon wird von Folterknecht unterworfen."),
	YAWN("@pokemon ist nicht mehr müde!", "@pokemon ist eingeschlafen!", "@pokemon wurde schläfrig gemacht!"),
	NOTYPEIMMUNITY("", "", "@pokemon wurde erkannt!"),
	NIGHTMARE("@pokemon hat sich von dem Alptraum erholt!", "@pokemon nimmt Schadem vom Alptraum", "@pokemon hat einen Alptraum!"),
	INFATUATION("@pokemon ist nicht mehr verliebt!", "@pokemon ist starr vor Liebe!", "@pokemon hat sich in @enemy verliebt!"),
	LEECHSEED("", "@pokemon wurde Energie abgesaugt!", "@pokemon wurde bepflanzt!"),
	UNKNOWN("", "", ""),
	PERISHSONG(3, 3, "", "Irgendwas mit Timer", "Perish Song für @pokemon"),
	INGRAIN("", "@pokemon nimmt über seine Wurzeln Nährstoffe auf!", "@pokemon pflanzt seine Wurzeln!"),
	DISABLE("@pokemon kann @move wieder einsetzen!", "@pokemon kann momentan @move nicht einsetzen!", "@move von @pokemon wurde blockiert!"),
	TRAP(4, 5, "@pokemon ist nicht mehr gefangen!", "@pokemon erleidet Schaden!", "@pokemon ist gefangen!"),
	EMBARGO("@pokemon kann wieder Items einsetzen!", "@pokemon kann gerade kein Item einsetzen!", "@pokemon kann keine Items mehr einsetzen!"),
	CONFUSION("@pokemon ist nicht mehr verwirrt!", "Es hat sich vor Verwirrung selbst verletzt!", "@pokemon ist verwirrt!"),
	PROTECTED(0, 0, null, "@pokemon schützt sich selbst!", "@pokemon schützt sich selbst!"),
	FLINCH(0, 0, null, "@pokemon schreckt zurück!", null),
	MAGICCOAT(0, 0, null, "@pokemon wird von einem Magiemantel geschützt!", "@pokemon schützt sich mit einem Magiemantel!");


	private int inflictedTurn;

	private boolean wearOff;
	private int minTurns;
	private int maxTurns;

	private String healed;
	private String affected;
	private String onHit;

	SecondaryAilment(String healed, String affected, String onHit) {
		inflictedTurn = GameController.getInstance().getFight() != null ? GameController.getInstance().getFight().getTurn() : -1;

		this.healed = healed;
		this.affected = affected;
		this.onHit = onHit;

		this.wearOff = false;
	}

	SecondaryAilment(int minTurns, int maxTurns, String healed, String affected, String onHit) {
		inflictedTurn = GameController.getInstance().getFight() != null ? GameController.getInstance().getFight().getTurn() : -1;

		this.minTurns = minTurns;
		this.maxTurns = maxTurns;

		this.healed = healed;
		this.affected = affected;
		this.onHit = onHit;

		this.wearOff = true;
	}

	public void inflict() {
		this.inflictedTurn = GameController.getInstance().getFight().getTurn();
	}

	public int getInflictedTurn() {
		return this.inflictedTurn;
	}

	public boolean isWearOff() {
		return wearOff;
	}

	public int getMinTurns() {
		return minTurns;
	}

	public int getMaxTurns() {
		return maxTurns;
	}

	public String getHealed() {
		return healed;
	}

	public String getAffected() {
		return affected;
	}

	public String getOnHit() {
		return onHit;
	}

}
