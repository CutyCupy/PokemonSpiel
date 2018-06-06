package de.alexanderciupka.pokemon.pokemon;

import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.constants.Moves;
import de.alexanderciupka.pokemon.fighting.Target;
import de.alexanderciupka.pokemon.map.GameController;

public class Move {

	private int id;
	private String name;
	private int pp;
	private int currentPP;
	private int minHits;
	private int maxHits;
	private int ailmentChance;
	private float statChance;
	private float healing;
	private float drain;
	private int power;
	private int priority;
	private int crit;
	private String description;
	private Ailment ailment;
	private SecondaryAilment secondaryAilment;
	private DamageClass damageClass;
	private float accuracy;
	private String category;
	private HashMap<Stat, Integer> statChanges;

	private String userAnimation;
	private String targetAnimation;

	private boolean disabled;

	private Target target;

	private Type moveType;

	public Move(int id) {
		this.statChanges = new HashMap<Stat, Integer>();
		for (Stat s : Stat.values()) {
			this.statChanges.put(s, 0);
		}
		this.id = id;
	}

	public Move(String name) {
		this.statChanges = new HashMap<Stat, Integer>();
		for (Stat s : Stat.values()) {
			this.statChanges.put(s, 0);
		}
		this.name = name;
	}

	public void setPower(String power) {
		try {
			this.power = Integer.parseInt(power);
		} catch (Exception e) {
			this.power = 1;
		}
	}

