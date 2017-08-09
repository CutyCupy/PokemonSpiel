package de.alexanderciupka.pokemon.pokemon;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.gui.After;
import de.alexanderciupka.pokemon.map.GameController;

//TODO: Unique stats for different Pokemons
public class Stats {

	public static final String[] STAT_NAMES = { "Angriff", "Verteidigung", "Spezialangriff", "Spezialverteidigung",
			"Initiative" };

	public static final String[] STAT_SAVE_NAMES = {"hp", "attack", "defense", "special-attack", "special-defense", "speed"};

	private Pokemon pokemon;
	private short level;
	// HP,ATTACK,DEFENSE,SPATTACK,SPDEFENSE,SPEED
	private short[] baseStats;
	private short[] stats;
	private short[] fightStats;
	private short[] fightStatsChanges;
	private HashMap<String, Short> dvs;
	private HashMap<String, Short> evs;
	private int currentXP;
	private int levelUpXP;
	private int allTimeXP;
	private short currentHP;
	private GameController gController;
	private boolean generated;

	private Random rng;

	public Stats(Pokemon pokemon) {
		gController = GameController.getInstance();
		this.pokemon = pokemon;
		rng = new Random();
		level = 1;
		levelUpXP = calculateLevelUpXP();
		currentXP = 0;
		allTimeXP = 0;
		baseStats = gController.getInformation().getBaseStats(pokemon.getId());
		stats = new short[6];
		dvs = new HashMap<String, Short>();// new short[] {(short) rng.nextInt(32), (short) rng.nextInt(32), (short) rng.nextInt(32), (short) rng.nextInt(32), (short) rng.nextInt(32), (short) rng.nextInt(32)};
		evs = new HashMap<String, Short>();

		for(String s : STAT_SAVE_NAMES) {
			dvs.put(s, (short) rng.nextInt(32));
			evs.put(s, (short) 0);
		}

		this.currentHP = stats[0];
		updateStats();
	}

	public void updateStats() {
		short oldHP = stats[0];
		stats[0] = (short) ((2 * baseStats[0] + dvs.get(STAT_SAVE_NAMES[0]) + (evs.get(STAT_SAVE_NAMES[0]) / 4) + 100) * (level / 100.0) + 10);
		currentHP += stats[0] - oldHP;
		for(int i = 1; i < Math.min(stats.length, baseStats.length); i++) {
			stats[i] = (short) ((2 * baseStats[i] + dvs.get(STAT_SAVE_NAMES[i]) + (evs.get(STAT_SAVE_NAMES[i]) / 4)) * (level / 100.0) + 5);
		}
		if (fightStats != null) {
			calcFightStats();
		}
	}

	public void levelUP() {
		if (level < 99) {
			level++;
			levelUpXP = calculateLevelUpXP();
			newMoves();
			evolve();
		} else {
			this.level = 100;
			this.currentXP = 0;
			levelUpXP = 0;
			newMoves();
			evolve();
		}
		updateStats();
	}

	private void calcFightStats() {
		this.fightStats = stats.clone();
		int i = 0;
		short[] fightStatsChanges = this.fightStatsChanges.clone();
		this.fightStatsChanges = new short[fightStatsChanges.length];
		for (short change : fightStatsChanges) {
			if (change > 0) {
				increaseStat(i, change);
			} else if (change < 0) {
				decreaseStat(i, Math.abs(change));
			}
			i++;
		}
	}

	public void newMoves() {
		for (Move newMove : gController.getInformation().getNewMove(this.pokemon.getId(), this.level)) {
			if (this.pokemon.getAmmountOfMoves() != 4) {
				try {
					if (!generated) {
						gController.getGameFrame().getFightPanel()
								.addText(this.pokemon.getName() + " erlernt " + newMove.getName() + "!");
					}
					this.pokemon.addMove(newMove.getName());
				} catch (Exception e) {
				}
			} else {
				if (!generated) {
					gController.getGameFrame().getFightPanel().addText(this.pokemon.getName() + " versucht "
							+ newMove.getName()
							+ " zu erlernen! M�chtest du eine andere Attacke vergessen oder deine momentanen Attacken behalten?");
					gController.getGameFrame().getFightPanel().getTextLabel().setAfter(After.NEW_ATTACK);
					gController.getGameFrame().displayNewMove(this.pokemon, newMove);
					while (this.gController.getFight().getCurrentFightOption() == FightOption.NEW_ATTACK) {
						gController.sleep(50);
					}
				} else {
					if(newMove.getPower() > 0) {
						this.pokemon.addMove(this.pokemon.getRandomMove().getName(), newMove.clone());
					}
				}
			}
		}
	}

