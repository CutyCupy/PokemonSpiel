package ciupka.pokemon.pokemon.abilities;

import ciupka.pokemon.fight.Battlefield;
import ciupka.pokemon.pokemon.Pokemon;
import ciupka.pokemon.pokemon.Team;
import ciupka.pokemon.pokemon.abilities.context.AfterAttackContext;
import ciupka.pokemon.pokemon.abilities.context.AfterStatusEffect;
import ciupka.pokemon.pokemon.abilities.context.AfterWeatherChange;
import ciupka.pokemon.pokemon.abilities.context.BattleEntryContext;
import ciupka.pokemon.pokemon.abilities.context.ItemSteal;
import ciupka.pokemon.pokemon.abilities.context.ItemUsage;
import ciupka.pokemon.pokemon.abilities.context.OnAttackContext;
import ciupka.pokemon.pokemon.abilities.context.OnEncounterChance;
import ciupka.pokemon.pokemon.abilities.context.OnFleeing;
import ciupka.pokemon.pokemon.abilities.context.OnStatusEffect;
import ciupka.pokemon.pokemon.abilities.context.OnWeatherChange;
import ciupka.pokemon.pokemon.abilities.context.StatChange;
import ciupka.pokemon.pokemon.abilities.context.StatusEffectTrigger;
import ciupka.pokemon.pokemon.abilities.context.Switch;
import ciupka.pokemon.pokemon.abilities.context.TurnContext;

public abstract class Ability {

	protected int id;

	protected String name;
	protected String description;

	public abstract void onAttack(Pokemon abilityHolder, Battlefield battlefield, OnAttackContext context);

	public abstract void afterAttack(Pokemon abilityHolder, Battlefield battlefield, AfterAttackContext context);

	public abstract void onBattleEntry(Pokemon abilityHolder, Battlefield battlefield, BattleEntryContext context);

	public abstract void afterBattleEntry(Pokemon abilityHolder, Battlefield battlefield, BattleEntryContext context);

	public abstract void onTurnStart(Pokemon abilityHolder, Battlefield battlefield, TurnContext context);

	public abstract void onTurnEnd(Pokemon abilityHolder, Battlefield battlefield, TurnContext context);

	public abstract void onAttacked(Pokemon abilityHolder, Battlefield battlefield, OnAttackContext context);

	public abstract void afterAttacked(Pokemon abilityHolder, Battlefield battlefield, AfterAttackContext context);

	public abstract void onStatusEffect(Pokemon abilityHolder, Battlefield battlefield, OnStatusEffect context);

	public abstract void afterStatusEffect(Pokemon abilityHolder, Battlefield battlefield, AfterStatusEffect context);

	public abstract void onWeatherChange(Pokemon abilityHolder, Battlefield battlefield, OnWeatherChange context);

	public abstract void afterWeatherChange(Pokemon abilityHolder, Battlefield battlefield, AfterWeatherChange context);

	public abstract void onStatChange(Pokemon abilityHolder, Battlefield battlefield, StatChange context);

	public abstract void afterStatChange(Pokemon abilityHolder, Battlefield battlefield, StatChange context);

	public abstract void onSwitch(Pokemon abilityHolder, Battlefield battlefield, Switch context);

	public abstract void afterSwitch(Pokemon abilityHolder, Battlefield battlefield, Switch context);

	public abstract void onFleeing(Pokemon abilityHolder, Battlefield battlefield, OnFleeing context);

	public abstract void onItemSteal(Pokemon abilityHolder, Battlefield battlefield, ItemSteal context);

	public abstract void afterItemSteal(Pokemon abilityHolder, Battlefield battlefield, ItemSteal context);

	public abstract void onStatusEffectTrigger(Pokemon abilityHolder, Battlefield battlefield,
			StatusEffectTrigger context);

	public abstract void afterStatusEffectTrigger(Pokemon abilityHolder, Battlefield battlefield,
			StatusEffectTrigger context);

	public abstract void onItemUsage(Pokemon abilityHolder, Battlefield battlefield, ItemUsage context);

	public abstract void afterItemUsage(Pokemon abilityHolder, Battlefield battlefield, ItemUsage context);

	public abstract void onEncounterChance(Pokemon abilityHolder, Team team, OnEncounterChance context);
}
