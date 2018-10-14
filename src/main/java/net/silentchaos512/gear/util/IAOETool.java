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
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IAOETool {
    /**
     * The tool class of the item (pickaxe, shovel, axe)
     */
    @Nonnull
    String getAOEToolClass();

    /**
     * Call the item's rayTrace method inside this.
     */
    @Nullable
    RayTraceResult rayTraceBlocks(World world, EntityPlayer player);

    default List<BlockPos> getExtraBlocks(World world, @Nullable RayTraceResult rt, EntityPlayer player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (player.isSneaking() || rt == null || rt.getBlockPos() == null || rt.sideHit == null)
            return positions;

        BlockPos pos = rt.getBlockPos();
        IBlockState state = world.getBlockState(pos);

        if (isEffectiveOnBlock(stack, world, pos, state)) {
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
//        SilentGear.log.debug("{}", positions);
        return positions;
    }

    default boolean isEffectiveOnBlock(ItemStack stack, World world, BlockPos pos, IBlockState state) {
        // The Forge.canToolHarvestBlock method seems to be very unreliable...
        return stack.getItem().canHarvestBlock(state, stack) || ForgeHooks.canToolHarvestBlock(world, pos, stack);
    }

    default void attemptAddExtraBlock(World world, IBlockState state1, BlockPos pos2, ItemStack stack, List<BlockPos> list) {
        final IBlockState state2 = world.getBlockState(pos2);
        if (!world.isAirBlock(pos2)
                && BreakHandler.areBlocksSimilar(state1, state2)
                && (state2.getBlock().isToolEffective(getAOEToolClass(), state2) || stack.getItem().canHarvestBlock(state2, stack))) {
            list.add(pos2);
        }
    }

    enum MatchMode {
        LOOSE, MODERATE, STRICT
    }

    /**
     * Handles actual AOE block breaking. Call {@link #onBlockStartBreak(ItemStack, BlockPos,
     * EntityPlayer)} inside the {@code onBlockStartBreak} method of the tool's item.
     */
    final class BreakHandler {
        private BreakHandler() {}

        public static boolean onBlockStartBreak(ItemStack tool, BlockPos pos, EntityPlayer player) {
            World world = player.getEntityWorld();
            if (world.isRemote || !(player instanceof EntityPlayerMP) || !(tool.getItem() instanceof IAOETool))
                return false;

            IAOETool item = (IAOETool) tool.getItem();
            RayTraceResult rt = item.rayTraceBlocks(world, player);
            IBlockState stateOriginal = world.getBlockState(pos);

            if (rt != null && rt.typeOfHit == RayTraceResult.Type.BLOCK && item.isEffectiveOnBlock(tool, world, pos, stateOriginal)) {
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

        private static final Set<Block> ORE_BLOCKS = new HashSet<>();

        public static void buildOreBlocksSet() {
            ORE_BLOCKS.clear();

            for (Block block : ForgeRegistries.BLOCKS) {
                if (block instanceof BlockOre) {
                    ORE_BLOCKS.add(block);
                } else {
                    ItemStack blockStack = new ItemStack(block);
                    for (String oreName : StackHelper.getOreNames(blockStack)) {
                        if (oreName.startsWith("ore")) {
                            ORE_BLOCKS.add(block);
                            break;
                        }
                    }
                }
            }

            SilentGear.log.info("IAOETool: Rebuilt ore block set, contains {} items", ORE_BLOCKS.size());
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
        static boolean areBlocksSimilar(IBlockState state1, IBlockState state2) {
            Block block1 = state1.getBlock();
            Block block2 = state2.getBlock();
            boolean isOre1 = ORE_BLOCKS.contains(block1);
            boolean isOre2 = ORE_BLOCKS.contains(block2);
            MatchMode mode = isOre1 && isOre2 ? Config.aoeToolOreMode : Config.aoeToolMatchMode;

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

    @Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, value = Side.CLIENT)
    final class HighlightHandler {
        private HighlightHandler() {}

        @SubscribeEvent
        public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
            EntityPlayer player = event.getPlayer();

            if (player != null && event.getSubID() == 0 && event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
                ItemStack stack = player.getHeldItemMainhand();

                if (stack.getItem() instanceof IAOETool) {
                    World world = player.getEntityWorld();
                    IAOETool item = (IAOETool) stack.getItem();

                    for (BlockPos pos : item.getExtraBlocks(world, event.getTarget(), player, stack)) {
                        event.getContext().drawSelectionBox(player, new RayTraceResult(Vec3d.ZERO, EnumFacing.UP, pos), 0, event.getPartialTicks());
                    }
                }
            }
        }
    }
}
