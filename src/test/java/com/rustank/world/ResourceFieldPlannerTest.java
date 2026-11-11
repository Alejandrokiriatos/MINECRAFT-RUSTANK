package com.rustank.world;

import com.rustank.resources.DepositType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceFieldPlannerTest {
    @Test
    void sameSeedProducesSameResourceFields() {
        assertEquals(ResourceFieldPlanner.plan(123456L, 12, 100),
                ResourceFieldPlanner.plan(123456L, 12, 100));
    }

    @Test
    void placesRequestedCountInsideMapWithMinimumSpacing() {
        int spacing = 150;
        List<ResourceFieldPlanner.PlannedDeposit> deposits =
                ResourceFieldPlanner.plan(99L, 10, spacing);
        assertEquals(30, deposits.size());
        for (int i = 0; i < deposits.size(); i++) {
            var a = deposits.get(i);
            assertTrue(WorldBounds.contains(a.x(), a.z()));
            for (int j = i + 1; j < deposits.size(); j++) {
                var b = deposits.get(j);
                long dx = (long) a.x() - b.x();
                long dz = (long) a.z() - b.z();
                assertTrue(dx * dx + dz * dz >= (long) spacing * spacing);
            }
        }
        for (DepositType type : DepositType.values()) {
            assertEquals(10, deposits.stream().filter(d -> d.type() == type).count());
        }
    }

    @Test
    void keepsDepositsOutsideMonumentAndSpawnExclusionAreas() {
        long seed = 20261111L;
        var features = WorldFeaturePlanner.plan(seed);
        var deposits = ResourceFieldPlanner.plan(seed, 20, 100, features);

        assertEquals(60, deposits.size());
        for (var deposit : deposits) {
            for (var feature : features) {
                long dx = (long) deposit.x() - feature.x();
                long dz = (long) deposit.z() - feature.z();
                long required = (long) feature.exclusionRadius() + 48;
                assertTrue(dx * dx + dz * dz >= required * required,
                        () -> deposit + " overlaps exclusion area of " + feature.type());
            }
        }
    }

    @Test
    void rejectsInvalidPlannerParametersAndFeatureLists() {
        assertThrows(IllegalArgumentException.class, () -> ResourceFieldPlanner.plan(1, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> ResourceFieldPlanner.plan(1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> ResourceFieldPlanner.plan(1, 1_001, 10));
        assertThrows(NullPointerException.class, () -> ResourceFieldPlanner.plan(1, 1, 10, null));
        assertThrows(IllegalArgumentException.class, () -> ResourceFieldPlanner.plan(1, 1, 10,
                List.of(new WorldFeaturePlanner.PlannedFeature(
                        WorldFeaturePlanner.FeatureType.HARBOR, 2000, 0, 170))));
    }
}