	public void evolve() {
		if (this.pokemon.evolve(gController.getInformation().checkEvolution(this.pokemon.getId(), this.level))) {

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
		updateStats();
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

	public boolean gainXP(int gain) {
		if (level < 100) {
			currentXP += gain;
			allTimeXP += gain;
			while (currentXP >= levelUpXP && level < 100) {
				currentXP = currentXP - levelUpXP;
				gController.getGameFrame().getFightPanel().updatePanels();
				gController.getGameFrame().getFightPanel()
						.addText(pokemon.getName() + " erreicht Level " + (this.level + 1) + "!");
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
			this.pokemon.setAilment(Ailment.FAINTED);
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
		if (fightStatsChanges[index] + ammount > 7) {
			gController.getGameFrame().getFightPanel().addText(
					STAT_NAMES[index] + " von " + this.pokemon.getName() + " kann nicht weiter " + "erhöht werden!");
			return false;
		}
		for (int i = 0; i < ammount; i++) {
			if (fightStatsChanges[index] < 0) {
				fightStats[index + 1] = (short) (fightStats[index + 1] * (3 / 2.0));
			} else {
				fightStats[index + 1] = (short) (fightStats[index + 1] * (5 / 4.0));
			}
			fightStatsChanges[index]++;
		}
		return true;
	}

	public boolean decreaseStat(int index, int ammount) {
		if (fightStatsChanges[index] - ammount < -7) {
			gController.getGameFrame().getFightPanel().addText(
					STAT_NAMES[index] + " von " + this.pokemon.getName() + " kann nicht weiter gesenkt werden!");
			return false;
		}
		for (int i = 0; i < ammount; i++) {
			if (fightStatsChanges[index] <= 0) {
				fightStats[index + 1] = (short) (fightStats[index + 1] * (2 / 3.0));
			} else {
				fightStats[index + 1] = (short) (fightStats[index + 1] * (4 / 5.0));
			}
			fightStatsChanges[index]--;
		}
		return true;
	}

	public short[] getFightStats() {
		short[] result = this.fightStats.clone();
		switch (this.pokemon.getAilment()) {
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
		return gController.getInformation().getLevelUpXP(this.pokemon, level);
	}

	public Color getHPColor() {
		double life = (this.currentHP) / ((double) this.stats[0]);
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
		for(int i = 0; i < STAT_SAVE_NAMES.length; i++) {
			JsonObject currentDV = new JsonObject();
			currentDV.addProperty("name", STAT_SAVE_NAMES[i]);
			currentDV.addProperty("dv", dvs.get(STAT_SAVE_NAMES[i]));
			currentDV.addProperty("ev", evs.get(STAT_SAVE_NAMES[i]));
			dvEvArray.add(currentDV);
		}
		data.add("dvev", dvEvArray);
		JsonObject statData = new JsonObject();
		for (int i = 0; i < stats.length; i++) {
			statData.addProperty(String.valueOf(i), stats[i]);
		}
		data.add("stats", statData);
		return data;
	}

	public void importSaveData(JsonObject saveData) {
		if(saveData.get("dvev") != null) {
			for(JsonElement je : saveData.get("dvev").getAsJsonArray()) {
				JsonObject currentDVEV = je.getAsJsonObject();
				this.dvs.put(currentDVEV.get("name").getAsString(), currentDVEV.get("dv").getAsShort());
				this.evs.put(currentDVEV.get("name").getAsString(), currentDVEV.get("ev").getAsShort());
			}
		}
		this.generateStats(saveData.get("level").getAsShort());
		this.currentXP = saveData.get("current_xp").getAsInt();
		this.currentHP = saveData.get("current_hp").getAsShort();
//		JsonObject stats = saveData.get("stats").getAsJsonObject();
//		for (int i = 0; i < this.stats.length; i++) {
//			this.stats[i] = stats.get(String.valueOf(i)).getAsShort();
//		}
	}

	public Random getRNG() {
		return this.rng;
	}

	public void setBaseStats(short[] baseStats) {
		this.baseStats = baseStats.clone();
		updateStats();
	}

	public void increaseEV(String name, short value) {
		short temp = (short) (this.evs.get(name) + value);
		this.evs.put(name, temp < 252 ? temp : 252);
		updateStats();
	}

	public void setDV(String stat, int value) {
		this.dvs.put(stat, (short) value);
		this.updateStats();
	}

}