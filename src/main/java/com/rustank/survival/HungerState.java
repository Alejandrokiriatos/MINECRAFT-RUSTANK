package com.rustank.survival;

/**
 * Server-authoritative hunger model, independent from Minecraft classes.
 *
 * <p>Food is tracked from 0 to 100. Normal activity drains one point every
 * 90 seconds; sprinting doubles the drain rate. The model is intentionally
 * separate from player persistence, networking and HUD integration.</p>
 */
public final class HungerState {
    public static final int MIN_HUNGER = 0;
    public static final int MAX_HUNGER = 100;
    public static final int NORMAL_DRAIN_TICKS = 1_800; // 90 seconds at 20 TPS
    public static final int SPRINT_DRAIN_TICKS = 900;   // 45 seconds at 20 TPS

    public enum Condition {
        SATISFIED,
        HUNGRY,
        STARVING,
        EMPTY
    }

    private int hunger = MAX_HUNGER;
    private int drainProgress;

    public int getHunger() {
        return hunger;
    }

    /** Returns the gameplay condition for the current food level. */
    public Condition getCondition() {
        if (hunger == MIN_HUNGER) {
            return Condition.EMPTY;
        }
        if (hunger <= 10) {
            return Condition.STARVING;
        }
        if (hunger <= 30) {
            return Condition.HUNGRY;
        }
        return Condition.SATISFIED;
    }

    /** A normalized 0..1 value suitable for HUD rendering. */
    public float getHudFraction() {
        return hunger / (float) MAX_HUNGER;
    }

    /**
     * Advances the hunger clock by one server tick.
     *
     * @return true when hunger changed and a HUD/state sync may be needed
     */
    public boolean tick(boolean sprinting, boolean creativeOrSpectator) {
        if (creativeOrSpectator) {
            return false;
        }

        int interval = sprinting ? SPRINT_DRAIN_TICKS : NORMAL_DRAIN_TICKS;
        if (drainProgress < interval - 1) {
            drainProgress++;
            return false;
        }

        drainProgress = 0;
        if (hunger == MIN_HUNGER) {
            return false;
        }

        hunger--;
        return true;
    }

    /**
     * Restores food, clamped to the supported 0..100 range.
     *
     * @return true when hunger changed
     */
    public boolean eat(int amount) {
        if (amount <= 0 || hunger == MAX_HUNGER) {
            return false;
        }

        int previous = hunger;
        hunger = (int) Math.min(MAX_HUNGER, (long) hunger + amount);
        drainProgress = 0;
        return hunger != previous;
    }

    /** Sets hunger when loading persisted player data. */
    public void loadHunger(int value) {
        hunger = Math.max(MIN_HUNGER, Math.min(MAX_HUNGER, value));
        drainProgress = 0;
    }
}
