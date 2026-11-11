package com.rustank;

import com.mojang.brigadier.CommandDispatcher;
import com.rustank.world.ChunkTerrainPlan;
import com.rustank.world.VegetationPlanner;
import com.rustank.world.WorldBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Manual first-playtest bridge. Materializes one planned chunk and its
 * deterministic vegetation; automatic world generation remains a later step.
 */
public final class RustankTerrainCommand {
    private RustankTerrainCommand() {}

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("rustank")
                .then(Commands.literal("generate")
                        .requires(source -> source.permissions()
                                .hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> generate(context.getSource()))));
    }

    private static int generate(CommandSourceStack source)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = source.getLevel();
        BlockPos playerPos = player.blockPosition();
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;

        ChunkTerrainPlan plan = ChunkTerrainPlan.create(level.getSeed(), chunkX, chunkZ);
        int changed = 0;
        int minY = level.getMinY();
        int maxY = level.getMaxY();

        for (int localZ = 0; localZ < ChunkTerrainPlan.CHUNK_SIZE; localZ++) {
            for (int localX = 0; localX < ChunkTerrainPlan.CHUNK_SIZE; localX++) {
                ChunkTerrainPlan.Column column = plan.column(localX, localZ);
                if (!column.isInsideMap()
                        || !WorldBounds.contains(column.blockX(), column.blockZ())) {
                    continue;
                }
                for (int y = minY; y <= maxY; y++) {
                    BlockState state = stateFor(column.materialAtY(y));
                    if (state == null) {
                        state = Blocks.AIR.defaultBlockState();
                    }
                    BlockPos pos = new BlockPos(column.blockX(), y, column.blockZ());
                    if (!level.getBlockState(pos).equals(state)
                            && level.setBlock(pos, state, 3)) {
                        changed++;
                    }
                }
            }
        }

        int vegetation = placeVegetation(level, chunkX, chunkZ);
        int changedBlocks = changed;
        source.sendSuccess(() -> Component.literal(
                "Rustank: chunk " + chunkX + ", " + chunkZ
                        + " materializado. Bloques cambiados: " + changedBlocks
                        + "; vegetación colocada: " + vegetation
                        + ". Prueba en un mundo desechable."),
                true);
        return changed + vegetation;
    }

    private static int placeVegetation(ServerLevel level, int chunkX, int chunkZ) {
        int placed = 0;
        long seed = level.getSeed();
        for (VegetationPlanner.Placement plant : VegetationPlanner.planChunk(seed, chunkX, chunkZ)) {
            int x = plant.x();
            int z = plant.z();
            int y = plant.groundY() + 1;
            if (!WorldBounds.contains(x, z) || !level.isEmptyBlock(new BlockPos(x, y, z))) {
                continue;
            }

            switch (plant.type()) {
                case OAK_TREE, PINE_TREE -> {
                    boolean pine = plant.type() == VegetationPlanner.Type.PINE_TREE;
                    BlockState log = (pine ? Blocks.SPRUCE_LOG : Blocks.OAK_LOG).defaultBlockState();
                    BlockState leaves = (pine ? Blocks.SPRUCE_LEAVES : Blocks.OAK_LEAVES)
                            .defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
                    int height = 4 + Math.floorMod((int) (seed ^ (x * 31L) ^ (z * 17L)), 3);
                    for (int dy = 0; dy < height; dy++) {
                        BlockPos pos = new BlockPos(x, y + dy, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, log, 3);
                            placed++;
                        }
                    }
                    for (int dy = height - 2; dy <= height; dy++) {
                        int radius = dy == height ? 1 : 2;
                        for (int dx = -radius; dx <= radius; dx++) {
                            for (int dz = -radius; dz <= radius; dz++) {
                                if (Math.abs(dx) == radius && Math.abs(dz) == radius
                                        && dy != height) {
                                    continue;
                                }
                                BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                                if (WorldBounds.contains(pos.getX(), pos.getZ())
                                        && level.isEmptyBlock(pos)) {
                                    level.setBlock(pos, leaves, 3);
                                    placed++;
                                }
                            }
                        }
                    }
                }
                case BUSH -> {
                    level.setBlock(new BlockPos(x, y, z), Blocks.FERN.defaultBlockState(), 3);
                    placed++;
                }
                case CACTUS -> {
                    int height = 1 + Math.floorMod(x * 13 + z * 7, 3);
                    for (int dy = 0; dy < height; dy++) {
                        BlockPos pos = new BlockPos(x, y + dy, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, Blocks.CACTUS.defaultBlockState(), 3);
                            placed++;
                        }
                    }
                }
            }
        }
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
