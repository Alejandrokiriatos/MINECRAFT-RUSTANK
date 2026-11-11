package com.rustank.survival;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.LogicalSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Forge event bridge for the server-authoritative per-player hunger and thirst.
 *
 * <p>PlayerEvent.LoadFromFile and SaveToFile provide a dedicated sidecar file
 * per player. The main player .dat file is never modified.</p>
 */
public final class SurvivalNeedsEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger("rustank");
    private static final String PLAYER_FILE_SUFFIX = "rustank-needs";
    private static final Map<UUID, SurvivalNeedsState> STATES = new ConcurrentHashMap<>();

    private SurvivalNeedsEvents() {
    }

    public static void register() {
        TickEvent.PlayerTickEvent.Post.BUS.addListener(SurvivalNeedsEvents::onPlayerTick);
        PlayerEvent.LoadFromFile.BUS.addListener(SurvivalNeedsEvents::onPlayerLoad);
        PlayerEvent.SaveToFile.BUS.addListener(SurvivalNeedsEvents::onPlayerSave);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(SurvivalNeedsEvents::onPlayerLogin);
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(SurvivalNeedsEvents::onPlayerLogout);
        PlayerEvent.Clone.BUS.addListener(SurvivalNeedsEvents::onPlayerClone);
    }

    private static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
        UUID playerId = event.getEntity().getUUID();
        SurvivalNeedsState state = new SurvivalNeedsState();
        Path playerFile = event.getPlayerFile(PLAYER_FILE_SUFFIX).toPath();

        try {
            Optional<SurvivalNeedsState.Snapshot> saved = SurvivalNeedsPersistence.load(playerFile);
            saved.ifPresent(state::restore);
        } catch (IOException exception) {
            LOGGER.warn("Could not load Rustank survival needs for player {}. Safe defaults will be used.",
                    playerId, exception);
        }

        STATES.put(playerId, state);
    }

    private static void onPlayerSave(PlayerEvent.SaveToFile event) {
        UUID playerId = event.getEntity().getUUID();
        SurvivalNeedsState state = STATES.get(playerId);
        if (state == null) {
            return;
        }

        Path playerFile = event.getPlayerFile(PLAYER_FILE_SUFFIX).toPath();
        try {
            SurvivalNeedsPersistence.save(playerFile, state.snapshot());
        } catch (IOException exception) {
            LOGGER.error("Could not save Rustank survival needs for player {}.", playerId, exception);
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (event.side() != LogicalSide.SERVER || !(event.player() instanceof ServerPlayer player)) {
            return;
        }

        SurvivalNeedsState state = STATES.computeIfAbsent(
                player.getUUID(), ignored -> new SurvivalNeedsState());
        state.tick(player.isSprinting(), player.isCreative() || player.isSpectator());
    }

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        STATES.computeIfAbsent(event.getEntity().getUUID(), ignored -> new SurvivalNeedsState());
    }

    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        STATES.remove(event.getEntity().getUUID());
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        UUID oldId = event.getOriginal().getUUID();
        SurvivalNeedsState oldState = STATES.get(oldId);
        if (oldState != null) {
            STATES.put(event.getEntity().getUUID(), oldState);
        }
    }
}
