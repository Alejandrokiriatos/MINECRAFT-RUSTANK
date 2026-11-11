package com.rustank.world;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VegetationPlannerTest {
    @Test
    void planningIsDeterministicAndReturnsImmutableResults() {
        var first = VegetationPlanner.planChunk(1234L, 2, -3);
        var second = VegetationPlanner.planChunk(1234L, 2, -3);
        assertEquals(first, second);
        assertThrows(UnsupportedOperationException.class,
                () -> first.add(new VegetationPlanner.Placement(0, 0, 64,
                        VegetationPlanner.Type.BUSH)));
    }

    @Test
    void placementsBelongToRequestedChunkAndAreInsideMap() {
        long seed = 991L;
        for (int chunkZ = -125; chunkZ <= 124; chunkZ += 7) {
            for (int chunkX = -125; chunkX <= 124; chunkX += 7) {
                for (var placement : VegetationPlanner.planChunk(seed, chunkX, chunkZ)) {
                    assertEquals(chunkX, Math.floorDiv(placement.x(), 16));
                    assertEquals(chunkZ, Math.floorDiv(placement.z(), 16));
                    assertTrue(WorldBounds.contains(placement.x(), placement.z()));
                    var sample = TerrainProfile.forSeed(seed).sample(placement.x(), placement.z());
                    assertEquals(sample.surfaceHeight(), placement.groundY());
                    assertTrue(sample.biome() == TerrainProfile.Biome.FOREST
                            || sample.biome() == TerrainProfile.Biome.PLAINS
                            || sample.biome() == TerrainProfile.Biome.DESERT);
                }
            }
        }
    }

    @Test
    void neighboringChunkQueriesNeverDuplicateWorldPositions() {
        long seed = 7654321L;
        Set<String> positions = new HashSet<>();
        for (int chunkZ = -4; chunkZ <= 4; chunkZ++) {
            for (int chunkX = -4; chunkX <= 4; chunkX++) {
                for (var placement : VegetationPlanner.planChunk(seed, chunkX, chunkZ)) {
                    assertTrue(positions.add(placement.x() + ":" + placement.z()));
                }
            }
        }
    }

    @Test
    void treeAndCactusTypesRespectBiomeRules() {
        long seed = 42L;
        for (int chunkZ = -100; chunkZ <= 100; chunkZ += 5) {
            for (int chunkX = -100; chunkX <= 100; chunkX += 5) {
                for (var placement : VegetationPlanner.planChunk(seed, chunkX, chunkZ)) {
                    var biome = TerrainProfile.forSeed(seed)
                            .sample(placement.x(), placement.z()).biome();
                    if (placement.type() == VegetationPlanner.Type.CACTUS) {
                        assertEquals(TerrainProfile.Biome.DESERT, biome);
                    }
                    if (placement.type() == VegetationPlanner.Type.OAK_TREE
                            || placement.type() == VegetationPlanner.Type.PINE_TREE) {
                        assertTrue(biome == TerrainProfile.Biome.FOREST
                                || biome == TerrainProfile.Biome.PLAINS);
                    }
                }
            }
        }
    }

    @Test
    void rejectsChunkCoordinateOverflow() {
        assertThrows(ArithmeticException.class,
                () -> VegetationPlanner.planChunk(1L, Integer.MAX_VALUE, 0));
    }
}
