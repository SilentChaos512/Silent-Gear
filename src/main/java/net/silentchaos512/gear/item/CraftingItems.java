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
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.util.generator.TagGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public enum CraftingItems implements IItemProvider, IStringSerializable {
    BLUEPRINT_PAPER("forge:paper/blueprint", "forge:paper"),
    UPGRADE_BASE("silentgear:upgrade_bases/basic"),
    ADVANCED_UPGRADE_BASE("silentgear:upgrade_bases/advanced"),
    CRIMSON_IRON_INGOT("forge:ingots/crimson_iron", "forge:ingots"),
    CRIMSON_STEEL_INGOT("forge:ingots/crimson_steel", "forge:ingots"),
    CRIMSON_IRON_NUGGET("forge:nuggets/crimson_iron", "forge:nuggets"),
    CRIMSON_STEEL_NUGGET("forge:nuggets/crimson_steel", "forge:nuggets"),
    DIAMOND_SHARD("forge:nuggets/diamond", "forge:nuggets"),
    EMERALD_SHARD("forge:nuggets/emerald", "forge:nuggets"),
    LEATHER_SCRAP,
    SINEW,
    DRIED_SINEW,
    SINEW_FIBER("forge:string/sinew", "forge:string"),
    FLAX_FIBER,
    FLAX_STRING("forge:string/flax", "forge:string"),
    BLACK_DYE("forge:dyes/black", "forge:dyes"),
    BLUE_DYE("forge:dyes/blue", "forge:dyes"),
    // Rods
    ROUGH_ROD("silentgear:rods/rough", "forge:rods"),
    STONE_ROD("forge:rods/stone", "forge:rods"),
    IRON_ROD("forge:rods/iron", "forge:rods"),
    NETHERWOOD_STICK("silentgear:rods/netherwood", "forge:rods"),
    // Tip Upgrades
    IRON_TIPPED_UPGRADE,
    GOLD_TIPPED_UPGRADE,
    DIAMOND_TIPPED_UPGRADE,
    EMERALD_TIPPED_UPGRADE,
    REDSTONE_COATED_UPGRADE,
    GLOWSTONE_COATED_UPGRADE,
    LAPIS_COATED_UPGRADE,
    QUARTZ_TIPPED_UPGRADE,
    // Grips
    LEATHER_WRAPPINGS,
    // Bowstrings
    PLAIN_BOWSTRING("silentgear:bowstrings/plain", "silentgear:bowstrings"),
    FLAX_BOWSTRING("silentgear:bowstrings/flax", "silentgear:bowstrings"),
    SINEW_BOWSTRING("silentgear:bowstrings/sinew", "silentgear:bowstrings"),
    // Misc Upgrades
    SPOON_UPGRADE,
    RED_CARD_UPGRADE
    ;

    private final Item item;
    @Nullable private final Tag<Item> tag;
    @Nullable private final Tag<Item> groupTag;

    CraftingItems() {
        this(null, null);
    }

    CraftingItems(@Nullable String itemTag) {
        this(itemTag, null);
    }

    CraftingItems(@Nullable String itemTag, @Nullable String groupTag) {
        this.item = new ItemInternal();
        this.tag = itemTag != null
                ? TagGenerator.item(new ResourceLocation(itemTag), getName())
                : null;
        this.groupTag = groupTag != null && this.tag != null
                ? TagGenerator.item(new ResourceLocation(groupTag), this.tag)
                : null;
    }

    @Override
    public Item asItem() {
        return this.item;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Nullable
    public Tag<Item> getTag() {
        return tag;
    }

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
