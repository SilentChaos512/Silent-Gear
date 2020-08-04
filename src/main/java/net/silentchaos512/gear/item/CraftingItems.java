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

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public enum CraftingItems implements IItemProvider {
    BLUEPRINT_PAPER,
    TEMPLATE_BOARD,
    UPGRADE_BASE,
    ADVANCED_UPGRADE_BASE,
    CRIMSON_IRON_INGOT,
    CRIMSON_STEEL_INGOT,
    BLAZE_GOLD_INGOT,
    AZURE_SILVER_INGOT,
    AZURE_ELECTRUM_INGOT,
    CRIMSON_IRON_NUGGET,
    CRIMSON_STEEL_NUGGET,
    BLAZE_GOLD_NUGGET,
    AZURE_SILVER_NUGGET,
    AZURE_ELECTRUM_NUGGET,
    CRIMSON_IRON_CHUNKS,
    CRIMSON_IRON_DUST,
    CRIMSON_STEEL_DUST,
    BLAZE_GOLD_DUST,
    AZURE_SILVER_CHUNKS,
    AZURE_SILVER_DUST,
    AZURE_ELECTRUM_DUST,
    DIAMOND_SHARD,
    EMERALD_SHARD,
    BLAZING_DUST,
    GLITTERY_DUST,
    LEATHER_SCRAP,
    SINEW,
    DRIED_SINEW,
    SINEW_FIBER,
    FLAX_FIBER,
    FLAX_STRING,
    // Rods
    ROUGH_ROD,
    STONE_ROD,
    IRON_ROD, // TODO: Remove
    NETHERWOOD_STICK, // TODO: Remove?
    // Misc Upgrades
    SPOON_UPGRADE,
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
            super(new Properties().group(SilentGear.ITEM_GROUP));
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            String descKey = this.getTranslationKey() + ".desc";
            if (I18n.hasKey(descKey)) {
                tooltip.add(new TranslationTextComponent(descKey));
            }
        }

        /*@Override
        public boolean isBeaconPayment(ItemStack stack) {
            return this.isIn(Tags.Items.INGOTS) || this == GLITTERY_DUST.asItem();
        }*/
    }
}
