package ciupka.pokemon.enums;

public enum eStat {
	ATTACK, DEFENSE, SPATTACK, SPDEFENSE, SPEED, HP, EVASIVENESS, ACCURACY;
	
	
	public boolean isFightOnly() {
		return this == EVASIVENESS || this == ACCURACY;
	}
	
	public int getStatValue(int base, int iv, int ev, int level, int modifyValue, eNature nature) {
		switch(this) {
		case ATTACK:
		case DEFENSE:
		case SPATTACK:
		case SPDEFENSE:
		case SPEED:
			int value = (int) Math.floor((Math.floor(((2 * base + iv + Math.floor(ev / 4.0)) * level) / 100.0) + 5) * nature.getModifier(this));
			return (int) (value * (modifyValue > 0 ? (2 + modifyValue) / 2.0 : 2.0 / (2 + modifyValue))); 
		case HP:
			return (int) Math.floor(((2 * base + iv + Math.floor(ev / 4.0)) * level) / 100.0) + level + 10;
		default:
			// TODO: Evasiveness + ACCURACY;
			return 0;
		}
	}

}
