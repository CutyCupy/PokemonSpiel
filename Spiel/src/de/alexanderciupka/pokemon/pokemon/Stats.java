package de.alexanderciupka.pokemon.pokemon;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.After;
import de.alexanderciupka.pokemon.gui.panels.NewAttackPanel;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;

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
		this.gController = GameController.getInstance();
		this.pokemon = pokemon;
		this.rng = new Random();
		this.level = 1;
		this.levelUpXP = this.calculateLevelUpXP();
		this.currentXP = 0;
		this.allTimeXP = 0;
		this.baseStats = this.gController.getInformation().getBaseStats(pokemon.getId());
		this.stats = new HashMap<Stat, Short>();
		this.dvs = new HashMap<Stat, Short>();
		this.evs = new HashMap<Stat, Short>();

		for (Stat s : Stat.values()) {
			this.stats.put(s, (short) 0);
			this.dvs.put(s, (short) this.rng.nextInt(32));
			this.evs.put(s, (short) 0);
		}

		this.currentHP = this.stats.get(Stat.HP);
		this.nature = Nature.getRandomNature();
		this.updateStats();
	}

	public void updateStats() {
		short oldHP = this.stats.get(Stat.HP).shortValue();
		this.stats.put(Stat.HP,
				(short) ((2 * this.baseStats.get(Stat.HP) + this.dvs.get(Stat.HP) + (this.evs.get(Stat.HP) / 4) + 100)
						* (this.level / 100.0) + 10));
		this.currentHP += this.stats.get(Stat.HP) - oldHP;
		for (Stat s : Stat.values()) {
			if (s.equals(Stat.HP)) {
				continue;
			} else if (s.equals(Stat.ACCURACY) || s.equals(Stat.EVASION)) {
				this.stats.put(s, (short) 1);
				continue;
			}
			this.stats.put(s, (short) ((2 * this.baseStats.get(s) + this.dvs.get(s) + (this.evs.get(s) / 4))
					* (this.level / 100.0) + 5));
		}

		if (this.nature.hasChange()) {
			this.stats.put(this.nature.getIncrease(),
					(short) (this.stats.get(this.nature.getIncrease()) * Nature.INCREASE_FACTOR));
			this.stats.put(this.nature.getDecrease(),
					(short) (this.stats.get(this.nature.getDecrease()) * Nature.DECREASE_FACTOR));
		}
		if (this.fightStats != null) {
			this.calcFightStats();
		}
	}

	public boolean levelUP() {
		boolean result = this.level < 100;
		if (this.level <= 99) {
			this.level++;
			if (this.gController.isFighting()) {
				this.gController.getGameFrame().getFightPanel().updatePanels();
				SoundController.getInstance().playSound(SoundController.LEVEL_UP);
				if (this.gController.getGameFrame().getFightPanel().getPokemonLabel(this.pokemon) != null) {
					this.gController.getGameFrame().getFightPanel().getPokemonLabel(this.pokemon).getAnimationLabel()
							.playAnimation("levelup");
				}
				this.gController.getGameFrame().getFightPanel()
						.addText(this.pokemon.getName() + " erreicht Level " + this.level + "!");
			} else {
				if (!this.generated) {
					SoundController.getInstance().playSound(SoundController.LEVEL_UP);
					this.gController.getGameFrame()
							.addDialogue(this.pokemon.getName() + " erreicht Level " + this.level + "!");
				}
			}
			this.levelUpXP = this.calculateLevelUpXP();
			this.newMoves();
		}
		if (this.level == 100) {
			this.currentXP = 0;
			this.levelUpXP = 0;
		}
		if (result) {
			if (this.gController.isFighting()) {
				if (this.pokemon.getHappiness() < 100) {
					this.pokemon.changeHappiness(5);
				} else if (this.pokemon.getHappiness() > 200) {
					this.pokemon.changeHappiness(3);
				} else {
					this.pokemon.changeHappiness(2);
				}
			}
			if (!this.generated) {
				this.evolve(Items.KEINS);
			}
			this.updateStats();
		}
		return result;
	}

	private void calcFightStats() {
		this.fightStats = new HashMap<>();
		if (this.fightStatsChanges == null) {
			this.fightStatsChanges = new HashMap<Stat, Short>();
		}
		for (Stat s : this.stats.keySet()) {
			if (this.fightStatsChanges.get(s) == null) {
				this.fightStatsChanges.put(s, (short) 0);
			}
			this.fightStats.put(s,
					this.stats.get(s).doubleValue() * this.calcStatFactor(s, this.fightStatsChanges.get(s)));
		}
	}

	private double calcStatFactor(Stat s, short value) {
		if (value == 0) {
			return 1;
		}
		switch (s) {
		case ACCURACY:
		case EVASION:
			if (value > 0) {
				return (3 + value) / 3.0;
			} else if (value < 0) {
				return 3.0 / (3 + Math.abs(value));
			}
			break;
		default:
			if (value > 0) {
				return (2 + value) / 2.0;
			} else if (value < 0) {
				return 2.0 / (2 + Math.abs(value));
			}
			break;
		}
		return 1;
	}

	public void newMoves() {
		for (Move newMove : this.gController.getInformation().getNewMove(this.pokemon, this.level)) {
			if (this.pokemon.getAmmountOfMoves() != 4) {
				try {
					if (!this.generated) {
						if (this.gController.isFighting()) {
							this.gController.getGameFrame().getFightPanel()
									.addText(this.pokemon.getName() + " erlernt " + newMove.getName() + "!");
						} else {
							this.gController.getGameFrame()
									.addDialogue(this.pokemon.getName() + " erlernt " + newMove.getName() + "!");
						}
					}
					this.pokemon.addMove(newMove.getName());
				} catch (Exception e) {
				}
			} else {
				if (!this.generated) {
					if (this.gController.isFighting()) {
						this.gController.getGameFrame().getFightPanel().addText(this.pokemon.getName() + " versucht "
								+ newMove.getName()
								+ " zu erlernen! Möchtest du eine andere Attacke vergessen oder deine momentanen Attacken behalten?");
						this.gController.getGameFrame().getFightPanel().getTextLabel().setAfter(After.NEW_ATTACK);
						this.gController.getGameFrame().displayNewMove(this.pokemon, newMove);
						while (this.gController.getFight().getCurrentFightOption() == FightOption.NEW_ATTACK) {
							this.gController.sleep(50);
						}
					} else {
						this.gController.getGameFrame().addDialogue(this.pokemon.getName() + " versucht "
								+ newMove.getName()
								+ " zu erlernen! Möchtest du eine andere Attacke vergessen oder deine momentanen Attacken behalten?");
						this.gController.waitDialogue();
						this.gController.getGameFrame().getDialogue().setAfter(After.NEW_ATTACK);
						this.gController.getGameFrame().displayNewMove(this.pokemon, newMove);
						while (this.gController.getGameFrame().getContentPane() instanceof NewAttackPanel) {
							this.gController.sleep(50);
						}
					}
				} else {
					if (newMove.getPower() > 0) {
						this.pokemon.addMove(this.pokemon.getRandomMove().getName(), newMove.clone());
					}
				}
			}
		}
	}

	public void evolve(Integer i) {
		if (this.pokemon.evolve(this.gController.getInformation().checkEvolution(this.pokemon, i))) {
			// TODO: Evolution?
		}
	}

	public HashMap<Stat, Short> getStats() {
		HashMap<Stat, Short> result = new HashMap<Stat, Short>();
		for (Stat s : Stat.values()) {
			result.put(s, this.stats.get(s).shortValue());
		}
		return result;
	}

	public Short getEv(Stat s) {
		return this.evs.get(s);
	}

	public void generateStats(short level) {
		this.generated = true;
		this.reset();
		for (short i = 1; i < level; i++) {
			this.levelUP();
		}
		this.generated = false;
	}

	private void reset() {
		this.level = 1;
		this.currentXP = 0;
		this.levelUpXP = this.calculateLevelUpXP();
		this.updateStats();
		this.newMoves();
	}

	public void setStats(short level, short hp, short attack, short defense, short spattack, short spdefense,
			short speed, int currentXP) {
		this.level = level;
		this.currentHP = hp;
		this.stats.put(Stat.HP, hp);
		this.stats.put(Stat.ATTACK, attack);
		this.stats.put(Stat.DEFENSE, defense);
		this.stats.put(Stat.SPECIALATTACK, spattack);
		this.stats.put(Stat.SPECIALDEFENSE, spdefense);
		this.stats.put(Stat.SPECIALATTACK, speed);
		this.setLevelUpXP(level);
		this.currentXP = currentXP;
	}

	public void setLevelUpXP(short level) {
		this.levelUpXP = this.calculateLevelUpXP();
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
		if (this.level < 100) {
			this.currentXP += gain;
			this.allTimeXP += gain;
			while (this.currentXP >= this.levelUpXP && this.level < 100) {
				this.currentXP = this.currentXP - this.levelUpXP;
				this.levelUP();
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

	public int restoreFullHP() {
		return restoreHP(this.stats.get(Stat.HP));
	}

	public int restoreHP(int ammount) {
		if(this.pokemon.getAilment() == Ailment.FAINTED) {
			return 0;
		}
		if (this.pokemon.getSecondaryAilments().containsKey(SecondaryAilment.HEALBLOCK)) {
			this.gController.getGameFrame().getFightPanel()
					.addText(SecondaryAilment.HEALBLOCK.getAffected().replace("@pokemon", this.pokemon.getName()));
			return 0;
		}
		short oldHP = this.currentHP;
		if (this.currentHP + ammount <= this.stats.get(Stat.HP)) {
			this.currentHP += ammount;
		} else {
			this.currentHP = this.stats.get(Stat.HP);
		}
		if (this.gController.isFighting() && (this.currentHP - oldHP) > 0
				&& this.gController.getGameFrame().getFightPanel().getPokemonLabel(this.pokemon) != null) {
			this.gController.getGameFrame().getFightPanel().getPokemonLabel(this.pokemon).getAnimationLabel()
					.playAnimation("heilung");
		}
		return this.currentHP - oldHP;
	}
	
	public int restoreHP(double percent) {
		return restoreHP((int) this.getStats().get(Stat.HP) * percent);
	}

	public int loseHP(int ammount) {
		if (this.gController.isFighting()) {
			Fighting fight = this.gController.getFight();
			for (int i = 0; i < 4; i++) {
				if (fight.getPokemon(i) != null && fight.isPlayer(i) == fight.isPlayer(this.pokemon)) {
					switch (fight.getPokemon(i).getAbility().getId()) {
					case Abilities.FREUNDESHUT:
						ammount *= .75;
						break;
					}
				}
			}
		}
		int lost = ammount;
		if (this.currentHP - ammount <= 0) {
			lost = this.currentHP;
			this.currentHP = 0;
			this.pokemon.setAilment(Ailment.FAINTED);
		} else {
			this.currentHP -= ammount;
		}
		return lost;
	}

	public void startFight() {
		this.fightStats = new HashMap<>();
		this.fightStatsChanges = new HashMap<>();
		this.calcFightStats();
	}

	public void stopFight() {
		switch (this.pokemon.getAbility().getId()) {
		case Abilities.INNERE_KRAFT:
			this.pokemon.setAilment(Ailment.NONE);
			break;
		case Abilities.BELEBEKRAFT:
			this.restoreHP((int) (this.getStats().get(Stat.HP) * (1.0 / 3.0)));
			break;
		}
	}

	public boolean increaseStat(Stat s, int value) {
		short currentChange = this.fightStatsChanges.get(s);
		switch (this.pokemon.getAbility().getId()) {
		case Abilities.WANKELMUT:
			value *= 2;
			break;
		}
		if (currentChange == MAX_CHANGE) {
			this.gController.getGameFrame().getFightPanel()
					.addText(s.getText() + " von " + this.pokemon.getName() + " kann nicht weiter erhöht werden!");
			return false;
		} else {
			boolean multipleBoost = value > 1;
			if (currentChange + value > MAX_CHANGE) {
				if (MAX_CHANGE - currentChange <= 1) {
					multipleBoost = false;
				}
			}
			this.fightStatsChanges.put(s, (short) Math.min(currentChange + value, MAX_CHANGE));
			this.calcFightStats();
			this.gController.getGameFrame().getFightPanel().addText(s.getText() + " von " + this.pokemon.getName()
					+ " wurde " + (multipleBoost ? "sehr stark " : "") + "erhöht!");
			return true;
		}

	}

	public boolean decreaseStat(Stat s, int value) {
		if(this.pokemon.getSecondaryAilments().containsKey(SecondaryAilment.MEGABLOCK)) {
			this.gController.getGameFrame().getFightPanel()
			.addText(s.getText() + " kann durch die Megablock nicht gesenkt werden!");
			return false;
		}
		switch (this.pokemon.getAbility().getId()) {
		case Abilities.NEUTRALTORSO:
			this.gController.getGameFrame().getFightPanel()
					.addText(s.getText() + " kann durch die Fähigkeit Neutraltorso nicht gesenkt werden!");
			return false;
		case Abilities.PULVERRAUCH:
			this.gController.getGameFrame().getFightPanel()
					.addText(s.getText() + " kann durch die Fähigkeit Puderabwehr nicht gesenkt werden!");
			return false;
		}
		short currentChange = this.fightStatsChanges.get(s);
		boolean nichtSenkbar = false;
		switch (this.pokemon.getAbility().getId()) {
		case Abilities.WANKELMUT:
			value *= 2;
			break;
		}
		switch (s) {
		case ACCURACY:
			if (this.pokemon.getAbility().getId() == Abilities.ADLERAUGE) {
				nichtSenkbar = true;
			}
			break;
		case ATTACK:
			if (this.pokemon.getAbility().getId() == Abilities.SCHERENMACHT) {
				nichtSenkbar = true;
			}
			break;
		case DEFENSE:
			if (this.pokemon.getAbility().getId() == Abilities.BRUSTBIETER) {
				nichtSenkbar = true;
			}
			break;
		case EVASION:
			break;
		case HP:
			break;
		case SPECIALATTACK:
			break;
		case SPECIALDEFENSE:
			break;
		case SPEED:
			break;
		default:
			break;
		}
		if (nichtSenkbar) {
			this.gController.getGameFrame().getFightPanel().addText(s.getText() + " von " + this.pokemon.getName()
					+ " kann durch " + this.pokemon.getAbility().getName() + " nicht gesenkt werden!");
			return false;
		}
		if (currentChange == -MAX_CHANGE) {
			this.gController.getGameFrame().getFightPanel()
					.addText(s.getText() + " von " + this.pokemon.getName() + " kann nicht weiter gesenkt werden!");
			return false;
		} else {
			boolean multipleBoost = value > 1;
			if (currentChange - value < -MAX_CHANGE) {
				if (currentChange + MAX_CHANGE <= 1) {
					multipleBoost = false;
				}
			}
			this.fightStatsChanges.put(s, (short) Math.max(currentChange - value, -MAX_CHANGE));
			this.calcFightStats();
			this.gController.getGameFrame().getFightPanel().addText(s.getText() + " von " + this.pokemon.getName()
					+ " wurde " + (multipleBoost ? "sehr stark " : "") + "gesenkt!");

			switch (this.pokemon.getAbility().getId()) {
			case Abilities.SIEGESWILLE:
				this.increaseStat(Stat.ATTACK, 2);
				break;
			}

			return true;
		}
	}

	public int getStatChange(Stat s) {
		return this.fightStatsChanges.get(s);
	}

	public HashMap<Stat, Double> getFightStats() {
		calcFightStats();
		HashMap<Stat, Double> result = new HashMap<Stat, Double>(this.fightStats);
		switch (this.pokemon.getAilment()) {
		case BURN:
			if (this.pokemon.getAbility().getId() != Abilities.ADRENALIN) {
				result.put(Stat.ATTACK, (result.get(Stat.ATTACK) * 0.5));
			}
			break;
		case PARALYSIS:
			if (this.pokemon.getAbility().getId() != Abilities.RASANZ) {
				result.put(Stat.SPEED, (result.get(Stat.SPEED) * 0.25));
			}
		default:
			break;
		}

		switch (this.gController.getFight().getField().getWeather()) {
		case HAIL:
			if (Abilities.SCHNEEMANTEL == this.pokemon.getAbility().getId()) {
				result.put(Stat.EVASION, result.get(Stat.EVASION) * 1.2);
			}
			break;
		case RAIN:
			if (Abilities.WASSERTEMPO == this.pokemon.getAbility().getId()) {
				result.put(Stat.SPEED, result.get(Stat.SPEED) * 2);
			}
			break;
		case SANDSTORM:
			if (Abilities.SANDSCHARRER == this.pokemon.getAbility().getId()) {
				result.put(Stat.SPEED, result.get(Stat.SPEED) * 2);
			} else if (Abilities.SANDSCHLEIER == this.pokemon.getAbility().getId()) {
				result.put(Stat.EVASION, result.get(Stat.EVASION) * 1.2);
			}
			if (this.pokemon.hasType(Type.ROCK)) {
				result.put(Stat.SPECIALDEFENSE, result.get(Stat.SPECIALDEFENSE) * 1.5);
			}
			break;
		case SUN:
			if (Abilities.CHLOROPHYLL == this.pokemon.getAbility().getId()) {
				result.put(Stat.SPEED, result.get(Stat.SPEED) * 2);
			} else if (Abilities.SOLARKRAFT == this.pokemon.getAbility().getId()) {
				result.put(Stat.SPECIALATTACK, result.get(Stat.SPECIALATTACK) * 1.5);
			}
			for (int i = 0; i < 4; i++) {
				Pokemon check = gController.getFight().getPokemon(i);
				if (check != null
						&& gController.getFight().isPlayer(check) == gController.getFight().isPlayer(this.pokemon)) {
					if (check.getAbility().getId() == Abilities.PFLANZENGABE) {
						result.put(Stat.ATTACK, result.get(Stat.ATTACK) * 1.5);
						result.put(Stat.SPECIALDEFENSE, result.get(Stat.SPECIALDEFENSE) * 1.5);
					}
				}
			}
			break;
		default:
			break;
		}

		switch (this.pokemon.getAbility().getId()) {
		case Abilities.KRAFTKOLOSS:
		case Abilities.MENTALKRAFT:
			result.put(Stat.ATTACK, result.get(Stat.ATTACK) * 2);
			break;
		case Abilities.MINUS:
			Pokemon partner = gController.getFight().getPokemon(gController.getFight().getPartner(this.pokemon));
			if (partner != null && partner.getAbility().getId() == Abilities.PLUS) {
				result.put(Stat.SPECIALATTACK, result.get(Stat.SPECIALATTACK) * 1.5);
			}
			break;
		case Abilities.PLUS:
			partner = gController.getFight().getPokemon(gController.getFight().getPartner(this.pokemon));
			if (partner != null && partner.getAbility().getId() == Abilities.MINUS) {
				result.put(Stat.SPECIALATTACK, result.get(Stat.SPECIALATTACK) * 1.5);
			}
			break;
		case Abilities.NOTSCHUTZ:
			if (this.pokemon.getAilment() != Ailment.FAINTED) {
				result.put(Stat.DEFENSE, result.get(Stat.DEFENSE) * 1.5);
			}
			break;
		case Abilities.ADRENALIN:
			if (this.pokemon.getAilment() != Ailment.NONE) {
				result.put(Stat.ATTACK, result.get(Stat.ATTACK) * 1.5);
			}
			break;
		case Abilities.HITZEWAHN:
			if (this.pokemon.getAilment() == Ailment.BURN) {
				result.put(Stat.SPECIALATTACK, result.get(Stat.SPECIALATTACK) * 1.5);
			}
			break;
		case Abilities.GIFTWAHN:
			if (this.pokemon.getAilment() == Ailment.HEAVY_POISON || this.pokemon.getAilment() == Ailment.POISON) {
				result.put(Stat.ATTACK, result.get(Stat.ATTACK) * 1.5);
			}
			break;
		case Abilities.RASANZ:
			if (this.pokemon.getAilment() != Ailment.NONE) {
				result.put(Stat.SPEED, result.get(Stat.SPEED) * 1.5);
			}
			break;
		case Abilities.SAUMSELIG:
			if (gController.getFight().getTurn() - this.pokemon.getFightingSince() < 5) {
				result.put(Stat.SPEED, result.get(Stat.SPEED) * .5);
				result.put(Stat.SPECIALATTACK, result.get(Stat.SPECIALATTACK) * .5);
				result.put(Stat.SPECIALDEFENSE, result.get(Stat.SPECIALDEFENSE) * .5);
			}
		case Abilities.SCHWÄCHLING:
			if (this.getCurrentHP() <= this.getStats().get(Stat.HP) * 0.5) {
				result.put(Stat.ATTACK, result.get(Stat.ATTACK) * .5);
				result.put(Stat.SPECIALATTACK, result.get(Stat.SPECIALATTACK) * .5);
			}
		}

		if (this.gController.getFight().getField().isTrickRoom()) {
			result.put(Stat.SPEED, result.get(Stat.SPEED) * -1);
		}

		return this.gController.getFight().getField().updateFightStats(result,
				this.gController.getFight().isPlayer(this.pokemon));
	}

	public int getCurrentXP() {
		return this.currentXP;
	}

	public void setCurrentXP(int currentXP) {
		this.currentXP = currentXP;
	}

	public int getLevelUpXP() {
		return this.levelUpXP;
	}

	private int calculateLevelUpXP() {
		return this.gController.getInformation().getLevelUpXP(this.pokemon, this.level + 1);
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
		for (Stat s : Stat.values()) {
			JsonObject currentDV = new JsonObject();
			currentDV.addProperty("name", s.name());
			currentDV.addProperty("dv", this.dvs.get(s));
			currentDV.addProperty("ev", this.evs.get(s));
			dvEvArray.add(currentDV);
		}
		data.add("dvev", dvEvArray);
		data.addProperty("nature", this.nature.name());
		return data;
	}

	public void importSaveData(JsonObject saveData) {
		if (saveData.get("dvev") != null) {
			this.dvs.clear();
			this.evs.clear();
			for (JsonElement je : saveData.get("dvev").getAsJsonArray()) {
				JsonObject currentDVEV = je.getAsJsonObject();
				Stat currentStat = Stat.valueOf(currentDVEV.get("name").getAsString().toUpperCase());
				this.dvs.put(currentStat, currentDVEV.get("dv").getAsShort());
				this.evs.put(currentStat, currentDVEV.get("ev").getAsShort());
			}
			for(Stat s : Stat.values()) {
				if(!this.dvs.containsKey(s)) {
					this.dvs.put(s, (short) Main.RNG.nextInt(32));
				}
				if(!this.evs.containsKey(s)) {
					this.evs.put(s, (short) 0);
				}
			}
		}
		this.generateStats(saveData.get("level").getAsShort());
		if(saveData.has("current_xp")) {
			this.currentXP = saveData.get("current_xp").getAsInt();
		}
		if(saveData.has("current_hp")) {
			this.currentHP = saveData.get("current_hp").getAsShort();
		} else {
			this.currentHP = this.stats.get(Stat.HP);
		}
		this.nature = saveData.get("nature") != null
				? Nature.valueOf(saveData.get("nature").getAsString().toUpperCase()) : Nature.getRandomNature();
	}

	public Random getRNG() {
		return this.rng;
	}

	public void setBaseStats(HashMap<Stat, Short> baseStats) {
		this.baseStats = new HashMap<>(baseStats);
		this.updateStats();
	}

	public short getBaseStat(Stat s) {
		return this.baseStats.get(s);
	}

	public void increaseEV(Stat stat, short value) {
		short sum = 0;
		for (Stat s : this.evs.keySet()) {
			sum += this.evs.get(s);
		}
		if (sum < 510) {
			short temp = (short) (this.evs.get(stat) + (sum + value > 510 ? 510 - sum : value));
			this.evs.put(stat, temp < 252 ? temp : 252);
			this.updateStats();
		}
	}

	public void setDV(Stat stat, int value) {
		this.dvs.put(stat, (short) value);
		this.updateStats();
	}

	public Nature getNature() {
		return this.nature;
	}

	public void setNature(Nature nature) {
		this.nature = nature;
	}


}
