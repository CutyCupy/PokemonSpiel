package ciupka.pokemon.characters;

public class TrainerInformation {

	// Money
	private int money;
	
	// Fighting
	private int fightsWon;
	private int fightsLost;
	
	
	public TrainerInformation() {
		
	}


	public int getMoney() {
		return money;
	}


	public void setMoney(int money) {
		this.money = money;
	}


	public int getFightsWon() {
		return fightsWon;
	}


	public void setFightsWon(int fightsWon) {
		this.fightsWon = fightsWon;
	}


	public int getFightsLost() {
		return fightsLost;
	}


	public void setFightsLost(int fightsLost) {
		this.fightsLost = fightsLost;
	}
	
}
