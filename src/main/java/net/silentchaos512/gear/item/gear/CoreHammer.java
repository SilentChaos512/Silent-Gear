package net.silentchaos512.gear.item.gear;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CoreHammer extends CorePickaxe {

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.hammer;
    }

    @Override
    public String getGearClass() {
        return "hammer";
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        World world = player.getEntityWorld();

        if (!world.isRemote && player instanceof EntityPlayerMP) {

            RayTraceResult rt = this.rayTrace(world, player, false);
            if (rt.typeOfHit == RayTraceResult.Type.BLOCK) {
                EnumFacing side = rt.sideHit;

                List<BlockPos> extraBlocks = getExtraBlocks(world, rt, player, itemstack);

                for (BlockPos pos2 : extraBlocks) {

                    IBlockState state = world.getBlockState(pos2);

                    if (!world.isBlockLoaded(pos2) || !player.canPlayerEdit(pos2, side, itemstack) || !(state.getBlock().canHarvestBlock(world, pos2, player))) {
                        continue;
                    }

                    if (player.capabilities.isCreativeMode) {
                        state.getBlock().onBlockHarvested(world, pos2, state, player);
                        if (state.getBlock().removedByPlayer(state, world, pos2, player, false)) {
                            state.getBlock().onPlayerDestroy(world, pos2, state);
                        }
                    } else {
                        int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos2);

                        state.getBlock().onBlockHarvested(world, pos2, state, player);
                        this.onBlockDestroyed(itemstack, world, state, pos2, player);
                        if (state.getBlock().removedByPlayer(state, world, pos2, player, true)) {
                            state.getBlock().onPlayerDestroy(world, pos2, state);
                            state.getBlock().harvestBlock(world, player, pos2, state, world.getTileEntity(pos2), itemstack);
                            state.getBlock().dropXpOnBlockBreak(world, pos2, xp);
                        }
                    }

                    world.playEvent(2001, pos, Block.getStateId(state));
                    ((EntityPlayerMP) player).connection.sendPacket(new SPacketBlockChange(world, pos));

                }

            }

        }

        return false;
    }

    public RayTraceResult rayTraceBlocks(World world, EntityPlayer player) {
        return this.rayTrace(world, player, false);
    }

    public List<BlockPos> getExtraBlocks(World world, RayTraceResult rt, EntityPlayer player, ItemStack stack) {
        List<BlockPos> positions = new ArrayList<>();

        if (rt == null || rt.getBlockPos() == null || rt.sideHit == null) {
            return positions;
        }

        if (player.isSneaking()) {
            return positions;
        }

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

    protected void attemptAddExtraBlock(World world, IBlockState state1, BlockPos pos2, ItemStack stack, List<BlockPos> list) {
        IBlockState state2 = world.getBlockState(pos2);

        if (world.isAirBlock(pos2)) {
            return;
        }
        if (state2.getBlock() != state1.getBlock()) {
            if (!((state2.getBlock() == Blocks.LIT_REDSTONE_ORE || state2.getBlock() == Blocks.REDSTONE_ORE) && (state1.getBlock() == Blocks.LIT_REDSTONE_ORE || state1.getBlock() == Blocks.REDSTONE_ORE))) {
                return;
            }
        }
        if (!state2.getBlock().isToolEffective("pickaxe", state2) && !canHarvestBlock(state2, stack)) {
            return;
        }

        list.add(pos2);
    }

    public static class HammerEvents {

        public static final HammerEvents INSTANCE = new HammerEvents();

        private HammerEvents() {
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
            EntityPlayer player = event.getPlayer();
            if (player == null)
                return;

            if (event.getSubID() == 0 && event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
                ItemStack stack = player.getHeldItemMainhand();

                if (stack.getItem() == ModItems.hammer) {
                    World world = player.getEntityWorld();
                    List<BlockPos> positions = ModItems.hammer.getExtraBlocks(world, event.getTarget(), player, stack);

                    for (BlockPos pos : positions)
                        event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());
                }
            }
        }
    }
}
