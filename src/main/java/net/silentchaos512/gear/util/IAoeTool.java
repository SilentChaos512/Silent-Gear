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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.GearApi;
import net.silentchaos512.gear.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IAoeTool {
    /**
     * The tool class of the item (pickaxe, shovel, axe)
     *
     * @return The tool type
     */
    @Nonnull
    ToolType getAoeToolType();

    default int getAoeRadius(ItemStack stack) {
        return 1 + GearApi.getTraitLevel(stack, Const.Traits.WIDEN);
    }

    /**
     * Call {@link net.minecraft.item.Item}'s {@code rayTrace} method inside this.
     *
     * @param world  The world
     * @param player The player
     * @return The ray trace result
     */
    @Nullable
    RayTraceResult rayTraceBlocks(World world, PlayerEntity player);

    default List<BlockPos> getExtraBlocks(World world, @Nullable BlockRayTraceResult rt, PlayerEntity player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (player.isCrouching() || rt == null || rt.getBlockPos() == null || rt.getDirection() == null)
            return positions;

        BlockPos pos = rt.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (isEffectiveOnBlock(stack, world, pos, state, player)) {
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
                        attemptAddExtraBlock(world, state, pos.relative(dir1, i).relative(dir2, j), stack, positions);
                    }
                }
            }
        }

        return positions;
    }

    default boolean isEffectiveOnBlock(ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return stack.getItem().canHarvestBlock(stack, state) || ForgeHooks.canHarvestBlock(state, player, world, pos);
    }

    default void attemptAddExtraBlock(World world, BlockState state, BlockPos pos, ItemStack stack, List<BlockPos> list) {
        final BlockState state1 = world.getBlockState(pos);
        // Prevent breaking of unbreakable blocks, like bedrock
        if (state1.getDestroySpeed(world, pos) < 0) return;

        if (!world.isEmptyBlock(pos)
                && BreakHandler.areBlocksSimilar(state, state1)
                && (state1.getBlock().isToolEffective(state1, getAoeToolType()) || stack.getItem().canHarvestBlock(stack, state1))) {
            list.add(pos);
        }
    }

    enum MatchMode {
        LOOSE, MODERATE, STRICT
    }

    /**
     * Handles actual AOE block breaking. Call {@link #onBlockStartBreak(ItemStack, BlockPos,
     * PlayerEntity)} inside the {@code onBlockStartBreak} method of the tool's item.
     */
    final class BreakHandler {
        private BreakHandler() {}

        public static boolean onBlockStartBreak(ItemStack tool, BlockPos pos, PlayerEntity player) {
            World world = player.getCommandSenderWorld();
            if (world.isClientSide || !(world instanceof ServerWorld) || !(player instanceof ServerPlayerEntity) || !(tool.getItem() instanceof IAoeTool))
                return false;

            IAoeTool item = (IAoeTool) tool.getItem();
            RayTraceResult rt = item.rayTraceBlocks(world, player);
            BlockState stateOriginal = world.getBlockState(pos);

            if (rt != null && rt.getType() == RayTraceResult.Type.BLOCK && item.isEffectiveOnBlock(tool, world, pos, stateOriginal, player)) {
                BlockRayTraceResult brt = (BlockRayTraceResult) rt;
                Direction side = brt.getDirection();
                List<BlockPos> extraBlocks = item.getExtraBlocks(world, brt, player, tool);

                for (BlockPos pos2 : extraBlocks) {
                    BlockState state = world.getBlockState(pos2);
                    if (!world.hasChunkAt(pos2) || !player.mayUseItemAt(pos2, side, tool) || !(state.canHarvestBlock(world, pos2, player)))
                        continue;

                    if (player.abilities.instabuild) {
                        if (state.removedByPlayer(world, pos2, player, true, state.getFluidState()))
                            state.getBlock().destroy(world, pos2, state);
                    } else {
                        int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).gameMode.getGameModeForPlayer(), (ServerPlayerEntity) player, pos2);
                        if (xp == -1) continue;
                        tool.getItem().mineBlock(tool, world, state, pos2, player);
                        TileEntity tileEntity = world.getBlockEntity(pos2);

                        if (state.removedByPlayer(world, pos2, player, true, state.getFluidState())) {
                            state.getBlock().destroy(world, pos2, state);
                            state.getBlock().playerDestroy(world, player, pos2, state, tileEntity, tool);
                            state.getBlock().popExperience((ServerWorld) world, pos2, xp);
                        }
                    }

                    // TODO: Maybe add a config? Unfortunately, this code is called only on the server...
                    //world.playEvent(2001, pos, Block.getStateId(state)); // Playing for each block gets very loud

                    ((ServerPlayerEntity) player).connection.send(new SChangeBlockPacket(world, pos));
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
            int level1 = block1.getHarvestLevel(state1);
            int level2 = block2.getHarvestLevel(state2);
            return level1 >= level2 || level2 == 0;
        }

        private static boolean isOre(BlockState state) {
            return state.is(Tags.Blocks.ORES);
        }
    }

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, value = Dist.CLIENT)
    final class HighlightHandler {
        private HighlightHandler() {}

        @SubscribeEvent
        public static void onDrawBlockHighlight(DrawHighlightEvent event) {
            ActiveRenderInfo info = event.getInfo();
            Entity entity = info.getEntity();
            if (!(entity instanceof PlayerEntity)) return;

            PlayerEntity player = (PlayerEntity) entity;

            RayTraceResult rt = event.getTarget();

            if (rt.getType() == RayTraceResult.Type.BLOCK) {
                ItemStack stack = player.getMainHandItem();

                if (stack.getItem() instanceof IAoeTool) {
                    World world = player.getCommandSenderWorld();
                    IAoeTool item = (IAoeTool) stack.getItem();

                    for (BlockPos pos : item.getExtraBlocks(world, (BlockRayTraceResult) rt, player, stack)) {
                        IVertexBuilder vertexBuilder = event.getBuffers().getBuffer(RenderType.lines());
                        Vector3d vec = event.getInfo().getPosition();
                        BlockState blockState = world.getBlockState(pos);
                        drawSelectionBox(event.getMatrix(), world, vertexBuilder, event.getInfo().getEntity(), vec.x, vec.y, vec.z, pos, blockState);
                    }
                }
            }
        }

        // Copied from WorldRenderer
        @SuppressWarnings("MethodWithTooManyParameters")
        private static void drawSelectionBox(MatrixStack matrixStackIn, World world, IVertexBuilder bufferIn, Entity entityIn, double xIn, double yIn, double zIn, BlockPos blockPosIn, BlockState blockStateIn) {
            drawShape(matrixStackIn, bufferIn, blockStateIn.getShape(world, blockPosIn, ISelectionContext.of(entityIn)), (double) blockPosIn.getX() - xIn, (double) blockPosIn.getY() - yIn, (double) blockPosIn.getZ() - zIn, 0.0F, 0.0F, 0.0F, 0.4F);
        }

        // Copied from WorldRenderer
        @SuppressWarnings("MethodWithTooManyParameters")
        private static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
            Matrix4f matrix4f = matrixStackIn.last().pose();
            shapeIn.forAllEdges((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
                bufferIn.vertex(matrix4f, (float) (p_230013_12_ + xIn), (float) (p_230013_14_ + yIn), (float) (p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
                bufferIn.vertex(matrix4f, (float) (p_230013_18_ + xIn), (float) (p_230013_20_ + yIn), (float) (p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
            });
        }
    }
}
