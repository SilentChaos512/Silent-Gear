/*
 * Silent Gear -- SalvagerTileEntity
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModBlockEntities;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.TimeUtils;
import net.silentchaos512.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.IntStream;

public class SalvagerTileEntity extends LockableSidedInventoryTileEntity {
    static final int BASE_WORK_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 2 : 10);
    private static final int INPUT_SLOT = 0;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT};
    private static final int[] SLOTS_OUTPUT = IntStream.rangeClosed(1, 18).toArray();
    private static final int[] SLOTS_ALL = IntStream.rangeClosed(0, 18).toArray();
    public static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;

    @SyncVariable(name = "progress") int progress = 0;

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

    public SalvagerTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SALVAGER.get(), INVENTORY_SIZE, pos, state);
    }

    @Nullable
    private SalvagingRecipe getRecipe() {
        if (level == null) return null;
        return level.getRecipeManager().getRecipeFor(SalvagingRecipe.SALVAGING_TYPE, this, level).orElse(null);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SalvagerTileEntity blockEntity) {
        ItemStack input = blockEntity.getItem(0);
        SalvagingRecipe recipe = blockEntity.getRecipe();
        if (recipe != null) {
            if (blockEntity.progress < BASE_WORK_TIME) {
                ++blockEntity.progress;
            }

            if (blockEntity.progress >= BASE_WORK_TIME && blockEntity.areAllOutputSlotsFree()) {
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
            PartData partData = PartData.from(part);
            double partLossRate = partData != null
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

    private boolean areAllOutputSlotsFree() {
        for (int slot : SLOTS_OUTPUT) {
            if (!getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);
        return tags;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    @Override
    public int[] getSlotsForFace(Direction side) {
        switch (side) {
            case DOWN:
                return SLOTS_OUTPUT;
            case UP:
                return SLOTS_INPUT;
            default:
                return SLOTS_ALL;
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.isEmpty() || isOutputSlot(index))
            return false;

        ItemStack current = getItem(index);
        if (!current.isEmpty() && !current.sameItem(stack))
            return false;

        return isInputSlot(index) || super.canPlaceItem(index, stack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return isOutputSlot(index);
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
}
