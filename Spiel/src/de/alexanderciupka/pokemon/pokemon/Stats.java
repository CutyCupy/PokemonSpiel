package de.alexanderciupka.pokemon.pokemon;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.gui.After;
import de.alexanderciupka.pokemon.gui.panels.NewAttackPanel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;

//TODO: Unique stats for different Pokemons
public class Stats {

	private Pokemon pokemon;
	private Nature nature;
	private short level;
	private HashMap<Stat, Short> baseStats;
	private HashMap<Stat, Short> stats;
	private HashMap<Stat, Double> fightStats;
	private HashMap<Stat, Short> fightStatsChanges;
	private HashMap<Stat, Short> dvs;
	private HashMap<Stat, Short> evs;
	private int currentXP;
	private int levelUpXP;
	private int allTimeXP;
	private short currentHP;
	private GameController gController;
	private boolean generated;

	private Random rng;

	private static final int MAX_CHANGE = 6;

	public Stats(Pokemon pokemon) {
		gController = GameController.getInstance();
		this.pokemon = pokemon;
		rng = new Random();
		level = 1;
		levelUpXP = calculateLevelUpXP();
		currentXP = 0;
		allTimeXP = 0;
		baseStats = gController.getInformation().getBaseStats(pokemon.getId());
		stats = new HashMap<Stat, Short>();
		dvs = new HashMap<Stat, Short>();
		evs = new HashMap<Stat, Short>();

		for(Stat s : Stat.values()) {
			stats.put(s, (short) 0);
			dvs.put(s, (short) rng.nextInt(32));
			evs.put(s, (short) 0);
		}

		this.currentHP = stats.get(Stat.HP);
		this.nature = Nature.getRandomNature();
		this.nature = Nature.BASHFUL;
		updateStats();
	}

	public void updateStats() {
		short oldHP = stats.get(Stat.HP).shortValue();
		stats.put(Stat.HP, (short) ((2 * baseStats.get(Stat.HP) + dvs.get(Stat.HP) + (evs.get(Stat.HP) / 4) + 100) * (level / 100.0) + 10));
		currentHP += stats.get(Stat.HP) - oldHP;
		for(Stat s : Stat.values()) {
			if(s.equals(Stat.HP)) {
				continue;
			} else if(s.equals(Stat.ACCURACY) || s.equals(Stat.EVASION)) {
				stats.put(s, (short) 1);
				continue;
			}
			stats.put(s, (short) ((2 * baseStats.get(s) + dvs.get(s) + (evs.get(s) / 4)) * (level / 100.0) + 5));
		}

		if(this.nature.hasChange()) {
			stats.put(this.nature.getIncrease(), (short) (stats.get(this.nature.getIncrease()) * Nature.INCREASE_FACTOR));
			stats.put(this.nature.getDecrease(), (short) (stats.get(this.nature.getDecrease()) * Nature.DECREASE_FACTOR));
		}
		if (fightStats != null) {
			calcFightStats();
		}
	}

	public boolean levelUP() {
		boolean result = this.level < 100;
		if (level < 99) {
			level++;
			if(gController.isFighting()) {
				gController.getGameFrame().getFightPanel().updatePanels();
				gController.getGameFrame().getFightPanel().addText(pokemon.getName() + " erreicht Level " + this.level + "!");
			} else {
				if(!generated) {
					SoundController.getInstance().playSound(SoundController.LEVEL_UP);
					gController.getGameFrame().addDialogue(pokemon.getName() + " erreicht Level " + this.level + "!");
				}
			}
			levelUpXP = calculateLevelUpXP();
			newMoves();
		} else {
			this.level = 100;
			if(gController.isFighting()) {
				gController.getGameFrame().getFightPanel().updatePanels();
				gController.getGameFrame().getFightPanel().addText(pokemon.getName() + " erreicht Level " + this.level + "!");
			} else {
				if(!generated) {
					SoundController.getInstance().playSound(SoundController.LEVEL_UP);
					gController.getGameFrame().addDialogue(pokemon.getName() + " erreicht Level " + this.level + "!");
				}
			}
			this.currentXP = 0;
			levelUpXP = 0;
			newMoves();
		}
		if(result) {
			if(gController.isFighting()) {
				if(pokemon.getHappiness() < 100) {
					pokemon.changeHappiness(5);
				} else if(pokemon.getHappiness() > 200) {
					pokemon.changeHappiness(3);
				} else {
					pokemon.changeHappiness(2);
				}
			}
			if(!generated) {
				evolve(Item.NONE);
			}
			updateStats();
		}
		return result;
	}

