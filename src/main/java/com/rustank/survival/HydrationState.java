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

    private int hydration = MAX_HYDRATION;
    private int drainProgress;

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

        drainProgress++;
        int interval = sprinting ? SPRINT_DRAIN_TICKS : NORMAL_DRAIN_TICKS;
        if (drainProgress < interval) {
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
     * @return true when hydration changed
     */
    public boolean drink(int amount) {
        if (amount <= 0 || hydration == MAX_HYDRATION) {
            return false;
        }

        int previous = hydration;
        hydration = Math.min(MAX_HYDRATION, hydration + amount);
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
