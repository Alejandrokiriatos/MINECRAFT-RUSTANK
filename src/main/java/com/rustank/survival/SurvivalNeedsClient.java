package com.rustank.survival;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;

/**
 * Client-only display cache and HUD layer for server-synchronized survival needs.
 * This class is referenced only from physical-client guarded code.
 */
public final class SurvivalNeedsClient {
    private static final int BAR_WIDTH = 118;
    private static volatile int hunger = 100;
    private static volatile int hydration = 100;

    private SurvivalNeedsClient() {
    }

    public static void register() {
        AddGuiOverlayLayersEvent.BUS.addListener(SurvivalNeedsClient::registerLayers);
    }

    /** Called by the clientbound snapshot packet on the main client thread. */
    public static void update(int newHunger, int newHydration) {
        hunger = clamp(newHunger);
        hydration = clamp(newHydration);
    }

    private static void registerLayers(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().addAbove(
                ForgeLayeredDraw.PRE_SLEEP_STACK,
                Identifier.fromNamespaceAndPath("rustank", "survival_needs"),
                ForgeLayeredDraw.HOTBAR_AND_DECOS,
                SurvivalNeedsClient::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        int x = 10;
        int y = 10;
        graphics.text(minecraft.font, Component.literal("RUSTANK"), x, y, 0xFFFFFFFF);
        drawNeed(graphics, minecraft, "HAMBRE", hunger, x, y + 13, 0xFFFFB347);
        drawNeed(graphics, minecraft, "SED", hydration, x, y + 39, 0xFF54C8FF);
    }

    private static void drawNeed(GuiGraphicsExtractor graphics, Minecraft minecraft,
                                 String label, int value, int x, int y, int fillColor) {
        graphics.text(minecraft.font,
                Component.literal(label + "  " + value + "%"),
                x, y, 0xFFFFFFFF);

        int barTop = y + 11;
        graphics.fill(x, barTop, x + BAR_WIDTH, barTop + 7, 0xAA101010);

        int filledWidth = Math.round(BAR_WIDTH * (value / 100.0f));
        if (filledWidth > 0) {
            graphics.fill(x + 1, barTop + 1,
                    x + 1 + Math.min(BAR_WIDTH - 2, filledWidth), barTop + 6,
                    fillColor);
        }
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
