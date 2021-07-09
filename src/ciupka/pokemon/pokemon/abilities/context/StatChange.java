package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.enums.eStat;
import ciupka.pokemon.pokemon.Pokemon;

public class StatChange {

    private Pokemon target;
    private eStat stat;
    private int modifier;

    public StatChange(Pokemon target, eStat stat, int modifier) {
        this.target = target;
        this.stat = stat;
        this.modifier = modifier;
    }

    public Pokemon getTarget() {
        return this.target;
    }

    public eStat getStat() {
        return this.stat;
    }

    public int getModifier() {
        return this.modifier;
    }
}