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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.gear.parts.type.PartMain;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.tile.TileSidedInventorySL;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class TilePartAnalyzer extends TileSidedInventorySL implements ITickable {
    static final int BASE_ANALYZE_TIME = TimeUtils.ticksFromSeconds(5);

    static final int INPUT_SLOT = 0;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT};
    private static final int[] SLOTS_OUTPUT = IntStream.rangeClosed(1, 4).toArray();
    static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;

    @SyncVariable(name = "progress")
    int progress = 0;
    private boolean requireClientSync = false;

    public TilePartAnalyzer() {
        super(ModTileEntities.PART_ANALYZER.type());
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        ItemStack input = getInputStack();
        IGearPart part = PartManager.from(input);

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

    @Override
    public boolean isEmpty() {
        return getInputStack().isEmpty();
    }

    private ItemStack getInputStack() {
        for (int slot : SLOTS_INPUT) {
            ItemStack stack = getStackInSlot(slot);
            if (PartManager.from(stack) instanceof PartMain && MaterialGrade.fromStack(stack) == MaterialGrade.NONE) {
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
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);

        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            NBTTagCompound itemTags = input.serializeNBT();
            tags.setTag("input_item", itemTags);
        }

        return new SPacketUpdateTileEntity(pos, 0, tags);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);

        NBTTagList tagList = new NBTTagList();
        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            NBTTagCompound itemTags = input.serializeNBT();
            itemTags.setByte("Slot", (byte) INPUT_SLOT);
            tagList.add(itemTags);
        }
        tags.setTag("Items", tagList);
        return tags;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        NBTTagCompound tags = packet.getNbtCompound();
        SyncVariable.Helper.readSyncVars(this, tags);

        if (tags.hasKey("input_item")) {
            setInventorySlotContents(INPUT_SLOT, ItemStack.read(tags.getCompound("input_item")));
        } else {
            setInventorySlotContents(INPUT_SLOT, ItemStack.EMPTY);
        }
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
        if (index != INPUT_SLOT || stack.isEmpty() || (!getStackInSlot(index).isEmpty() && !getStackInSlot(index).isItemEqual(stack)))
            return false;

        MaterialGrade grade = MaterialGrade.fromStack(stack);
        if (grade != MaterialGrade.NONE) return false;
        return PartManager.from(stack) instanceof PartMain;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index != INPUT_SLOT;
    }

    @Override
    public ITextComponent getName() {
        return new TextComponentTranslation("block.silentgear.part_analyzer");
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
    }
}
