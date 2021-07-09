package ciupka.pokemon.pokemon.abilities.context;

public class AfterAttackContext {

    private OnAttackContext context;
    private boolean hit;
    private int damage;

    public AfterAttackContext(OnAttackContext context, boolean hit, int damage) {
        this.context = context;
        this.hit = hit;
        this.damage = damage;
    }

    public boolean isHit() {
        return this.hit;
    }

    public int getDamage() {
        return this.damage;
    }

    public OnAttackContext getContext() {
        return this.context;
    }
}
