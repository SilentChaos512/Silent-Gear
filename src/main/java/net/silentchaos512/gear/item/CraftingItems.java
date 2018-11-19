/*
 * Silent Gear -- CraftingItems
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

package net.silentchaos512.gear.item;

import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.lib.item.IEnumItems;

import javax.annotation.Nonnull;

public enum CraftingItems implements IEnumItems<CraftingItems, Item> {
    BLUEPRINT_PAPER,
    UPGRADE_BASE,
    ADVANCED_UPGRADE_BASE,
    ROUGH_ROD,
    STONE_ROD,
    IRON_ROD,
    CRIMSON_IRON_INGOT,
    CRIMSON_STEEL_INGOT,
    DIAMOND_SHARD,
    EMERALD_SHARD,
    LEATHER_SCRAP,
    SINEW,
    DRIED_SINEW,
    SINEW_FIBER,
    FLAX_FIBER,
    FLAX_STRING,
    BLACK_DYE,
    BLUE_DYE;

    private final Item item;

    CraftingItems() {
        this.item = new Item();
    }

    @Nonnull
    @Override
    public CraftingItems getEnum() {
        return this;
    }

    @Nonnull
    @Override
    public Item getItem() {
        return this.item;
    }

    public static void registerOreDict() {
        OreDictionary.registerOre("dyeBlack", BLACK_DYE.item);
        OreDictionary.registerOre("dyeBlue", BLUE_DYE.item);
        OreDictionary.registerOre("ingotCrimsonIron", CRIMSON_IRON_INGOT.item);
        OreDictionary.registerOre("ingotCrimsonSteel", CRIMSON_STEEL_INGOT.item);
        OreDictionary.registerOre("nuggetDiamond", DIAMOND_SHARD.item);
        OreDictionary.registerOre("nuggetEmerald", EMERALD_SHARD.item);
        OreDictionary.registerOre("rodIron", IRON_ROD.item);
        OreDictionary.registerOre("rodStone", STONE_ROD.item);
        OreDictionary.registerOre("stickIron", IRON_ROD.item);
        OreDictionary.registerOre("stickStone", STONE_ROD.item);
        OreDictionary.registerOre("string", FLAX_STRING.item);
    }
}
