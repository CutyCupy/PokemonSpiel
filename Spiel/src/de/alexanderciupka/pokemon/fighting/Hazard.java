package de.alexanderciupka.pokemon.fighting;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;
import de.alexanderciupka.pokemon.pokemon.Type;

public enum Hazard {

	STEALTH_ROCK(1), TOXIC_SPIKES(2), SPIKES(3), STICKY_WEB(1);

	int max;

	Hazard(int max) {
		this.max = max;
	}

	public int getMax() {
		return this.max;
	}

	boolean onEntry(int amount, Pokemon p) {
		if (amount == 0) {
			return false;
		}
		double damage = 0;
		switch (Hazard.valueOf(this.name())) {
		case SPIKES:
			if (Type.getEffectiveness(Type.GROUND, p) > Type.USELESS) {
				damage = 0.125 * ((2.0 + (amount - 1)) / 2.0);
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) * damage));
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat sich an den Stacheln verletzt!", true);
			}
			break;
		case STEALTH_ROCK:
			damage = 0.125 * Type.getEffectiveness(Type.ROCK, p);
			if (damage > 0) {
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) * damage));
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " wird durch die spitzen Steine auf dem Boden verletzt!", true);
			}
			break;
		case TOXIC_SPIKES:
			if (p.hasType(Type.POISON)) {
				return true;
			} else if (Type.getEffectiveness(Type.POISON, p) > Type.USELESS) {
				if (p.setAilment((amount == 1 ? Ailment.POISON : Ailment.HEAVY_POISON))) {
					GameController.getInstance().getGameFrame().getFightPanel().addText(p.getName()
							+ " ist durch die herumliegenden Giftspitzen " + p.getAilment().getInflictedTurn() + "!",
							true);
				}
			}
			break;
		case STICKY_WEB:
			if (Type.getEffectiveness(Type.GROUND, p) > Type.USELESS) {
				p.getStats().decreaseStat(Stat.SPEED, 1);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat sich an den Stacheln verletzt!", true);
			}
			break;
		}
		return false;
	}
}
