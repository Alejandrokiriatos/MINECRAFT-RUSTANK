package com.rustank.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldBoundsTest {
    @Test
    void acceptsCoordinatesInsideTheFourThousandBlockSquare() {
        assertTrue(WorldBounds.contains(-2_000, -2_000));
        assertTrue(WorldBounds.contains(1_999, 1_999));
        assertTrue(WorldBounds.contains(0, 0));
    }

    @Test
    void rejectsCoordinatesOnOrBeyondExclusiveMaximumEdges() {
        assertFalse(WorldBounds.contains(2_000, 0));
        assertFalse(WorldBounds.contains(0, 2_000));
        assertFalse(WorldBounds.contains(-2_001, 0));
        assertFalse(WorldBounds.contains(0, -2_001));
    }

    @Test
    void clampsCoordinatesToTheLastValidBlock() {
        assertEquals(-2_000, WorldBounds.clampX(-9_000));
        assertEquals(1_999, WorldBounds.clampX(9_000));
        assertEquals(-2_000, WorldBounds.clampZ(-9_000));
        assertEquals(1_999, WorldBounds.clampZ(9_000));
    }
}
