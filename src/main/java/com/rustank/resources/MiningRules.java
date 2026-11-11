package com.rustank.resources;

import java.util.Set;

/**
 * Resource-economy rules independent from Forge event wiring.
 *
 * Vanilla geology must not be a renewable source of Rustank building resources:
 * those resources will come from dedicated, finite/respawning surface deposits.
 */
public final class MiningRules {
    private static final Set<String> BLOCKED_VANILLA_BLOCKS = Set.of(
            "stone", "cobblestone", "mossy_cobblestone",
            "deepslate", "cobbled_deepslate", "cobbled_deepslate",
            "granite", "diorite", "andesite", "tuff", "calcite",
            "dripstone_block", "pointed_dripstone",
            "coal_ore", "deepslate_coal_ore",
            "iron_ore", "deepslate_iron_ore",
            "copper_ore", "deepslate_copper_ore",
            "gold_ore", "deepslate_gold_ore",
            "redstone_ore", "deepslate_redstone_ore",
            "lapis_ore", "deepslate_lapis_ore",
            "diamond_ore", "deepslate_diamond_ore",
            "emerald_ore", "deepslate_emerald_ore",
            "nether_gold_ore", "nether_quartz_ore",
            "ancient_debris"
    );

    private MiningRules() {
    }

    /**
     * True when a vanilla block must not yield its normal item drops.
     * Non-Minecraft namespaces are deliberately left alone for Rustank nodes
     * and future third-party compatibility.
     */
    public static boolean suppressVanillaResourceDrop(String namespace, String path) {
        return "minecraft".equals(namespace) && BLOCKED_VANILLA_BLOCKS.contains(path);
    }
}
