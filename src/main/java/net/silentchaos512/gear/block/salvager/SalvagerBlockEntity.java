package net.silentchaos512.gear.block.salvager;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.SgContainerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.lib.util.MathUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.IntStream;

public class SalvagerBlockEntity extends SgContainerBlockEntity {
    static final int BASE_WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);
    private static final int INPUT_SLOT = 0;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT};
    private static final int[] SLOTS_OUTPUT = IntStream.rangeClosed(1, 18).toArray();
    private static final int[] SLOTS_ALL = IntStream.rangeClosed(0, 18).toArray();
    public static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;

    private final RecipeManager.CachedCheck<SingleRecipeInput, SalvagingRecipe> quickCheck;

    int progress = 0;

    private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            return progress;
        }

        @Override
        public void set(int index, int value) {
            progress = value;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public SalvagerBlockEntity(BlockPos pos, BlockState state) {
        super(SgBlockEntities.SALVAGER.get(), pos, state);
        this.quickCheck = RecipeManager.createCheck(SgRecipes.SALVAGING_TYPE.get());
    }

    @Override
    public ItemStackHandler createItemHandler() {
        return new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot == INPUT_SLOT && !stack.isEmpty();
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == INPUT_SLOT) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Nullable
    private SalvagingRecipe getRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) return null;
        var holder = quickCheck.getRecipeFor(new SingleRecipeInput(getInputItem()), level).orElse(null);
        if (holder != null) {
            return holder.value();
        }
        return null;
    }

    public ItemStack getInputItem() {
        return this.items.getStackInSlot(INPUT_SLOT);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SalvagerBlockEntity blockEntity) {
        ItemStack input = blockEntity.getItem(0);
        SalvagingRecipe recipe = blockEntity.getRecipe(input);
        if (recipe != null) {
            if (blockEntity.progress < BASE_WORK_TIME) {
                ++blockEntity.progress;
            }

            if (blockEntity.progress >= BASE_WORK_TIME && !blockEntity.isOutputFull()) {
                for (ItemStack stack : blockEntity.getSalvagedPartsWithChance(recipe, input)) {
                    int slot = blockEntity.getFreeOutputSlot();
                    if (slot > 0) {
                        blockEntity.setItem(slot, stack);
                    } else {
                        SilentGear.LOGGER.warn("Item lost in salvager: {}", stack);
                    }
                }

                blockEntity.progress = 0;
                input.shrink(1);
                if (input.isEmpty()) {
                    blockEntity.setItem(0, ItemStack.EMPTY);
                }
            }
        } else {
            blockEntity.progress = 0;
        }
    }

    private Collection<ItemStack> getSalvagedPartsWithChance(SalvagingRecipe recipe, ItemStack stack) {
        double lossRate = getLossRate(stack);
        SilentGear.LOGGER.debug("Loss rate for '{}': {}", stack, lossRate);
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (ItemStack part : recipe.getPossibleResults(this)) {
            ItemStack copy = part.copy();
            int count = copy.getCount();
            PartInstance partData = PartInstance.from(part);
            double partLossRate = partData != null && partData.isValid()
                    ? partData.get().getSalvageLossRate(partData, stack, lossRate)
                    : lossRate;

            for (int i = 0; i < count; ++i) {
                if (MathUtils.tryPercentage(SilentGear.RANDOM, partLossRate)) {
                    copy.shrink(1);
                }
            }

            if (!copy.isEmpty()) {
                builder.add(copy);
            }
        }
        return builder.build();
    }

    private static double getLossRate(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        double min = Config.Common.salvagerMinLossRate.get();
        if (maxDamage == 0) {
            return min;
        }
        double ratio = (double) stack.getDamageValue() / maxDamage;
        return min + ratio * (Config.Common.salvagerMaxLossRate.get() - min);
    }

    private int getFreeOutputSlot() {
        for (int slot : SLOTS_OUTPUT) {
            if (getItem(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private boolean isOutputFull() {
        for (int slot : SLOTS_OUTPUT) {
            if (getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tags = super.getUpdateTag(provider);
        tags.putInt("Progress", this.progress);
        return tags;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.isEmpty() || isOutputSlot(index)) {
            return false;
        }

        ItemStack current = getItem(index);
        if (!current.isEmpty() && !ItemStack.isSameItemSameComponents(stack, current)) {
            return false;
        }

        return isInputSlot(index) || super.canPlaceItem(index, stack);
    }

    private static boolean isInputSlot(int index) {
        for (int k : SLOTS_INPUT) {
            if (index == k) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOutputSlot(int index) {
        for (int k : SLOTS_OUTPUT) {
            if (index == k) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.silentgear.salvager");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new SalvagerContainer(id, playerInventory, this, fields);
    }

    @Override
    public void setChanged() {
        this.progress = 0;
        super.setChanged();
    }
}
