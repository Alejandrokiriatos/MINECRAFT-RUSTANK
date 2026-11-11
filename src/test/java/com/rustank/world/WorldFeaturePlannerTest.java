package com.rustank.world;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WorldFeaturePlannerTest {
    @Test
    void sameSeedProducesSameFeatureLayout() {
        assertEquals(WorldFeaturePlanner.plan(987654321L), WorldFeaturePlanner.plan(987654321L));
    }

    @Test
    void plansEveryMajorFeatureInsideTheMapWithClearance() {
        List<WorldFeaturePlanner.PlannedFeature> features = WorldFeaturePlanner.plan(42L);
        assertEquals(WorldFeaturePlanner.FeatureType.values().length, features.size());
        Set<WorldFeaturePlanner.FeatureType> types = new HashSet<>();

        for (int i = 0; i < features.size(); i++) {
            var a = features.get(i);
            assertTrue(types.add(a.type()));
            assertEquals(a.type().exclusionRadius(), a.exclusionRadius());
            assertTrue(WorldBounds.contains(a.x(), a.z()));
            assertTrue(a.x() - a.exclusionRadius() >= WorldBounds.MIN_X);
            assertTrue(a.x() + a.exclusionRadius() < WorldBounds.MAX_X_EXCLUSIVE);
            assertTrue(a.z() - a.exclusionRadius() >= WorldBounds.MIN_Z);
            assertTrue(a.z() + a.exclusionRadius() < WorldBounds.MAX_Z_EXCLUSIVE);

            for (int j = i + 1; j < features.size(); j++) {
                var b = features.get(j);
                long dx = (long) a.x() - b.x();
                long dz = (long) a.z() - b.z();
                long required = (long) a.exclusionRadius() + b.exclusionRadius() + 180;
                assertTrue(dx * dx + dz * dz >= required * required);
            }
        }
        assertTrue(types.containsAll(Set.of(WorldFeaturePlanner.FeatureType.values())));
    }

    @Test
    void spawnIsKeptNearTheCenter() {
        for (long seed = 0; seed < 100; seed++) {
            var spawn = WorldFeaturePlanner.plan(seed).getFirst();
            assertEquals(WorldFeaturePlanner.FeatureType.PLAYER_SPAWN, spawn.type());
            assertTrue(Math.abs(spawn.x()) <= 100);
            assertTrue(Math.abs(spawn.z()) <= 100);
        }
    }
}
