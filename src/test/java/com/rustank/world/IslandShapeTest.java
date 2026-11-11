package com.rustank.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandShapeTest {
    @Test
    void sameSeedProducesSameCoastline() {
        var first = IslandShape.forSeed(1234L);
        var second = IslandShape.forSeed(1234L);
        for (int angle = 0; angle < 360; angle++) {
            double radians = Math.toRadians(angle);
            assertEquals(first.coastRadiusAtAngle(radians), second.coastRadiusAtAngle(radians));
        }
    }

    @Test
    void centerIsLandAndMapCornersAreOcean() {
        var island = IslandShape.forSeed(42L);
        assertTrue(island.isLand(0, 0));
        assertTrue(island.isOcean(1_999, 1_999));
        assertFalse(island.isLand(2_000, 0));
        assertFalse(island.isOcean(2_000, 0));
    }

    @Test
    void coastlineRadiusStaysInsideTheConfiguredMap() {
        var island = IslandShape.forSeed(99L);
        for (int angle = 0; angle < 360; angle++) {
            double radius = island.coastRadiusAtAngle(Math.toRadians(angle));
            assertTrue(radius > 1_500 && radius < 2_000);
        }
    }

    @Test
    void rejectsInvalidCoastlineInputs() {
        var island = IslandShape.forSeed(1L);
        assertThrows(IllegalArgumentException.class, () -> island.coastRadiusAtAngle(Double.NaN));
        assertFalse(island.isNearCoast(0, 0, -1));
        assertFalse(island.isNearCoast(0, 0, Double.NaN));
    }
}
