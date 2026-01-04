package net.silentchaos512.gear.block.press;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.SgContainerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.press.PressingRecipe;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;

public class MetalPressBlockEntity extends SgContainerBlockEntity {
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);

    private final RecipeManager.CachedCheck<SingleRecipeInput, PressingRecipe> quickCheck;

    private int progress = 0;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass") private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return progress;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                progress = value;
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
    public PressingRecipe getRecipe(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return null;
        }
        if (!(level instanceof ServerLevel serverLevel)) return null;

        var holder = quickCheck.getRecipeFor(new SingleRecipeInput(stack), serverLevel).orElse(null);
        if (holder != null) {
            return holder.value();
        }
        return null;
    }

    private ItemStack getWorkOutput(@Nullable PressingRecipe recipe, RegistryAccess registryAccess) {
        if (recipe != null) {
            return recipe.assemble(new SingleRecipeInput(getItem(0)), registryAccess);
        }
        return ItemStack.EMPTY;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MetalPressBlockEntity blockEntity) {
        PressingRecipe recipe = blockEntity.getRecipe(blockEntity.getItem(0));
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

            if (!ItemStack.isSameItemSameComponents(current, output) || newCount > output.getMaxStackSize()) {
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
    public void setChanged() {
        this.progress = 0;
        super.setChanged();
    }

    @Override
    protected Component getDefaultName() {
        return TextUtil.translate("container", "metal_press");
    }

    @Override
    public NonNullList<ItemStack> createInternalItemList() {
        return new ItemStackHandler(2) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot == 0 && getRecipe(stack) != null;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 0) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new MetalPressContainer(id, player, this, this.fields);
    }
}
