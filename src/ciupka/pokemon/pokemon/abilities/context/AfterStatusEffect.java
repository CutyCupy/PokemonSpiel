package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Pokemon;

public class AfterStatusEffect {

    private Pokemon target;

    public AfterStatusEffect(Pokemon self) {
        this.target = self;
    }

    public Pokemon getSelf() {
        return this.target;
    }
}