	private void calcFightStats() {
		this.fightStats = new HashMap<>();
		if(this.fightStatsChanges == null) {
			this.fightStatsChanges = new HashMap<Stat, Short>();
		}
		for(Stat s : this.stats.keySet()) {
			if(this.fightStatsChanges.get(s) == null) {
				this.fightStatsChanges.put(s, (short) 0);
			}
			fightStats.put(s, this.stats.get(s).doubleValue() * calcStatFactor(s, this.fightStatsChanges.get(s)));
		}
//		System.err.println(this.fightStatsChanges);
//		HashMap<Stat, Short> fightStatsChanges = new HashMap<>(this.fightStatsChanges);
//		this.fightStatsChanges = new HashMap<>();
//		for (Stat s : fightStatsChanges.keySet()) {
//			short change = fightStatsChanges.get(s);
//			if (change > 0) {
//				increaseStat(s, change);
//			} else if (change < 0) {
//				decreaseStat(s, -change);
//			}
//		}
	}

	private double calcStatFactor(Stat s, short value) {
		if(value == 0) {
			return 1;
		}
		switch(s) {
		case ACCURACY:
		case EVASION:
			if(value > 0) {
				return (3 + value) / 3.0;
			} else if(value < 0) {
				return 3.0 / (3 + Math.abs(value));
			}
			break;
		default:
			if(value > 0) {
				return (2 + value) / 2.0;
			} else if(value < 0) {
				return 2.0 / (2 + Math.abs(value));
			}
			break;
		}
		return 1;
	}

	public void newMoves() {
		for (Move newMove : gController.getInformation().getNewMove(this.pokemon, this.level)) {
			if (this.pokemon.getAmmountOfMoves() != 4) {
				try {
					if (!generated) {
						if(gController.isFighting()) {
							gController.getGameFrame().getFightPanel()
							.addText(this.pokemon.getName() + " erlernt " + newMove.getName() + "!");
						} else {
							gController.getGameFrame().addDialogue(this.pokemon.getName() + " erlernt " + newMove.getName() + "!");
						}
					}
					this.pokemon.addMove(newMove.getName());
				} catch (Exception e) {
				}
			} else {
				if (!generated) {
					if(gController.isFighting()) {
						gController.getGameFrame().getFightPanel().addText(this.pokemon.getName() + " versucht "
								+ newMove.getName()
								+ " zu erlernen! M�chtest du eine andere Attacke vergessen oder deine momentanen Attacken behalten?");
						gController.getGameFrame().getFightPanel().getTextLabel().setAfter(After.NEW_ATTACK);
						gController.getGameFrame().displayNewMove(this.pokemon, newMove);
						while (this.gController.getFight().getCurrentFightOption() == FightOption.NEW_ATTACK) {
							gController.sleep(50);
						}
					} else {
						gController.getGameFrame().addDialogue(this.pokemon.getName() + " versucht "
								+ newMove.getName()
								+ " zu erlernen! M�chtest du eine andere Attacke vergessen oder deine momentanen Attacken behalten?");
						gController.waitDialogue();
						gController.getGameFrame().getDialogue().setAfter(After.NEW_ATTACK);
						gController.getGameFrame().displayNewMove(this.pokemon, newMove);
						while (this.gController.getGameFrame().getContentPane() instanceof NewAttackPanel) {
							gController.sleep(50);
						}
					}
				} else {
					if(newMove.getPower() > 0) {
						this.pokemon.addMove(this.pokemon.getRandomMove().getName(), newMove.clone());
					}
				}
			}
		}
	}

	public void evolve(Item i) {
		if(this.pokemon.evolve(gController.getInformation().checkEvolution(this.pokemon, i))) {
//			newMoves();
		}
	}

	public HashMap<Stat, Short> getStats() {
		HashMap<Stat, Short> result = new HashMap<Stat, Short>();
		for(Stat s : Stat.values()) {
			result.put(s, this.stats.get(s).shortValue());
		}
		return result;
	}

