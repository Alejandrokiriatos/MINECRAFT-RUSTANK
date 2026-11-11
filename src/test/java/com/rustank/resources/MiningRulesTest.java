package com.rustank.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MiningRulesTest {
    @Test
    void suppressesVanillaStoneAndCobblestoneDrops() {
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "stone"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "deepslate"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "cobblestone"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "cobbled_deepslate"));
    }

    @Test
    void suppressesVanillaOreDropsSoMiningCannotBypassTheEconomy() {
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "iron_ore"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "deepslate_diamond_ore"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "ancient_debris"));
    }

    @Test
    void doesNotSuppressRustankOrOtherModBlocks() {
        assertFalse(MiningRules.suppressVanillaResourceDrop("rustank", "stone_node"));
        assertFalse(MiningRules.suppressVanillaResourceDrop("othermod", "stone"));
    }

    @Test
    void doesNotSuppressUnrelatedVanillaBlocks() {
        assertFalse(MiningRules.suppressVanillaResourceDrop("minecraft", "dirt"));
        assertFalse(MiningRules.suppressVanillaResourceDrop("minecraft", "oak_log"));
    }
}
