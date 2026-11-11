package com.rustank;

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
        LOGGER.info("Rustank server mod initialized. Minecraft 26.1.2 / Forge 64.1.5");
    }
}
