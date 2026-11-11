package com.rustank.survival;

import com.rustank.RustankMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * Versioned play-channel transport for server-authoritative player needs.
 * Only the server sends hunger and hydration values to clients.
 */
public final class SurvivalNeedsNetwork {
    private static final int PROTOCOL_VERSION = 1;
    private static SimpleChannel channel;

    private SurvivalNeedsNetwork() {
    }

    /** Must be called during common setup, before player connections begin. */
    public static void register() {
        if (channel != null) {
            return;
        }

        channel = ChannelBuilder
                .named(Identifier.fromNamespaceAndPath(RustankMod.MOD_ID, "survival"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
                .simpleChannel()
                .play()
                .clientbound()
                .addMain(NeedsSnapshotPacket.class, NeedsSnapshotPacket.STREAM_CODEC,
                        SurvivalNeedsNetwork::handleSnapshot)
                .build();
    }

    /** Sends the current authoritative values to this player only. */
    public static void sync(ServerPlayer player, SurvivalNeedsState state) {
        if (channel == null || player == null || state == null) {
            return;
        }

        channel.send(
                new NeedsSnapshotPacket(state.getHunger(), state.getHydration()),
                PacketDistributor.PLAYER.with(player));
    }

    private static void handleSnapshot(NeedsSnapshotPacket packet, CustomPayloadEvent.Context context) {
        // The packet is clientbound-only. Do not initialize client classes on a dedicated server.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            SurvivalNeedsClient.update(packet.hunger(), packet.hydration());
        }
    }

    private record NeedsSnapshotPacket(int hunger, int hydration) {
        private static final StreamCodec<RegistryFriendlyByteBuf, NeedsSnapshotPacket> STREAM_CODEC =
                StreamCodec.ofMember(NeedsSnapshotPacket::encode, NeedsSnapshotPacket::decode);

        private void encode(RegistryFriendlyByteBuf buffer) {
            buffer.writeVarInt(hunger);
            buffer.writeVarInt(hydration);
        }

        private static NeedsSnapshotPacket decode(RegistryFriendlyByteBuf buffer) {
            return new NeedsSnapshotPacket(clamp(buffer.readVarInt()), clamp(buffer.readVarInt()));
        }

        private static int clamp(int value) {
            return Math.max(0, Math.min(100, value));
        }
    }
}
