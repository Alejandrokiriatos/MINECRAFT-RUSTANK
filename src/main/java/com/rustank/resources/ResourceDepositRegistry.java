package com.rustank.resources;

import com.rustank.world.ResourceFieldPlanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-authoritative domain registry for finite surface deposits.
 *
 * This class deliberately has no Minecraft dependency. A Forge SavedData adapter
 * will own one registry per world and persist its snapshots.
 */
public final class ResourceDepositRegistry {
    public record DepositKey(int x, int z) { }
    public record EntrySnapshot(int x, int z, ResourceDeposit.Snapshot deposit) { }

    private final Map<DepositKey, ResourceDeposit> deposits = new HashMap<>();

    public ResourceDepositRegistry() { }

    private ResourceDepositRegistry(List<EntrySnapshot> snapshots) {
        for (EntrySnapshot entry : snapshots) {
            if (entry == null || entry.deposit() == null) {
                throw new IllegalArgumentException("Deposit snapshot entry cannot be null");
            }
            DepositKey key = new DepositKey(entry.x(), entry.z());
            if (deposits.putIfAbsent(key, ResourceDeposit.restore(entry.deposit())) != null) {
                throw new IllegalArgumentException("Duplicate deposit coordinates: " + key);
            }
        }
    }

    public static ResourceDepositRegistry restore(List<EntrySnapshot> snapshots) {
        if (snapshots == null) throw new IllegalArgumentException("snapshots cannot be null");
        return new ResourceDepositRegistry(snapshots);
    }

    /** Creates the initial field once, using stable positions derived from the world seed. */
    public static ResourceDepositRegistry createInitial(long seed, int countPerType, int minSpacing) {
        ResourceDepositRegistry registry = new ResourceDepositRegistry();
        for (ResourceFieldPlanner.PlannedDeposit planned
                : ResourceFieldPlanner.plan(seed, countPerType, minSpacing)) {
            DepositKey key = new DepositKey(planned.x(), planned.z());
            if (registry.deposits.putIfAbsent(key, new ResourceDeposit(planned.type())) != null) {
                throw new IllegalStateException("Planner produced duplicate deposit coordinates");
            }
        }
        return registry;
    }

    public int size() {
        return deposits.size();
    }

    public int remainingAt(int x, int z) {
        ResourceDeposit deposit = deposits.get(new DepositKey(x, z));
        return deposit == null ? 0 : deposit.remaining();
    }

    /** Returns zero for coordinates that do not contain a Rustank deposit. */
    public int harvestAt(int x, int z, int requested, long currentTick) {
        ResourceDeposit deposit = deposits.get(new DepositKey(x, z));
        return deposit == null ? 0 : deposit.harvest(requested, currentTick);
    }

    /** Advances all respawn timers; returns the number of deposits restored this tick. */
    public int tick(long currentTick) {
        int restored = 0;
        for (ResourceDeposit deposit : deposits.values()) {
            if (deposit.tick(currentTick)) restored++;
        }
        return restored;
    }

    /** Stable ordering makes serialized snapshots and tests deterministic. */
    public List<EntrySnapshot> snapshot() {
        List<EntrySnapshot> result = new ArrayList<>(deposits.size());
        deposits.forEach((key, deposit) ->
                result.add(new EntrySnapshot(key.x(), key.z(), deposit.snapshot())));
        result.sort(Comparator.comparingInt(EntrySnapshot::x).thenComparingInt(EntrySnapshot::z));
        return List.copyOf(result);
    }
}
