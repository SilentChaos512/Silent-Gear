/*
 * Silent Gear -- TileSalvager
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.block.salvager;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.tile.TileSidedInventorySL;
import net.silentchaos512.lib.util.GameUtil;
import net.silentchaos512.lib.util.MathUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.IntStream;

public class TileSalvager extends TileSidedInventorySL implements ITickable {
    static final int BASE_WORK_TIME = TimeUtils.ticksFromSeconds(GameUtil.isDeobfuscated() ? 2 : 10);
    private static final int INPUT_SLOT = 0;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT};
    private static final int[] SLOTS_OUTPUT = IntStream.rangeClosed(1, 18).toArray();
    private static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;

    @SyncVariable(name = "progress")
    int progress = 0;

    public TileSalvager() {
        super(ModTileEntities.SALVAGER.type());
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        boolean requiresClientSync = false;

        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            if (progress < BASE_WORK_TIME) {
                ++progress;
                requiresClientSync = true;
            }

            if (progress >= BASE_WORK_TIME && areAllOutputSlotsFree()) {
                for (ItemStack stack : getSalvagedPartsWithChance(input)) {
                    int slot = getFreeOutputSlot();
                    if (slot > 0) {
                        setInventorySlotContents(slot, stack);
                    } else {
                        SilentGear.LOGGER.warn("Item lost in salvager: {}", stack);
                    }
                }

                progress = 0;
                input.shrink(1);
                requiresClientSync = true;
            }
        }

        if (requiresClientSync) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private boolean canSalvage(ItemStack stack) {
        return stack.getItem() instanceof ICoreItem || VanillaGearSalvage.isVanillaGear(stack);
    }

    private Collection<ItemStack> getSalvagedPartsWithChance(ItemStack stack) {
        double lossRate = getLossRate(stack);
        SilentGear.LOGGER.debug("Loss rate for '{}': {}", stack, lossRate);
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (ItemStack part : getSalvageableParts(stack)) {
            ItemStack copy = part.copy();
            int count = copy.getCount();
            for (int i = 0; i < count; ++i) {
                if (MathUtils.tryPercentage(SilentGear.random, lossRate)) {
                    copy.shrink(1);
                }
            }

            if (!copy.isEmpty()) {
                builder.add(copy);
            }
        }
        return builder.build();
    }

    private double getLossRate(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        Double min = Config.GENERAL.salvagerMinLossRate.get();
        if (maxDamage == 0) {
            return min;
        }
        double ratio = (double) stack.getDamage() / maxDamage;
        return min + ratio * (Config.GENERAL.salvagerMaxLossRate.get() - min);
    }

    private Collection<ItemStack> getSalvageableParts(ItemStack stack) {
        if (stack.getItem() instanceof ICoreItem) {
            return getSalvageFromGearItem(stack);
        } else if (VanillaGearSalvage.isVanillaGear(stack)) {
            return getSalvageFromVanillaItem(stack);
        } else {
            // TODO: Other item types? Custom handlers?
            return ImmutableList.of();
        }
    }

    private static Collection<ItemStack> getSalvageFromGearItem(ItemStack stack) {
        ICoreItem item = (ICoreItem) stack.getItem();
        ConfigOptionEquipment config = item.getConfig();
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        for (PartData part : GearData.getConstructionParts(stack)) {
            if (part.getType() == PartType.ROD) {
                ItemStack rod = part.getCraftingItem().copy();
                rod.setCount(Math.max(1, config.getRodCount()));
                builder.add(rod);
            } else if (part.getType() == PartType.MAIN) {
                ItemStack craftingItem = part.getCraftingItem().copy();
                // TODO: Add a chance of grade loss?
                part.getGrade().setGradeOnStack(craftingItem);
                builder.add(craftingItem);
            } else {
                builder.add(part.getCraftingItem().copy());
            }
        }

        return builder.build();
    }

    private static Collection<ItemStack> getSalvageFromVanillaItem(ItemStack stack) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

        IItemProvider headItem = VanillaGearSalvage.getHeadItem(stack);
        int headCount = VanillaGearSalvage.getHeadCount(stack);
        if (headItem != null && headCount > 0) {
            builder.add(new ItemStack(headItem, headCount));
        }

        Item rodItem = Items.STICK;
        int rodCount = VanillaGearSalvage.getRodCount(stack);
        if (rodCount > 0) {
            builder.add(new ItemStack(rodItem, rodCount));
        }

        return builder.build();
    }

    private ItemStack getInputStack() {
        for (int slot : SLOTS_INPUT) {
            ItemStack stack = getStackInSlot(slot);
            if (canSalvage(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private int getFreeOutputSlot() {
        for (int slot : SLOTS_OUTPUT) {
            if (getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private boolean areAllOutputSlotsFree() {
        for (int slot : SLOTS_OUTPUT) {
            if (!getStackInSlot(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getInputStack().isEmpty();
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return progress;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.progress = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);
        return tags;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        switch (side) {
            case DOWN:
                return SLOTS_OUTPUT.clone();
            default:
                return SLOTS_INPUT.clone();
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty() || isOutputSlot(index))
            return false;

        ItemStack current = getStackInSlot(index);
        if (!current.isEmpty() && !current.isItemEqual(stack))
            return false;

        if (isInputSlot(index))
            return canSalvage(stack);
        return super.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return isOutputSlot(index);
    }

    private static boolean isInputSlot(int index) {
        for (int k : SLOTS_INPUT)
            if (index == k)
                return true;
        return false;
    }

    private static boolean isOutputSlot(int index) {
        for (int k : SLOTS_OUTPUT)
            if (index == k)
                return true;
        return false;
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentTranslation("block.silentgear.salvager");
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
    }
}
