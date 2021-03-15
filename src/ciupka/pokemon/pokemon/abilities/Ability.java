package ciupka.pokemon.pokemon.abilities;

public abstract class Ability {
	
	protected int id;
	
	protected String name;
	protected String description;
	
	
	
	// TODO: think about what should be given as parameter and returned
	public abstract void onAttack();
	public abstract void afterAttack();
	
	public abstract void onBattleEntry();
	public abstract void afterBattleEntry();
	
	public abstract void onTurnEnd();
	
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
	
	// TODO Backentaschen, Schnarchnase, Plus, Minus, Völlerei, Zeitspiel, Schwermetall, Leichtmetall, Reiche Ernte, Trance-Modus
}
