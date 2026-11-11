package com.rustank.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDepositTest {
    @Test
    void harvestIsFiniteAndCannotOverdraw() {
        ResourceDeposit deposit = new ResourceDeposit(DepositType.STONE);
        assertEquals(300, deposit.harvest(300, 10));
        assertEquals(600, deposit.remaining());
        assertEquals(600, deposit.harvest(900, 11));
        assertEquals(0, deposit.harvest(1, 12));
        assertTrue(deposit.depleted());
    }

    @Test
    void depletedDepositRespawnsOnlyAfterItsConfiguredDelay() {
        ResourceDeposit deposit = new ResourceDeposit(DepositType.METAL);
        long now = 1_000;
        assertEquals(DepositType.METAL.capacity(), deposit.harvest(Integer.MAX_VALUE, now));
        long due = now + DepositType.METAL.respawnTicks();
        assertFalse(deposit.tick(due - 1));
        assertTrue(deposit.tick(due));
        assertEquals(DepositType.METAL.capacity(), deposit.remaining());
        assertFalse(deposit.tick(due + 1));
    }

    @Test
    void invalidHarvestDoesNotChangeStock() {
        ResourceDeposit deposit = new ResourceDeposit(DepositType.SULFUR);
        assertEquals(0, deposit.harvest(0, 0));
        assertEquals(0, deposit.harvest(-5, 0));
        assertEquals(DepositType.SULFUR.capacity(), deposit.remaining());
    }

    @Test
    void snapshotPreservesPartialStockAndDepletionTimer() {
        ResourceDeposit active = new ResourceDeposit(DepositType.STONE);
        active.harvest(125, 50);
        ResourceDeposit restoredActive = ResourceDeposit.restore(active.snapshot());
        assertEquals(DepositType.STONE, restoredActive.type());
        assertEquals(775, restoredActive.remaining());
        assertEquals(-1L, restoredActive.respawnAtTick());

        ResourceDeposit depleted = new ResourceDeposit(DepositType.SULFUR);
        depleted.harvest(Integer.MAX_VALUE, 500);
        ResourceDeposit restoredDepleted = ResourceDeposit.restore(depleted.snapshot());
        assertTrue(restoredDepleted.depleted());
        assertEquals(depleted.respawnAtTick(), restoredDepleted.respawnAtTick());
        assertFalse(restoredDepleted.tick(depleted.respawnAtTick() - 1));
        assertTrue(restoredDepleted.tick(depleted.respawnAtTick()));
    }

    @Test
    void rejectsCorruptSnapshots() {
        assertThrows(IllegalArgumentException.class,
                () -> ResourceDeposit.restore(new ResourceDeposit.Snapshot("STONE", -1, -1)));
        assertThrows(IllegalArgumentException.class,
                () -> ResourceDeposit.restore(new ResourceDeposit.Snapshot("STONE", 0, -1)));
        assertThrows(IllegalArgumentException.class,
                () -> ResourceDeposit.restore(new ResourceDeposit.Snapshot("STONE", 10, 99)));
        assertThrows(IllegalArgumentException.class,
                () -> ResourceDeposit.restore(new ResourceDeposit.Snapshot("UNKNOWN", 1, -1)));
    }
}
