package com.rustank.resources;

/** Surface resource families; none are mined from ordinary vanilla geology. */
public enum DepositType {
    STONE(900, 30L * 60L * 20L),
    METAL(450, 45L * 60L * 20L),
    SULFUR(350, 60L * 60L * 20L);

    private final int capacity;
    private final long respawnTicks;

    DepositType(int capacity, long respawnTicks) {
        this.capacity = capacity;
        this.respawnTicks = respawnTicks;
    }

    public int capacity() { return capacity; }
    public long respawnTicks() { return respawnTicks; }
}
