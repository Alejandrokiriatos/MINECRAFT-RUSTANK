package com.rustank.survival;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SurvivalNeedsStateTest {
    @Test
    void advancesBothClocksAndReportsOnlyActualValueChanges() {
        SurvivalNeedsState state = new SurvivalNeedsState();
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }

        assertTrue(state.tick(false, false));
        assertEquals(99, state.getHydration());
        assertEquals(100, state.getHunger());

        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS - HydrationState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertTrue(state.tick(false, false));
        assertEquals(99, state.getHydration());
        assertEquals(99, state.getHunger());

        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS / 2 - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertTrue(state.tick(false, false));
        assertEquals(98, state.getHydration());
        assertEquals(99, state.getHunger());
    }

    @Test
    void creativeAndSpectatorFreezeBothNeeds() {
        SurvivalNeedsState state = new SurvivalNeedsState();
        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS * 2; i++) {
            assertFalse(state.tick(false, true));
        }
        assertEquals(100, state.getHunger());
        assertEquals(100, state.getHydration());
    }

    @Test
    void eatingAndDrinkingUpdateOnlyTheirOwnNeed() {
        SurvivalNeedsState state = new SurvivalNeedsState();
        state.restore(new SurvivalNeedsState.Snapshot(
                new HungerState.Snapshot(40, 0),
                new HydrationState.Snapshot(30, 0)));

        assertTrue(state.eat(25));
        assertTrue(state.drink(50));
        assertEquals(65, state.getHunger());
        assertEquals(80, state.getHydration());
        assertEquals(0.65f, state.getHungerHudFraction());
        assertEquals(0.80f, state.getHydrationHudFraction());
    }

    @Test
    void combinedSnapshotPreservesBothDrainTimers() {
        SurvivalNeedsState original = new SurvivalNeedsState();
        for (int i = 0; i < 350; i++) {
            assertFalse(original.tick(false, false));
        }

        SurvivalNeedsState restored = new SurvivalNeedsState();
        restored.restore(original.snapshot());

        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS - 351; i++) {
            assertFalse(restored.tick(false, false));
        }
        assertTrue(restored.tick(false, false));
        assertEquals(99, restored.getHydration());
        assertEquals(100, restored.getHunger());
    }

    @Test
    void rejectsNullCombinedSnapshots() {
        SurvivalNeedsState state = new SurvivalNeedsState();
        assertThrows(IllegalArgumentException.class, () -> state.restore(null));
        assertThrows(IllegalArgumentException.class,
                () -> new SurvivalNeedsState.Snapshot(null, new HydrationState.Snapshot(100, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> new SurvivalNeedsState.Snapshot(new HungerState.Snapshot(100, 0), null));
    }
}
