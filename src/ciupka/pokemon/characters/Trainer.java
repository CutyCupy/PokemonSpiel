package ciupka.pokemon.characters;

import ciupka.pokemon.pokemon.Team;

public abstract class Trainer extends Character {

	private Team team;
	
	private TrainerInformation information;
	
	public Trainer(String name, Location loc) {
		super(name, loc);
		this.team = new Team();
		this.information = new TrainerInformation();
	}
	
}
