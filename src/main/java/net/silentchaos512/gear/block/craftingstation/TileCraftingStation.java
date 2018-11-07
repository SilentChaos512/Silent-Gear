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
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.tile.TileInventorySL;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class TileCraftingStation extends TileInventorySL {
    public static final int CRAFTING_GRID_SIZE = 3 * 3;
    public static final int GEAR_PARTS_SIZE = 3 * 2;
    public static final int SIDE_INVENTORY_SIZE = 3 * 6;

    // Slot order by index = craft grid, parts grid, side inventory, player + hotbar, output slot
    public static final int CRAFTING_GRID_START = 0;
    public static final int GEAR_PARTS_START = CRAFTING_GRID_START + CRAFTING_GRID_SIZE;
    public static final int SIDE_INVENTORY_START = GEAR_PARTS_START + GEAR_PARTS_SIZE;

    private static final int CURRENT_VERSION = 1;
    private static final String NBT_VERSION = "SGCS_Version";

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
        // "Version updates" will adjust inventory slots if they need to change, but this has to be
        // coded for each version of the tile entity.
        handleVersionUpdates((int) tags.getByte(NBT_VERSION));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags.setByte(NBT_VERSION, (byte) CURRENT_VERSION);
        return super.writeToNBT(tags);
    }

    private void handleVersionUpdates(int previousVersion) {
        if (previousVersion != CURRENT_VERSION) {
            SilentGear.log.info("Crafting Station at {} is updating from version '{}' to '{}'. This should not result in item loss.",
                    pos, previousVersion, CURRENT_VERSION);
        }

        if (previousVersion == 0) {
            // Original without crafting grid retention or part slots
            // Move side inventory to correct location to prevent item loss
            for (int i = 18; i >= 0; --i) { // Original side inventory size is 18 (3x6)
                final ItemStack stack = getStackInSlot(i);
                if (!stack.isEmpty()) {
                    setInventorySlotContents(i + SIDE_INVENTORY_START, stack);
                    setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
