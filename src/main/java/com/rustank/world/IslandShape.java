package com.rustank.world;

import java.util.SplittableRandom;

/**
 * Deterministic first-pass island coastline mask.
 *
 * This defines the planned land/ocean footprint only; it does not generate
 * terrain, beaches, water blocks, elevation, or biome data.
 */
public final class IslandShape {
    private static final double BASE_RADIUS = 1_750.0;
    private static final double WAVE_ONE = 90.0;
    private static final double WAVE_TWO = 55.0;
    private static final double WAVE_THREE = 35.0;
    private static final double NEAR_COAST_DISTANCE = 300.0;

    private final double phaseOne;
    private final double phaseTwo;
    private final double phaseThree;

    private IslandShape(long seed) {
        SplittableRandom random = new SplittableRandom(seed);
        phaseOne = random.nextDouble(0.0, Math.PI * 2.0);
        phaseTwo = random.nextDouble(0.0, Math.PI * 2.0);
        phaseThree = random.nextDouble(0.0, Math.PI * 2.0);
    }

    public static IslandShape forSeed(long seed) {
        return new IslandShape(seed);
    }

    public boolean isLand(int x, int z) {
        if (!WorldBounds.contains(x, z)) return false;
        double distance = Math.hypot((double) x, (double) z);
        return distance <= coastRadius(Math.atan2(z, x));
    }

    public boolean isOcean(int x, int z) {
        return WorldBounds.contains(x, z) && !isLand(x, z);
    }

    /** True for land points no farther than the requested distance from shore. */
    public boolean isNearCoast(int x, int z, double maximumDistance) {
        if (maximumDistance < 0.0 || !Double.isFinite(maximumDistance) || !isLand(x, z)) return false;
        double distance = Math.hypot((double) x, (double) z);
        return coastRadius(Math.atan2(z, x)) - distance <= maximumDistance;
    }

    public double coastRadiusAtAngle(double angleRadians) {
        if (!Double.isFinite(angleRadians)) throw new IllegalArgumentException("angle must be finite");
        return coastRadius(angleRadians);
    }

    private double coastRadius(double angle) {
        return BASE_RADIUS
                + WAVE_ONE * Math.sin(3.0 * angle + phaseOne)
                + WAVE_TWO * Math.sin(5.0 * angle + phaseTwo)
                + WAVE_THREE * Math.cos(9.0 * angle + phaseThree);
    }

    public static double nearCoastDistance() {
        return NEAR_COAST_DISTANCE;
    }
}
