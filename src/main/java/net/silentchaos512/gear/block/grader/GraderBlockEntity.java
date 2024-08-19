package net.silentchaos512.gear.block.grader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialModifiers;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.lib.util.EnumUtils;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.TimeUtils;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class GraderBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    static final int BASE_ANALYZE_TIME = TimeUtils.ticksFromSeconds(SilentGear.isDevBuild() ? 1 : 5);

    static final int INPUT_SLOT = 0;
    static final int CATALYST_SLOT = 1;
    private static final int[] SLOTS_INPUT = {INPUT_SLOT, CATALYST_SLOT};
    private static final int[] SLOTS_OUTPUT = {2, 3, 4, 5};
    static final int INVENTORY_SIZE = SLOTS_INPUT.length + SLOTS_OUTPUT.length;
    private static final int[] SLOTS_ALL = IntStream.rangeClosed(0, INVENTORY_SIZE).toArray();

    private NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int progress = 0;
    private MaterialGrade lastGradeAttempt = MaterialGrade.NONE;
    private boolean requireClientSync = false;

    private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> lastGradeAttempt.ordinal();
                default -> 0;
            };
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

    public GraderBlockEntity(BlockPos pos, BlockState state) {
        super(SgBlockEntities.MATERIAL_GRADER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GraderBlockEntity blockEntity) {
        // Don't waste time if there is no input or no free output slots
        ItemStack input = blockEntity.getInputStack();
        if (input.isEmpty()) return;

        int outputSlot = blockEntity.getFreeOutputSlot();
        if (outputSlot < 0) return;

        ItemStack catalyst = blockEntity.getCatalystStack();
        int catalystTier = getCatalystTier(catalyst);
        if (catalystTier < 1) return;

        MaterialInstance material = MaterialInstance.from(input);
        if (material != null && canGrade(input)) {
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
    }

    private void tryGradeItem(ItemStack input, int catalystTier, MaterialInstance material) {
        MaterialGrade targetGrade = MaterialGrade.selectWithCatalyst(SilentGear.RANDOM, catalystTier);
        this.lastGradeAttempt = targetGrade;
        var currentGradeMod = material.getModifier(MaterialModifiers.GRADE);

        if (currentGradeMod == null || targetGrade.ordinal() > currentGradeMod.grade().ordinal()) {
            // Assign grade, move to output slot
            ItemStack stack = input.split(1);
            targetGrade.setGradeOnStack(stack);

            InventoryUtils.mergeItem(this, 2, 2 + SLOTS_OUTPUT.length, stack);
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return getInputStack().isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemstack = this.items.get(pSlot);
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, pStack);
        this.items.set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }

        if (pSlot < INVENTORY_SIZE - 1 && !flag) {
            this.progress = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    public static boolean canGrade(ItemStack stack) {
        var material = MaterialInstance.from(stack);
        if (material == null) return false;

        var gradeMod = material.getModifier(MaterialModifiers.GRADE);
        return gradeMod == null || gradeMod.grade() != MaterialGrade.MAX;
    }

    private ItemStack getInputStack() {
        ItemStack stack = getItem(INPUT_SLOT);
        if (!stack.isEmpty() && canGrade(stack)) {
            return stack;
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

    public static int getCatalystTier(ItemStack stack) {
        if (!stack.isEmpty()) {
            for (int i = SgTags.Items.GRADER_CATALYSTS_TIERS.size() - 1; i >= 0; --i) {
                if (stack.is(SgTags.Items.GRADER_CATALYSTS_TIERS.get(i))) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries);
        this.progress = pTag.getInt("Progress");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("Progress", this.progress);
        ContainerHelper.saveAllItems(pTag, this.items, pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tags = super.getUpdateTag(pRegistries);
        tags.putInt("Progress", this.progress);

        ListTag tagList = new ListTag();
        ItemStack input = getInputStack();
        if (!input.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putByte("Slot", (byte) 0);
            tagList.add(input.save(pRegistries, itemTag));
        }
        tags.put("Items", tagList);
        return tags;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);

        CompoundTag tags = pkt.getTag();
        if (tags == null) return;

        this.progress = tags.getInt("Progress");

        if (tags.contains("input_item")) {
            var inputItem = ItemStack.parse(lookupProvider, tags.getCompound("input_item")).orElse(ItemStack.EMPTY);
            setItem(INPUT_SLOT, inputItem);
        } else {
            setItem(INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> SLOTS_INPUT;
            case DOWN -> SLOTS_OUTPUT;
            default -> SLOTS_ALL;
        };
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index != INPUT_SLOT && index != CATALYST_SLOT) {
            return false;
        }

        ItemStack stackInSlot = getItem(index);
        if (stack.isEmpty() || (!stackInSlot.isEmpty() && !ItemStack.isSameItemSameComponents(stackInSlot, stack))) {
            return false;
        }

        if (index == INPUT_SLOT) {
            return canGrade(stack);
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
        return Component.translatable("container.silentgear.material_grader");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new GraderContainer(id, playerInventory, this, fields);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void fillStackedContents(StackedContents pContents) {
        for (ItemStack stack : this.items) {
            pContents.accountStack(stack);
        }
    }
}
