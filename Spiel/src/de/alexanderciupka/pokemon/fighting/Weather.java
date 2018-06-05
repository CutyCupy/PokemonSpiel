package de.alexanderciupka.pokemon.fighting;

import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Moves;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
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
		Type type = m.getMoveType();

		if (Moves.METEOROLOGE.equals(m.getName()) && w != Weather.NONE) {
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
			if (Moves.SOLARSTRAHL.equals(m.getName())) {
				power *= .5;
			} else if (Abilities.SANDGEWALT.equals(user.getAbility().getName())) {
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

	// Type getMoveType(Move m) {
	// Type type = m.getMoveType();
	// if (Moves.METEOROLOGE.equals(m.getName())) {
	// switch (Weather.valueOf(this.name())) {
	// case HAIL:
	// type = Type.ICE;
	// break;
	// case RAIN:
	// type = Type.WATER;
	// break;
	// case SANDSTORM:
	// type = Type.ROCK;
	// break;
	// case SUN:
	// type = Type.FIRE;
	// break;
	// default:
	// break;
	// }
	// }
	// return type;
	// }

	// Type[] getPokemonTypes(Pokemon p) {
	// Type[] types = p.getTypes();
	// Weather w = Weather.valueOf(this.name());
	// if (PokemonNames.FORMEO == p.getId()) {
	// switch (w) {
	// case HAIL:
	// types = new Type[] { Type.ICE, null };
	// break;
	// case RAIN:
	// types = new Type[] { Type.WATER, null };
	// break;
	// case SANDSTORM:
	// types = new Type[] { Type.ROCK, null };
	// break;
	// case SUN:
	// types = new Type[] { Type.FIRE, null };
	// break;
	// default:
	// break;
	// }
	// }
	// return types;
	// }

	// float getAccuracy(Pokemon user, Move m) {
	// Weather w = Weather.valueOf(this.name());
	//
	// float accuracy = m.getAccuracy();
	//
	// switch (w) {
	// case FOG:
	// accuracy *= 0.6;
	// break;
	// case HAIL:
	// if (Moves.BLIZZARD.equals(m.getName())) {
	// accuracy = 1f;
	// }
	// break;
	// case RAIN:
	// if (Moves.DONNER.equals(m.getName()) || Moves.ORKAN.equals(m.getName())) {
	// accuracy = 1f;
	// }
	// break;
	// case SUN:
	// if (Moves.DONNER.equals(m.getName()) || Moves.ORKAN.equals(m.getName())) {
	// accuracy = 0.5f;
	// }
	// break;
	// default:
	// break;
	//
	// }
	// return accuracy;
	// }

	void onEndOfTurn(Pokemon p) {
		switch (Weather.valueOf(this.name())) {
		case HAIL:
			if (!p.hasType(Type.ICE)) {
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 16.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat sich durch den Hagel verletzt!", true);
			} else {
				if (Abilities.EISHAUT.equals(p.getAbility().getName())) {
					p.getStats().restoreHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 16.0));
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
					GameController.getInstance().getGameFrame().getFightPanel()
							.addText(p.getName() + " freut sich über den Hagelsturm!", true);
				}
			}
			break;
		case NONE:
			break;
		case RAIN:
			if (Abilities.TROCKENHEIT.equals(p.getAbility().getName())
					|| Abilities.REGENGENUSS.equals(p.getAbility().getName())) {
				p.getStats().restoreHP((int) Math.round(p.getStats().getStats().get(Stat.HP)
						/ (Abilities.TROCKENHEIT.equals(p.getAbility().getName()) ? 8.0 : 16.0)));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " freut sich über den Regen!", true);
			} else if (Abilities.HYDRATION.equals(p.getAbility().getName())) {
				if (p.getAilment() != Ailment.NONE) {
					p.setAilment(Ailment.NONE);
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
					GameController.getInstance().getGameFrame().getFightPanel()
							.addText(p.getName() + " wurde durch den Regen geheilt!", true);
				}
			}
			break;
		case SANDSTORM:
			if (!(p.hasType(Type.ROCK) || p.hasType(Type.STEEL) || p.hasType(Type.GROUND))) {
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 16.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " hat sich durch den Sandsturm verletzt!", true);
			}
			break;
		case SUN:
			if (Abilities.TROCKENHEIT.equals(p.getAbility().getName())) {
				p.getStats().loseHP((int) Math.round(p.getStats().getStats().get(Stat.HP) / 8.0));
				SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(p.getName() + " tut die Sonne nicht gut!", true);
			}
			break;
		default:
			break;
		}
	}

	float getHealing(Move m) {
		float heal = m.getHealing();

		switch (Weather.valueOf(this.name())) {
		case SUN:
			if (Moves.MONDSCHEIN.equals(m.getName()) || Moves.MONDSCHEIN.equals(m.getName())
					|| Moves.MONDSCHEIN.equals(m.getName())) {
				heal = 0.66f;
			}
			break;
		case SANDSTORM:
			if (Moves.SANDSAMMLER.equals(m.getName())) {
				heal = 0.66f;
			}
		case RAIN:
			if (Moves.MONDSCHEIN.equals(m.getName()) || Moves.MONDSCHEIN.equals(m.getName())
					|| Moves.MONDSCHEIN.equals(m.getName())) {
				heal = 0.25f;
			}
			break;
		default:
			break;
		}

		return heal;
	}

	// int getSpeed(Pokemon p) {
	// double init = p.getStats().getFightStats().get(Stat.SPEED);
	// switch (Weather.valueOf(this.name())) {
	//
	// case HAIL:
	// if (Abilities.SCHNEESCHARRER.equals(p.getAbility().getName())) {
	// init *= 2;
	// }
	// break;
	// case RAIN:
	// if (Abilities.WASSERTEMPO.equals(p.getAbility().getName())) {
	// init *= 2;
	// }
	// break;
	// case SANDSTORM:
	// if (Abilities.SANDSCHARRER.equals(p.getAbility().getName())) {
	// init *= 2;
	// }
	// break;
	// case SUN:
	// if (Abilities.CHLOROPHYLL.equals(p.getAbility().getName())) {
	// init *= 2;
	// }
	// break;
	// default:
	// break;
	// }
	//
	// return (int) Math.round(init);
	// }
	//
	// int getSpecialDefense(Pokemon p) {
	//
	// double spdef = p.getStats().getFightStats().get(Stat.SPECIALDEFENSE);
	//
	// switch (Weather.valueOf(this.name())) {
	// case SANDSTORM:
	// if (p.hasType(Type.ROCK)) {
	// spdef *= 1.5;
	// }
	// break;
	// case SUN:
	// if (Abilities.PFLANZENGABE.equals(pokemon.getAbility().getName())) {
	// spdef *= 1.5;
	// }
	// break;
	// default:
	// break;
	// }
	//
	// return (int) Math.round(spdef);
	// }
	//
	// int getDefense(Pokemon p) {
	// double def = pokemon.getStats().getFightStats().get(Stat.DEFENSE);
	// switch (Weather.valueOf(this.name())) {
	// case FOG:
	// break;
	// case HAIL:
	// break;
	// case NONE:
	// break;
	// case RAIN:
	// break;
	// case SANDSTORM:
	// break;
	// case SUN:
	// break;
	// default:
	// break;
	// }
	// return (int) Math.round(def);
	// }
	//
	// int getSpecialAttack(Pokemon p) {
	// double spatk = pokemon.getStats().getFightStats().get(Stat.SPECIALATTACK);
	// switch (Weather.valueOf(this.name())) {
	// case SUN:
	// if (Abilities.SOLARKRAFT.equals(pokemon.getAbility().getName())) {
	// spatk *= 1.5;
	// }
	// break;
	// default:
	// break;
	// }
	// return (int) Math.round(spatk);
	// }
	//
	// int getAttack(Pokemon p) {
	// double atk = pokemon.getStats().getFightStats().get(Stat.ATTACK);
	// switch (Weather.valueOf(this.name())) {
	// case SUN:
	// if (Abilities.PFLANZENGABE.equals(pokemon.getAbility().getName())) {
	// atk *= 1.5;
	// }
	// break;
	// default:
	// break;
	// }
	// return (int) Math.round(atk);
	// }
	//
	// int getEvasion(Pokemon p) {
	//
	// double evasion = pokemon.getStats().getFightStats().get(Stat.EVASION);
	//
	// switch(Weather.valueOf(this.name()))
	// {
	// case HAIL:
	// if (Abilities.SCHNEEMANTEL.equals(pokemon.getAbility().getName())) {
	// this.evasion *= 1.2;
	// }
	// break;
	// case SANDSTORM:
	// if (Abilities.SANDSCHLEIER.equals(pokemon.getAbility().getName())) {
	// this.evasion *= 1.2;
	// }
	// break;
	// default:
	// break;
	// }return(int)Math.round(evasion);
	// }
	//
	// boolean isStatusable(Pokemon p) {
	// switch (Weather.valueOf(this.name())) {
	// case SUN:
	// if (Abilities.FLORASCHILD.equals(p.getAbility().getName())) {
	// return false;
	// }
	// default:
	// return true;
	// }
	// }

}
