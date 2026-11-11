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
