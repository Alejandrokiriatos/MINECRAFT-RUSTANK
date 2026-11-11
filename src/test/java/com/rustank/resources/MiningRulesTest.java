package com.rustank.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MiningRulesTest {
    @Test
    void suppressesStoneAndCobbleDropsThatWouldBypassSurfaceDeposits() {
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "stone"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "cobblestone"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "deepslate"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "cobbled_deepslate"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "granite"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "tuff"));
    }

    @Test
    void suppressesVanillaOreAndNetherResourceDrops() {
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "iron_ore"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "deepslate_diamond_ore"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "nether_quartz_ore"));
        assertTrue(MiningRules.suppressVanillaResourceDrop("minecraft", "ancient_debris"));
    }

    @Test
    void leavesUnrelatedVanillaBlocksAndOtherNamespacesUntouched() {
        assertFalse(MiningRules.suppressVanillaResourceDrop("minecraft", "oak_log"));
        assertFalse(MiningRules.suppressVanillaResourceDrop("minecraft", "dirt"));
        assertFalse(MiningRules.suppressVanillaResourceDrop("rustank", "stone"));
        assertFalse(MiningRules.suppressVanillaResourceDrop("othermod", "iron_ore"));
    }

    @Test
    void handlesNullAndUnknownIdentifiersSafely() {
        assertFalse(MiningRules.suppressVanillaResourceDrop(null, "stone"));
        assertFalse(MiningRules.suppressVanillaResourceDrop("minecraft", null));
        assertFalse(MiningRules.suppressVanillaResourceDrop(null, null));
        assertFalse(MiningRules.suppressVanillaResourceDrop("minecraft", "not_a_block"));
    }
}
