package com.rustank.resources;

/**
 * Server-owned finite deposit stock. All time is expressed in server ticks.
 * A depleted deposit starts one respawn timer; it cannot be harvested while empty.
 */
public final class ResourceDeposit {
    private final DepositType type;
    private int remaining;
    private long respawnAtTick = -1L;

    public ResourceDeposit(DepositType type) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        this.type = type;
        this.remaining = type.capacity();
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
}
