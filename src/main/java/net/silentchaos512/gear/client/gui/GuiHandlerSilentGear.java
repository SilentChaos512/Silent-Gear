package net.silentchaos512.gear.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.craftingstation.ContainerCraftingStation;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.gear.block.craftingstation.TileCraftingStation;

public class GuiHandlerSilentGear implements IGuiHandler {

    public static final int ID_CRAFTING = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            SilentGear.log.warning(String.format("Missing TileEntity at %d %d %d!", x, y, z));
            return null;
        }

        switch (ID) {
            case ID_CRAFTING:
                if (tile instanceof TileCraftingStation) {
                    TileCraftingStation tileCrafting = (TileCraftingStation) tile;
                    return new ContainerCraftingStation(player.inventory, world, pos, tileCrafting);
                }
                return null;
            default:
                SilentGear.log.warning("No GUI with ID " + ID + "!");
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            SilentGear.log.warning(String.format("Missing TileEntity at %d %d %d!", x, y, z));
            return null;
        }

        switch (ID) {
            case ID_CRAFTING:
                if (tile instanceof TileCraftingStation) {
                    TileCraftingStation tileCrafting = (TileCraftingStation) tile;
                    ContainerCraftingStation container = new ContainerCraftingStation(player.inventory, world, pos, tileCrafting);
                    return new GuiCraftingStation(tileCrafting, container);
                }
                return null;
            default:
                SilentGear.log.warning("No GUI with ID " + ID + "!");
                return null;
        }
    }

}
