package net.silentchaos512.gear.block.compounder;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.IDroppableInventory;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@SuppressWarnings("WeakerAccess")
public class CompounderTileEntity<R extends CompoundingRecipe> extends LockableSidedInventoryTileEntity implements IDroppableInventory {
    public static final int STANDARD_INPUT_SLOTS = 4;
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);

    private final CompounderInfo<R> info;
    private final int[] allSlots;

    @SyncVariable(name = "Progress")
    private int progress = 0;
    @SyncVariable(name = "WorkEnabled")
    private boolean workEnabled = true;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass") private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return progress;
                case 1:
                    return workEnabled ? 1 : 0;
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
                case 1:
                    workEnabled = value != 0;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public CompounderTileEntity(CompounderInfo<R> info, BlockPos pos, BlockState state) {
        super(info.getBlockEntityType(), info.getInputSlotCount() + 2, pos, state);
        this.info = info;
        this.allSlots = IntStream.range(0, this.items.size()).toArray();
    }

    protected RecipeType<R> getRecipeType() {
        return this.info.getRecipeType();
    }

    @Nullable
    public R getRecipe() {
        if (level == null) return null;
        return level.getRecipeManager().getRecipeFor(getRecipeType(), this, level).orElse(null);
    }

    protected CompoundMaterialItem getOutputItem(MaterialList materials) {
        return this.info.getOutputItem();
    }

    protected ItemStack getWorkOutput(@Nullable R recipe, MaterialList materials) {
        if (recipe != null) {
            return recipe.assemble(this);
        }
        return getOutputItem(materials).create(materials);
    }

    public int getInputSlotCount() {
        return getContainerSize() - 2;
    }

    public int getOutputSlotIndex() {
        return getContainerSize() - 2;
    }

    public int getOutputHintSlotIndex() {
        return getContainerSize() - 1;
    }

    public ItemStack getHintStack() {
        return getItem(getOutputHintSlotIndex());
    }

    public void encodeExtraData(FriendlyByteBuf buffer) {
        buffer.writeByte(this.items.size());
        buffer.writeByte(this.fields.getCount());
    }

    private boolean areInputsEmpty() {
        for (int i = 0; i < this.getInputSlotCount(); ++i) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static <R extends CompoundingRecipe> void tick(Level level, BlockPos pos, BlockState state, CompounderTileEntity<R> blockEntity) {
        if (blockEntity.areInputsEmpty()) {
            // No point in doing anything when input slots are empty
            blockEntity.updateOutputHint(ItemStack.EMPTY);
            return;
        }

        R recipe = blockEntity.getRecipe();
        if (recipe != null) {
            // Inputs match a custom recipe
            blockEntity.doWork(recipe, MaterialList.empty());
        } else {
            // No recipe, but we might be able to make a generic compound
            MaterialList materials = blockEntity.getInputs();
            if (!hasMultipleMaterials(materials) || !blockEntity.canCompoundMaterials(materials)) {
                // Not a valid combination
                blockEntity.stopWork(true);
                return;
            }
            blockEntity.doWork(null, materials);
        }
    }

    private void doWork(@Nullable R recipe, MaterialList materials) {
        assert level != null;

        ItemStack current = getItem(getOutputSlotIndex());
        ItemStack output = getWorkOutput(recipe, materials);

        updateOutputHint(output);

        if (!current.isEmpty()) {
            int newCount = current.getCount() + output.getCount();

            if (!InventoryUtils.canItemsStack(current, output) || newCount > output.getMaxStackSize()) {
                // Output items do not match or not enough room
                stopWork(false);
                return;
            }
        }

        if (workEnabled) {
            if (progress < WORK_TIME) {
                ++progress;
            }

            if (progress >= WORK_TIME && !level.isClientSide) {
                finishWork(recipe, materials, current);
            }
        } else {
            stopWork(false);
        }
    }

    private void updateOutputHint(ItemStack hintStack) {
        setItem(getOutputHintSlotIndex(), hintStack);
    }

    private void stopWork(boolean clearHintItem) {
        progress = 0;

        if (clearHintItem) {
            setItem(getOutputHintSlotIndex(), ItemStack.EMPTY);
        }
    }

    private void finishWork(@Nullable R recipe, MaterialList materials, ItemStack current) {
        progress = 0;
        for (int i = 0; i < getInputSlotCount(); ++i) {
            removeItem(i, 1);
        }

        ItemStack output = getWorkOutput(recipe, materials);
        if (!current.isEmpty()) {
            current.grow(output.getCount());
        } else {
            setItem(getOutputSlotIndex(), output);
        }
    }

    private static boolean hasMultipleMaterials(List<IMaterialInstance> materials) {
        if (materials.size() < 2) {
            return false;
        }

        IMaterial first = materials.get(0).get();
        for (int i = 1; i < materials.size(); ++i) {
            if (materials.get(i).get() != first) {
                return true;
            }
        }

        return false;
    }

    private boolean canCompoundMaterials(Iterable<IMaterialInstance> materials) {
        Set<PartType> partTypes = new HashSet<>(PartType.getValues());
        for (IMaterialInstance material : materials) {
            if (!material.hasAnyCategory(this.info.getCategories())) {
                return false;
            }
            partTypes.removeIf(pt -> !material.getPartTypes().contains(pt));
        }
        return !partTypes.isEmpty();
    }

    private MaterialList getInputs() {
        boolean allEmpty = true;

        for (int i = 0; i < getInputSlotCount(); ++i) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                allEmpty = false;
                break;
            }
        }

        if (allEmpty) {
            return MaterialList.empty();
        }

        MaterialList ret = MaterialList.empty();

        for (int i = 0; i < getInputSlotCount(); ++i) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                MaterialInstance material = MaterialInstance.from(stack);
                if (material != null && material.get().isSimple()) {
                    ret.add(material);
                } else {
                    return MaterialList.empty();
                }
            }
        }

        return ret;
    }

    @Override
    public NonNullList<ItemStack> getItemsToDrop() {
        // Gets the items dropped when the block is broken. Excludes the "hint stack"
        NonNullList<ItemStack> ret = NonNullList.create();
        for (int i = 0; i < this.getContainerSize() - 1; ++i) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                ret.add(stack);
            }
        }
        return ret;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return allSlots.clone();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index < getInputSlotCount()/* && isSimpleMaterial(stack)*/;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == getOutputSlotIndex();
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent(Util.makeDescriptionId("container", this.info.getBlock().getRegistryName()));
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new CompounderContainer(this.info.getContainerType(),
                id,
                player,
                this,
                this.fields,
                this.info.getCategories());
    }

    @Override
    public void load(CompoundTag tags) {
        super.load(tags);
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    @Override
    public CompoundTag save(CompoundTag tags) {
        CompoundTag compoundTag = super.save(tags);
        SyncVariable.Helper.writeSyncVars(this, compoundTag, SyncVariable.Type.WRITE);
        return compoundTag;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);
        return tags;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        CompoundTag tags = packet.getTag();
        SyncVariable.Helper.readSyncVars(this, tags);
    }
}
