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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.lib.tile.TileInventorySL;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;

public class TileCraftingStation extends TileInventorySL {
    public static final int CRAFTING_GRID_SIZE = 3 * 3;
    public static final int SIDE_INVENTORY_SIZE = 3 * 6;

    public static final int CRAFTING_GRID_START = 0;
    public static final int SIDE_INVENTORY_START = CRAFTING_GRID_SIZE;

    public TileCraftingStation() {
        super(ModTileEntities.CRAFTING_STATION.type(), CRAFTING_GRID_SIZE + SIDE_INVENTORY_SIZE);
    }

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
    public ITextComponent getName() {
        return new TextComponentTranslation("block.silengear.crafting_station");
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
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
