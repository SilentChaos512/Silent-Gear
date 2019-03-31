package net.silentchaos512.gear.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.GearHelper;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ToolBlockPlaceHandler {
    private ToolBlockPlaceHandler() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        // Is the item allowed to place blocks?
        if (stack.isEmpty() || !canToolPlaceBlock(stack)) return;

        EntityPlayer player = event.getEntityPlayer();
        if (!player.isSneaking()) return;

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing side = event.getFace();
        if (side == null) return;

        ItemStack stackOffHand = player.getHeldItemOffhand();
        Vec3d hit = event.getHitVec();
        ItemUseContext context = new ItemUseContext(player, stackOffHand, pos, side, (float) hit.x, (float) hit.y, (float) hit.z);

        // If block is in offhand, allow that to place instead.
        if (!stackOffHand.isEmpty() && stackOffHand.getItem() instanceof ItemBlock && !canPlaceBlockAt(context)) {
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
                if (itemNotPlaceable(nextStack)) {
                    return;
                }
            }
        }

        Item item = nextStack.getItem();
        BlockPos targetPos = pos.offset(side);

        // Check for block overlap with player, if necessary
        if (item instanceof ItemBlock) {
            int px = targetPos.getX();
            int py = targetPos.getY();
            int pz = targetPos.getZ();
            AxisAlignedBB blockBounds = new AxisAlignedBB(px, py, pz, px + 1, py + 1, pz + 1);
            AxisAlignedBB playerBounds = player.getBoundingBox();
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.getBlock();
            IBlockState state = block.getDefaultState();
            if (state.getMaterial().blocksMovement() && playerBounds.intersects(blockBounds))
                return;
        }

        int prevSize = nextStack.getCount();
        ItemUseContext context1 = new ItemUseContext(player, nextStack, pos, side, (float) hit.x, (float) hit.y, (float) hit.z);
        EnumActionResult result = useItemAsPlayer(context1);
        if (result == EnumActionResult.SUCCESS) {
            player.swingArm(EnumHand.MAIN_HAND);
        }

        // Don't consume blocks in creative mode
        if (player.abilities.isCreativeMode) {
            nextStack.setCount(prevSize);
        }
        // Remove empty stacks
        if (nextStack.isEmpty()) {
            player.inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
        }
    }

    private static EnumActionResult useItemAsPlayer(ItemUseContext context) {
        return context.getItem().onItemUse(context);
    }

    private static boolean canToolPlaceBlock(ItemStack stack) {
        // Ignore broken tools
        if (stack.getItem() instanceof ICoreItem && GearHelper.isBroken(stack)) return false;
        return Config.GENERAL.isPlacerTool(stack);
    }

    private static boolean canPlaceBlockAt(ItemUseContext context) {
        EntityPlayer player = context.getPlayer();
        if (player == null) return false;

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        EnumFacing side = context.getFace();
        ItemStack usedStack = context.getItem();

        if (usedStack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) usedStack.getItem()).getBlock();

            BlockItemUseContext blockContext = new BlockItemUseContext(context);
            if (block.getDefaultState().isReplaceable(blockContext)) {
                pos = pos.offset(side);
            }
        }

        // FIXME?
        return player.canPlayerEdit(pos, side, usedStack) /*&& world.mayPlace(block, pos, false, side, player)*/;
    }

    private static boolean itemNotPlaceable(ItemStack stack) {
        return stack.isEmpty()
                || (stack.hasTag() && stack.getOrCreateTag().contains("NoPlacing"))
                || (!(stack.getItem() instanceof ItemBlock) && !Config.GENERAL.isPlaceableItem(stack));
    }
}
