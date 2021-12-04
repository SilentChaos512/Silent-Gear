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

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public enum CraftingItems implements ItemLike {
    BLUEPRINT_PAPER,
    TEMPLATE_BOARD,
    UPGRADE_BASE,
    ADVANCED_UPGRADE_BASE,
    BORT,
    CRIMSON_IRON_INGOT,
    CRIMSON_STEEL_INGOT,
    BLAZE_GOLD_INGOT,
    AZURE_SILVER_INGOT,
    AZURE_ELECTRUM_INGOT,
    TYRIAN_STEEL_INGOT,
    CRIMSON_IRON_NUGGET,
    CRIMSON_STEEL_NUGGET,
    BLAZE_GOLD_NUGGET,
    AZURE_SILVER_NUGGET,
    AZURE_ELECTRUM_NUGGET,
    TYRIAN_STEEL_NUGGET,
    RAW_CRIMSON_IRON,
    CRIMSON_IRON_DUST,
    CRIMSON_STEEL_DUST,
    BLAZE_GOLD_DUST,
    RAW_AZURE_SILVER,
    AZURE_SILVER_DUST,
    AZURE_ELECTRUM_DUST,
    TYRIAN_STEEL_DUST,
    DIAMOND_SHARD,
    EMERALD_SHARD,
    NETHER_STAR_FRAGMENT,
    STARMETAL_DUST,
    GLOWING_DUST,
    BLAZING_DUST,
    GLITTERY_DUST,
    CRUSHED_SHULKER_SHELL,
    LEATHER_SCRAP,
    SINEW,
    DRIED_SINEW,
    SINEW_FIBER,
    FINE_SILK,
    FINE_SILK_CLOTH,
    FLAX_FIBER,
    FLAX_STRING,
    FLAX_FLOWERS,
    FLUFFY_PUFF,
    FLUFFY_FABRIC,
    FLUFFY_STRING,
    FLUFFY_FEATHER,
    // Rods
    ROUGH_ROD,
    STONE_ROD,
    IRON_ROD,
    NETHERWOOD_STICK,
    // Misc Upgrades
    SPOON_UPGRADE,
    ROAD_MAKER_UPGRADE,
    WIDE_PLATE_UPGRADE,
    RED_CARD_UPGRADE;

    @SuppressWarnings("NonFinalFieldInEnum")
    private RegistryObject<ItemInternal> item = null;

    @Override
    public Item asItem() {
        if (this.item == null) {
            throw new NullPointerException("CraftingItems accessed too early!");
        }
        return this.item.get();
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static void register(DeferredRegister<Item> items) {
        for (CraftingItems item : values()) {
            item.item = items.register(item.getName(), ItemInternal::new);
        }
    }

    private static final class ItemInternal extends Item {
        ItemInternal() {
            super(new Properties().tab(SilentGear.ITEM_GROUP));
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
            String descKey = this.getDescriptionId() + ".desc";
            if (I18n.exists(descKey)) {
                tooltip.add(new TranslatableComponent(descKey).withStyle(ChatFormatting.ITALIC));
            }
        }

        /*@Override
        public boolean isBeaconPayment(ItemStack stack) {
            return this.isIn(Tags.Items.INGOTS) || this == GLITTERY_DUST.asItem();
        }*/
    }
}