	public int getPower() {
		return this.power;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getAccuracy() {
		float accuracy = this.accuracy;

		if (accuracy <= 1.0f && GameController.getInstance().isFighting()) {
			switch (GameController.getInstance().getFight().getField().getWeather()) {
			case FOG:
				accuracy *= 0.6;
				break;
			case HAIL:
				if (Moves.BLIZZARD == this.getId()) {
					accuracy = 1f;
				}
				break;
			case RAIN:
				if (Moves.DONNER == this.getId() || Moves.ORKAN == this.getId()) {
					accuracy = 1f;
				}
				break;
			case SUN:
				if (Moves.DONNER == this.getId() || Moves.ORKAN == this.getId()) {
					accuracy = 0.5f;
				}
				break;
			default:
				break;
			}
		}
		return accuracy;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPp() {
		return this.pp;
	}

	public void setPp(int pp) {
		this.pp = pp;
		this.currentPP = pp;
	}

	public int getMinHits() {
		return this.minHits;
	}

	public int getMaxHits() {
		return this.maxHits;
	}

	public float getAilmentChance() {
		return this.ailmentChance;
	}

	public void setAilmentChance(int ailmentChance) {
		this.ailmentChance = ailmentChance;
	}

	public float getStatChance() {
		return this.statChance;
	}

	public void setStatChance(float statChance) {
		this.statChance = statChance;
	}

	public float getHealing() {
		float healing = this.healing;

		if (GameController.getInstance().isFighting()) {
			switch (GameController.getInstance().getFight().getField().getWeather()) {
			case SUN:
				if (Moves.MONDSCHEIN == this.getId() || Moves.MORGENGRAUEN == this.getId()
						|| Moves.SYNTHESE == this.getId()) {
					healing = 0.66f;
				}
				break;
			case SANDSTORM:
			case HAIL:
			case FOG:
			case RAIN:
				if (Moves.MONDSCHEIN == this.getId() || Moves.MORGENGRAUEN == this.getId()
						|| Moves.SYNTHESE == this.getId()) {
					healing = 0.25f;
				}
				break;
			default:
				break;
			}
		}

		return healing;
	}

	public void setHealing(float healing) {
		this.healing = healing;
	}

	public float getDrain() {
		return this.drain;
	}

	public void setDrain(float drain) {
		this.drain = drain;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Move) {
			return ((Move) obj).getName().equals(this.getName());
		}
		return false;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public Move clone() {
		Move newMove = new Move(this.id);
		newMove.setAccuracy(this.accuracy);
		newMove.setAilmentChance(this.ailmentChance);
		newMove.setDrain(this.drain);
		newMove.setHealing(this.healing);
		newMove.setMaxHits(this.maxHits);
		newMove.setMinHits(this.minHits);
		newMove.setName(this.name);
		newMove.setPower(this.power);
		newMove.setPp(this.pp);
		newMove.setStatChance(this.statChance);
		newMove.setCurrentPP(this.currentPP);
		newMove.statChanges = new HashMap<>(this.statChanges);
		newMove.setMoveType(this.moveType);
		newMove.setAilment(this.ailment);
		newMove.setDamageClass(this.damageClass);
		newMove.setDescription(this.description);
		newMove.setPriority(this.priority);
		newMove.setCategory(this.category);
		newMove.setTarget(this.target);
		newMove.setCrit(this.crit);
		newMove.setAilment(this.secondaryAilment);
		newMove.setUserAnimation(this.userAnimation);
		newMove.setTargetAnimation(this.targetAnimation);
		return newMove;
	}

	public void setMinHits(int minHits) {
		this.minHits = minHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public void reducePP() {
		this.currentPP--;
	}

	public int getCurrentPP() {
		return this.currentPP;
	}

	public void setCurrentPP(int pp) {
		this.currentPP = pp;
	}

	public void addStatChange(Stat s, int change) {
		this.statChanges.put(s, change);
	}

	public boolean checkStatChange() {
		Random rng = new Random();
		if (rng.nextFloat() < this.statChance) {
			return true;
		}
		return false;
	}

	public boolean checkUserBuff() {
		if (this.category.contains("raise")) {
			return true;
		}
		switch (this.target) {
		case ALL:
		case USER:
			return true;
		case OPPONENT:
		default:
			return false;
		}
	}

	public boolean checkEnemyBuff() {
		if (this.category.contains("raise")) {
			return false;
		}
		switch (this.target) {
		case ALL:
		case OPPONENT:
			return true;
		case USER:
		default:
			return false;
		}
	}

	public int changeStat(Stat s) {
		if (this.statChanges.containsKey(s)) {
			return this.statChanges.get(s);
		}
		return 0;
	}

	public Type getMoveType() {
		if (GameController.getInstance().isFighting()) {
			Type type = this.getMoveType();
			if (Moves.METEOROLOGE == this.getId()) {
				switch (GameController.getInstance().getFight().getField().getWeather()) {
				case HAIL:
					type = Type.ICE;
					break;
				case RAIN:
					type = Type.WATER;
					break;
				case SANDSTORM:
					type = Type.ROCK;
					break;
				case SUN:
					type = Type.FIRE;
					break;
				default:
					break;
				}
			}
			return type;
		}
		return this.moveType;
	}

	public void setMoveType(Type moveType) {
		this.moveType = moveType;
	}

	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Ailment getAilment() {
		return this.ailment;
	}

	public void setAilment(Ailment ailment) {
		this.ailment = ailment;
	}

	public SecondaryAilment getSecondaryAilment() {
		return this.secondaryAilment;
	}

	public void setAilment(SecondaryAilment secondaryAilment) {
		this.secondaryAilment = secondaryAilment;
	}

	public DamageClass getDamageClass() {
		return this.damageClass;
	}

	public void setDamageClass(DamageClass damageClass) {
		this.damageClass = damageClass;
	}

	public HashMap<Stat, Integer> getstatChanges() {
		return this.statChanges;
	}

	public void setStatChanges(HashMap<Stat, Integer> statChanges) {
		this.statChanges = new HashMap<>(statChanges);
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Target getTarget() {
		return this.target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public int getCrit() {
		return this.crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		data.addProperty("id", this.id);
		data.addProperty("currentPP", this.currentPP);
		return data;
	}

	public static Move importSaveData(JsonObject saveData) {
		Move result = GameController.getInstance().getInformation().getMoveById(saveData.get("id").getAsInt());
		result.setCurrentPP(saveData.get("currentPP").getAsInt());
		return result;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getUserAnimation() {
		return this.userAnimation;
	}

	public void setUserAnimation(String animation) {
		this.userAnimation = animation;
	}

	public String getTargetAnimation() {
		return this.targetAnimation;
	}

	public void setTargetAnimation(String animation) {
		this.targetAnimation = animation;
	}

}
