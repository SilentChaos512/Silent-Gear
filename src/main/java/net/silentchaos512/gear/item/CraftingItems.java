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

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public enum CraftingItems implements IItemProvider, IStringSerializable {
    BLUEPRINT_PAPER,
    UPGRADE_BASE,
    ADVANCED_UPGRADE_BASE,
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
    BLUE_DYE,
    // Rods
    ROUGH_ROD,
    STONE_ROD,
    IRON_ROD,
    NETHERWOOD_STICK,
    // Tip Upgrades
    IRON_TIPPED_UPGRADE,
    GOLD_TIPPED_UPGRADE,
    DIAMOND_TIPPED_UPGRADE,
    EMERALD_TIPPED_UPGRADE,
    REDSTONE_COATED_UPGRADE,
    GLOWSTONE_COATED_UPGRADE,
    LAPIS_COATED_UPGRADE,
    QUARTZ_TIPPED_UPGRADE,
    // Misc Upgrades
    SPOON_UPGRADE,
    RED_CARD_UPGRADE
    ;

    private final Item item;

    CraftingItems() {
        this.item = new ItemInternal();
    }

    @Override
    public Item asItem() {
        return this.item;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /*
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
        OreDictionary.registerOre("stickWood", NETHERWOOD_STICK.item);
        OreDictionary.registerOre("string", FLAX_STRING.item);
    }
    */

    private static final class ItemInternal extends Item {
        ItemInternal() {
            super(new Builder().group(SilentGear.ITEM_GROUP));
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new TextComponentTranslation(getTranslationKey()+ ".desc"));
        }
    }
}
