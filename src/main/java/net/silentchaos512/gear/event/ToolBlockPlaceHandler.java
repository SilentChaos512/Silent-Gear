package net.silentchaos512.gear.event;

import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public final class ToolBlockPlaceHandler {
    private ToolBlockPlaceHandler() {}

    /*
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
        ItemStack stackOffHand = player.getHeldItemOffhand();
        Vec3d hit = event.getHitVec();
        ItemUseContext context = new ItemUseContext(player, stack, pos, side, (float) hit.x, (float) hit.y, (float) hit.z);

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
            AxisAlignedBB playerBounds = player.getBoundingBox();
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.getBlock();
            IBlockState state = block.getDefaultState();
            if (state.getMaterial().blocksMovement() && playerBounds.intersects(blockBounds))
                return;
        }

        int prevSize = nextStack.getCount();
        EnumActionResult result = useItemAsPlayerMainHand(nextStack, player, world, pos, side, (float) hit.x, (float) hit.y, (float) hit.z);
        if (result == EnumActionResult.SUCCESS) player.swingArm(EnumHand.MAIN_HAND);

        // Don't consume blocks in creative mode
        if (player.abilities.isCreativeMode) nextStack.setCount(prevSize);
        // Remove empty stacks
        if (nextStack.isEmpty()) {
            nextStack = ItemStack.EMPTY;
            player.inventory.setInventorySlotContents(itemSlot, ItemStack.EMPTY);
        }
    }

    private EnumActionResult useItemAsPlayerMainHand(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack currentEquipped = player.getHeldItemMainhand();
        // TODO: Is swapping items still necessary?
        player.setHeldItem(EnumHand.MAIN_HAND, stack);
        ItemUseContext context = new ItemUseContext(player, stack, pos, facing, hitX, hitY, hitZ);
        EnumActionResult result = stack.getItem().onItemUse(context);
        player.setHeldItem(EnumHand.MAIN_HAND, currentEquipped);
        return result;
    }

    private boolean canToolPlaceBlock(ItemStack stack) {
        Item item = stack.getItem();
        // Ignore broken tools
        if (item instanceof ICoreItem && GearHelper.isBroken(stack)) return false;
        return Config.blockPlacerTools.matches(item);
    }

    private boolean canPlaceBlockAt(ItemUseContext context) {
        EntityPlayer player = context.getPlayer();
        if (player == null) return false;

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        EnumFacing side = context.getFace();
        ItemStack blockStack = context.getItem();
        Block block = ((ItemBlock) blockStack.getItem()).getBlock();

        BlockItemUseContext blockContext = new BlockItemUseContext(context);
        if (block.getDefaultState().isReplaceable(blockContext)) pos = pos.offset(side);
        // FIXME?
        return player.canPlayerEdit(pos, side, blockStack) && world.mayPlace(block, pos, false, side, player);
    }

    private boolean itemNotPlaceable(ItemStack stack) {
        return stack.isEmpty()
                || (stack.hasTag() && stack.getOrCreateTag().hasKey("NoPlacing"))
                || (!(stack.getItem() instanceof ItemBlock) && !Config.itemsThatToolsCanUse.matches(stack.getItem()));
    }
    */
}
