package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.lib.tile.TileInventorySL;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class CraftingStationTileEntity extends TileInventorySL {
    public static final int CRAFTING_GRID_SIZE = 3 * 3;
    public static final int SIDE_INVENTORY_SIZE = 3 * 6;

    public static final int CRAFTING_GRID_START = 0;
    public static final int SIDE_INVENTORY_START = CRAFTING_GRID_SIZE;

    public CraftingStationTileEntity() {
        super(ModTileEntities.CRAFTING_STATION.type(), CRAFTING_GRID_SIZE + SIDE_INVENTORY_SIZE);
    }

    public NonNullList<Pair<ItemStack, IInventory>> getAdjacentInventories() {
        NonNullList<Pair<ItemStack, IInventory>> list = NonNullList.create();

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
        sendUpdate();
    }
}
