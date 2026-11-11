package com.rustank.world;

/**
 * Fixed, square Rustank play area. Coordinates use Minecraft's X/Z plane.
 * The maximum edge is exclusive so the configured size is exactly 4,000 blocks.
 */
public final class WorldBounds {
    public static final int SIZE = 4_000;
    public static final int MIN_X = -SIZE / 2;
    public static final int MIN_Z = -SIZE / 2;
    public static final int MAX_X_EXCLUSIVE = SIZE / 2;
    public static final int MAX_Z_EXCLUSIVE = SIZE / 2;

    private WorldBounds() {
    }

    public static boolean contains(int blockX, int blockZ) {
        return blockX >= MIN_X && blockX < MAX_X_EXCLUSIVE
                && blockZ >= MIN_Z && blockZ < MAX_Z_EXCLUSIVE;
    }

    public static int clampX(int blockX) {
        return Math.max(MIN_X, Math.min(MAX_X_EXCLUSIVE - 1, blockX));
    }

    public static int clampZ(int blockZ) {
        return Math.max(MIN_Z, Math.min(MAX_Z_EXCLUSIVE - 1, blockZ));
    }
}
