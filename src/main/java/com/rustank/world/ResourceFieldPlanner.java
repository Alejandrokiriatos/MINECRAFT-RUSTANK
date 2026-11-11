package com.rustank.world;

import com.rustank.resources.DepositType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;

/** Seed-stable first-pass planner for surface resource fields. */
public final class ResourceFieldPlanner {
    public record PlannedDeposit(int x, int z, DepositType type) { }

    private static final int MAX_DEPOSITS_PER_TYPE = 1_000;
    private static final int MAX_ATTEMPTS = 2_000_000;
    private static final int FEATURE_CLEARANCE = 48;

    private ResourceFieldPlanner() { }

    public static List<PlannedDeposit> plan(long seed, int countPerType, int minSpacing) {
        return plan(seed, countPerType, minSpacing, List.of());
    }

    /**
     * Plans deposits on island land and outside major point-of-interest exclusion areas.
     * A fixed clearance keeps nodes away from the immediate monument perimeter.
     */
    public static List<PlannedDeposit> plan(long seed, int countPerType, int minSpacing,
                                            List<WorldFeaturePlanner.PlannedFeature> features) {
        if (countPerType < 0 || countPerType > MAX_DEPOSITS_PER_TYPE) {
            throw new IllegalArgumentException("countPerType must be between 0 and " + MAX_DEPOSITS_PER_TYPE);
        }
        if (minSpacing < 1) throw new IllegalArgumentException("minSpacing must be positive");
        Objects.requireNonNull(features, "features cannot be null");
        for (WorldFeaturePlanner.PlannedFeature feature : features) {
            Objects.requireNonNull(feature, "feature cannot be null");
            if (feature.exclusionRadius() < 0 || feature.exclusionRadius() > 1_000
                    || !WorldBounds.contains(feature.x(), feature.z())) {
                throw new IllegalArgumentException("Feature has invalid coordinates or exclusion radius");
            }
        }

        SplittableRandom random = new SplittableRandom(seed);
        IslandShape island = IslandShape.forSeed(seed);
        List<PlannedDeposit> result = new ArrayList<>();
        long spacingSquared = (long) minSpacing * minSpacing;
        long requestedAttempts = Math.max(100L, (long) countPerType * DepositType.values().length * 100L);
        int attempts = (int) Math.min(MAX_ATTEMPTS, requestedAttempts);

        for (DepositType type : DepositType.values()) {
            int placed = 0;
            for (int attempt = 0; attempt < attempts && placed < countPerType; attempt++) {
                int x = random.nextInt(WorldBounds.MIN_X + 64, WorldBounds.MAX_X_EXCLUSIVE - 64);
                int z = random.nextInt(WorldBounds.MIN_Z + 64, WorldBounds.MAX_Z_EXCLUSIVE - 64);
                if (!island.isLand(x, z) || conflictsWithFeature(features, x, z)
                        || conflictsWithDeposit(result, x, z, spacingSquared)) continue;
                result.add(new PlannedDeposit(x, z, type));
                placed++;
            }
            if (placed != countPerType) {
                throw new IllegalStateException("Unable to place requested deposits; reduce count or spacing");
            }
        }
        return Collections.unmodifiableList(result);
    }

    private static boolean conflictsWithFeature(List<WorldFeaturePlanner.PlannedFeature> features, int x, int z) {
        for (WorldFeaturePlanner.PlannedFeature feature : features) {
            long dx = (long) x - feature.x();
            long dz = (long) z - feature.z();
            long required = (long) feature.exclusionRadius() + FEATURE_CLEARANCE;
            if (dx * dx + dz * dz < required * required) return true;
        }
        return false;
    }

    private static boolean conflictsWithDeposit(List<PlannedDeposit> deposits, int x, int z,
                                                long spacingSquared) {
        for (PlannedDeposit existing : deposits) {
            long dx = (long) x - existing.x();
            long dz = (long) z - existing.z();
            if (dx * dx + dz * dz < spacingSquared) return true;
        }
        return false;
    }
}
