package com.rustank;

import com.rustank.world.WorldBounds;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the server-side Rustank mod.
 */
@Mod(RustankMod.MOD_ID)
public final class RustankMod {
    public static final String MOD_ID = "rustank";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RustankMod(FMLJavaModLoadingContext context) {
        ServerStartingEvent.BUS.addListener(this::onServerStarting);
        RegisterCommandsEvent.BUS.addListener(RustankTerrainCommand::register);
        LOGGER.info("Rustank server mod initialized. Minecraft 26.1.2 / Forge 64.1.5");
    }

    /**
     * Applies the playable map boundary to every server dimension.
     * The vanilla world border is server-authoritative and also prevents
     * players from simply walking out of the 4,000 by 4,000 play area.
     */
    private void onServerStarting(ServerStartingEvent event) {
        double centerX = (WorldBounds.MIN_X + WorldBounds.MAX_X_EXCLUSIVE) / 2.0;
        double centerZ = (WorldBounds.MIN_Z + WorldBounds.MAX_Z_EXCLUSIVE) / 2.0;
        double diameter = WorldBounds.MAX_X_EXCLUSIVE - WorldBounds.MIN_X;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            WorldBorder border = level.getWorldBorder();
            border.setCenter(centerX, centerZ);
            border.setSize(diameter);
            border.setWarningBlocks(16);
        }

        LOGGER.info("Rustank map boundary enabled: {} x {} blocks, centered at ({}, {}).",
                diameter, diameter, centerX, centerZ);
    }
}
