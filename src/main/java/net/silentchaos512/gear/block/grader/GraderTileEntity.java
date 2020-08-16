package net.silentchaos512.gear.block.grader;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class GraderTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity {
    static final int BASE_ANALYZE_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 1 : 5);

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
            return GraderTileEntity.this.progress;
        }

        @Override
        public void set(int index, int value) {
            GraderTileEntity.this.progress = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public GraderTileEntity() {
        super(ModTileEntities.MATERIAL_GRADER.get(), INVENTORY_SIZE);
    }

    @Override
    public void tick() {
        if (world == null) return;

        // Don't waste time if there is no input or no free output slots
        ItemStack input = getInputStack();
        if (input.isEmpty()) return;

        int outputSlot = getFreeOutputSlot();
        if (outputSlot < 0) return;

        ItemStack catalyst = getCatalystStack();
        int catalystTier = getCatalystTier(catalyst);
        if (catalystTier < 1) return;

        MaterialInstance material = MaterialInstance.from(input);
        if (material != null && material.getGrade() != MaterialGrade.SSS) {
            if (progress < BASE_ANALYZE_TIME) {
                ++progress;
            }

            if (progress >= BASE_ANALYZE_TIME && !world.isRemote) {
                progress = 0;
                catalyst.shrink(1);
                tryGradeItem(input, catalystTier, material);
            }
        } else {
            progress = 0;
        }

//        if (requireClientSync) {
//            BlockState state = world.getBlockState(pos);
//            world.notifyBlockUpdate(pos, state, state, 3);
//            requireClientSync = false;
//        }
    }

    private void tryGradeItem(ItemStack input, int catalystTier, IMaterialInstance material) {
        MaterialGrade targetGrade = MaterialGrade.selectWithCatalyst(SilentGear.RANDOM, catalystTier);
        if (targetGrade.ordinal() > material.getGrade().ordinal()) {
            // Assign grade, move to output slot
            ItemStack stack = input.split(1);
            targetGrade.setGradeOnStack(stack);

            InventoryUtils.mergeItem(this, 2, 2 + SLOTS_OUTPUT.length, stack);
//                    if (input.getCount() <= 0) {
//                        for (int slot : SLOTS_INPUT) {
//                            if (getStackInSlot(slot).isEmpty()) {
//                                setInventorySlotContents(slot, ItemStack.EMPTY);
//                            }
//                        }
//                    }
        }
    }

    @Override
    public boolean isEmpty() {
        return getInputStack().isEmpty();
    }

    private ItemStack getInputStack() {
        ItemStack stack = getStackInSlot(INPUT_SLOT);
        if (!stack.isEmpty()) {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null && material.getGrade() != MaterialGrade.SSS) {
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

    static boolean canAcceptInput(ItemStack stack) {
        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && material.getGrade() != MaterialGrade.SSS;
    }

    static int getCatalystTier(ItemStack stack) {
        if (!stack.isEmpty()) {
            for (int i = ModTags.Items.GRADER_CATALYSTS_TIERS.size() - 1; i >= 0; --i) {
                if (stack.getItem().isIn(ModTags.Items.GRADER_CATALYSTS_TIERS.get(i))) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    @Override
    public void read(BlockState state, CompoundNBT tags) {
        super.read(state, tags);
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
            return canAcceptInput(stack);
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
        return new TranslationTextComponent("container.silentgear.material_grader");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new GraderContainer(id, playerInventory, this, fields);
    }
}
