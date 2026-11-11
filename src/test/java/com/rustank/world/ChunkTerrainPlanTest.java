package com.rustank.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChunkTerrainPlanTest {
    @Test
    void createsExactly256ColumnsWithCorrectBlockCoordinates() {
        var plan = ChunkTerrainPlan.create(42L, 3, -2);
        assertEquals(ChunkTerrainPlan.COLUMN_COUNT, plan.columns().length);
        assertEquals(3, plan.chunkX());
        assertEquals(-2, plan.chunkZ());
        assertEquals(48, plan.column(0, 0).blockX());
        assertEquals(-32, plan.column(0, 0).blockZ());
        assertEquals(63, plan.column(15, 15).blockX());
        assertEquals(-17, plan.column(15, 15).blockZ());
    }

    @Test
    void sameSeedAndChunkProduceIdenticalBlueprints() {
        var first = ChunkTerrainPlan.create(123L, -4, 7);
        var second = ChunkTerrainPlan.create(123L, -4, 7);
        assertArrayEquals(first.columns(), second.columns());
    }

    @Test
    void oceanColumnsHaveWaterAndASeabed() {
        var plan = ChunkTerrainPlan.create(42L, 124, 124);
        var column = plan.column(15, 15);
        assertEquals(TerrainProfile.Biome.OCEAN, column.biome());
        assertTrue(column.groundY() >= 30 && column.groundY() <= 50);
        assertEquals(63, column.waterTopY());
        assertEquals(ChunkTerrainPlan.Material.WATER, column.surfaceMaterial());
        assertEquals(ChunkTerrainPlan.Material.SAND, column.groundMaterial());
        assertEquals(ChunkTerrainPlan.Material.WATER, column.materialAtY(63));
        assertEquals(ChunkTerrainPlan.Material.WATER, column.materialAtY(column.groundY() + 1));
        assertEquals(ChunkTerrainPlan.Material.SAND, column.materialAtY(column.groundY()));
        assertEquals(ChunkTerrainPlan.Material.SANDSTONE, column.materialAtY(column.groundY() - 1));
        assertEquals(ChunkTerrainPlan.Material.STONE, column.materialAtY(column.groundY() - 4));
        assertTrue(column.hasWater());
    }

    @Test
    void landColumnsHaveBiomeAppropriateSurfaceMaterials() {
        var plan = ChunkTerrainPlan.create(42L, 0, 0);
        var center = plan.column(0, 0);
        assertTrue(center.isInsideMap());
        assertNotEquals(TerrainProfile.Biome.OCEAN, center.biome());
        assertTrue(center.surfaceMaterial() == ChunkTerrainPlan.Material.GRASS
                || center.surfaceMaterial() == ChunkTerrainPlan.Material.SAND
                || center.surfaceMaterial() == ChunkTerrainPlan.Material.STONE);
        assertEquals(center.surfaceY() - 1, center.groundY());
        assertEquals(center.surfaceMaterial(), center.materialAtY(center.surfaceY()));
        assertEquals(center.groundMaterial(), center.materialAtY(center.surfaceY() - 1));
        assertEquals(ChunkTerrainPlan.Material.STONE,
                center.materialAtY(center.surfaceY() - center.subsurfaceDepth() - 1));
        assertEquals(ChunkTerrainPlan.Material.NONE, center.materialAtY(center.surfaceY() + 1));
    }

    @Test
    void clipsPartialChunksAtTheConfiguredWorldBounds() {
        var plan = ChunkTerrainPlan.create(7L, 124, 0);
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                var column = plan.column(localX, localZ);
                if (column.blockX() >= WorldBounds.MAX_X_EXCLUSIVE) {
                    assertEquals(TerrainProfile.Biome.OUT_OF_BOUNDS, column.biome());
                    assertEquals(ChunkTerrainPlan.Material.NONE, column.surfaceMaterial());
                } else {
                    assertTrue(WorldBounds.contains(column.blockX(), column.blockZ()));
                }
            }
        }
    }

    @Test
    void soilAndSandstoneDepthsStayWithinConfiguredRange() {
        var plan = ChunkTerrainPlan.create(987654321L, 2, -3);
        for (var column : plan.columns()) {
            if (column.biome() == TerrainProfile.Biome.PLAINS
                    || column.biome() == TerrainProfile.Biome.FOREST
                    || column.biome() == TerrainProfile.Biome.BEACH
                    || column.biome() == TerrainProfile.Biome.DESERT) {
                assertTrue(column.subsurfaceDepth() >= 3);
                assertTrue(column.subsurfaceDepth() <= 5);
                assertEquals(column.groundMaterial(),
                        column.materialAtY(column.surfaceY() - column.subsurfaceDepth()));
            }
        }
    }

    @Test
    void rejectsChunkCoordinateOverflowAndInvalidLocalCoordinates() {
        assertThrows(ArithmeticException.class,
                () -> ChunkTerrainPlan.create(1L, Integer.MAX_VALUE, 0));
        var plan = ChunkTerrainPlan.create(1L, 0, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> plan.column(16, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> plan.column(0, -1));
    }

    @Test
    void returnedColumnArrayCannotMutateThePlan() {
        var plan = ChunkTerrainPlan.create(1L, 0, 0);
        var original = plan.column(0, 0);
        var copy = plan.columns();
        copy[0] = null;
        assertEquals(original, plan.column(0, 0));
    }
}
