/*
 * Silent Gear -- TilePartAnalyzer
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

package net.silentchaos512.gear.block.analyzer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.tile.TileSidedInventorySL;
import net.silentchaos512.lib.util.TimeHelper;

import java.util.stream.IntStream;

public class TilePartAnalyzer extends TileSidedInventorySL implements ITickable {
    static final int BASE_ANALYZE_TIME = TimeHelper.ticksFromSeconds(30);

    static final int INPUT_SLOT = 0;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT};
    private static final int[] SLOTS_OUTPUT = IntStream.rangeClosed(1, 4).toArray();
    static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;

    @SyncVariable(name = "progress")
    int progress = 0;
    private boolean requireClientSync = false;

    @Override
    public void update() {
        if (world.isRemote) return;

        ItemStack input = getInputStack();
        ItemPart part = PartRegistry.get(input);

        if (part != null) {
            // Analyzing
            if (progress < BASE_ANALYZE_TIME) {
                ++progress;
                requireClientSync = true;
            }

            // Grade part if any output slot is free
            if (progress >= BASE_ANALYZE_TIME) {
                int outputSlot = getFreeOutputSlot();
                if (outputSlot > 0) {
                    progress = 0;

                    // Take one from input stack
                    ItemStack stack = input.copy();
                    stack.setCount(1);
                    input.shrink(1);

                    // Assign grade, move to output slot
                    MaterialGrade.selectRandom(SilentGear.random).setGradeOnStack(stack);
                    setInventorySlotContents(outputSlot, stack);
                    if (input.getCount() <= 0) {
                        for (int slot : SLOTS_INPUT) {
                            if (getStackInSlot(slot).isEmpty()) {
                                setInventorySlotContents(slot, ItemStack.EMPTY);
                            }
                        }
                    }

                    requireClientSync = true;
                }
            }
        } else {
            progress = 0;
        }

        if (requireClientSync) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            requireClientSync = false;
        }
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
    }

    private ItemStack getInputStack() {
        for (int slot : SLOTS_INPUT) {
            ItemStack stack = getStackInSlot(slot);
            if (PartRegistry.get(stack) instanceof PartMain && MaterialGrade.fromStack(stack) == MaterialGrade.NONE) {
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tags = new NBTTagCompound();
        writeSyncVars(tags, SyncVariable.Type.PACKET);

        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            NBTTagCompound itemTags = input.writeToNBT(new NBTTagCompound());
            tags.setTag("input_item", itemTags);
        }

        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tags);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tags = super.getUpdateTag();
        writeSyncVars(tags, SyncVariable.Type.PACKET);

        NBTTagList tagList = new NBTTagList();
        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            NBTTagCompound itemTags = new NBTTagCompound();
            itemTags.setByte("Slot", (byte) INPUT_SLOT);
            input.writeToNBT(itemTags);
            tagList.appendTag(itemTags);
        }
        tags.setTag("Items", tagList);
        return tags;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        NBTTagCompound tags = packet.getNbtCompound();
        readSyncVars(tags);

        if (tags.hasKey("input_item")) {
            setInventorySlotContents(INPUT_SLOT, new ItemStack(tags.getCompoundTag("input_item")));
        } else {
            setInventorySlotContents(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        switch (side) {
            case DOWN:
                return SLOTS_OUTPUT;
            default:
                return SLOTS_INPUT;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index != INPUT_SLOT || stack.isEmpty()) return false;

        ItemPart part = PartRegistry.get(stack);
        MaterialGrade grade = MaterialGrade.fromStack(stack);
        return part instanceof PartMain && grade == MaterialGrade.NONE;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index != INPUT_SLOT;
    }
}
