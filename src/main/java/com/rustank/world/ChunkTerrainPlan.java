package com.rustank.world;

import java.util.Arrays;

/**
 * Immutable 16x16 chunk blueprint sampled from TerrainProfile.
 *
 * This is an intermediate data model for the future Forge chunk generator:
 * it does not place blocks or modify a Minecraft LevelChunk.
 */
public final class ChunkTerrainPlan {
    public static final int CHUNK_SIZE = 16;
    public static final int COLUMN_COUNT = CHUNK_SIZE * CHUNK_SIZE;

    public enum Material {
        GRASS,
        DIRT,
        SAND,
        SANDSTONE,
        STONE,
        WATER,
        NONE
    }

    public record Column(
            int blockX,
            int blockZ,
            TerrainProfile.Biome biome,
            int groundY,
            int surfaceY,
            Material groundMaterial,
            Material surfaceMaterial,
            int waterTopY,
            int subsurfaceDepth
    ) {
        public boolean isInsideMap() {
            return biome != TerrainProfile.Biome.OUT_OF_BOUNDS;
        }

        public boolean hasWater() {
            return waterTopY >= TerrainProfile.SEA_LEVEL;
        }

        /**
         * Returns the logical block material at a world Y coordinate.
         * This is a column blueprint only; a Forge chunk adapter must place it.
         */
        public Material materialAtY(int y) {
            if (!isInsideMap() || y > surfaceY) {
                return Material.NONE;
            }
            if (biome == TerrainProfile.Biome.OCEAN) {
                if (y > 48) {
                    return y <= waterTopY ? Material.WATER : Material.NONE;
                }
                if (y == 48) {
                    return Material.SAND;
                }
                if (y >= 45) {
                    return Material.SANDSTONE;
                }
                return Material.STONE;
            }
            if (y == surfaceY) {
                return surfaceMaterial;
            }
            if (y >= surfaceY - subsurfaceDepth) {
                return groundMaterial;
            }
            return Material.STONE;
        }
    }

    private final int chunkX;
    private final int chunkZ;
    private final Column[] columns;

    private ChunkTerrainPlan(int chunkX, int chunkZ, Column[] columns) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.columns = columns;
    }

    public static ChunkTerrainPlan create(long seed, int chunkX, int chunkZ) {
        int originX = Math.multiplyExact(chunkX, CHUNK_SIZE);
        int originZ = Math.multiplyExact(chunkZ, CHUNK_SIZE);
        TerrainProfile terrain = TerrainProfile.forSeed(seed);
        Column[] columns = new Column[COLUMN_COUNT];

        for (int localZ = 0; localZ < CHUNK_SIZE; localZ++) {
            for (int localX = 0; localX < CHUNK_SIZE; localX++) {
                int x = Math.addExact(originX, localX);
                int z = Math.addExact(originZ, localZ);
                columns[index(localX, localZ)] = createColumn(terrain, x, z);
            }
        }
        return new ChunkTerrainPlan(chunkX, chunkZ, columns);
    }

    private static Column createColumn(TerrainProfile terrain, int x, int z) {
        TerrainProfile.TerrainSample sample = terrain.sample(x, z);
        if (!sample.insideMap()) {
            return new Column(x, z, sample.biome(), 0, 0,
                    Material.NONE, Material.NONE, -1, 0);
        }

        if (sample.biome() == TerrainProfile.Biome.OCEAN) {
            // Initial seabed datum; later terrain passes can add underwater relief.
            int seabed = 48;
            return new Column(x, z, sample.biome(), seabed, TerrainProfile.SEA_LEVEL,
                    Material.SAND, Material.WATER, TerrainProfile.SEA_LEVEL, 3);
        }

        Material surface = switch (sample.biome()) {
            case BEACH, DESERT -> Material.SAND;
            case ROCKY_HIGHLANDS -> Material.STONE;
            case PLAINS, FOREST -> Material.GRASS;
            case OCEAN, OUT_OF_BOUNDS -> Material.NONE;
        };
        Material ground = switch (surface) {
            case GRASS -> Material.DIRT;
            case SAND -> Material.SANDSTONE;
            case STONE -> Material.STONE;
            default -> Material.NONE;
        };
        int subsurfaceDepth = surface == Material.STONE ? 0
                : 3 + Math.floorMod(x * 31 + z * 17, 3);
        return new Column(x, z, sample.biome(), sample.surfaceHeight() - 1,
                sample.surfaceHeight(), ground, surface, -1, subsurfaceDepth);
    }

    private static int index(int localX, int localZ) {
        if (localX < 0 || localX >= CHUNK_SIZE || localZ < 0 || localZ >= CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Local chunk coordinates must be between 0 and 15");
        }
        return localZ * CHUNK_SIZE + localX;
    }

    public int chunkX() {
        return chunkX;
    }

    public int chunkZ() {
        return chunkZ;
    }

    public Column column(int localX, int localZ) {
        return columns[index(localX, localZ)];
    }

    public Column[] columns() {
        return Arrays.copyOf(columns, columns.length);
    }
}
