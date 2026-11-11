package com.rustank.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerrainProfileTest {
    @Test
    void sameSeedAndCoordinatesProduceIdenticalSamples() {
        var first = TerrainProfile.forSeed(123456L);
        var second = TerrainProfile.forSeed(123456L);
        for (int x = -1_500; x <= 1_500; x += 125) {
            for (int z = -1_500; z <= 1_500; z += 125) {
                assertEquals(first.sample(x, z), second.sample(x, z));
            }
        }
    }

    @Test
    void oceanAndOutOfBoundsHaveExplicitSeaLevelSamples() {
        var terrain = TerrainProfile.forSeed(42L);
        var ocean = terrain.sample(1_999, 1_999);
        assertTrue(ocean.insideMap());
        assertEquals(TerrainProfile.Biome.OCEAN, ocean.biome());
        assertEquals(TerrainProfile.SEA_LEVEL, ocean.surfaceHeight());

        var outside = terrain.sample(2_000, 0);
        assertFalse(outside.insideMap());
        assertEquals(TerrainProfile.Biome.OUT_OF_BOUNDS, outside.biome());
    }

    @Test
    void landHeightsStayWithinConfiguredVerticalRange() {
        var terrain = TerrainProfile.forSeed(77L);
        for (int x = -1_700; x <= 1_700; x += 55) {
            for (int z = -1_700; z <= 1_700; z += 55) {
                var sample = terrain.sample(x, z);
                if (sample.biome() == TerrainProfile.Biome.OCEAN
                        || sample.biome() == TerrainProfile.Biome.OUT_OF_BOUNDS) continue;
                assertTrue(sample.surfaceHeight() >= TerrainProfile.MIN_LAND_HEIGHT);
                assertTrue(sample.surfaceHeight() <= TerrainProfile.MAX_LAND_HEIGHT);
            }
        }
    }

    @Test
    void oceanFloorHasDeterministicBoundedReliefBelowSeaLevel() {
        var first = TerrainProfile.forSeed(42L);
        var second = TerrainProfile.forSeed(42L);
        int minimum = Integer.MAX_VALUE;
        int maximum = Integer.MIN_VALUE;
        for (int x = -1_900; x <= 1_900; x += 137) {
            for (int z = -1_900; z <= 1_900; z += 137) {
                if (first.sample(x, z).biome() != TerrainProfile.Biome.OCEAN) continue;
                int height = first.oceanFloorHeight(x, z);
                assertEquals(height, second.oceanFloorHeight(x, z));
                assertTrue(height >= 30 && height <= 50);
                assertTrue(height < TerrainProfile.SEA_LEVEL);
                minimum = Math.min(minimum, height);
                maximum = Math.max(maximum, height);
            }
        }
        assertTrue(maximum > minimum, "ocean floor should not be perfectly flat");
    }

    @Test
    void coastlineIsClassifiedAsBeachAndInlandIsNot() {
        var terrain = TerrainProfile.forSeed(99L);
        var island = IslandShape.forSeed(99L);
        int x = (int) Math.floor(island.coastRadiusAtAngle(0.0)) - 10;
        var shore = terrain.sample(x, 0);
        assertEquals(TerrainProfile.Biome.BEACH, shore.biome());
        assertTrue(shore.coastal());
        var center = terrain.sample(0, 0);
        assertFalse(center.coastal());
        assertNotEquals(TerrainProfile.Biome.OCEAN, center.biome());
    }

    @Test
    void samplesOnlyKnownBiomeCategories() {
        var terrain = TerrainProfile.forSeed(2026L);
        for (int x = -1_800; x < 1_800; x += 90) {
            for (int z = -1_800; z < 1_800; z += 90) {
                assertNotNull(terrain.sample(x, z).biome());
            }
        }
    }
}
