package com.rustank.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Deterministic first-pass layout for the island's major points of interest.
 * It plans X/Z locations only; terrain, roads, structures and loot are generated later.
 */
public final class WorldFeaturePlanner {
    public enum FeatureType {
        PLAYER_SPAWN(260),
        AIRFIELD(190),
        HARBOR(170),
        MILITARY_BASE(230),
        RADIATION_ZONE(210);

        private final int exclusionRadius;

        FeatureType(int exclusionRadius) {
            this.exclusionRadius = exclusionRadius;
        }

        public int exclusionRadius() {
            return exclusionRadius;
        }
    }

    public record PlannedFeature(FeatureType type, int x, int z, int exclusionRadius) { }

    private static final int EDGE_MARGIN = 64;
    private static final int EXTRA_CLEARANCE = 180;
    private static final int MAX_ATTEMPTS_PER_FEATURE = 2_000;

    private WorldFeaturePlanner() { }

    public static List<PlannedFeature> plan(long seed) {
        SplittableRandom random = new SplittableRandom(seed);
        IslandShape island = IslandShape.forSeed(seed);
        List<PlannedFeature> features = new ArrayList<>();

        // Keep the initial spawn near the island's center, but vary it by world seed.
        int spawnX = random.nextInt(-100, 101);
        int spawnZ = random.nextInt(-100, 101);
        FeatureType spawn = FeatureType.PLAYER_SPAWN;
        features.add(new PlannedFeature(spawn, spawnX, spawnZ, spawn.exclusionRadius()));

        for (FeatureType type : FeatureType.values()) {
            if (type == FeatureType.PLAYER_SPAWN) continue;
            boolean placed = false;
            int min = WorldBounds.MIN_X + EDGE_MARGIN + type.exclusionRadius();
            int max = WorldBounds.MAX_X_EXCLUSIVE - EDGE_MARGIN - type.exclusionRadius();
            int minZ = WorldBounds.MIN_Z + EDGE_MARGIN + type.exclusionRadius();
            int maxZ = WorldBounds.MAX_Z_EXCLUSIVE - EDGE_MARGIN - type.exclusionRadius();

            for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_FEATURE; attempt++) {
                int x = random.nextInt(min, max);
                int z = random.nextInt(minZ, maxZ);
                if (!isValidIslandLocation(island, type, x, z)
                        || !hasClearance(features, type, x, z)) continue;
                features.add(new PlannedFeature(type, x, z, type.exclusionRadius()));
                placed = true;
                break;
            }
            if (!placed) {
                throw new IllegalStateException("Unable to place world feature " + type
                        + "; reduce exclusion radii or spacing");
            }
        }
        return Collections.unmodifiableList(features);
    }

    private static boolean isValidIslandLocation(IslandShape island, FeatureType type, int x, int z) {
        if (!island.isLand(x, z)) return false;
        return type != FeatureType.HARBOR
                || island.isNearCoast(x, z, IslandShape.nearCoastDistance());
    }

    private static boolean hasClearance(List<PlannedFeature> existing, FeatureType type, int x, int z) {
        for (PlannedFeature feature : existing) {
            long dx = (long) x - feature.x();
            long dz = (long) z - feature.z();
            long required = (long) type.exclusionRadius() + feature.exclusionRadius() + EXTRA_CLEARANCE;
            if (dx * dx + dz * dz < required * required) return false;
        }
        return true;
    }
}
