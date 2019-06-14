package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;

public class CraftingStationTileEntity extends LockableSidedInventoryTileEntity {
    public static final int CRAFTING_GRID_SIZE = 3 * 3;
    public static final int SIDE_INVENTORY_SIZE = 3 * 6;

    public static final int CRAFTING_GRID_START = 0;
    public static final int SIDE_INVENTORY_START = CRAFTING_GRID_SIZE;

    public CraftingStationTileEntity() {
        super(ModTileEntities.CRAFTING_STATION.type(), CRAFTING_GRID_SIZE + SIDE_INVENTORY_SIZE);
    }

    public NonNullList<Pair<ItemStack, IInventory>> getAdjacentInventories() {
        NonNullList<Pair<ItemStack, IInventory>> list = NonNullList.create();
        if (world == null) return list;

        for (Direction side : Arrays.asList(null, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST)) {
            BlockPos pos = side == null ? this.pos : this.pos.offset(side);
            TileEntity te = this.world.getTileEntity(pos);

            if (te instanceof IInventory) {
                BlockState state = this.world.getBlockState(pos);
                RayTraceResult raytrace = new BlockRayTraceResult(new Vec3d(0, 0, 0), side, pos, false);
                ItemStack stack = state.getBlock().getPickBlock(state, raytrace, world, pos, null);

                list.add(new ImmutablePair<>(stack, (IInventory) te));
            }
        }

        return list;
    }

    public IInventory getInternalStorage() {
        return this;
    }

    @Override
    public int getSizeInventory() {
        return CRAFTING_GRID_SIZE + SIDE_INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sendUpdate();
    }

    private void sendUpdate() {
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemStack, Direction direction) {
        return false;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.silentgear.crafting_station");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new CraftingStationContainer(id, playerInventory, this);
    }
}
