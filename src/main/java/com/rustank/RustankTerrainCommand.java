package com.rustank;

import com.mojang.brigadier.CommandDispatcher;
import com.rustank.world.ChunkTerrainPlan;
import com.rustank.world.WorldBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Small, explicit first-playtest bridge: builds the logical terrain plan into
 * the one chunk containing the executing player. This is intentionally a
 * manual command until the custom world generator is registered.
 */
public final class RustankTerrainCommand {
    private RustankTerrainCommand() {}

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("rustank")
                .then(Commands.literal("generate")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> generate(context.getSource()))));
    }

    private static int generate(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos playerPos = player.blockPosition();
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;

        ChunkTerrainPlan plan = ChunkTerrainPlan.create(level.getSeed(), chunkX, chunkZ);
        int placed = 0;
        for (int localZ = 0; localZ < ChunkTerrainPlan.CHUNK_SIZE; localZ++) {
            for (int localX = 0; localX < ChunkTerrainPlan.CHUNK_SIZE; localX++) {
                ChunkTerrainPlan.Column column = plan.column(localX, localZ);
                if (!column.isInsideMap()) {
                    continue;
                }
                int topY = Math.max(column.surfaceY(), column.waterTopY());
                for (int y = level.getMinBuildHeight(); y <= topY; y++) {
                    BlockState state = stateFor(column.materialAtY(y));
                    if (state == null) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(column.blockX(), y, column.blockZ());
                    if (level.setBlock(pos, state, 3)) {
                        placed++;
                    }
                }
            }
        }

        source.sendSuccess(() -> Component.literal(
                "Rustank: chunk " + chunkX + ", " + chunkZ
                        + " generado. Bloques modificados: " + placed
                        + ". Usa un mundo nuevo para evitar restos del terreno vanilla."),
                true);
        return placed;
    }

    private static BlockState stateFor(ChunkTerrainPlan.Material material) {
        return switch (material) {
            case GRASS -> Blocks.GRASS_BLOCK.defaultBlockState();
            case DIRT -> Blocks.DIRT.defaultBlockState();
            case SAND -> Blocks.SAND.defaultBlockState();
            case SANDSTONE -> Blocks.SANDSTONE.defaultBlockState();
            case STONE -> Blocks.STONE.defaultBlockState();
            case WATER -> Blocks.WATER.defaultBlockState();
            case NONE -> null;
        };
    }
}
