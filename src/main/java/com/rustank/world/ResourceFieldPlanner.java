package com.rustank.world;

import com.rustank.resources.DepositType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Seed-stable first-pass planner for surface resource fields.
 * Coordinates are reproducible for a given seed and independent of load order.
 */
public final class ResourceFieldPlanner {
    public record PlannedDeposit(int x, int z, DepositType type) { }

    private static final int MAX_DEPOSITS_PER_TYPE = 1_000;
    private static final int MAX_ATTEMPTS = 2_000_000;

    private ResourceFieldPlanner() { }

    public static List<PlannedDeposit> plan(long seed, int countPerType, int minSpacing) {
        if (countPerType < 0 || countPerType > MAX_DEPOSITS_PER_TYPE) {
            throw new IllegalArgumentException("countPerType must be between 0 and " + MAX_DEPOSITS_PER_TYPE);
        }
        if (minSpacing < 1) throw new IllegalArgumentException("minSpacing must be positive");

        SplittableRandom random = new SplittableRandom(seed);
        List<PlannedDeposit> result = new ArrayList<>();
        long spacingSquared = (long) minSpacing * minSpacing;
        long requestedAttempts = Math.max(100L, (long) countPerType * DepositType.values().length * 100L);
        int attempts = (int) Math.min(MAX_ATTEMPTS, requestedAttempts);

        for (DepositType type : DepositType.values()) {
            int placed = 0;
            for (int attempt = 0; attempt < attempts && placed < countPerType; attempt++) {
                int x = random.nextInt(WorldBounds.MIN_X + 64, WorldBounds.MAX_X_EXCLUSIVE - 64);
                int z = random.nextInt(WorldBounds.MIN_Z + 64, WorldBounds.MAX_Z_EXCLUSIVE - 64);
                if (!WorldBounds.contains(x, z)) continue;
                boolean clear = true;
                for (PlannedDeposit existing : result) {
                    long dx = (long) x - existing.x();
                    long dz = (long) z - existing.z();
                    if (dx * dx + dz * dz < spacingSquared) {
                        clear = false;
                        break;
                    }
                }
                if (clear) {
                    result.add(new PlannedDeposit(x, z, type));
                    placed++;
                }
            }
            if (placed != countPerType) {
                throw new IllegalStateException("Unable to place requested deposits; reduce count or spacing");
            }
        }
        return Collections.unmodifiableList(result);
    }
}
