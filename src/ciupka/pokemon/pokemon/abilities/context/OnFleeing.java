package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Pokemon;

public class OnFleeing {

    private Pokemon source;

    public OnFleeing(Pokemon source) {
        this.source = source;
    }

    public Pokemon getSource() {
        return this.source;
    }

}
