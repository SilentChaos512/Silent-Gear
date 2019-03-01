/*
 * Silent Gear -- GuiHandlerSilentGear
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
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
import net.silentchaos512.gear.block.salvager.ContainerSalvager;
import net.silentchaos512.gear.block.salvager.GuiSalvager;
import net.silentchaos512.gear.block.salvager.TileSalvager;
import net.silentchaos512.gear.item.blueprint.book.ContainerBlueprintBook;
import net.silentchaos512.gear.item.blueprint.book.GuiBlueprintBook;

public class GuiHandlerSilentGear implements IGuiHandler {
    public enum GuiType {
        INVALID,
        CRAFTING_STATION,
        PART_ANALYZER,
        SALVAGER,
        BLUEPRINT_BOOK;

        public final int id = ordinal() - 1;

        static GuiType byId(int id) {
            for (GuiType type : values())
                if (type.id == id)
                    return type;
            return INVALID;
        }

        public void open(EntityPlayer player, World world, BlockPos pos) {
            // FIXME
//            player.openGui(SilentGear.instance, this.id, world, pos.getX(), pos.getY(), pos.getZ());
        }

        public void open(EntityPlayer player, World world, int subtype) {
            // FIXME
//            player.openGui(SilentGear.instance, this.id, world, subtype, 0, 0);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        GuiType guiType = GuiType.byId(ID);

        if (tile == null && guiType != GuiType.BLUEPRINT_BOOK) {
            SilentGear.LOGGER.warn("Missing TileEntity at ({}, {}, {})!", x, y, z);
            return null;
        }

        switch (guiType) {
            case CRAFTING_STATION:
                if (tile instanceof TileCraftingStation) {
                    TileCraftingStation tileCrafting = (TileCraftingStation) tile;
                    return new ContainerCraftingStation(player.inventory, world, tileCrafting);
                }
                return null;
            case PART_ANALYZER:
                if (tile instanceof TilePartAnalyzer) {
                    TilePartAnalyzer tileAnalyzer = (TilePartAnalyzer) tile;
                    return new ContainerPartAnalyzer(player.inventory, tileAnalyzer);
                }
                return null;
            case SALVAGER:
                if (tile instanceof TileSalvager) {
                    TileSalvager tileSalvager = (TileSalvager) tile;
                    return new ContainerSalvager(player.inventory, tileSalvager);
                }
                return null;
            case BLUEPRINT_BOOK:
                EnumHand hand = x == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                ItemStack stack = player.getHeldItem(hand);
                return new ContainerBlueprintBook(stack, player.inventory, hand);
            default:
                SilentGear.LOGGER.warn("No GUI with ID {}!", ID);
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        GuiType guiType = GuiType.byId(ID);

        if (tile == null && guiType != GuiType.BLUEPRINT_BOOK) {
            SilentGear.LOGGER.warn("Missing TileEntity at ({}, {}, {})!", x, y, z);
            return null;
        }

        switch (guiType) {
            case CRAFTING_STATION:
                if (tile instanceof TileCraftingStation) {
                    TileCraftingStation tileCrafting = (TileCraftingStation) tile;
                    ContainerCraftingStation container = new ContainerCraftingStation(player.inventory, world, tileCrafting);
                    return new GuiCraftingStation(container);
                }
                return null;
            case PART_ANALYZER:
                if (tile instanceof TilePartAnalyzer) {
                    TilePartAnalyzer tileAnalyzer = (TilePartAnalyzer) tile;
                    return new GuiPartAnalyzer(player.inventory, tileAnalyzer);
                }
                return null;
            case SALVAGER:
                if (tile instanceof TileSalvager) {
                    TileSalvager tileSalvager = (TileSalvager) tile;
                    return new GuiSalvager(player.inventory, tileSalvager);
                }
            case BLUEPRINT_BOOK:
                ContainerBlueprintBook container = (ContainerBlueprintBook) getServerGuiElement(ID, player, world, x, y, z);
                return container != null ? new GuiBlueprintBook(container) : null;
            default:
                SilentGear.LOGGER.warn("No GUI with ID {}!", ID);
                return null;
        }
    }

}
