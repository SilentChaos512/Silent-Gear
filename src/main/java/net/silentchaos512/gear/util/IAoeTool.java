/*
 * Silent Gear -- IAOETool
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.GearApi;
import net.silentchaos512.gear.config.Config;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IAoeTool {
    default int getAoeRadius(ItemStack stack) {
        return 1 + GearApi.getTraitLevel(stack, Const.Traits.WIDEN);
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
            switch (rt.getDirection().getAxis()) {
                case Y:
                    dir1 = Direction.SOUTH;
                    dir2 = Direction.EAST;
                    break;
                case X:
                    dir1 = Direction.UP;
                    dir2 = Direction.SOUTH;
                    break;
                default: // Z
                    dir1 = Direction.UP;
                    dir2 = Direction.EAST;
                    break;
            }

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
        boolean isCorrectTool = stack.getItem().isCorrectToolForDrops(stack, state) || ForgeHooks.isCorrectToolForDrops(state, player);
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
     * Handles actual AOE block breaking. Call {@link #onBlockStartBreak(ItemStack, BlockPos,
     * Player)} inside the {@code onBlockStartBreak} method of the tool's item.
     */
    final class BreakHandler {
        private BreakHandler() {}

        public static boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
            Level world = player.getCommandSenderWorld();
            if (world.isClientSide || !(world instanceof ServerLevel) || !(player instanceof ServerPlayer) || !(tool.getItem() instanceof IAoeTool))
                return false;

            IAoeTool item = (IAoeTool) tool.getItem();
            HitResult rt = item.rayTraceBlocks(world, player);
            BlockState stateOriginal = world.getBlockState(pos);

            if (rt != null && rt.getType() == HitResult.Type.BLOCK && item.isEffectiveOnBlock(tool, stateOriginal, player)) {
                BlockHitResult brt = (BlockHitResult) rt;
                Direction side = brt.getDirection();
                List<BlockPos> extraBlocks = item.getExtraBlocks(world, brt, player, tool);

                for (BlockPos pos2 : extraBlocks) {
                    BlockState state = world.getBlockState(pos2);
                    if (!world.hasChunkAt(pos2) || !player.mayUseItemAt(pos2, side, tool) || !(state.canHarvestBlock(world, pos2, player)))
                        continue;

                    if (player.getAbilities().instabuild) {
                        if (state.onDestroyedByPlayer(world, pos2, player, true, state.getFluidState()))
                            state.getBlock().destroy(world, pos2, state);
                    } else {
                        int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayer) player).gameMode.getGameModeForPlayer(), (ServerPlayer) player, pos2);
                        if (xp == -1) continue;
                        tool.getItem().mineBlock(tool, world, state, pos2, player);
                        BlockEntity tileEntity = world.getBlockEntity(pos2);

                        if (state.onDestroyedByPlayer(world, pos2, player, true, state.getFluidState())) {
                            state.getBlock().destroy(world, pos2, state);
                            state.getBlock().playerDestroy(world, player, pos2, state, tileEntity, tool);
                            state.getBlock().popExperience((ServerLevel) world, pos2, xp);
                        }
                    }

                    // TODO: Maybe add a config? Unfortunately, this code is called only on the server...
                    //world.playEvent(2001, pos, Block.getStateId(state)); // Playing for each block gets very loud

                    ((ServerPlayer) player).connection.send(new ClientboundBlockUpdatePacket(world, pos));
                }
            }
            return false;
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

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, value = Dist.CLIENT)
    final class HighlightHandler {
        private HighlightHandler() {}

        /*@SubscribeEvent
        public static void onDrawBlockHighlight(DrawHighlightEvent event) {
            Camera info = event.getInfo();
            Entity entity = info.getEntity();
            if (!(entity instanceof Player)) return;

            Player player = (Player) entity;

            HitResult rt = event.getTarget();

            if (rt.getType() == HitResult.Type.BLOCK) {
                ItemStack stack = player.getMainHandItem();

                if (stack.getItem() instanceof IAoeTool) {
                    Level world = player.getCommandSenderWorld();
                    IAoeTool item = (IAoeTool) stack.getItem();

                    for (BlockPos pos : item.getExtraBlocks(world, (BlockHitResult) rt, player, stack)) {
                        VertexConsumer vertexBuilder = event.getBuffers().getBuffer(RenderType.lines());
                        Vec3 vec = event.getInfo().getPosition();
                        BlockState blockState = world.getBlockState(pos);
                        drawSelectionBox(event.getMatrix(), world, vertexBuilder, event.getInfo().getEntity(), vec.x, vec.y, vec.z, pos, blockState);
                    }
                }
            }
        }*/

        // Copied from WorldRenderer
        @SuppressWarnings("MethodWithTooManyParameters")
        private static void drawSelectionBox(PoseStack matrixStackIn, Level world, VertexConsumer bufferIn, Entity entityIn, double xIn, double yIn, double zIn, BlockPos blockPosIn, BlockState blockStateIn) {
            drawShape(matrixStackIn, bufferIn, blockStateIn.getShape(world, blockPosIn, CollisionContext.of(entityIn)), (double) blockPosIn.getX() - xIn, (double) blockPosIn.getY() - yIn, (double) blockPosIn.getZ() - zIn, 0.0F, 0.0F, 0.0F, 0.4F);
        }

        // Copied from WorldRenderer
        @SuppressWarnings("MethodWithTooManyParameters")
        private static void drawShape(PoseStack matrixStackIn, VertexConsumer bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
            Matrix4f matrix4f = matrixStackIn.last().pose();
            shapeIn.forAllEdges((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
                bufferIn.vertex(matrix4f, (float) (p_230013_12_ + xIn), (float) (p_230013_14_ + yIn), (float) (p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
                bufferIn.vertex(matrix4f, (float) (p_230013_18_ + xIn), (float) (p_230013_20_ + yIn), (float) (p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
            });
        }
    }
}
