package net.silentchaos512.gear.block.paintmixer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.SgContainerBlockEntity;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.lib.util.TimeUtils;

public class PaintMixerBlockEntity extends SgContainerBlockEntity {
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);
    public static final int INVENTORY_SIZE = 9;
    public static final int INPUT_SLOT_COUNT = INVENTORY_SIZE - 1;
    public static final int OUTPUT_SLOT_INDEX = INVENTORY_SIZE - 1;
    public static final int DATA_COUNT = 3;

    private int progress = 0;
    private boolean workEnabled = true;
    private int paintColor = 0;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass")
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> workEnabled ? 1 : 0;
                case 2 -> paintColor;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    progress = value;
                    break;
                case 1:
                    workEnabled = value != 0;
                    break;
                case 2:
                    paintColor = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public PaintMixerBlockEntity(BlockPos pos, BlockState state) {
        super(SgBlockEntities.PAINT_MIXER.get(), pos, state, INVENTORY_SIZE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.silentgear.paint_mixer");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PaintMixerMenu(containerId, inventory, this, this.data);
    }

    private boolean areInputsEmpty() {
        for (int i = 0; i < INPUT_SLOT_COUNT; ++i) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private int getValidInputCount() {
        int count = 0;
        for (int i = 0; i < INPUT_SLOT_COUNT; ++i) {
            var stack = getItem(i);
            if (!stack.isEmpty()) {
                if (PaintUtils.isPaintMixerInput(stack)) {
                    ++count;
                } else {
                    return -1;
                }
            }
        }
        return count;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PaintMixerBlockEntity blockEntity) {
        if (blockEntity.areInputsEmpty()) {
            // No point in doing anything when input slots are empty
            blockEntity.paintColor = 0;
            return;
        }

        if (blockEntity.canWork()) {
            var newColor = PaintUtils.getBlendedColor(blockEntity);
            blockEntity.paintColor = newColor.isPresent() ? newColor.getAsInt() : 0;
            blockEntity.doWork(level);
        }
    }

    private boolean canWork() {
        // true if any item provides a paint color
        for (int i = 0; i < INPUT_SLOT_COUNT; ++i) {
            if (PaintUtils.isPaintMixerColorInput(getItem(i))) {
                return true;
            }
        }
        return false;
    }

    private void doWork(Level level) {
        ItemStack current = getItem(OUTPUT_SLOT_INDEX);
        ItemStack output = getWorkOutput();

        if (!current.isEmpty()) {
            int newCount = current.getCount() + output.getCount();

            if (!ItemStack.matches(current, output) || newCount > output.getMaxStackSize()) {
                // Output items do not match or not enough room
                stopWork();
                return;
            }
        }

        if (workEnabled) {
            if (progress < WORK_TIME) {
                ++progress;
            }

            if (progress >= WORK_TIME && !level.isClientSide()) {
                finishWork(current);
            }
        } else {
            stopWork();
        }
    }

    private void stopWork() {
        progress = 0;
    }

    private void finishWork(ItemStack current) {
        progress = 0;

        ItemStack output = getWorkOutput();
        if (!current.isEmpty()) {
            current.grow(output.getCount());
        } else {
            setItem(OUTPUT_SLOT_INDEX, output);
        }

        for (int i = 0; i < INPUT_SLOT_COUNT; ++i) {
            removeItem(i, 1);
        }
    }

    private ItemStack getWorkOutput() {
        int count = getValidInputCount();
        var result = new ItemStack(SgItems.PAINT.get(), count);
        result.set(SgDataComponents.PAINT_COLOR, this.paintColor);
        return result;
    }

    @Override
    public NonNullList<ItemStack> createInternalItemList() {
        return NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= 0 && slot < INVENTORY_SIZE - 1 && PaintUtils.isPaintMixerInput(stack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.progress = input.getIntOr("Progress", 0);
        this.workEnabled = input.getBooleanOr("WorkEnabled", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Progress", this.progress);
        output.putBoolean("WorkEnabled", this.workEnabled);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tags = super.getUpdateTag(provider);
        tags.putInt("Progress", this.progress);
        tags.putBoolean("WorkEnabled", this.workEnabled);
        return tags;
    }

    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
        this.progress = valueInput.getIntOr("Progress", 0);
        this.workEnabled = valueInput.getBooleanOr("WorkEnabled", false);
    }
}
