package net.silentchaos512.gear.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.silentchaos512.gear.Config;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IAoeTool {
    default int getAoeRadius(ItemStack stack) {
        return 1 + TraitHelper.getTraitLevel(stack, Const.Traits.WIDEN);
    }

    /**
     * Call {@link net.minecraft.world.item.Item}'s {@code rayTrace} method inside this.
     *
     * @param world  The world
     * @param player The player
     * @return The ray trace result
     */
    @Nullable
    HitResult rayTraceBlocks(Level world, Player player);

    default List<BlockPos> getExtraBlocks(Level world, @Nullable BlockHitResult rt, Player player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (player.isCrouching() || rt == null || rt.getBlockPos() == null || rt.getDirection() == null)
            return positions;

        BlockPos pos = rt.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (isEffectiveOnBlock(stack, state, player)) {
            Direction dir1, dir2;
            dir2 = switch (rt.getDirection().getAxis()) {
                case Y -> {
                    dir1 = Direction.SOUTH;
                    yield Direction.EAST;
                }
                case X -> {
                    dir1 = Direction.UP;
                    yield Direction.SOUTH;
                }
                default -> {
                    dir1 = Direction.UP;
                    yield Direction.EAST; // Z
                }
            };

            int r = getAoeRadius(stack);
            for (int i = -r; i <= r; ++i) {
                for (int j = -r; j <= r; ++j) {
                    if (!(i == 0 && j == 0)) {
                        attemptAddExtraBlock(world, state, pos.relative(dir1, i).relative(dir2, j), stack, player, positions);
                    }
                }
            }
        }

        return positions;
    }

    default boolean isEffectiveOnBlock(ItemStack stack, BlockState state, Player player) {
        boolean isCorrectTool = stack.getItem().isCorrectToolForDrops(stack, state);
        return isCorrectTool && stack.getDestroySpeed(state) > 1f;
    }

    default void attemptAddExtraBlock(Level world, BlockState state, BlockPos pos, ItemStack stack, Player player, List<BlockPos> list) {
        final BlockState state1 = world.getBlockState(pos);
        // Prevent breaking of unbreakable blocks, like bedrock
        if (state1.getDestroySpeed(world, pos) < 0) return;

        if (!world.isEmptyBlock(pos)
                && BreakHandler.areBlocksSimilar(state, state1)
                && isEffectiveOnBlock(stack, state1, player)) {
            list.add(pos);
        }
    }

    enum MatchMode {
        LOOSE, MODERATE, STRICT
    }

    /**
     * Handles actual AOE block breaking.
     */
    @EventBusSubscriber
    final class BreakHandler {
        private BreakHandler() {}

        @SubscribeEvent
        public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
            var player = event.getPlayer();
            if (!(player instanceof ServerPlayer)) return;

            var tool = player.getMainHandItem();
            if (!(tool.getItem() instanceof IAoeTool aoeToolItem)) return;

            var level = player.getCommandSenderWorld();
            if (level.isClientSide || !(level instanceof ServerLevel)) return;

            var pos = event.getPos();

            HitResult hitResult = aoeToolItem.rayTraceBlocks(level, player);
            BlockState stateOriginal = level.getBlockState(pos);

            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK && aoeToolItem.isEffectiveOnBlock(tool, stateOriginal, player)) {
                BlockHitResult brt = (BlockHitResult) hitResult;
                Direction side = brt.getDirection();
                List<BlockPos> extraBlocks = aoeToolItem.getExtraBlocks(level, brt, player, tool);

                for (BlockPos extraPos : extraBlocks) {
                    BlockState extraState = level.getBlockState(extraPos);
                    if (!level.hasChunkAt(extraPos) || !player.mayUseItemAt(extraPos, side, tool) || !(extraState.canHarvestBlock(level, extraPos, player)))
                        continue;

                    var extraBlock = extraState.getBlock();
                    if (player.getAbilities().instabuild) {
                        if (extraState.onDestroyedByPlayer(level, extraPos, player, true, extraState.getFluidState()))
                            extraBlock.destroy(level, extraPos, extraState);
                    } else {
                        BlockEntity blockEntity = level.getBlockEntity(extraPos);
                        int xp = extraState.getExpDrop(level, extraPos, blockEntity, player, tool);
                        tool.getItem().mineBlock(tool, level, extraState, extraPos, player);

                        if (extraState.onDestroyedByPlayer(level, extraPos, player, true, extraState.getFluidState())) {
                            extraBlock.destroy(level, extraPos, extraState);
                            extraBlock.playerDestroy(level, player, extraPos, extraState, blockEntity, tool);
                            extraBlock.popExperience((ServerLevel) level, extraPos, xp);
                        }
                    }

                    ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(level, pos));
                }
            }
        }

        /**
         * Determine if the blocks are similar enough to be considered the same. This depends on the
         * match mode configs. STRICT will only match the same block (ignoring exact state),
         * MODERATE will break anything with the same or lower harvest level (except level -1 and 0
         * blocks will still break together regardless of which is targeted), and LOOSE will match
         * anything.
         *
         * @return True if the blocks are the same (equal) or similar, false otherwise
         */
        static boolean areBlocksSimilar(BlockState state1, BlockState state2) {
            Block block1 = state1.getBlock();
            Block block2 = state2.getBlock();
            boolean isOre1 = isOre(state1);
            boolean isOre2 = isOre(state2);
            MatchMode mode = isOre1 && isOre2
                    ? Config.Common.matchModeOres.get()
                    : Config.Common.matchModeStandard.get();

            if (mode == MatchMode.LOOSE || block1 == block2)
                return true;

            if (mode == MatchMode.STRICT || (!isOre1 && isOre2))
                return false;

            // Otherwise, anything with same or lower harvest level should be okay
            int level1 = guessHarvestLevel(state1);
            int level2 = guessHarvestLevel(state2);
            return level1 >= level2 || level2 == 0;
        }

        private static boolean isOre(BlockState state) {
            // TODO: Add a tag specifically for hammers to filter blocks they consider "ores"?
            return state.is(Tags.Blocks.ORES);
        }

        private static int guessHarvestLevel(BlockState state) {
            if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) return 4;
            if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return 3;
            if (state.is(BlockTags.NEEDS_IRON_TOOL)) return 2;
            if (state.is(BlockTags.NEEDS_STONE_TOOL)) return 1;
            return 0;
        }
    }
}
