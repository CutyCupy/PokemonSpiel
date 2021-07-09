package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.enums.eStatus;

public class OnStatusEffect {

    private eStatus effect;

    public OnStatusEffect(eStatus effect) {
        this.effect = effect;
    }

    public eStatus getStatusEffect() {
        return this.effect;
    }

}