package com.rustank.survival;

/**
 * Aggregates the server-side hunger and hydration models for one player.
 *
 * <p>This is the per-player domain object intended for the upcoming Forge
 * persistence and networking adapter. It contains no Minecraft references.</p>
 */
public final class SurvivalNeedsState {
    public record Snapshot(HungerState.Snapshot hunger, HydrationState.Snapshot hydration) {
        public Snapshot {
            if (hunger == null || hydration == null) {
                throw new IllegalArgumentException("Need snapshots cannot be null");
            }
        }
    }

    private final HungerState hunger = new HungerState();
    private final HydrationState hydration = new HydrationState();

    public int getHunger() {
        return hunger.getHunger();
    }

    public int getHydration() {
        return hydration.getHydration();
    }

    public HungerState.Condition getHungerCondition() {
        return hunger.getCondition();
    }

    public HydrationState.Condition getHydrationCondition() {
        return hydration.getCondition();
    }

    public float getHungerHudFraction() {
        return hunger.getHudFraction();
    }

    public float getHydrationHudFraction() {
        return hydration.getHudFraction();
    }

    /**
     * Advances both survival clocks once. Returns true when either value changed.
     */
    public boolean tick(boolean sprinting, boolean creativeOrSpectator) {
        boolean hungerChanged = hunger.tick(sprinting, creativeOrSpectator);
        boolean hydrationChanged = hydration.tick(sprinting, creativeOrSpectator);
        return hungerChanged || hydrationChanged;
    }

    public boolean eat(int foodPoints) {
        return hunger.eat(foodPoints);
    }

    public boolean drink(int hydrationPoints) {
        return hydration.drink(hydrationPoints);
    }

    /** Captures both values and both partial drain timers for player persistence. */
    public Snapshot snapshot() {
        return new Snapshot(hunger.snapshot(), hydration.snapshot());
    }

    /** Restores both need models from one validated per-player snapshot. */
    public void restore(Snapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot cannot be null");
        }
        hunger.restore(snapshot.hunger());
        hydration.restore(snapshot.hydration());
    }
}
