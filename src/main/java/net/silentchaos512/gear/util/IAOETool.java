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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.client.renderer.ActiveRenderInfo;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IAOETool {
    /**
     * The tool class of the item (pickaxe, shovel, axe)
     *
     * @return The tool type
     */
    @Nonnull
    ToolType getAOEToolClass();

    /**
     * Call the item's rayTrace method inside this.
     *
     * @param world  The world
     * @param player The player
     * @return The ray trace result
     */
    @Nullable
    RayTraceResult rayTraceBlocks(World world, PlayerEntity player);

    default List<BlockPos> getExtraBlocks(World world, @Nullable BlockRayTraceResult rt, PlayerEntity player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (player.isSneaking() || rt == null || rt.getPos() == null || rt.getFace() == null)
            return positions;

        BlockPos pos = rt.getPos();
        BlockState state = world.getBlockState(pos);

        if (isEffectiveOnBlock(stack, world, pos, state)) {
            switch (rt.getFace().getAxis()) {
                case Y:
                    attemptAddExtraBlock(world, state, pos.offset(Direction.NORTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.NORTH).offset(Direction.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.EAST).offset(Direction.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.SOUTH).offset(Direction.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.WEST).offset(Direction.NORTH), stack, positions);
                    break;
                case X:
                    attemptAddExtraBlock(world, state, pos.offset(Direction.NORTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.DOWN), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.NORTH).offset(Direction.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.UP).offset(Direction.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.SOUTH).offset(Direction.DOWN), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.DOWN).offset(Direction.NORTH), stack, positions);
                    break;
                case Z:
                    attemptAddExtraBlock(world, state, pos.offset(Direction.DOWN), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.DOWN).offset(Direction.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.EAST).offset(Direction.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.UP).offset(Direction.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(Direction.WEST).offset(Direction.DOWN), stack, positions);
                    break;
            }
        }
//        SilentGear.log.debug("{}", positions);
        return positions;
    }

    default boolean isEffectiveOnBlock(ItemStack stack, World world, BlockPos pos, BlockState state) {
        // The Forge.canToolHarvestBlock method seems to be very unreliable...
        return stack.getItem().canHarvestBlock(stack, state) || ForgeHooks.canToolHarvestBlock(world, pos, stack);
    }

    default void attemptAddExtraBlock(World world, BlockState state1, BlockPos pos2, ItemStack stack, List<BlockPos> list) {
        final BlockState state2 = world.getBlockState(pos2);
        // Prevent breaking of unbreakable blocks, like bedrock
        if (state2.getBlockHardness(world, pos2) < 0) return;

        if (!world.isAirBlock(pos2)
                && BreakHandler.areBlocksSimilar(state1, state2)
                && (state2.getBlock().isToolEffective(state2, getAOEToolClass()) || stack.getItem().canHarvestBlock(stack, state2))) {
            list.add(pos2);
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
            World world = player.getEntityWorld();
            if (world.isRemote || !(player instanceof ServerPlayerEntity) || !(tool.getItem() instanceof IAOETool))
                return false;

            IAOETool item = (IAOETool) tool.getItem();
            RayTraceResult rt = item.rayTraceBlocks(world, player);
            BlockState stateOriginal = world.getBlockState(pos);

            if (rt != null && rt.getType() == RayTraceResult.Type.BLOCK && item.isEffectiveOnBlock(tool, world, pos, stateOriginal)) {
                BlockRayTraceResult brt = (BlockRayTraceResult) rt;
                Direction side = brt.getFace();
                List<BlockPos> extraBlocks = item.getExtraBlocks(world, brt, player, tool);

                for (BlockPos pos2 : extraBlocks) {
                    BlockState state = world.getBlockState(pos2);
                    if (!world.isBlockLoaded(pos2) || !player.canPlayerEdit(pos2, side, tool) || !(state.canHarvestBlock(world, pos2, player)))
                        continue;

                    if (player.abilities.isCreativeMode) {
                        if (state.removedByPlayer(world, pos2, player, true, state.getFluidState()))
                            state.getBlock().onPlayerDestroy(world, pos2, state);
                    } else {
                        int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos2);
                        if (xp == -1) continue;
                        tool.getItem().onBlockDestroyed(tool, world, state, pos2, player);
                        TileEntity tileEntity = world.getTileEntity(pos2);
                        if (state.removedByPlayer(world, pos2, player, true, state.getFluidState())) {
                            state.getBlock().onPlayerDestroy(world, pos2, state);
                            state.getBlock().harvestBlock(world, player, pos2, state, tileEntity, tool);
                            state.getBlock().dropXpOnBlockBreak(world, pos2, xp);
                        }
                    }

                    world.playEvent(2001, pos, Block.getStateId(state));
                    ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
                }
            }
            return false;
        }

        private static final Set<Block> ORE_BLOCKS = new HashSet<>();

        public static void buildOreBlocksSet() {
            ORE_BLOCKS.clear();

            for (Block block : ForgeRegistries.BLOCKS) {
                if (block instanceof OreBlock || Tags.Blocks.ORES.contains(block)) {
                    ORE_BLOCKS.add(block);
                }
            }

            SilentGear.LOGGER.info("IAOETool: Rebuilt ore block set, contains {} items", ORE_BLOCKS.size());
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
            boolean isOre1 = ORE_BLOCKS.contains(block1);
            boolean isOre2 = ORE_BLOCKS.contains(block2);
            MatchMode mode = isOre1 && isOre2
                    ? Config.GENERAL.matchModeOres.get()
                    : Config.GENERAL.matchModeStandard.get();

            if (mode == MatchMode.LOOSE || block1 == block2)
                return true;

            if (mode == MatchMode.STRICT || (!isOre1 && isOre2))
                return false;

            // Otherwise, anything with same or lower harvest level should be okay
            int level1 = block1.getHarvestLevel(state1);
            int level2 = block2.getHarvestLevel(state2);
            return level1 >= level2 || level2 == 0;
        }
    }

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, value = Dist.CLIENT)
    final class HighlightHandler {
        private HighlightHandler() {}

        @SubscribeEvent
        public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
            ActiveRenderInfo info = event.getInfo();
            Entity entity = info.getRenderViewEntity();
            if (!(entity instanceof PlayerEntity)) return;

            PlayerEntity player = (PlayerEntity) entity;

            RayTraceResult rt = event.getTarget();

            if (event.getSubID() == 0 && rt.getType() == RayTraceResult.Type.BLOCK) {
                ItemStack stack = player.getHeldItemMainhand();

                if (stack.getItem() instanceof IAOETool) {
                    World world = player.getEntityWorld();
                    IAOETool item = (IAOETool) stack.getItem();

                    for (BlockPos pos : item.getExtraBlocks(world, (BlockRayTraceResult) rt, player, stack)) {
                        event.getContext().drawSelectionBox(info, new BlockRayTraceResult(Vec3d.ZERO, Direction.UP, pos, false), 0);
                    }
                }
            }
        }
    }
}
