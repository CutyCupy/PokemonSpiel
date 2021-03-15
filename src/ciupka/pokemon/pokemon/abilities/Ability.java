package ciupka.pokemon.pokemon.abilities;

import ciupka.pokemon.fight.Battlefield;
import ciupka.pokemon.pokemon.Move;
import ciupka.pokemon.pokemon.Pokemon;

public abstract class Ability {
	
	protected int id;
	
	protected String name;
	protected String description;
	
	
	// TODO: think about what should be given as parameter and returned
	public abstract void onAttack(Move usedMove, Pokemon self, Pokemon[] hitPokemon);
	public abstract void afterAttack(Move usedMove, Pokemon self, Pokemon[] hitPokemon, int damage, boolean hit);
	
	public abstract void onBattleEntry(Pokemon[] opponents, Battlefield field);
	public abstract void afterBattleEntry(Pokemon[] opponents, Battlefield field);
	
	public abstract void onTurnEnd(Pokemon self, Pokemon[] teammates);
	
	public abstract void onAttacked();
	public abstract void afterAttacked();
	
	public abstract void onStatusEffect();
	public abstract void afterStatusEffect();
	
	public abstract void onWeatherChange();
	public abstract void afterWeatherChange();
	
	public abstract void onEnemySwitch();
	public abstract void afterEnemySwitch();
	
	public abstract void onStatDecrease();
	public abstract void afterStatDecrease();
	
	public abstract void onStatIncrease();
	public abstract void afterStatIncrease();
	
	public abstract void onSwitchout();
	public abstract void afterSwitchout();
	
	public abstract void onFleeing();
	public abstract void afterFleeing();
	
	public abstract void onItemSteal();
	public abstract void afterItemSteal();
	
	public abstract void onDamaged();
	public abstract void afterDamaged();
	
	public abstract void onStatusEffectTrigger();
	public abstract void afterStatusEffectTrigger();
	
	public abstract void getStat();
	
	public abstract void onItemUsage();
	public abstract void afterItemUsage();
	
	public abstract void getTurnPrio();
	
	public abstract void getMoveType();
	public abstract void getMoveStrength();
	public abstract void getMoveAccuracy();
	
	public abstract double getEncounterChanceModifier();
	
	// TODO Backentaschen, Schnarchnase, Plus, Minus, Völlerei, Zeitspiel, Schwermetall, Leichtmetall, Reiche Ernte, Trance-Modus
}
