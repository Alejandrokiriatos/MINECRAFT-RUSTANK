package com.rustank.survival;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HydrationStateTest {
    @Test
    void drainsOncePerMinuteAtNormalPace() {
        HydrationState state = new HydrationState();
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertTrue(state.tick(false, false));
        assertEquals(99, state.getHydration());
    }

    @Test
    void drainsTwiceAsFastWhileSprinting() {
        HydrationState state = new HydrationState();
        for (int i = 0; i < HydrationState.SPRINT_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(true, false));
        }
        assertTrue(state.tick(true, false));
        assertEquals(99, state.getHydration());
    }

    @Test
    void creativeAndSpectatorDoNotAdvanceHydration() {
        HydrationState state = new HydrationState();
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS * 2; i++) {
            assertFalse(state.tick(false, true));
        }
        assertEquals(100, state.getHydration());
    }

    @Test
    void drinkingClampsAtOneHundredAndRejectsNonPositiveAmounts() {
        HydrationState state = new HydrationState();
        state.loadHydration(90);
        assertTrue(state.drink(50));
        assertEquals(100, state.getHydration());
        assertFalse(state.drink(0));
        assertFalse(state.drink(-5));
    }

    @Test
    void veryLargeDrinkAmountCannotOverflow() {
        HydrationState state = new HydrationState();
        state.loadHydration(50);
        assertTrue(state.drink(Integer.MAX_VALUE));
        assertEquals(100, state.getHydration());
    }

    @Test
    void drinkingResetsTheDrainClock() {
        HydrationState state = new HydrationState();
        state.loadHydration(50);
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertTrue(state.drink(10));
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertEquals(60, state.getHydration());
        assertTrue(state.tick(false, false));
        assertEquals(59, state.getHydration());
    }

    @Test
    void loadingClampsValuesToValidRange() {
        HydrationState state = new HydrationState();
        state.loadHydration(150);
        assertEquals(100, state.getHydration());
        state.loadHydration(-20);
        assertEquals(0, state.getHydration());
        assertTrue(state.isDehydrated());
    }

    @Test
    void conditionChangesAtDocumentedHydrationThresholds() {
        HydrationState state = new HydrationState();
        assertEquals(HydrationState.Condition.HYDRATED, state.getCondition());
        assertEquals(1.0f, state.getHudFraction());

        state.loadHydration(25);
        assertEquals(HydrationState.Condition.THIRSTY, state.getCondition());
        assertEquals(0.25f, state.getHudFraction());

        state.loadHydration(10);
        assertEquals(HydrationState.Condition.SEVERELY_THIRSTY, state.getCondition());

        state.loadHydration(0);
        assertEquals(HydrationState.Condition.DEHYDRATED, state.getCondition());
        assertEquals(0.0f, state.getHudFraction());
    }

    @Test
    void snapshotRestoresHydrationAndPartialDrainProgress() {
        HydrationState original = new HydrationState();
        original.loadHydration(70);
        for (int i = 0; i < 400; i++) {
            assertFalse(original.tick(false, false));
        }

        HydrationState restored = new HydrationState();
        restored.restore(original.snapshot());
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS - 401; i++) {
            assertFalse(restored.tick(false, false));
        }
        assertTrue(restored.tick(false, false));
        assertEquals(69, restored.getHydration());
    }

    @Test
    void restoreClampsMalformedSnapshotFields() {
        HydrationState state = new HydrationState();
        state.restore(new HydrationState.Snapshot(150, Integer.MAX_VALUE));
        assertEquals(100, state.getHydration());
        assertEquals(1.0f, state.getHudFraction());
        assertTrue(state.tick(false, false));
        assertEquals(99, state.getHydration());
    }

    @Test
    void hydrationNeverDropsBelowZero() {
        HydrationState state = new HydrationState();
        state.loadHydration(1);
        for (int i = 0; i < HydrationState.NORMAL_DRAIN_TICKS * 3; i++) {
            state.tick(false, false);
        }
        assertEquals(0, state.getHydration());
        assertTrue(state.isDehydrated());
    }
}
