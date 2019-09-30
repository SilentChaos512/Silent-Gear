package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silentchaos512.lib.tile.TileInventorySL;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class TileCraftingStation extends TileInventorySL {
    public static final int CRAFTING_GRID_SIZE = 3 * 3;
    public static final int GEAR_PARTS_SIZE = 3 * 2;
    public static final int SIDE_INVENTORY_SIZE = 3 * 6;

    public static final int CRAFTING_GRID_START = 0;
    public static final int GEAR_PARTS_START = CRAFTING_GRID_START + CRAFTING_GRID_SIZE;
    public static final int SIDE_INVENTORY_START = GEAR_PARTS_START + GEAR_PARTS_SIZE;

    private static final int CURRENT_VERSION = 1;

    public NonNullList<Pair<ItemStack, IInventory>> getAdjacentInventories() {
        NonNullList<Pair<ItemStack, IInventory>> list = NonNullList.create();

        for (EnumFacing side : Arrays.asList(null, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST)) {
            BlockPos pos = side == null ? this.pos : this.pos.offset(side);
            TileEntity te = this.world.getTileEntity(pos);

            if (te instanceof IInventory) {
                IBlockState state = this.world.getBlockState(pos);
                RayTraceResult raytrace = new RayTraceResult(new Vec3d(0, 0, 0), side, pos);
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
    public String getName() {
        return "crafting_station";
    }

    @Override
    public int getSizeInventory() {
        return CRAFTING_GRID_SIZE + GEAR_PARTS_SIZE + SIDE_INVENTORY_SIZE;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        return super.writeToNBT(tags);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