	public Short getEv(Stat s) {
		return this.evs.get(s);
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
		updateStats();
		newMoves();
	}

	public void setStats(short level, short hp, short attack, short defense, short spattack, short spdefense,
			short speed, int currentXP) {
		this.level = level;
		this.currentHP = hp;
		stats.put(Stat.HP, hp);
		stats.put(Stat.ATTACK, attack);
		stats.put(Stat.DEFENSE, defense);
		stats.put(Stat.SPECIALATTACK, spattack);
		stats.put(Stat.SPECIALDEFENSE, spdefense);
		stats.put(Stat.SPECIALATTACK, speed);
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

	public boolean gainXP(int gain) {
		if (level < 100) {
			currentXP += gain;
			allTimeXP += gain;
			while (currentXP >= levelUpXP && level < 100) {
				currentXP = currentXP - levelUpXP;
				levelUP();
			}
			return true;
		}
		return false;
	}

	public short getCurrentHP() {
		return this.currentHP;
	}

	public void setCurrentHP(short currentHP) {
		this.currentHP = currentHP;
	}

	public void restoreFullHP() {
		this.currentHP = stats.get(Stat.HP);
	}

	public int restoreHP(int ammount) {
		short oldHP = this.currentHP;
		if (this.currentHP + ammount <= stats.get(Stat.HP)) {
			this.currentHP += ammount;
		} else {
			this.currentHP = stats.get(Stat.HP);
		}
		return this.currentHP - oldHP;
	}

	public int loseHP(int ammount) {
		int lost = ammount;
		if (this.currentHP - ammount <= 0) {
			lost = currentHP;
			this.currentHP = 0;
			this.pokemon.setAilment(Ailment.FAINTED);
		}
		this.currentHP -= ammount;
		return lost;
	}

	public void startFight() {
		this.fightStats = new HashMap<>();
		this.fightStatsChanges = new HashMap<>();
		calcFightStats();
	}

	public boolean increaseStat(Stat s, int value) {
		short currentChange = fightStatsChanges.get(s);
		if(currentChange == MAX_CHANGE) {
			gController.getGameFrame().getFightPanel().addText(
					s.getText() + " von " + this.pokemon.getName() + " kann nicht weiter erhöht werden!");
			return false;
		} else {
			boolean multipleBoost = value > 1;
			if(currentChange + value > MAX_CHANGE) {
				if(MAX_CHANGE - currentChange  <= 1) {
					multipleBoost = false;
				}
			}
			this.fightStatsChanges.put(s, (short) Math.min(currentChange + value, MAX_CHANGE));
			this.calcFightStats();
			gController.getGameFrame().getFightPanel().addText(s.getText() + " von " + this.pokemon.getName() +
					" wurde " + (multipleBoost ? "sehr stark " : "") + "erhöht!");
			return true;
		}

//		if (fightStatsChanges.get(s) + value > 7) {
//			gController.getGameFrame().getFightPanel().addText(
//					s.getText() + " von " + this.pokemon.getName() + " kann nicht weiter erhöht werden!");
//			return false;
//		}
//		for (int i = 0; i < value; i++) {
//			if (fightStatsChanges.get(s) < 0) {
//				fightStats.put(s, (fightStats.get(s) * (3 / 2.0)));
//			} else {
//				fightStats.put(s, (fightStats.get(s) * (5 / 4.0)));
//			}
//			fightStatsChanges.put(s, (short) (fightStatsChanges.get(s) + 1));
//		}
//		return true;
	}

	public boolean decreaseStat(Stat s, int value) {
		short currentChange = fightStatsChanges.get(s);
		if(currentChange == -MAX_CHANGE) {
			gController.getGameFrame().getFightPanel().addText(
					s.getText() + " von " + this.pokemon.getName() + " kann nicht weiter gesenkt werden!");
			return false;
		} else {
			boolean multipleBoost = value > 1;
			if(currentChange - value < -MAX_CHANGE) {
				if(currentChange + MAX_CHANGE <= 1) {
					multipleBoost = false;
				}
			}
			this.fightStatsChanges.put(s, (short) Math.max(currentChange - value, -MAX_CHANGE));
			this.calcFightStats();
			gController.getGameFrame().getFightPanel().addText(s.getText() + " von " + this.pokemon.getName() +
					" wurde " + (multipleBoost ? "sehr stark " : "") + "gesenkt!");
			return true;
		}
//		if(fightStatsChanges.get(s) == null) {
//			fightStatsChanges.put(s, (short) 0);
//		}
//		if (fightStatsChanges.get(s) + value > 7) {
//			gController.getGameFrame().getFightPanel().addText(
//					s.getText() + " von " + this.pokemon.getName() + " kann nicht weiter gesenkt werden!");
//			return false;
//		}
//		for (int i = 0; i < value; i++) {
//			if (fightStatsChanges.get(s) <= 0) {
//				fightStats.put(s, (fightStats.get(s) * (2 / 3.0)));
//			} else {
//				fightStats.put(s, (fightStats.get(s) * (4 / 5.0)));
//			}
//			fightStatsChanges.put(s, (short) (fightStatsChanges.get(s) - 1));
//		}
//		return true;
	}

	public HashMap<Stat, Double> getFightStats() {
		HashMap<Stat, Double> result = new HashMap<Stat, Double>(this.fightStats);
		switch (this.pokemon.getAilment()) {
		case BURN:
			result.put(Stat.ATTACK, (result.get(Stat.ATTACK) * 0.5));
			break;
		case PARALYSIS:
			result.put(Stat.SPEED, (result.get(Stat.SPEED) * 0.25));
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
		return gController.getInformation().getLevelUpXP(this.pokemon, level + 1);
	}

	public Color getHPColor() {
		double life = (this.currentHP) / (this.stats.get(Stat.HP));
		if (life > 0.5) {
			return Color.GREEN;
		} else if (life > 0.15) {
			return new Color(255, 255, 0);
		} else {
			return Color.RED;
		}
	}

	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		data.addProperty("level", this.level);
		data.addProperty("current_xp", this.currentXP);
		data.addProperty("current_hp", this.currentHP);
		JsonArray dvEvArray = new JsonArray();
		for(Stat s : Stat.values()) {
			JsonObject currentDV = new JsonObject();
			currentDV.addProperty("name", s.name());
			currentDV.addProperty("dv", dvs.get(s));
			currentDV.addProperty("ev", evs.get(s));
			dvEvArray.add(currentDV);
		}
		data.add("dvev", dvEvArray);
		data.addProperty("nature", this.nature.name());
		return data;
	}

	public void importSaveData(JsonObject saveData) {
		if(saveData.get("dvev") != null) {
			for(JsonElement je : saveData.get("dvev").getAsJsonArray()) {
				JsonObject currentDVEV = je.getAsJsonObject();
				Stat currentStat = Stat.valueOf(currentDVEV.get("name").getAsString().toUpperCase());
				this.dvs.put(currentStat, currentDVEV.get("dv").getAsShort());
				this.evs.put(currentStat, currentDVEV.get("ev").getAsShort());
			}
		}
		this.generateStats(saveData.get("level").getAsShort());
		this.currentXP = saveData.get("current_xp").getAsInt();
		this.currentHP = saveData.get("current_hp").getAsShort();
		this.nature = saveData.get("nature") != null ? Nature.valueOf(saveData.get("nature").getAsString().toUpperCase()) : Nature.getRandomNature();
//		JsonObject stats = saveData.get("stats").getAsJsonObject();
//		for (int i = 0; i < this.stats.length; i++) {
//			this.stats[i] = stats.get(String.valueOf(i)).getAsShort();
//		}
	}

	public Random getRNG() {
		return this.rng;
	}

	public void setBaseStats(HashMap<Stat, Short> baseStats) {
		this.baseStats = new HashMap<>(baseStats);
		updateStats();
	}

	public void increaseEV(Stat stat, short value) {
		short sum = 0;
		for(Stat s : evs.keySet()) {
			sum += evs.get(s);
		}
		if(sum < 510) {
			short temp = (short) (this.evs.get(stat) + (sum + value > 510 ? 510 - sum : value));
			this.evs.put(stat, temp < 252 ? temp : 252);
			updateStats();
		}
	}

	public void setDV(Stat stat, int value) {
		this.dvs.put(stat, (short) value);
		this.updateStats();
	}

	public Nature getNature() {
		return nature;
	}

	public void setNature(Nature nature) {
		this.nature = nature;
	}

}
