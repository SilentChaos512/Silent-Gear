package net.silentchaos512.gear.block.craftingstation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.silentchaos512.lib.tile.TileInventorySL;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class TileCraftingStation extends TileInventorySL {
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
    public int getSizeInventory() {
        // TODO Auto-generated method stub
        return 3 * 6;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "crafting_station";
    }
}
