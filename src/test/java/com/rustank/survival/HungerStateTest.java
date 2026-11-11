package com.rustank.survival;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HungerStateTest {
    @Test
    void drainsOnceEveryNinetySecondsAtNormalPace() {
        HungerState state = new HungerState();
        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertTrue(state.tick(false, false));
        assertEquals(99, state.getHunger());
    }

    @Test
    void drainsTwiceAsFastWhileSprinting() {
        HungerState state = new HungerState();
        for (int i = 0; i < HungerState.SPRINT_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(true, false));
        }
        assertTrue(state.tick(true, false));
        assertEquals(99, state.getHunger());
    }

    @Test
    void creativeAndSpectatorDoNotAdvanceHunger() {
        HungerState state = new HungerState();
        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS * 2; i++) {
            assertFalse(state.tick(false, true));
        }
        assertEquals(100, state.getHunger());
    }

    @Test
    void eatingClampsAtOneHundredAndRejectsNonPositiveAmounts() {
        HungerState state = new HungerState();
        state.loadHunger(80);
        assertTrue(state.eat(50));
        assertEquals(100, state.getHunger());
        assertFalse(state.eat(0));
        assertFalse(state.eat(-10));
        assertFalse(state.eat(1));
    }

    @Test
    void veryLargeFoodAmountCannotOverflow() {
        HungerState state = new HungerState();
        state.loadHunger(50);
        assertTrue(state.eat(Integer.MAX_VALUE));
        assertEquals(100, state.getHunger());
    }

    @Test
    void eatingResetsTheDrainClock() {
        HungerState state = new HungerState();
        state.loadHunger(50);
        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertTrue(state.eat(10));
        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS - 1; i++) {
            assertFalse(state.tick(false, false));
        }
        assertEquals(60, state.getHunger());
        assertTrue(state.tick(false, false));
        assertEquals(59, state.getHunger());
    }

    @Test
    void conditionsAndHudFractionMatchThresholds() {
        HungerState state = new HungerState();
        assertEquals(HungerState.Condition.SATISFIED, state.getCondition());
        assertEquals(1.0f, state.getHudFraction());

        state.loadHunger(30);
        assertEquals(HungerState.Condition.HUNGRY, state.getCondition());
        assertEquals(0.30f, state.getHudFraction());

        state.loadHunger(10);
        assertEquals(HungerState.Condition.STARVING, state.getCondition());

        state.loadHunger(0);
        assertEquals(HungerState.Condition.EMPTY, state.getCondition());
        assertEquals(0.0f, state.getHudFraction());
    }

    @Test
    void loadingClampsValuesToValidRange() {
        HungerState state = new HungerState();
        state.loadHunger(140);
        assertEquals(100, state.getHunger());
        state.loadHunger(-20);
        assertEquals(0, state.getHunger());
        assertEquals(HungerState.Condition.EMPTY, state.getCondition());
    }

    @Test
    void hungerNeverDropsBelowZero() {
        HungerState state = new HungerState();
        state.loadHunger(1);
        for (int i = 0; i < HungerState.NORMAL_DRAIN_TICKS * 3; i++) {
            state.tick(false, false);
        }
        assertEquals(0, state.getHunger());
        assertEquals(HungerState.Condition.EMPTY, state.getCondition());
    }
}
