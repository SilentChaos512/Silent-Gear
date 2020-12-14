package net.silentchaos512.gear.block.compounder;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.init.ModItems;
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
import java.util.stream.IntStream;

public class CompounderTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity {
    public static final int STANDARD_SIZE = 5;
    static final int WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 10 : 15);

    private final ContainerType<? extends CompounderContainer> containerType;
    private final Collection<IMaterialCategory> categories;
    @SyncVariable(name = "progress")
    private int progress = 0;

    private final IIntArray fields = new IIntArray() {
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
        public int size() {
            return 1;
        }
    };

    public CompounderTileEntity(TileEntityType<?> typeIn, ContainerType<? extends CompounderContainer> containerType, int inventorySize, Collection<IMaterialCategory> categoriesIn) {
        super(typeIn, inventorySize);
        this.containerType = containerType;
        this.categories = ImmutableSet.copyOf(categoriesIn);
    }

    public static boolean canAcceptInput(ItemStack stack, Collection<IMaterialCategory> categories) {
        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && material.hasAnyCategory(categories);
    }

    public CompoundMaterialItem getOutputItem(Collection<MaterialInstance> materials) {
        // TODO
        return ModItems.ALLOY_INGOT.get();
    }

    @Override
    public void tick() {
        if (world == null) {
            return;
        }

        List<MaterialInstance> materials = getInputs();
        if (materials.size() < 2) {
            progress = 0;
            return;
        }

        ItemStack current = getStackInSlot(getSizeInventory() - 1);
        if (!current.isEmpty()) {
            ItemStack output = getOutputItem(materials).create(materials);
            if (!InventoryUtils.canItemsStack(current, output) || current.getCount() + output.getCount() > output.getMaxStackSize()) {
                return;
            }
        }

        if (progress < WORK_TIME) {
            ++progress;
        }

        if (progress >= WORK_TIME && !world.isRemote) {
            progress = 0;
            for (int i = 0; i < getSizeInventory() - 1; ++i) {
                decrStackSize(i, 1);
            }

            if (!current.isEmpty()) {
                current.grow(materials.size());
            } else {
                ItemStack output = getOutputItem(materials).create(materials);
                setInventorySlotContents(getSizeInventory() - 1, output);
            }
        }
    }

    private List<MaterialInstance> getInputs() {
        boolean allEmpty = true;
        int inputSlotCount = getSizeInventory() - 1;

        for (int i = 0; i < inputSlotCount; ++i) {
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

        for (int i = 0; i < inputSlotCount; ++i) {
            ItemStack stack = getStackInSlot(i);
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null) {
                ret.add(material);
            }
        }

        return ret;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(0, this.items.size()).toArray();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index < this.items.size() - 1 && MaterialManager.from(stack) != null;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return index == this.items.size() - 1;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.silentgear.compounder");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new CompounderContainer(this.containerType, id, player, this, this.fields, this.categories);
    }

    public void encodeExtraData(PacketBuffer buffer) {
        buffer.writeByte(this.items.size());
        buffer.writeByte(this.fields.size());
    }
}
