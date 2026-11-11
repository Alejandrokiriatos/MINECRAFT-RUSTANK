package com.rustank.survival;

/**
 * Server-authoritative hydration model.
 *
 * <p>This class deliberately contains no Minecraft or client references so its
 * rules can be tested independently and reused by player persistence, commands
 * and the HUD without duplicating gameplay calculations.</p>
 */
public final class HydrationState {
    public static final int MIN_HYDRATION = 0;
    public static final int MAX_HYDRATION = 100;
    public static final int NORMAL_DRAIN_TICKS = 1_200; // 60 seconds at 20 TPS
    public static final int SPRINT_DRAIN_TICKS = 600;   // 30 seconds at 20 TPS

    public enum Condition {
        HYDRATED,
        THIRSTY,
        SEVERELY_THIRSTY,
        DEHYDRATED
    }

    private int hydration = MAX_HYDRATION;
    private int drainProgress;

    /** Returns the gameplay condition for the current hydration level. */
    public Condition getCondition() {
        if (hydration == MIN_HYDRATION) {
            return Condition.DEHYDRATED;
        }
        if (hydration <= 10) {
            return Condition.SEVERELY_THIRSTY;
        }
        if (hydration <= 25) {
            return Condition.THIRSTY;
        }
        return Condition.HYDRATED;
    }

    /** A normalized 0..1 value suitable for HUD rendering. */
    public float getHudFraction() {
        return hydration / (float) MAX_HYDRATION;
    }

    public int getHydration() {
        return hydration;
    }

    public boolean isDehydrated() {
        return hydration == MIN_HYDRATION;
    }

    /**
     * Advances the hydration clock by one server tick.
     *
     * @return true when hydration changed and a HUD/state sync may be needed
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
        if (hydration == MIN_HYDRATION) {
            return false;
        }

        hydration--;
        return true;
    }

    /**
     * Restores hydration, clamped to the supported 0..100 range.
     *
     * <p>The addition is performed as a long to prevent integer overflow when
     * a malformed or unusually large amount is supplied.</p>
     *
     * @return true when hydration changed
     */
    public boolean drink(int amount) {
        if (amount <= 0 || hydration == MAX_HYDRATION) {
            return false;
        }

        int previous = hydration;
        hydration = (int) Math.min(MAX_HYDRATION, (long) hydration + amount);
        drainProgress = 0;
        return hydration != previous;
    }

    /**
     * Sets hydration when loading persisted player data.
     */
    public void loadHydration(int value) {
        hydration = Math.max(MIN_HYDRATION, Math.min(MAX_HYDRATION, value));
        drainProgress = 0;
    }
}
