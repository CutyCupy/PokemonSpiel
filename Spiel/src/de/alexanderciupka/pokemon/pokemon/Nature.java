package de.alexanderciupka.pokemon.pokemon;

import java.util.Random;

public enum Nature {

	LONELY(Stat.ATTACK, Stat.DEFENSE), BRAVE(Stat.ATTACK, Stat.SPEED), ADAMANT(Stat.ATTACK, Stat.SPECIALATTACK), NAUGHTY(Stat.ATTACK, Stat.SPECIALDEFENSE),
	BOLD(Stat.DEFENSE, Stat.ATTACK), RELAXED(Stat.DEFENSE, Stat.SPEED), IMPISH(Stat.DEFENSE, Stat.SPECIALATTACK), LAX(Stat.DEFENSE, Stat.SPECIALDEFENSE),
	MODEST(Stat.SPECIALATTACK, Stat.ATTACK), MILD(Stat.SPECIALATTACK, Stat.DEFENSE), QUIET(Stat.SPECIALATTACK, Stat.SPEED), RASH(Stat.SPECIALATTACK, Stat.SPECIALDEFENSE),
	CALM(Stat.SPECIALDEFENSE, Stat.ATTACK), GENTLE(Stat.SPECIALDEFENSE, Stat.DEFENSE), SASSY(Stat.SPECIALDEFENSE, Stat.SPEED), CAREFUL(Stat.SPECIALDEFENSE, Stat.SPECIALATTACK),
	TIMID(Stat.SPEED, Stat.ATTACK), HASTY(Stat.SPEED, Stat.DEFENSE), JOLLY(Stat.SPEED, Stat.SPECIALATTACK), NAIVE(Stat.SPEED, Stat.SPECIALDEFENSE),
	HARDY(null, null), DOCILE(null, null), SERIOUS(null, null), BASHFUL(null, null), QUIRKY(null, null);


	private Stat increase;
	private Stat decrease;


	public static final double DECREASE_FACTOR = 0.9;
	public static final double INCREASE_FACTOR = 1.1;

	public static final java.awt.Color DECREASE_COLOR = java.awt.Color.BLUE;
	public static final java.awt.Color INCREASE_COLOR = java.awt.Color.RED;

	private static Random rng;


	Nature(Stat increase, Stat decrease) {
		this.increase = increase;
		this.decrease = decrease;
	}


	public Stat getIncrease() {
		return increase;
	}


	public Stat getDecrease() {
		return decrease;
	}

	public boolean hasChange() {
		return this.decrease != null && this.increase != null;
	}


	public static Nature getRandomNature() {
		if(rng == null) {
			rng = new Random();
		}
		return Nature.values()[rng.nextInt(Nature.values().length)];
	}


}
