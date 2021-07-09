package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Pokemon;

public class ItemSteal {

    private Pokemon source;
    private Pokemon target;

    public ItemSteal(Pokemon source, Pokemon target) {
        this.source = source;
        this.target = target;
    }

    public Pokemon getSource() {
        return this.source;
    }

    public Pokemon getTarget() {
        return this.target;
    }

}
