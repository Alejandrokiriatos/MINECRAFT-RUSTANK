package com.rustank.resources;

/**
 * Server-owned finite deposit stock. All time is expressed in world ticks.
 */
public final class ResourceDeposit {
    public record Snapshot(String type, int remaining, long respawnAtTick) { }

    private final DepositType type;
    private int remaining;
    private long respawnAtTick = -1L;

    public ResourceDeposit(DepositType type) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        this.type = type;
        this.remaining = type.capacity();
    }

    private ResourceDeposit(DepositType type, int remaining, long respawnAtTick) {
        this.type = type;
        this.remaining = remaining;
        this.respawnAtTick = respawnAtTick;
    }

    public DepositType type() { return type; }
    public int remaining() { return remaining; }
    public boolean depleted() { return remaining == 0; }

    public int harvest(int requested, long currentTick) {
        if (requested <= 0 || depleted()) return 0;
        int taken = Math.min(requested, remaining);
        remaining -= taken;
        if (remaining == 0) {
            long safeTick = Math.max(0L, currentTick);
            respawnAtTick = safeTick > Long.MAX_VALUE - type.respawnTicks()
                    ? Long.MAX_VALUE : safeTick + type.respawnTicks();
        }
        return taken;
    }

    /** Returns true only on the tick that the deposit becomes available again. */
    public boolean tick(long currentTick) {
        if (!depleted() || respawnAtTick < 0L || currentTick < respawnAtTick) return false;
        remaining = type.capacity();
        respawnAtTick = -1L;
        return true;
    }

    public long respawnAtTick() { return respawnAtTick; }

    public Snapshot snapshot() {
        return new Snapshot(type.name(), remaining, respawnAtTick);
    }

    /**
     * Restores validated world data. Invalid or obsolete values fail closed rather
     * than creating extra stock or an immediately respawning deposit.
     */
    public static ResourceDeposit restore(Snapshot snapshot) {
        if (snapshot == null) throw new IllegalArgumentException("snapshot cannot be null");
        final DepositType type;
        try {
            type = DepositType.valueOf(snapshot.type());
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Unknown deposit type", exception);
        }
        if (snapshot.remaining() < 0 || snapshot.remaining() > type.capacity()) {
            throw new IllegalArgumentException("Deposit stock outside allowed range");
        }
        if (snapshot.remaining() == 0 && snapshot.respawnAtTick() < 0) {
            throw new IllegalArgumentException("Depleted deposit requires a respawn tick");
        }
        if (snapshot.remaining() > 0 && snapshot.respawnAtTick() != -1L) {
            throw new IllegalArgumentException("Active deposit cannot have a respawn timer");
        }
        return new ResourceDeposit(type, snapshot.remaining(), snapshot.respawnAtTick());
    }
}
