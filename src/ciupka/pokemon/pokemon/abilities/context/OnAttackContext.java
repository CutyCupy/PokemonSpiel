package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.pokemon.Move;
import ciupka.pokemon.pokemon.Pokemon;

public class OnAttackContext {
    private Move move;

    private Pokemon source;
    private Pokemon target;

    public OnAttackContext(Move move, Pokemon source, Pokemon target) {
        this.move = move;
        this.source = source;
        this.target = target;
    }

    public Pokemon getSource() {
        return source;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Pokemon getTarget() {
        return target;
    }

    public void setTarget(Pokemon target) {
        this.target = target;
    }

    public void setSource(Pokemon source) {
        this.source = source;
    }
}