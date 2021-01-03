package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CompounderTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity {
    public static final int STANDARD_INPUT_SLOTS = 4;
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 10 : 15);

    private final ContainerType<? extends CompounderContainer> containerType;
    private final Collection<IMaterialCategory> categories;
    private final Supplier<CompoundMaterialItem> outputItem;
    private final int[] allSlots;

    @SyncVariable(name = "Progress")
    private int progress = 0;
    @SyncVariable(name = "WorkEnabled")
    private boolean workEnabled = true;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass") private final IIntArray fields = new IIntArray() {
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
        public int size() {
            return 2;
        }
    };

    public CompounderTileEntity(TileEntityType<?> typeIn,
                                ContainerType<? extends CompounderContainer> containerType,
                                Supplier<CompoundMaterialItem> outputItem,
                                int inputSlotCount,
                                Collection<IMaterialCategory> categoriesIn) {
        super(typeIn, inputSlotCount + 2);
        this.containerType = containerType;
        this.outputItem = outputItem;
        this.categories = ImmutableSet.copyOf(categoriesIn);
        this.allSlots = IntStream.range(0, this.items.size()).toArray();
    }

    public static boolean canAcceptInput(ItemStack stack, Collection<IMaterialCategory> categories) {
        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && material.getMaterial().isSimple() && material.hasAnyCategory(categories);
    }

    protected IRecipeType<? extends CompoundingRecipe> getRecipeType() {
        return ModRecipes.COMPOUNDING_METAL_TYPE;
    }

    @Nullable
    public CompoundingRecipe getRecipe() {
        if (world == null) return null;
        return world.getRecipeManager().getRecipe(getRecipeType(), this, world).orElse(null);
    }

    protected CompoundMaterialItem getOutputItem(Collection<MaterialInstance> materials) {
        return this.outputItem.get();
    }

    protected ItemStack getWorkOutput(@Nullable CompoundingRecipe recipe, List<MaterialInstance> materials) {
        if (recipe != null) {
            return recipe.getCraftingResult(this);
        }
        return getOutputItem(materials).create(materials);
    }

    public boolean isWorkEnabled() {
        return workEnabled;
    }

    public void setWorkEnabled(boolean workEnabled) {
        this.workEnabled = workEnabled;
    }

    public int getInputSlotCount() {
        return getSizeInventory() - 2;
    }

    public int getOutputSlotIndex() {
        return getSizeInventory() - 2;
    }

    public int getOutputHintSlotIndex() {
        return getSizeInventory() - 1;
    }

    public void encodeExtraData(PacketBuffer buffer) {
        buffer.writeByte(this.items.size());
        buffer.writeByte(this.fields.size());
    }

    @Override
    public void tick() {
        if (world == null || world.isRemote) {
            return;
        }

        CompoundingRecipe recipe = getRecipe();
        if (recipe != null) {
            doWork(recipe, Collections.emptyList());
        } else {
            List<MaterialInstance> materials = getInputs();
            if (!hasMultipleMaterials(materials)) {
                stopWork();
                return;
            }

            doWork(null, materials);
        }
    }

    private void doWork(@Nullable CompoundingRecipe recipe, List<MaterialInstance> materials) {
        assert this.world != null;

        ItemStack current = getStackInSlot(getOutputSlotIndex());
        ItemStack output = getWorkOutput(recipe, materials);

        if (!current.isEmpty()) {
            int newCount = current.getCount() + output.getCount();

            if (!InventoryUtils.canItemsStack(current, output) || newCount > output.getMaxStackSize()) {
                // Output items do not match or not enough room
                stopWork();
                return;
            }
        }

        updateOutputHint(output);

        if (workEnabled) {
            if (progress < WORK_TIME) {
                ++progress;
            }

            if (progress >= WORK_TIME && !world.isRemote) {
                finishWork(recipe, materials, current);
            }
        }
    }

    private void stopWork() {
        progress = 0;
        setInventorySlotContents(getOutputHintSlotIndex(), ItemStack.EMPTY);
    }

    private void finishWork(@Nullable CompoundingRecipe recipe, List<MaterialInstance> materials, ItemStack current) {
        progress = 0;
        for (int i = 0; i < getInputSlotCount(); ++i) {
            decrStackSize(i, 1);
        }

        if (!current.isEmpty()) {
            current.grow(materials.size());
        } else {
            ItemStack output = getWorkOutput(recipe, materials);
            setInventorySlotContents(getOutputSlotIndex(), output);
        }
    }

    private void updateOutputHint(ItemStack hintStack) {
        setInventorySlotContents(getOutputHintSlotIndex(), hintStack);
    }

    private static boolean hasMultipleMaterials(List<MaterialInstance> materials) {
        if (materials.size() < 2) {
            return false;
        }

        IMaterial first = materials.get(0).getMaterial();
        for (int i = 1; i < materials.size(); ++i) {
            if (materials.get(i).getMaterial() != first) {
                return true;
            }
        }

        return false;
    }

    private List<MaterialInstance> getInputs() {
        boolean allEmpty = true;

        for (int i = 0; i < getInputSlotCount(); ++i) {
            ItemStack stack = getStackInSlot(i);
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
            ItemStack stack = getStackInSlot(i);
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null && material.getMaterial().isSimple()) {
                ret.add(material);
            }
        }

        return ret;
    }

    private static boolean isSimpleMaterial(ItemStack stack) {
        IMaterial material = MaterialManager.from(stack);
        return material != null && material.isSimple();
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return allSlots.clone();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index < getInputSlotCount()/* && isSimpleMaterial(stack)*/;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return index == getOutputSlotIndex();
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.silentgear.compounder");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new CompounderContainer(this.containerType, id, player, this, this.fields, this.categories);
    }
}
