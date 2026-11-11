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
    void rejectsInvalidPlannerParameters() {
        assertThrows(IllegalArgumentException.class, () -> ResourceFieldPlanner.plan(1, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> ResourceFieldPlanner.plan(1, 1, 0));
    }
}
