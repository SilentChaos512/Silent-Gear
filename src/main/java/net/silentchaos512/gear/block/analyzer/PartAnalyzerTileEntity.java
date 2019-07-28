/*
 * Silent Gear -- PartAnalyzerTileEntity
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

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class PartAnalyzerTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity {
    static final int BASE_ANALYZE_TIME = TimeUtils.ticksFromSeconds(5);
    public static final List<Tag<Item>> CATALYST_TAGS = ImmutableList.of(
            new ItemTags.Wrapper(SilentGear.getId("analyzer_catalyst/tier1")),
            new ItemTags.Wrapper(SilentGear.getId("analyzer_catalyst/tier2")),
            new ItemTags.Wrapper(SilentGear.getId("analyzer_catalyst/tier3"))
    );

    static final int INPUT_SLOT = 0;
    static final int CATALYST_SLOT = 1;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT, CATALYST_SLOT};
    private static final int[] SLOTS_OUTPUT = {2, 3, 4, 5};
    static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;
    private static final int[] SLOTS_ALL = IntStream.rangeClosed(0, INVENTORY_SIZE).toArray();

    @SyncVariable(name = "progress") private int progress = 0;
    private boolean requireClientSync = false;

    private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            return PartAnalyzerTileEntity.this.progress;
        }

        @Override
        public void set(int index, int value) {
            PartAnalyzerTileEntity.this.progress = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public PartAnalyzerTileEntity() {
        super(ModTileEntities.PART_ANALYZER.type(), INVENTORY_SIZE);
    }

    @Override
    public void tick() {
        if (world == null) return;

        // Don't waste time if there is no input or no free output slots
        ItemStack input = getInputStack();
        if (input.isEmpty()) return;
        int outputSlot = getFreeOutputSlot();
        if (outputSlot < 0) return;

        IGearPart part = PartManager.from(input);
        if (part != null) {
            // Analyzing
            if (progress < BASE_ANALYZE_TIME) {
                ++progress;
                //requireClientSync = true;
            }

            // Grade part if any output slot is free
            if (progress >= BASE_ANALYZE_TIME && !world.isRemote) {
                progress = 0;

                // Take one from input stack
                ItemStack stack = input.copy();
                stack.setCount(1);
                input.shrink(1);

                // Get catalyst
                ItemStack catalyst = getCatalystStack();
                int catalystTier = getCatalystTier(catalyst);
                if (catalystTier > 0 && !catalyst.isEmpty()) {
                    catalyst.shrink(1);
                }

                // Assign grade, move to output slot
                MaterialGrade.selectWithCatalyst(SilentGear.random, catalystTier).setGradeOnStack(stack);
                setInventorySlotContents(outputSlot, stack);
                if (input.getCount() <= 0) {
                    for (int slot : SLOTS_INPUT) {
                        if (getStackInSlot(slot).isEmpty()) {
                            setInventorySlotContents(slot, ItemStack.EMPTY);
                        }
                    }
                }

//                requireClientSync = true;
            }
        } else {
            progress = 0;
        }

        if (requireClientSync) {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            requireClientSync = false;
        }
    }

    @Override
    public boolean isEmpty() {
        return getInputStack().isEmpty();
    }

    private ItemStack getInputStack() {
        ItemStack stack = getStackInSlot(INPUT_SLOT);
        if (!stack.isEmpty()) {
            IGearPart part = PartManager.from(stack);
            if (part != null && part.getType() == PartType.MAIN && MaterialGrade.fromStack(stack) == MaterialGrade.NONE) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getCatalystStack() {
        return getStackInSlot(CATALYST_SLOT);
    }

    private int getFreeOutputSlot() {
        for (int slot : SLOTS_OUTPUT) {
            if (getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    static boolean isUngradedMainPart(ItemStack stack) {
        MaterialGrade grade = MaterialGrade.fromStack(stack);
        if (grade != MaterialGrade.NONE) return false;

        IGearPart part = PartManager.from(stack);
        return part != null && part.getType() == PartType.MAIN;
    }

    static int getCatalystTier(ItemStack stack) {
        Item item = stack.getItem();
        for (int i = 0; i < CATALYST_TAGS.size(); ++i) {
            if (item.isIn(CATALYST_TAGS.get(i))) {
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public void read(CompoundNBT tags) {
        super.read(tags);
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    @Override
    public CompoundNBT write(CompoundNBT tags) {
        CompoundNBT compoundTag = super.write(tags);
        SyncVariable.Helper.writeSyncVars(this, compoundTag, SyncVariable.Type.WRITE);
        return compoundTag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tags = getUpdateTag();

        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            CompoundNBT itemTags = input.serializeNBT();
            tags.put("input_item", itemTags);
        }

        return new SUpdateTileEntityPacket(pos, 0, tags);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);
//        tags.putInt("progress", this.progress);

        ListNBT tagList = new ListNBT();
        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            CompoundNBT itemTags = input.serializeNBT();
            itemTags.putByte("Slot", (byte) INPUT_SLOT);
            tagList.add(itemTags);
        }
        tags.put("Items", tagList);
        return tags;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        CompoundNBT tags = packet.getNbtCompound();
        SyncVariable.Helper.readSyncVars(this, tags);
//        this.progress = tags.getInt("progress");

        if (tags.contains("input_item")) {
            setInventorySlotContents(INPUT_SLOT, ItemStack.read(tags.getCompound("input_item")));
        } else {
            setInventorySlotContents(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    @Override
    public int[] getSlotsForFace(Direction side) {
        switch (side) {
            case UP:
                return SLOTS_INPUT;
            case DOWN:
                return SLOTS_OUTPUT;
            default:
                return SLOTS_ALL;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index != INPUT_SLOT && index != CATALYST_SLOT) {
            return false;
        }

        ItemStack stackInSlot = getStackInSlot(index);
        if (stack.isEmpty() || (!stackInSlot.isEmpty() && !stackInSlot.isItemEqual(stack))) {
            return false;
        }

        if (index == INPUT_SLOT) {
            return isUngradedMainPart(stack);
        } else {
            return getCatalystTier(stack) > 0;
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return index != INPUT_SLOT && index != CATALYST_SLOT;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.silentgear.part_analyzer");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new PartAnalyzerContainer(id, playerInventory, this, fields);
    }
}
