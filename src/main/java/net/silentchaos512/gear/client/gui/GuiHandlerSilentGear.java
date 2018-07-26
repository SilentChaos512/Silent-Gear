/*
 * Silent Gear
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms instance the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * instance the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty instance
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy instance the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.analyzer.ContainerPartAnalyzer;
import net.silentchaos512.gear.block.analyzer.GuiPartAnalyzer;
import net.silentchaos512.gear.block.analyzer.TilePartAnalyzer;
import net.silentchaos512.gear.block.craftingstation.ContainerCraftingStation;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.gear.block.craftingstation.TileCraftingStation;

public class GuiHandlerSilentGear implements IGuiHandler {

    public enum GuiType {
        INVALID,
        CRAFTING_STATION,
        PART_ANALYZER;

        public final int id = ordinal() - 1;

        static GuiType byId(int id) {
            for (GuiType type : values())
                if (type.id == id)
                    return type;
            return INVALID;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            SilentGear.log.warn("Missing TileEntity at ({}, {}, {})!", x, y, z);
            return null;
        }

        switch (GuiType.byId(ID)) {
            case CRAFTING_STATION:
                if (tile instanceof TileCraftingStation) {
                    TileCraftingStation tileCrafting = (TileCraftingStation) tile;
                    return new ContainerCraftingStation(player.inventory, world, pos, tileCrafting);
                }
                return null;
            case PART_ANALYZER:
                if (tile instanceof TilePartAnalyzer) {
                    TilePartAnalyzer tileAnalyzer = (TilePartAnalyzer) tile;
                    return new ContainerPartAnalyzer(player.inventory, tileAnalyzer);
                }
            default:
                SilentGear.log.warn("No GUI with ID {}!", ID);
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            SilentGear.log.warn("Missing TileEntity at ({}, {}, {})!", x, y, z);
            return null;
        }

        switch (GuiType.byId(ID)) {
            case CRAFTING_STATION:
                if (tile instanceof TileCraftingStation) {
                    TileCraftingStation tileCrafting = (TileCraftingStation) tile;
                    ContainerCraftingStation container = new ContainerCraftingStation(player.inventory, world, pos, tileCrafting);
                    return new GuiCraftingStation(tileCrafting, container);
                }
                return null;
            case PART_ANALYZER:
                if (tile instanceof TilePartAnalyzer) {
                    TilePartAnalyzer tileAnalyzer = (TilePartAnalyzer) tile;
                    return new GuiPartAnalyzer(player.inventory, tileAnalyzer);
                }
            default:
                SilentGear.log.warn("No GUI with ID {}!", ID);
                return null;
        }
    }

}
