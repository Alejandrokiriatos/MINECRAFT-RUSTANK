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
 * Forge event bridge for server-authoritative hunger and hydration.
 *
 * <p>PlayerEvent.LoadFromFile and SaveToFile provide a sidecar file per player.
 * Login also loads the file for brand-new vanilla player records, and logout
 * saves before Forge removes the player from its live player list.</p>
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
        Path playerFile = event.getPlayerFile(PLAYER_FILE_SUFFIX).toPath();
        STATES.put(playerId, readState(playerId, playerFile));
    }

    private static void onPlayerSave(PlayerEvent.SaveToFile event) {
        UUID playerId = event.getEntity().getUUID();
        SurvivalNeedsState state = STATES.get(playerId);
        if (state != null) {
            saveState(playerId, event.getPlayerFile(PLAYER_FILE_SUFFIX).toPath(), state);
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
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        STATES.computeIfAbsent(player.getUUID(), ignored ->
                readState(player.getUUID(), playerDataPath(player)));
    }

    /**
     * Forge fires PlayerLoggedOutEvent before PlayerList saves/removes the
     * player. Save while the ServerPlayer still has access to PlayerDataStorage.
     */
    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerId = player.getUUID();
            SurvivalNeedsState state = STATES.get(playerId);
            if (state == null || saveState(playerId, playerDataPath(player), state)) {
                STATES.remove(playerId);
            }
            return;
        }

        STATES.remove(event.getEntity().getUUID());
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        UUID oldId = event.getOriginal().getUUID();
        SurvivalNeedsState oldState = STATES.get(oldId);
        if (oldState != null) {
            STATES.put(event.getEntity().getUUID(), oldState);
        }
    }

    /**
     * Forge exposes the player data directory through PlayerList#getPlayerIo.
     * Using this path covers first-time players for whom LoadFromFile is not
     * fired because no vanilla .dat record exists yet.
     */
    private static Path playerDataPath(ServerPlayer player) {
        return player.level().getServer().getPlayerList().getPlayerIo()
                .getPlayerDataFolder().toPath()
                .resolve(player.getStringUUID() + "." + PLAYER_FILE_SUFFIX);
    }

    private static SurvivalNeedsState readState(UUID playerId, Path file) {
        SurvivalNeedsState state = new SurvivalNeedsState();
        try {
            Optional<SurvivalNeedsState.Snapshot> saved = SurvivalNeedsPersistence.load(file);
            saved.ifPresent(state::restore);
        } catch (IOException exception) {
            LOGGER.warn("Could not load Rustank survival needs for player {}. Safe defaults will be used.",
                    playerId, exception);
        }
        return state;
    }

    private static boolean saveState(UUID playerId, Path file, SurvivalNeedsState state) {
        try {
            SurvivalNeedsPersistence.save(file, state.snapshot());
            return true;
        } catch (IOException exception) {
            LOGGER.error("Could not save Rustank survival needs for player {}.", playerId, exception);
            return false;
        }
    }
}
