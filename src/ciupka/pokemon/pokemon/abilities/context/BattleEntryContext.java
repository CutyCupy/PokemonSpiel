package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Pokemon;

public class BattleEntryContext {

    private Pokemon entry;

    public BattleEntryContext(Pokemon entry) {
        this.entry = entry;
    }

    public Pokemon getEntry() {
        return this.entry;
    }
}
