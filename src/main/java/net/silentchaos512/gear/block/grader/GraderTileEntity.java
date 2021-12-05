package net.silentchaos512.gear.block.grader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.init.ModBlockEntities;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;
import net.silentchaos512.utils.EnumUtils;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class GraderTileEntity extends LockableSidedInventoryTileEntity {
    static final int BASE_ANALYZE_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 1 : 5);

    static final int INPUT_SLOT = 0;
    static final int CATALYST_SLOT = 1;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT, CATALYST_SLOT};
    private static final int[] SLOTS_OUTPUT = {2, 3, 4, 5};
    static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;
    private static final int[] SLOTS_ALL = IntStream.rangeClosed(0, INVENTORY_SIZE).toArray();

    @SyncVariable(name = "progress")
    private int progress = 0;
    private MaterialGrade lastGradeAttempt = MaterialGrade.NONE;
    private boolean requireClientSync = false;

    private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return progress;
                case 1:
                    return lastGradeAttempt.ordinal();
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
                    lastGradeAttempt = EnumUtils.byOrdinal(value, MaterialGrade.NONE);
                    break;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public GraderTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATERIAL_GRADER.get(), INVENTORY_SIZE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GraderTileEntity blockEntity) {
        // Don't waste time if there is no input or no free output slots
        ItemStack input = blockEntity.getInputStack();
        if (input.isEmpty()) return;

        int outputSlot = blockEntity.getFreeOutputSlot();
        if (outputSlot < 0) return;

        ItemStack catalyst = blockEntity.getCatalystStack();
        int catalystTier = getCatalystTier(catalyst);
        if (catalystTier < 1) return;

        MaterialInstance material = MaterialInstance.from(input);
        if (material != null && material.getGrade() != MaterialGrade.getMax()) {
            if (blockEntity.progress < BASE_ANALYZE_TIME) {
                ++blockEntity.progress;
            }

            if (blockEntity.progress >= BASE_ANALYZE_TIME && !level.isClientSide) {
                blockEntity.progress = 0;
                catalyst.shrink(1);
                blockEntity.tryGradeItem(input, catalystTier, material);
            }
        } else {
            blockEntity.progress = 0;
        }

//        if (requireClientSync) {
//            BlockState state = world.getBlockState(pos);
//            world.notifyBlockUpdate(pos, state, state, 3);
//            requireClientSync = false;
//        }
    }

    private void tryGradeItem(ItemStack input, int catalystTier, IMaterialInstance material) {
        MaterialGrade targetGrade = MaterialGrade.selectWithCatalyst(SilentGear.RANDOM, catalystTier);
        this.lastGradeAttempt = targetGrade;

        if (targetGrade.ordinal() > material.getGrade().ordinal()) {
            // Assign grade, move to output slot
            ItemStack stack = input.split(1);
            targetGrade.setGradeOnStack(stack);

            InventoryUtils.mergeItem(this, 2, 2 + SLOTS_OUTPUT.length, stack);
        }
    }

    @Override
    public boolean isEmpty() {
        return getInputStack().isEmpty();
    }

    private ItemStack getInputStack() {
        ItemStack stack = getItem(INPUT_SLOT);
        if (!stack.isEmpty()) {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null && material.getGrade() != MaterialGrade.getMax()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getCatalystStack() {
        return getItem(CATALYST_SLOT);
    }

    private int getFreeOutputSlot() {
        for (int slot : SLOTS_OUTPUT) {
            if (getItem(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    static boolean canAcceptInput(ItemStack stack) {
        MaterialInstance material = MaterialInstance.from(stack);
        return material != null && material.getGrade() != MaterialGrade.getMax();
    }

    public static int getCatalystTier(ItemStack stack) {
        if (!stack.isEmpty()) {
            for (int i = ModTags.Items.GRADER_CATALYSTS_TIERS.size() - 1; i >= 0; --i) {
                if (stack.is(ModTags.Items.GRADER_CATALYSTS_TIERS.get(i))) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    @Override
    public void load(CompoundTag tags) {
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    @Override
    public CompoundTag save(CompoundTag tags) {
        CompoundTag compoundTag = super.save(tags);
        SyncVariable.Helper.writeSyncVars(this, compoundTag, SyncVariable.Type.WRITE);
        return compoundTag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, this::getTagsForUpdatePacket);
    }

    private CompoundTag getTagsForUpdatePacket(BlockEntity be) {
        CompoundTag tags = getUpdateTag();

        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            CompoundTag itemTags = input.serializeNBT();
            tags.put("input_item", itemTags);
        }

        return tags;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);
//        tags.putInt("progress", this.progress);

        ListTag tagList = new ListTag();
        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            CompoundTag itemTags = input.serializeNBT();
            itemTags.putByte("Slot", (byte) INPUT_SLOT);
            tagList.add(itemTags);
        }
        tags.put("Items", tagList);
        return tags;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        CompoundTag tags = packet.getTag();
        SyncVariable.Helper.readSyncVars(this, tags);
//        this.progress = tags.getInt("progress");

        if (tags.contains("input_item")) {
            setItem(INPUT_SLOT, ItemStack.of(tags.getCompound("input_item")));
        } else {
            setItem(INPUT_SLOT, ItemStack.EMPTY);
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
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index != INPUT_SLOT && index != CATALYST_SLOT) {
            return false;
        }

        ItemStack stackInSlot = getItem(index);
        if (stack.isEmpty() || (!stackInSlot.isEmpty() && !stackInSlot.sameItem(stack))) {
            return false;
        }

        if (index == INPUT_SLOT) {
            return canAcceptInput(stack);
        } else {
            return getCatalystTier(stack) > 0;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index != INPUT_SLOT && index != CATALYST_SLOT;
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.silentgear.material_grader");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new GraderContainer(id, playerInventory, this, fields);
    }
}
