package net.silentchaos512.gear.block.press;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.recipe.press.PressingRecipe;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;

public class MetalPressBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);

    private final RecipeManager.CachedCheck<Container, PressingRecipe> quickCheck;

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress = 0;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass") private final ContainerData fields = new ContainerData() {
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

    public MetalPressBlockEntity(BlockPos pos, BlockState state) {
        super(SgBlockEntities.METAL_PRESS.get(), pos, state);
        this.quickCheck = RecipeManager.createCheck(SgRecipes.PRESSING_TYPE.get());
    }

    @Nullable
    public PressingRecipe getRecipe() {
        if (level == null || getItem(0).isEmpty()) {
            return null;
        }
        var holder = quickCheck.getRecipeFor(this, level).orElse(null);
        if (holder != null) {
            return holder.value();
        }
        return null;
    }

    private ItemStack getWorkOutput(@Nullable PressingRecipe recipe, RegistryAccess registryAccess) {
        if (recipe != null) {
            return recipe.assemble(this, registryAccess);
        }
        return ItemStack.EMPTY;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MetalPressBlockEntity blockEntity) {
        PressingRecipe recipe = blockEntity.getRecipe();
        if (recipe != null) {
            blockEntity.doWork(recipe, level.registryAccess());
        } else {
            blockEntity.stopWork();
        }
    }

    private void doWork(PressingRecipe recipe, RegistryAccess registryAccess) {
        assert level != null;

        ItemStack current = getItem(1);
        ItemStack output = getWorkOutput(recipe, registryAccess);

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
            finishWork(recipe, registryAccess, current);
        }

        sendUpdate(this.getBlockState().setValue(MetalPressBlock.LIT, true));
    }

    private void stopWork() {
        progress = 0;
        sendUpdate(this.getBlockState().setValue(MetalPressBlock.LIT, false));
    }

    private void finishWork(PressingRecipe recipe, RegistryAccess registryAccess, ItemStack current) {
        ItemStack output = getWorkOutput(recipe, registryAccess);
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
    protected Component getDefaultName() {
        return TextUtil.translate("container", "metal_press");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new MetalPressContainer(id, player, this, this.fields);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemstack = this.items.get(pSlot);
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, pStack);
        this.items.set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot < this.items.size() - 1 && !flag) {
            this.progress = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void fillStackedContents(StackedContents pContents) {
        for (ItemStack stack : this.items) {
            pContents.accountStack(stack);
        }
    }

    @Override
    public void load(CompoundTag tags) {
        super.load(tags);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tags, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
    }
}
