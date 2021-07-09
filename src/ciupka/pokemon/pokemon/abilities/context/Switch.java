package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Pokemon;

public class Switch {

    private Pokemon target;
    private Pokemon replacement;

    public Switch(Pokemon target, Pokemon replacement) {
        this.target = target;
        this.replacement = replacement;
    }

    public Pokemon getTarget() {
        return this.target;
    }

    public Pokemon getReplacement() {
        return this.replacement;
    }

}