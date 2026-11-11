package com.rustank.resources;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDepositRegistryTest {
    @Test
    void createsDeterministicInitialDepositSet() {
        var first = ResourceDepositRegistry.createInitial(77L, 8, 120);
        var second = ResourceDepositRegistry.createInitial(77L, 8, 120);
        assertEquals(24, first.size());
        assertEquals(first.snapshot(), second.snapshot());
    }

    @Test
    void harvestsOnlyRegisteredCoordinatesAndPersistsDepletionTimer() {
        var registry = ResourceDepositRegistry.createInitial(88L, 1, 100);
        var entry = registry.snapshot().getFirst();
        assertEquals(0, registry.harvestAt(entry.x() + 10, entry.z(), 20, 100));
        assertEquals(100, registry.harvestAt(entry.x(), entry.z(), 100, 100));
        assertEquals(0, registry.remainingAt(entry.x(), entry.z()));

        var restored = ResourceDepositRegistry.restore(registry.snapshot());
        assertEquals(0, restored.tick(100 + DepositType.valueOf(entry.deposit().type()).respawnTicks() - 1));
        assertEquals(1, restored.tick(100 + DepositType.valueOf(entry.deposit().type()).respawnTicks()));
        assertEquals(DepositType.valueOf(entry.deposit().type()).capacity(),
                restored.remainingAt(entry.x(), entry.z()));
    }

    @Test
    void rejectsDuplicateOrNullSnapshotEntries() {
        var duplicate = new ResourceDepositRegistry.EntrySnapshot(
                12, -4, new ResourceDeposit(DepositType.STONE).snapshot());
        assertThrows(IllegalArgumentException.class,
                () -> ResourceDepositRegistry.restore(List.of(duplicate, duplicate)));
        assertThrows(IllegalArgumentException.class,
                () -> ResourceDepositRegistry.restore(java.util.Arrays.asList((ResourceDepositRegistry.EntrySnapshot) null)));
        assertThrows(IllegalArgumentException.class, () -> ResourceDepositRegistry.restore(null));
    }

    @Test
    void snapshotOrderIsStableByCoordinates() {
        var registry = ResourceDepositRegistry.createInitial(9L, 10, 90);
        var snapshot = registry.snapshot();
        for (int i = 1; i < snapshot.size(); i++) {
            var previous = snapshot.get(i - 1);
            var current = snapshot.get(i);
            assertTrue(previous.x() < current.x()
                    || (previous.x() == current.x() && previous.z() <= current.z()));
        }
    }
}
