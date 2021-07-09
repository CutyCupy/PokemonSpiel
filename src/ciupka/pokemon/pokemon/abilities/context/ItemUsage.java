package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Item;
import ciupka.pokemon.pokemon.Pokemon;

public class ItemUsage {

    private Pokemon target;
    private Item item;

    public ItemUsage(Pokemon target, Item item) {
        this.target = target;
        this.item = item;
    }

    public Pokemon getTarget() {
        return this.target;
    }

    public Item getItem() {
        return this.item;
    }

}
