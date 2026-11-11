package com.rustank.world;

/**
 * Deterministic first-pass terrain sampler for the Rustank island.
 *
 * It returns a height and biome classification for X/Z coordinates. It does
 * not place Minecraft blocks or register a chunk generator yet.
 */
public final class TerrainProfile {
    public static final int SEA_LEVEL = 63;
    public static final int MIN_LAND_HEIGHT = 64;
    public static final int MAX_LAND_HEIGHT = 160;
    private static final double BEACH_WIDTH = 85.0;

    public enum Biome {
        OCEAN,
        BEACH,
        PLAINS,
        FOREST,
        DESERT,
        ROCKY_HIGHLANDS,
        OUT_OF_BOUNDS
    }

    public record TerrainSample(boolean insideMap, Biome biome, int surfaceHeight, boolean coastal) { }

    private final long seed;
    private final IslandShape island;

    private TerrainProfile(long seed) {
        this.seed = seed;
        this.island = IslandShape.forSeed(seed);
    }

    public static TerrainProfile forSeed(long seed) {
        return new TerrainProfile(seed);
    }

    public TerrainSample sample(int x, int z) {
        if (!WorldBounds.contains(x, z)) {
            return new TerrainSample(false, Biome.OUT_OF_BOUNDS, SEA_LEVEL, false);
        }
        if (!island.isLand(x, z)) {
            return new TerrainSample(true, Biome.OCEAN, SEA_LEVEL, false);
        }

        boolean coastal = island.isNearCoast(x, z, BEACH_WIDTH);
        if (coastal) {
            double distanceToShore = Math.max(0.0,
                    island.coastRadiusAtAngle(Math.atan2(z, x)) - Math.hypot((double) x, (double) z));
            int beachHeight = SEA_LEVEL + 1 + (int) Math.round(4.0 * distanceToShore / BEACH_WIDTH);
            return new TerrainSample(true, Biome.BEACH, Math.min(68, beachHeight), true);
        }

        double broadRelief = valueNoise(x, z, 420, 0x51A7L);
        double hills = valueNoise(x, z, 145, 0xA11CEL);
        double mountainField = valueNoise(x, z, 690, 0xB00BL);
        int height = 78 + (int) Math.round(broadRelief * 23.0 + hills * 8.0);
        if (mountainField > 0.42) {
            height += (int) Math.round((mountainField - 0.42) * 58.0);
        }
        height = Math.max(MIN_LAND_HEIGHT, Math.min(MAX_LAND_HEIGHT, height));

        double moisture = (valueNoise(x, z, 510, 0xC0FFEE) + 1.0) * 0.5;
        double temperature = (valueNoise(x, z, 760, 0x7E4A) + 1.0) * 0.5;
        Biome biome;
        if (height >= 112) {
            biome = Biome.ROCKY_HIGHLANDS;
        } else if (temperature > 0.62 && moisture < 0.43) {
            biome = Biome.DESERT;
        } else if (moisture > 0.54) {
            biome = Biome.FOREST;
        } else {
            biome = Biome.PLAINS;
        }
        return new TerrainSample(true, biome, height, false);
    }

    private double valueNoise(int x, int z, int scale, long salt) {
        int cellX = Math.floorDiv(x, scale);
        int cellZ = Math.floorDiv(z, scale);
        double localX = (double) Math.floorMod(x, scale) / scale;
        double localZ = (double) Math.floorMod(z, scale) / scale;
        localX = smooth(localX);
        localZ = smooth(localZ);

        double a = lattice(cellX, cellZ, salt);
        double b = lattice(cellX + 1, cellZ, salt);
        double c = lattice(cellX, cellZ + 1, salt);
        double d = lattice(cellX + 1, cellZ + 1, salt);
        double north = lerp(a, b, localX);
        double south = lerp(c, d, localX);
        return lerp(north, south, localZ);
    }

    private double lattice(int x, int z, long salt) {
        long value = seed ^ salt;
        value ^= (long) x * 0x9E3779B97F4A7C15L;
        value = Long.rotateLeft(value, 27);
        value ^= (long) z * 0xC2B2AE3D27D4EB4FL;
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        value ^= value >>> 31;
        return ((value >>> 11) * 0x1.0p-52) - 1.0;
    }

    private static double smooth(double value) {
        return value * value * (3.0 - 2.0 * value);
    }

    private static double lerp(double from, double to, double amount) {
        return from + (to - from) * amount;
    }
}
