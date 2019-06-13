package net.silentchaos512.gear.event;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
        // FIXME: How to get raytrace?
/*        ItemStack stack = event.getItemStack();
        // Is the item allowed to place blocks?
        if (stack.isEmpty() || !canToolPlaceBlock(stack)) return;

        PlayerEntity player = event.getEntityPlayer();
        if (!player.isSneaking()) return;

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Direction side = event.getFace();
        if (side == null) return;

        ItemStack stackOffHand = player.getHeldItemOffhand();
        ItemUseContext context = new ItemUseContext(player, Hand.OFF_HAND, new BlockRayTraceResult());

        // If block is in offhand, allow that to place instead.
        if (!stackOffHand.isEmpty() && stackOffHand.getItem() instanceof BlockItem && !canPlaceBlockAt(context)) {
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
        if (item instanceof BlockItem) {
            int px = targetPos.getX();
            int py = targetPos.getY();
            int pz = targetPos.getZ();
            AxisAlignedBB blockBounds = new AxisAlignedBB(px, py, pz, px + 1, py + 1, pz + 1);
            AxisAlignedBB playerBounds = player.getBoundingBox();
            BlockItem blockItem = (BlockItem) item;
            Block block = blockItem.getBlock();
            BlockState state = block.getDefaultState();
            if (state.getMaterial().blocksMovement() && playerBounds.intersects(blockBounds))
                return;
        }

        int prevSize = nextStack.getCount();
        ItemUseContext context1 = new FakeItemUseContext(context, nextStack);
        ActionResultType result = useItemAsPlayer(context1);
        if (result == ActionResultType.SUCCESS) {
            player.swingArm(Hand.MAIN_HAND);
        }

        // Don't consume blocks in creative mode
        if (player.abilities.isCreativeMode) {
            nextStack.setCount(prevSize);
        }
        // Remove empty stacks
        if (nextStack.isEmpty()) {
            player.inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
        }*/
    }

    private static ActionResultType useItemAsPlayer(ItemUseContext context) {
        return context.getItem().onItemUse(context);
    }

    private static boolean canToolPlaceBlock(ItemStack stack) {
        // Ignore broken tools
        if (stack.getItem() instanceof ICoreItem && GearHelper.isBroken(stack)) return false;
        return Config.GENERAL.isPlacerTool(stack);
    }

    private static boolean canPlaceBlockAt(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return false;

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        ItemStack usedStack = context.getItem();

        if (usedStack.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) usedStack.getItem()).getBlock();

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
                || (!(stack.getItem() instanceof BlockItem) && !Config.GENERAL.isPlaceableItem(stack));
    }
}
