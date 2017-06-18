package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Color;
import java.util.Random;

import de.alexanderciupka.sarahspiel.gui.After;
import de.alexanderciupka.sarahspiel.map.GameController;

//TODO: Unique stats for different Pokemons
public class Stats {

	private final short BASE_VALUE = 5;
	public static final String[] STAT_NAMES = {"Angriff","Verteidigung","Spezialangriff","Spezialverteidigung","Initiative"};

	private Pokemon pokemon;
	private short level;
	// HP,ATTACK,DEFENSE,SPATTACK,SPDEFENSE,SPEED
	private short[] stats;
	private short[] fightStats;
	private short[] fightStatsChanges;
	private int currentXP;
	private int levelUpXP;
	private int allTimeXP;
	private short currentHP;
	private GameController gController;
	private boolean generated;

	private Random rng;

	public Stats(Pokemon pokemon) {
		this.pokemon = pokemon;
		rng = new Random();
		level = 1;
		levelUpXP = calculateLevelUpXP();
		currentXP = 0;
		allTimeXP = 0;
		stats = new short[6];
		for (int i = 0; i < stats.length; i++) {
			stats[i] = BASE_VALUE;
		}
		stats[0] *= 2;
		this.currentHP = stats[0];
		gController = GameController.getInstance();
	}

	public Stats(short level, short hp, short attack, short defense, short spattack, short spdefense, short speed,
			int currentXP) {
		rng = new Random();
		this.level = level;
		setLevelUpXP(level);
		this.currentXP = currentXP;
		this.currentHP = hp;
		stats = new short[] { hp, attack, defense, spattack, spdefense, speed };
	}

	public void levelUP() {
		if (level < 99) {
//			levelUpXP *= (Math.pow((3250 / 3.0), 1 / (99.0)));
			level++;
			levelUpXP = calculateLevelUpXP();
//			if(this.pokemon.getName().equals("Sarah") || this.pokemon.getName().equals("Entoron"))
//				System.err.println(level + ": " + levelUpXP);
			newMoves();
			evolve();
		} else if (level == 99) {
			levelUpXP = 0;
			level++;
			newMoves();
			evolve();
		} else {
			return;
		}
		float random;
		for (int i = 0; i < stats.length; i++) {
			random = rng.nextFloat();
			if (random <= 0.15) {
				stats[i] += 1;
				if (i == 0) {
					this.currentHP += 1;
				}
			} else if (random <= 0.5) {
				stats[i] += 2;
				if (i == 0) {
					this.currentHP += 2;
				}
			} else if (random <= 0.85) {
				stats[i] += 3;
				if (i == 0) {
					this.currentHP += 3;
				}
			} else {
				stats[i] += 4;
				if (i == 0) {
					this.currentHP += 4;
				}
			}
		}
	}

	public void newMoves() {
		for(Move newMove : gController.getInformation().getNewMove(this.pokemon.getId(), this.level)) {
			if(this.pokemon.getAmmountOfMoves() != 4) {
				try {
					if(!generated) {
						gController.getGameFrame().getFightPanel().addText(this.pokemon.getName() + " erlernt " + newMove.getName() + "!");
					}
					this.pokemon.addMove(newMove.getName());
				} catch(Exception e) {}
			} else {
				if(!generated) {
					gController.getGameFrame().getFightPanel().addText(this.pokemon.getName() + " versucht " + newMove.getName() + " zu erlernen! M�chtest du eine andere Attacke vergessen oder deine momentanen Attacken behalten?");
					gController.getGameFrame().getFightPanel().getTextLabel().setAfter(After.NEW_ATTACK);
					gController.getGameFrame().displayNewMove(this.pokemon, newMove);
					while(this.gController.getFight().getCurrentFightOption() == FightOption.NEW_ATTACK) {
						gController.sleep(50);
					}
				} else {
					 this.pokemon.addMove(this.pokemon.getRandomMove().getName(), newMove.clone());
				}
			}
		}
	}

	public void evolve() {
		if(this.pokemon.evolve(gController.getInformation().checkEvolution(this.pokemon.getId(), this.level))) {
			newMoves();
		}
	}

	public short[] getStats() {
		return this.stats;
	}

	public void generateStats(short level) {
		generated = true;
		reset();
		for (short i = 1; i < level; i++) {
			levelUP();
		}
		generated = false;
	}

	private void reset() {
		this.level = 1;
		this.currentXP = 0;
		this.levelUpXP = calculateLevelUpXP();
		for (int i = 0; i < stats.length; i++) {
			stats[i] = BASE_VALUE;
		}
		stats[0] *= 2;
		newMoves();
	}

