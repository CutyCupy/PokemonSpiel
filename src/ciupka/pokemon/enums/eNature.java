package ciupka.pokemon.enums;

public enum eNature {
	
	HARDY(eStat.ATTACK, eStat.ATTACK),
	LONELY(eStat.ATTACK, eStat.DEFENSE),
	ADAMANT(eStat.ATTACK, eStat.SPATTACK),
	NAUGHTY(eStat.ATTACK, eStat.SPDEFENSE),
	BRAVE(eStat.ATTACK, eStat.SPEED),

	BOLD(eStat.DEFENSE, eStat.ATTACK),
	DOCILE(eStat.DEFENSE, eStat.DEFENSE),
	IMPISH(eStat.DEFENSE, eStat.SPATTACK),
	LAX(eStat.DEFENSE, eStat.SPDEFENSE),
	RELAXED(eStat.DEFENSE, eStat.SPEED),

	MODEST(eStat.SPATTACK, eStat.ATTACK),
	MILD(eStat.SPATTACK, eStat.DEFENSE),
	BASHFUL(eStat.SPATTACK, eStat.SPATTACK),
	RASH(eStat.SPATTACK, eStat.SPDEFENSE),
	QUIET(eStat.SPATTACK, eStat.SPEED),

	CALM(eStat.SPDEFENSE, eStat.ATTACK),
	GENTLE(eStat.SPDEFENSE, eStat.DEFENSE),
	CAREFUL(eStat.SPDEFENSE, eStat.SPATTACK),
	QUIRKY(eStat.SPDEFENSE, eStat.SPDEFENSE),
	SASSY(eStat.SPDEFENSE, eStat.SPEED),

	TIMID(eStat.SPEED, eStat.ATTACK),
	HASTY(eStat.SPEED, eStat.DEFENSE),
	JOLLY(eStat.SPEED, eStat.SPATTACK),
	NAIVE(eStat.SPEED, eStat.SPDEFENSE),
	SERIOUS(eStat.SPEED, eStat.SPEED);
	
	private eStat decreased;
	private eStat increased;
	

	public static final double INCREASE_MODIFIER = 1.1;
	public static final double DECREASE_MODIFIER = 0.9;
	
	eNature(eStat increased, eStat decreased) {
		this.decreased = decreased;
		this.increased = increased;
	}
	
	public boolean isNeutral() {
		return this.decreased == this.increased;
	}
	
	public double getModifier(eStat stat) {
		if (!this.isNeutral()) {
			if (stat == this.increased) {
				return INCREASE_MODIFIER;
			} else if (stat == this.decreased) {
				return DECREASE_MODIFIER;
			}
		}
		return 1;
	}
	
	public eStat getDecreased() {
		return this.decreased;
	}
	
	public eStat getIncreased() {
		return this.increased;
	}

	
}
