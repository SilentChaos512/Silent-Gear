package net.silentchaos512.gear.block.paintmixer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.SgContainerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipe;
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
    public ItemStackHandler createItemHandler() {
        return new ItemStackHandler(INVENTORY_SIZE);
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

    public static <R extends AlloyRecipe> void tick(Level level, BlockPos pos, BlockState state, PaintMixerBlockEntity blockEntity) {
        if (blockEntity.areInputsEmpty()) {
            // No point in doing anything when input slots are empty
            blockEntity.paintColor = 0;
            return;
        }

        var newColor = PaintUtils.getBlendedColor(blockEntity);
        blockEntity.paintColor = newColor.isPresent() ? newColor.getAsInt() : 0;
        blockEntity.doWork(level);
    }

    private void doWork(Level level) {
        ItemStack current = getItem(OUTPUT_SLOT_INDEX);
        ItemStack output = getWorkOutput();

        if (!current.isEmpty()) {
            int newCount = current.getCount() + output.getCount();

            if (!ItemStack.isSameItemSameComponents(current, output) || newCount > output.getMaxStackSize()) {
                // Output items do not match or not enough room
                stopWork();
                return;
            }
        }

        if (workEnabled) {
            if (progress < WORK_TIME) {
                ++progress;
            }

            if (progress >= WORK_TIME && !level.isClientSide) {
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
        for (int i = 0; i < INPUT_SLOT_COUNT; ++i) {
            removeItem(i, 1);
        }

        ItemStack output = getWorkOutput();
        if (!current.isEmpty()) {
            current.grow(output.getCount());
        } else {
            setItem(OUTPUT_SLOT_INDEX, output);
        }
    }

    private ItemStack getWorkOutput() {
        int count = getValidInputCount();
        var result = new ItemStack(SgItems.PAINT.get(), count);
        result.set(SgDataComponents.PAINT_COLOR, this.paintColor);
        return result;
    }

    @Override
    public void loadAdditional(CompoundTag tags, HolderLookup.Provider provider) {
        super.loadAdditional(tags, provider);
        this.progress = tags.getInt("Progress");
        this.workEnabled = tags.getBoolean("WorkEnabled");
    }

    @Override
    public void saveAdditional(CompoundTag tags, HolderLookup.Provider provider) {
        super.saveAdditional(tags, provider);
        tags.putInt("Progress", this.progress);
        tags.putBoolean("WorkEnabled", this.workEnabled);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tags = super.getUpdateTag(provider);
        tags.putInt("Progress", this.progress);
        tags.putBoolean("WorkEnabled", this.workEnabled);
        return tags;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        super.onDataPacket(net, packet, provider);
        CompoundTag tags = packet.getTag();
        if (tags != null) {
            this.progress = tags.getInt("Progress");
            this.workEnabled = tags.getBoolean("WorkEnabled");
        }
    }
}
