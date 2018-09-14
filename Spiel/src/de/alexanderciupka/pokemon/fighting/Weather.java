package de.alexanderciupka.pokemon.fighting;

import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Moves;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;
import de.alexanderciupka.pokemon.pokemon.Type;

public enum Weather {

	RAIN, SANDSTORM, HAIL, SUN, FOG, NONE;

	int turns = 5;

	String startWeather() {
		this.turns = 5;
		Weather w = Weather.valueOf(this.name());
		switch (w) {
		case RAIN:
			return "Es beginnt zu regnen.";
		case SANDSTORM:
			return "Ein Sandsturm zieht auf.";
		case HAIL:
			return "Es beginnt zu hageln.";
		case SUN:
			return "Die Sonne fängt an zu scheinen.";
		case FOG:
			return "Dichter Nebel zieht auf.";
		case NONE:
			return null;
		}
		return null;
	}

	void increase() {
		this.turns = 8;
	}

	int getTurns() {
		return this.turns;
	}

	void setTurns(int turns) {
		this.turns = turns;
	}

	String onStop() {
		Weather w = Weather.valueOf(this.name());
		switch (w) {
		case RAIN:
			return "Der Regen lässt nach.";
		case SANDSTORM:
			return "Der Sandsturm lässt nach.";
		case HAIL:
			return "Der Hagel lässt nach.";
		case SUN:
			return "Die Sonne lässt nach.";
		case FOG:
			return "Der Nebel lässt nach.";
		case NONE:
			return null;
		}
		return null;
	}

	int getPower(Pokemon user, Move m) {
		Weather w = Weather.valueOf(this.name());
		double power = m.getPower();
		Type type = m.getMoveType(user);

		if (Moves.METEOROLOGE == m.getId() && w != Weather.NONE) {
			power = 100;
		}

		switch (w) {
		case RAIN:
			if (type.equals(Type.WATER)) {
				power *= 1.5;
			} else if (type.equals(Type.FIRE)) {
				power *= .5;
			}
		case SANDSTORM:
			if (Moves.SOLARSTRAHL == m.getId()) {
				power *= .5;
			} else if (Abilities.SANDGEWALT == user.getAbility().getId()) {
				if (Type.ROCK.equals(type) || Type.STEEL.equals(type) || Type.GROUND.equals(type)) {
					power *= 1.3;
				}
			}
		case SUN:
			if (type.equals(Type.FIRE)) {
				power *= 1.5;
			} else if (type.equals(Type.WATER)) {
				power *= .5;
			}
			break;
		default:
			break;
		}
		return (int) Math.round(power);
	}

	void onEndOfTurn(Pokemon p) {
		if(p == null) {
			return;
		}
		switch (Weather.valueOf(this.name())) {
		case HAIL:
			if (!p.hasType(Type.ICE) && p.getAbility().getId() != Abilities.SCHNEEMANTEL
					&& p.getAbility().getId() != Abilities.EISHAUT && p.getAbility().getId() != Abilities.WETTERFEST) {
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 16.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat sich durch den Hagel verletzt!", true);
			} else if (p.getAbility().getId() == Abilities.EISHAUT) {
				p.getStats().restoreHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 16.0));
				SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " freut sich über den Hagelsturm!", true);
			}
			break;
		case RAIN:
			if (Abilities.TROCKENHEIT == p.getAbility().getId() || Abilities.REGENGENUSS == p.getAbility().getId()) {
				p.getStats().restoreHP((int) Math.round(p.getStats().getStats().get(Stat.HP)
						/ (Abilities.TROCKENHEIT == p.getAbility().getId() ? 8.0 : 16.0)));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " freut sich über den Regen!", true);
			}
			break;
		case SANDSTORM:
			if (!(p.hasType(Type.ROCK) || p.hasType(Type.STEEL) || p.hasType(Type.GROUND))
					&& p.getAbility().getId() != Abilities.WETTERFEST
					&& p.getAbility().getId() != Abilities.SANDSCHARRER
					&& p.getAbility().getId() != Abilities.SANDGEWALT) {
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 16.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat sich durch den Sandsturm verletzt!", true);
			}
			break;
		case SUN:
			switch (p.getAbility().getId()) {
			case Abilities.TROCKENHEIT:
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 8.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " tut die Sonne nicht gut!", true);
				break;
			case Abilities.SOLARKRAFT:
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 8.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " nutzt die Kraft der Sonne!", true);
				break;
			}
			break;
		default:
			break;
		}
		GameController.getInstance().getGameFrame().getFightPanel().updateFight();
		GameController.getInstance().getGameFrame().getFightPanel().getTextLabel().waitText();
	}

	float getHealing(Move m) {
		float heal = m.getHealing();

		switch (Weather.valueOf(this.name())) {
		case SUN:
			if (Moves.MONDSCHEIN == m.getId() || Moves.MONDSCHEIN == m.getId() || Moves.MONDSCHEIN == m.getId()) {
				heal = 0.66f;
			}
			break;
		case RAIN:
			if (Moves.MONDSCHEIN == m.getId() || Moves.MONDSCHEIN == m.getId() || Moves.MONDSCHEIN == m.getId()) {
				heal = 0.25f;
			}
			break;
		default:
			break;
		}

		return heal;
	}
}
