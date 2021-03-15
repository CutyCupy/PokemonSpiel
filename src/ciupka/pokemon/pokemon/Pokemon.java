package ciupka.pokemon.pokemon;

import java.util.Map;

import ciupka.pokemon.enums.eGender;
import ciupka.pokemon.enums.eStat;

public class Pokemon {

	private int id;

	private String name;
	private eGender gender;
	public boolean isShiny;
	private Map<eStat, Integer> stats;

}
