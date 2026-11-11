package com.rustank.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Deterministic, chunk-addressable vegetation placement plan.
 *
 * Positions are generated from a world-space grid, not from per-chunk random
 * loops, so querying adjacent chunks in any order cannot duplicate plants.
 * This planner describes placements only; it does not place Minecraft blocks.
 */
public final class VegetationPlanner {
    private static final int CELL_SIZE = 12;
    private static final int JITTER = 3;
    private static final int MAX_SLOPE = 4;

    public enum Type {
        OAK_TREE,
        PINE_TREE,
        BUSH,
        CACTUS
    }

    public record Placement(int x, int z, int groundY, Type type) { }

    private VegetationPlanner() { }

    public static List<Placement> planChunk(long seed, int chunkX, int chunkZ) {
        int minX = Math.multiplyExact(chunkX, ChunkTerrainPlan.CHUNK_SIZE);
        int minZ = Math.multiplyExact(chunkZ, ChunkTerrainPlan.CHUNK_SIZE);
        int maxX = Math.addExact(minX, ChunkTerrainPlan.CHUNK_SIZE - 1);
        int maxZ = Math.addExact(minZ, ChunkTerrainPlan.CHUNK_SIZE - 1);
        int minCellX = Math.floorDiv(minX - JITTER, CELL_SIZE);
        int minCellZ = Math.floorDiv(minZ - JITTER, CELL_SIZE);
        int maxCellX = Math.floorDiv(maxX + JITTER, CELL_SIZE);
        int maxCellZ = Math.floorDiv(maxZ + JITTER, CELL_SIZE);
        TerrainProfile terrain = TerrainProfile.forSeed(seed);
        List<Placement> placements = new ArrayList<>();

        for (int cellZ = minCellZ; cellZ <= maxCellZ; cellZ++) {
            for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
                SplittableRandom random = new SplittableRandom(cellSeed(seed, cellX, cellZ));
                int x = cellX * CELL_SIZE + random.nextInt(-JITTER, JITTER + 1);
                int z = cellZ * CELL_SIZE + random.nextInt(-JITTER, JITTER + 1);
                if (x < minX || x > maxX || z < minZ || z > maxZ) {
                    continue;
                }

                TerrainProfile.TerrainSample sample = terrain.sample(x, z);
                if (!sample.insideMap() || !hasGentleSlope(terrain, x, z, sample.surfaceHeight())) {
                    continue;
                }
                Type type = chooseType(sample.biome(), random.nextDouble());
                if (type != null) {
                    placements.add(new Placement(x, z, sample.surfaceHeight(), type));
                }
            }
        }

        placements.sort(Comparator.comparingInt(Placement::z).thenComparingInt(Placement::x));
        return List.copyOf(placements);
    }

    private static Type chooseType(TerrainProfile.Biome biome, double roll) {
        return switch (biome) {
            case FOREST -> roll < 0.78 ? Type.OAK_TREE
                    : roll < 0.90 ? Type.PINE_TREE : Type.BUSH;
            case PLAINS -> roll < 0.12 ? Type.OAK_TREE
                    : roll < 0.30 ? Type.BUSH : null;
            case DESERT -> roll < 0.16 ? Type.CACTUS : null;
            case OCEAN, BEACH, ROCKY_HIGHLANDS, OUT_OF_BOUNDS -> null;
        };
    }

    private static boolean hasGentleSlope(TerrainProfile terrain, int x, int z, int height) {
        return Math.abs(terrain.sample(x - 2, z).surfaceHeight() - height) <= MAX_SLOPE
                && Math.abs(terrain.sample(x + 2, z).surfaceHeight() - height) <= MAX_SLOPE
                && Math.abs(terrain.sample(x, z - 2).surfaceHeight() - height) <= MAX_SLOPE
                && Math.abs(terrain.sample(x, z + 2).surfaceHeight() - height) <= MAX_SLOPE;
    }

    private static long cellSeed(long seed, int cellX, int cellZ) {
        long value = seed ^ ((long) cellX * 0x9E3779B97F4A7C15L);
        value = Long.rotateLeft(value, 29) ^ ((long) cellZ * 0xC2B2AE3D27D4EB4FL);
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
