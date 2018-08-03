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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface IAOETool {
    /**
     * The tool class of the item (pickaxe, shovel, axe)
     */
    @Nonnull
    String getAOEToolClass();

    /**
     * Call the item's rayTrace method inside this.
     */
    @Nonnull
    RayTraceResult rayTraceBlocks(World world, EntityPlayer player);

    default List<BlockPos> getExtraBlocks(World world, RayTraceResult rt, EntityPlayer player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (player.isSneaking() || rt == null || rt.getBlockPos() == null || rt.sideHit == null)
            return positions;

        BlockPos pos = rt.getBlockPos();
        IBlockState state = world.getBlockState(pos);

        if (ForgeHooks.canToolHarvestBlock(world, pos, stack)) {
            switch (rt.sideHit.getAxis()) {
                case Y:
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.NORTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.EAST).offset(EnumFacing.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.WEST).offset(EnumFacing.NORTH), stack, positions);
                    break;
                case X:
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.NORTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.DOWN), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.NORTH).offset(EnumFacing.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.UP).offset(EnumFacing.SOUTH), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH), stack, positions);
                    break;
                case Z:
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.DOWN), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.DOWN).offset(EnumFacing.EAST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.EAST).offset(EnumFacing.UP), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.UP).offset(EnumFacing.WEST), stack, positions);
                    attemptAddExtraBlock(world, state, pos.offset(EnumFacing.WEST).offset(EnumFacing.DOWN), stack, positions);
                    break;
            }
        }
        return positions;
    }

    default void attemptAddExtraBlock(World world, IBlockState state1, BlockPos pos2, ItemStack stack, List<BlockPos> list) {
        IBlockState state2 = world.getBlockState(pos2);

        if (world.isAirBlock(pos2))
            return;
        Block block1 = state1.getBlock();
        Block block2 = state2.getBlock();
        if (!BreakHandler.areBlocksSimilar(block1, block2))
            return;
        if (!block2.isToolEffective(getAOEToolClass(), state2) && !stack.getItem().canHarvestBlock(state2, stack))
            return;

        list.add(pos2);
    }

    /**
     * Handles actual AOE block breaking. Call {@link #onBlockStartBreak(ItemStack, BlockPos,
     * EntityPlayer)} inside the {@code onBlockStartBreak} method of the tool's item.
     */
    class BreakHandler {
        public static boolean onBlockStartBreak(ItemStack tool, BlockPos pos, EntityPlayer player) {
            World world = player.getEntityWorld();
            if (world.isRemote || !(player instanceof EntityPlayerMP) || !(tool.getItem() instanceof IAOETool))
                return false;

            IAOETool item = (IAOETool) tool.getItem();
            RayTraceResult rt = item.rayTraceBlocks(world, player);

            if (rt != null && rt.typeOfHit == RayTraceResult.Type.BLOCK) {
                EnumFacing side = rt.sideHit;
                List<BlockPos> extraBlocks = item.getExtraBlocks(world, rt, player, tool);

                for (BlockPos pos2 : extraBlocks) {
                    IBlockState state = world.getBlockState(pos2);
                    if (!world.isBlockLoaded(pos2) || !player.canPlayerEdit(pos2, side, tool) || !(state.getBlock().canHarvestBlock(world, pos2, player)))
                        continue;

                    if (player.capabilities.isCreativeMode) {
                        state.getBlock().onBlockHarvested(world, pos2, state, player);
                        if (state.getBlock().removedByPlayer(state, world, pos2, player, false))
                            state.getBlock().onPlayerDestroy(world, pos2, state);
                    } else {
                        int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos2);
                        state.getBlock().onBlockHarvested(world, pos2, state, player);
                        tool.getItem().onBlockDestroyed(tool, world, state, pos2, player);
                        if (state.getBlock().removedByPlayer(state, world, pos2, player, true)) {
                            state.getBlock().onPlayerDestroy(world, pos2, state);
                            state.getBlock().harvestBlock(world, player, pos2, state, world.getTileEntity(pos2), tool);
                            state.getBlock().dropXpOnBlockBreak(world, pos2, xp);
                        }
                    }

                    world.playEvent(2001, pos, Block.getStateId(state));
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));
                }
            }
            return false;
        }

        /**
         * Determine if the blocks are similar enough to be considered the same. For example, dirt
         * and grass, or redstone ore and lit redstone ore.
         *
         * @return True if the blocks are the same (equal) or similar, false otherwise
         */
        static boolean areBlocksSimilar(Block block1, Block block2) {
            if (block1 == block2) return true;
            return compareBlocksSimilar(block1, block2, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE)
                    || compareBlocksSimilar(block1, block2, Blocks.DIRT, Blocks.GRASS);
        }

        // This just makes areBlocksSimilar easier to read
        private static boolean compareBlocksSimilar(Block block1, Block block2, Block possible1, Block possible2) {
            return (block1 == possible1 || block1 == possible2) && (block2 == possible1 || block2 == possible2);
        }
    }

    class HighlightHandler {
        @SubscribeEvent
        public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
            EntityPlayer player = event.getPlayer();
            if (player == null)
                return;

            if (event.getSubID() == 0 && event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
                ItemStack stack = player.getHeldItemMainhand();

                if (stack.getItem() instanceof IAOETool) {
                    World world = player.getEntityWorld();
                    List<BlockPos> positions = ((IAOETool) stack.getItem()).getExtraBlocks(world, event.getTarget(), player, stack);

                    for (BlockPos pos : positions)
                        event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());
                }
            }
        }
    }
}
