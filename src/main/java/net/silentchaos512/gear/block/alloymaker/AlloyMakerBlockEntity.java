package net.silentchaos512.gear.block.alloymaker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.IDroppableInventory;
import net.silentchaos512.gear.block.SgContainerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.AlloyRecipeInput;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.lib.util.TimeUtils;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class AlloyMakerBlockEntity<R extends AlloyRecipe> extends SgContainerBlockEntity implements IDroppableInventory {
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);

    private final AlloyMakerInfo<R> info;
    private final RecipeManager.CachedCheck<AlloyRecipeInput, R> quickCheck;

    private ItemStack outputItemHint = ItemStack.EMPTY;
    private int progress = 0;
    private boolean workEnabled = true;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass") private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> workEnabled ? 1 : 0;
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
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public AlloyMakerBlockEntity(AlloyMakerInfo<R> info, BlockPos pos, BlockState state) {
        super(info.getBlockEntityType(), pos, state, info.getInputSlotCount() + 2, () -> createInternalItemList(info));
        this.info = info;
        this.quickCheck = RecipeManager.createCheck(info.getRecipeType());
    }

    protected RecipeType<R> getRecipeType() {
        return this.info.getRecipeType();
    }

    protected CompoundMaterialItem getOutputItem(List<MaterialInstance> materials) {
        return this.info.getOutputItem();
    }

    protected ItemStack getWorkOutput(@Nullable R recipe, List<MaterialInstance> materials) {
        if (recipe != null) {
            return recipe.assemble(AlloyRecipeInput.of(this));
        }
        var result = getOutputItem(materials).create(materials);
        applyModifiers(result);
        return result;
    }

    protected void applyModifiers(ItemStack result) {
        // Apply data components for material modifiers here, if necessary
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
        buffer.writeByte(getContainerSize());
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

    public static <R extends AlloyRecipe> void tick(Level level, BlockPos pos, BlockState state, AlloyMakerBlockEntity<R> blockEntity) {
        if (blockEntity.areInputsEmpty()) {
            // No point in doing anything when input slots are empty
            blockEntity.updateOutputHint(ItemStack.EMPTY);
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) return;

        var recipe = blockEntity.quickCheck.getRecipeFor(AlloyRecipeInput.of(blockEntity), serverLevel).orElse(null);
        if (recipe != null) {
            // Inputs match a custom recipe
            blockEntity.doWork(recipe.value(), level.registryAccess(), Collections.emptyList());
        } else {
            // No recipe, but we might be able to make a generic compound
            List<MaterialInstance> materials = blockEntity.getInputs();
            if (!hasMultipleMaterials(materials) || !blockEntity.canCompoundMaterials(materials)) {
                // Not a valid combination
                blockEntity.stopWork(true);
                return;
            }
            blockEntity.doWork(null, level.registryAccess(), materials);
        }
    }

    private void doWork(@Nullable R recipe, RegistryAccess registryAccess, List<MaterialInstance> materials) {
        assert level != null;

        ItemStack current = getItem(getOutputSlotIndex());
        ItemStack output = getWorkOutput(recipe, materials);

        updateOutputHint(output);

        if (!current.isEmpty()) {
            int newCount = current.getCount() + output.getCount();

            if (!ItemStack.isSameItemSameComponents(current, output) || newCount > output.getMaxStackSize()) {
                // Output items do not match or not enough room
                stopWork(false);
                return;
            }
        }

        if (workEnabled) {
            if (progress < WORK_TIME) {
                ++progress;
            }

            if (progress >= WORK_TIME && !level.isClientSide()) {
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

    private void finishWork(@Nullable R recipe, List<MaterialInstance> materials, ItemStack current) {
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

    private static boolean hasMultipleMaterials(List<MaterialInstance> materials) {
        if (materials.size() < 2) {
            return false;
        }

        Material first = materials.get(0).get();
        for (int i = 1; i < materials.size(); ++i) {
            if (materials.get(i).get() != first) {
                return true;
            }
        }

        return false;
    }

    private boolean canCompoundMaterials(Iterable<MaterialInstance> materials) {
        Set<PartType> partTypes = new HashSet<>(SgRegistries.PART_TYPE.stream().toList());
        for (MaterialInstance material : materials) {
            if (!this.info.acceptsMaterial(material)) {
                return false;
            }
            partTypes.removeIf(pt -> !material.getPartTypes().contains(pt));
        }
        // Can only compound if not all materials are additives
        partTypes.removeIf(partType -> {
            for (MaterialInstance material : materials) {
                if (!material.getProperty(partType, GearProperties.ADDITIVE.get())) {
                    return false;
                }
            }
            return true;
        });
        return !partTypes.isEmpty();
    }

    private List<MaterialInstance> getInputs() {
        boolean allEmpty = true;

        for (int i = 0; i < getInputSlotCount(); ++i) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                allEmpty = false;
                break;
            }
        }

        if (allEmpty) {
            return Collections.emptyList();
        }

        List<MaterialInstance> ret = new ArrayList<>();

        for (int i = 0; i < getInputSlotCount(); ++i) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                MaterialInstance material = MaterialInstance.from(stack);
                if (material != null && material.get().isSimple()) {
                    ret.add(material);
                } else {
                    return Collections.emptyList();
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
    public void setChanged() {
        this.progress = 0;
        super.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= getInputSlotCount()) return false;
        var material = MaterialInstance.from(stack);
        return material != null && this.info.acceptsMaterial(material);
    }

    @Override
    public boolean canExtractItem(int slot) {
        return slot == getOutputSlotIndex();
    }

    @Override
    protected Component getDefaultName() {
        Identifier key = BuiltInRegistries.BLOCK.getKey(this.info.getBlock());
        return Component.translatable(Util.makeDescriptionId("container", key));
    }

    @Override
    public NonNullList<ItemStack> createInternalItemList() {
        throw new NotImplementedException("Please use the secondary SgContainerBlockEntity constructor for AlloyMakerBlockEntity");
    }

    public static NonNullList<ItemStack> createInternalItemList(AlloyMakerInfo<?> info) {
        return NonNullList.withSize(info.getInputSlotCount() + 2, ItemStack.EMPTY);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new AlloyMakerContainer(this.info.getContainerType(),
                id,
                player,
                this,
                this.fields,
                this.info.getCategories());
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.progress = input.getIntOr("Progress", 0);
        this.workEnabled = input.getBooleanOr("WorkEnabled", false);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
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
    public void onDataPacket(Connection net, ValueInput input) {
        super.onDataPacket(net, input);
        this.progress = input.getIntOr("Progress", 0);
        this.workEnabled = input.getBooleanOr("WorkEnabled", false);
    }
}
