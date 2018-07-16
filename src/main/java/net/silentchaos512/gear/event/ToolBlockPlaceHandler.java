package net.silentchaos512.gear.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.gear.api.item.IBlockPlacer;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.ItemHelper;

public class ToolBlockPlaceHandler {

    public static final ToolBlockPlaceHandler INSTANCE = new ToolBlockPlaceHandler();

    private ToolBlockPlaceHandler() {
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        // Is the item allowed to place blocks?
        if (stack.isEmpty() || !canToolPlaceBlock(stack)) return;

        EntityPlayer player = event.getEntityPlayer();
        if (!player.isSneaking()) return; // Not working?

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing side = event.getFace();
        ItemStack stackOffHand = player.getHeldItemOffhand();

        // If block is in offhand, allow that to place instead.
        if (!stackOffHand.isEmpty() && stackOffHand.getItem() instanceof ItemBlock && side != null
                && !canPlaceBlockAt(stackOffHand, player, world, pos, side)) {
            return;
        }

        int toolSlot = player.inventory.currentItem;
        int itemSlot = toolSlot + 1;
        ItemStack nextStack = ItemStack.EMPTY;
        ItemStack lastStack = player.inventory.getStackInSlot(8);

        if (toolSlot < 8) {
            // Get stack in slot after tool
            nextStack = player.inventory.getStackInSlot(itemSlot);
            // If there's nothing there we can use, try slot 9 instead
            if (itemNotPlaceable(nextStack)) {
                nextStack = lastStack;
                itemSlot = 8;
                // And is that stack placeable?
                if (itemNotPlaceable(nextStack)) return;
            }
        }

        Item item = nextStack.getItem();
        BlockPos targetPos = side != null ? pos.offset(side) : pos;

        // Check for block overlap with player, if necessary
        if (item instanceof ItemBlock) {
            int px = targetPos.getX();
            int py = targetPos.getY();
            int pz = targetPos.getZ();
            AxisAlignedBB blockBounds = new AxisAlignedBB(px, py, pz, px + 1, py + 1, pz + 1);
            AxisAlignedBB playerBounds = player.getEntityBoundingBox();
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.getBlock();
            IBlockState state = block.getStateFromMeta(itemBlock.getMetadata(nextStack));
            if (state.getMaterial().blocksMovement() && playerBounds.intersects(blockBounds)) return;
        }

        int prevSize = nextStack.getCount();
        Vec3d hit = event.getHitVec();
        EnumActionResult result = ItemHelper.useItemAsPlayer(nextStack, player, world, pos, side, (float) hit.x, (float) hit.y, (float) hit.z);
        if (result == EnumActionResult.SUCCESS) player.swingArm(EnumHand.MAIN_HAND);

        // Don't consume blocks in creative mode
        if (player.capabilities.isCreativeMode) nextStack.setCount(prevSize);
        // Remove empty stacks
        if (nextStack.isEmpty()) {
            nextStack = ItemStack.EMPTY;
            player.inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
        }
    }

    private boolean canToolPlaceBlock(ItemStack stack) {
        Item item = stack.getItem();
        // Ignore broken tools
        if (item instanceof ICoreItem && GearHelper.isBroken(stack)) return false;
        return Config.blockPlacerTools.matches(item);
    }

    private boolean canPlaceBlockAt(ItemStack blockStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        Block block = ((ItemBlock) blockStack.getItem()).getBlock();
        if (block.isReplaceable(world, pos)) pos = pos.offset(side);
        return player.canPlayerEdit(pos, side, blockStack) && world.mayPlace(block, pos, false, side, player);
    }

    private boolean itemNotPlaceable(ItemStack stack) {
        return stack.isEmpty()
                || (stack.hasTagCompound() && stack.getTagCompound().hasKey("NoPlacing"))
                || (!(stack.getItem() instanceof ItemBlock) && !(stack.getItem() instanceof IBlockPlacer));
    }
}
