package net.silentchaos512.gear.block.press;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.recipe.press.PressingRecipe;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;

public class MetalPressTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity {
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);

    @SyncVariable(name = "Progress")
    private int progress = 0;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass") private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return progress;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    progress = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public MetalPressTileEntity() {
        super(ModTileEntities.METAL_PRESS.get(), 2);
    }

    @Nullable
    public PressingRecipe getRecipe() {
        if (level == null || getItem(0).isEmpty()) {
            return null;
        }
        return level.getRecipeManager().getRecipeFor(ModRecipes.PRESSING_TYPE, this, level).orElse(null);
    }

    private ItemStack getWorkOutput(@Nullable PressingRecipe recipe) {
        if (recipe != null) {
            return recipe.assemble(this);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        PressingRecipe recipe = getRecipe();
        if (recipe != null) {
            doWork(recipe);
        } else {
            stopWork();
        }
    }

    private void doWork(PressingRecipe recipe) {
        assert level != null;

        ItemStack current = getItem(1);
        ItemStack output = getWorkOutput(recipe);

        if (!current.isEmpty()) {
            int newCount = current.getCount() + output.getCount();

            if (!InventoryUtils.canItemsStack(current, output) || newCount > output.getMaxStackSize()) {
                // Output items do not match or not enough room
                stopWork();
                return;
            }
        }

        if (progress < WORK_TIME) {
            ++progress;
        }

        if (progress >= WORK_TIME && !level.isClientSide) {
            finishWork(recipe, current);
        }

        sendUpdate(this.getBlockState().setValue(MetalPressBlock.LIT, true));
    }

    private void stopWork() {
        progress = 0;
        sendUpdate(this.getBlockState().setValue(MetalPressBlock.LIT, false));
    }

    private void finishWork(PressingRecipe recipe, ItemStack current) {
        ItemStack output = getWorkOutput(recipe);
        if (!current.isEmpty()) {
            current.grow(output.getCount());
        } else {
            setItem(1, output);
        }

        progress = 0;
        removeItem(0, 1);
    }

    private void sendUpdate(BlockState newState) {
        if (level == null) return;
        BlockState oldState = level.getBlockState(worldPosition);
        if (oldState != newState) {
            level.setBlock(worldPosition, newState, 3);
            level.sendBlockUpdated(worldPosition, oldState, newState, 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == 1;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return TextUtil.translate("container", "metal_press");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new MetalPressContainer(id, player, this, this.fields);
    }

    void encodeExtraData(PacketBuffer buffer) {
        buffer.writeByte(this.fields.getCount());
    }
}
