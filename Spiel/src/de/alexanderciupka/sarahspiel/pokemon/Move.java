package de.alexanderciupka.sarahspiel.pokemon;

import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonObject;

public class Move {

	private int id;
	private String name;
	private int pp;
	private int currentPP;
	private int minHits;
	private int maxHits;
	private float ailmentChance;
	private float statChance;
	private float healing;
	private float drain;
	private int power;
	private int priority;
	private String description;
	private Ailment ailment;
	private DamageClass damageClass;
	private float accuracy;
	private HashMap<Integer,Integer> stat_changes;

	private Type moveType;

	public Move(int id) {
		stat_changes = new HashMap<Integer,Integer>();
		this.id = id;
	}

	public Move(String name) {
		stat_changes = new HashMap<Integer,Integer>();
		this.name = name;
	}

	public void setPower(String power) {
		try {
			this.power = Integer.parseInt(power);
		} catch(Exception e) {
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
		return this.accuracy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPp() {
		return pp;
	}

	public void setPp(int pp) {
		this.pp = pp;
		this.currentPP = pp;
	}

	public int getMinHits() {
		return minHits;
	}

	public int getMaxHits() {
		return maxHits;
	}

	public float getAilmentChance() {
		return ailmentChance;
	}

	public void setAilmentChance(float ailmentChance) {
		this.ailmentChance = ailmentChance;
	}

	public float getStatChance() {
		return statChance;
	}

	public void setStatChance(float statChance) {
		this.statChance = statChance;
	}

	public float getHealing() {
		return healing;
	}

	public void setHealing(float healing) {
		this.healing = healing;
	}

	public float getDrain() {
		return drain;
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
		return super.equals(obj);
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
		newMove.stat_changes = (HashMap<Integer, Integer>) stat_changes.clone();
		newMove.setMoveType(this.moveType);
		newMove.setAilment(this.ailment);
		newMove.setDamageClass(this.damageClass);
		newMove.setDescription(this.description);
		newMove.setPriority(this.priority);
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

	public void addStatChange(int index, int change) {
		if(index >= 0 && index < 5)
			stat_changes.put(index, change);
	}

	public boolean checkStatChange() {
		Random rng = new Random();
		if(rng.nextFloat() < statChance) {
			return true;
		}
		return false;
	}

	public boolean checkUserBuff() {
		if(accuracy > 100) {
			return true;
		}
		return false;
	}

	public int changeStat(int index) {
		if(stat_changes.containsKey(index)) {
			return stat_changes.get(index);
		}
		return 0;
	}

	public Type getMoveType() {
		return moveType;
	}

	public void setMoveType(Type moveType) {
		this.moveType = moveType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Ailment getAilment() {
		return ailment;
	}

	public void setAilment(Ailment ailment) {
		this.ailment = ailment;
	}

	public DamageClass getDamageClass() {
		return damageClass;
	}

	public void setDamageClass(DamageClass damageClass) {
		this.damageClass = damageClass;
	}

	public HashMap<Integer, Integer> getStat_changes() {
		return stat_changes;
	}

	public void setStat_changes(HashMap<Integer, Integer> stat_changes) {
		this.stat_changes = stat_changes;
	}

	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		data.addProperty("id", this.id);
		data.addProperty("currentPP", this.currentPP);
		return data;
	}

}
