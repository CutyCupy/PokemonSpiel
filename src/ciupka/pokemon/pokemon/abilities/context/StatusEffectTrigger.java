package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.enums.eStatus;
import ciupka.pokemon.pokemon.Pokemon;

public class StatusEffectTrigger {

    private Pokemon target;
    private eStatus effect;

    public StatusEffectTrigger(Pokemon target, eStatus effect) {
        this.target = target;
        this.effect = effect;
    }

    public Pokemon getTarget() {
        return this.target;
    }

    public eStatus getStatusEffect() {
        return this.effect;
    }

}
