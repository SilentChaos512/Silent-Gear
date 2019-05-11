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

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.type.PartMain;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.tile.TileSidedInventorySL;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class TilePartAnalyzer extends TileSidedInventorySL implements ITickable {
    static final int BASE_ANALYZE_TIME = TimeUtils.ticksFromSeconds(5);
    private static final List<Tag<Item>> CATALYST_TAGS = ImmutableList.of(
            new ItemTags.Wrapper(SilentGear.getId("analyzer_catalyst/tier1")),
            new ItemTags.Wrapper(SilentGear.getId("analyzer_catalyst/tier2"))
    );

    static final int INPUT_SLOT = 0;
    static final int CATALYST_SLOT = 1;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT, CATALYST_SLOT};
    private static final int[] SLOTS_OUTPUT = {2, 3, 4, 5};
    static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;
    private static final int[] SLOTS_ALL = IntStream.rangeClosed(0, INVENTORY_SIZE).toArray();

    @SyncVariable(name = "progress")
    int progress = 0;
    private boolean requireClientSync = false;

    public TilePartAnalyzer() {
        super(ModTileEntities.PART_ANALYZER.type(), INVENTORY_SIZE);
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
        ItemStack stack = getStackInSlot(INPUT_SLOT);
        if (PartManager.from(stack) instanceof PartMain && MaterialGrade.fromStack(stack) == MaterialGrade.NONE) {
            return stack;
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
        return PartManager.from(stack) instanceof PartMain;
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tags = new NBTTagCompound();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);

        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            NBTTagCompound itemTags = input.serializeNBT();
            tags.put("input_item", itemTags);
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
            itemTags.putByte("Slot", (byte) INPUT_SLOT);
            tagList.add(itemTags);
        }
        tags.put("Items", tagList);
        return tags;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        NBTTagCompound tags = packet.getNbtCompound();
        SyncVariable.Helper.readSyncVars(this, tags);

        if (tags.contains("input_item")) {
            setInventorySlotContents(INPUT_SLOT, ItemStack.read(tags.getCompound("input_item")));
        } else {
            setInventorySlotContents(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        switch (side) {
            case UP:
                return SLOTS_INPUT.clone();
            case DOWN:
                return SLOTS_OUTPUT.clone();
            default:
                return SLOTS_ALL.clone();
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
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index != INPUT_SLOT;
    }

    private final LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if (!this.removed && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == EnumFacing.UP)
                return handlers[0].cast();
            if (side == EnumFacing.DOWN)
                return handlers[1].cast();
            return handlers[2].cast();
        }
        return super.getCapability(cap, side);
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