	public void setStats(short level, short hp, short attack, short defense, short spattack, short spdefense,
			short speed, int currentXP) {
		this.level = level;
		this.currentHP = hp;
		stats[0] = hp;
		stats[1] = attack;
		stats[2] = defense;
		stats[3] = spattack;
		stats[4] = spdefense;
		stats[5] = speed;
		setLevelUpXP(level);
		this.currentXP = currentXP;
	}

	public void setLevelUpXP(short level) {
		levelUpXP = calculateLevelUpXP();
	}

	public short getLevel() {
		return this.level;
	}

	public void setAllTimeXP(int xp) {
		this.allTimeXP = xp;
	}

	public int getAllTimeXP() {
		return this.allTimeXP;
	}

	public void gainXP(int gain) {
		if (level < 100) {
			currentXP += gain;
			allTimeXP += gain;
			while (currentXP >= levelUpXP) {
				currentXP = currentXP - levelUpXP;
				gController.getGameFrame().getFightPanel().updatePanels();
				gController.getGameFrame().getFightPanel().addText(pokemon.getName() + " erreicht Level " + (this.level + 1) + "!");
				levelUP();
			}
		}
	}

	public short getCurrentHP() {
		return this.currentHP;
	}

	public void setCurrentHP(short currentHP) {
		this.currentHP = currentHP;
	}

	public void restoreFullHP() {
		this.currentHP = stats[0];
	}

	public void restoreHP(int ammount) {
		if (this.currentHP + ammount <= stats[0]) {
			this.currentHP += ammount;
		} else {
			this.currentHP = stats[0];
		}
	}

	public boolean loseHP(int ammount) {
		if (this.currentHP - ammount <= 0) {
			this.currentHP = 0;
			return true;
		}
		this.currentHP -= ammount;
		return false;
	}

	public void startFight() {
		this.fightStats = stats.clone();
		this.fightStatsChanges = new short[5];
	}

	public boolean increaseStat(int index, int ammount) {
		for (int i = 0; i < ammount; i++) {
			if(fightStatsChanges[index] >= 7) {
				gController.getGameFrame().getFightPanel().addText(STAT_NAMES[index] + " von " + this.pokemon.getName() + " kann nicht weiter erhoeht werden!");
				return false;
			} else {
				if (fightStatsChanges[index] < 0) {
					fightStats[index + 1] = (short) (fightStats[index + 1] * (3 / 2.0));
				} else {
					fightStats[index + 1] = (short) (fightStats[index + 1] * (5 / 4.0));
				}
				fightStatsChanges[index]++;
			}
		}
		return true;
	}

	public boolean decreaseStat(int index, int ammount) {
		for (int i = 0; i < ammount; i++) {
			if(fightStatsChanges[index] <= -7) {
				gController.getGameFrame().getFightPanel().addText(STAT_NAMES[index] + " von " + this.pokemon.getName() + " kann nicht weiter gesenkt werden!");
				return false;
			} else {
				if (fightStatsChanges[index] <= 0) {
					fightStats[index + 1] = (short) (fightStats[index + 1] * (2 / 3.0));
				} else {
					fightStats[index + 1] = (short) (fightStats[index + 1] * (4 / 5.0));
				}
				fightStatsChanges[index]--;
			}
		}
		return true;
	}

	public short[] getFightStats() {
		short[] result = this.fightStats.clone();
		switch(this.pokemon.getAilment()) {
		case BURN:
			result[1] *= 0.5;
			break;
		case PARALYSIS:
			result[5] *= 0.5;
		default:
			break;
		}
		return result;
	}

	public int getCurrentXP() {
		return this.currentXP;
	}

	public void setCurrentXP(int currentXP) {
		this.currentXP = currentXP;
	}

	public int getLevelUpXP() {
		return levelUpXP;
	}

	private int calculateLevelUpXP() {
		return calculateLevelUpXP(level + 1) - calculateLevelUpXP(level);
	}

	private int calculateLevelUpXP(int level) {
		if(level > 1 && level < 99) {
			return (int) (Math.pow(level, 3));
		} else {
			return 0;
		}
	}

	public Color getHPColor() {
		double life = (this.currentHP) / ((double) this.stats[0]);
		if(life > 0.5) {
			return Color.GREEN;
		} else if(life > 0.15) {
			return new Color(255, 255, 0);
		} else {
			return Color.RED;
		}
	}
}
