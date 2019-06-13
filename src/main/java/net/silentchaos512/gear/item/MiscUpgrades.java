/*
 * Silent Gear -- MiscUpgrades
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.utils.Lazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

public enum MiscUpgrades implements IItemProvider {
    SPOON(item -> item == ModItems.pickaxe),
    RED_CARD(Objects::nonNull);

    private final Lazy<Item> item;
    private final ResourceLocation partId;

    MiscUpgrades(final Predicate<ICoreItem> canApplyTo) {
        this.item = Lazy.of(() -> new Item(new Item.Properties().group(SilentGear.ITEM_GROUP)) {
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                tooltip.add(new TranslationTextComponent(getTranslationKey() + ".desc").applyTextStyle(TextFormatting.ITALIC));
            }
        });
        this.partId = SilentGear.getId("misc/" + name().toLowerCase(Locale.ROOT));
    }

    @Override
    public Item asItem() {
        return this.item.get();
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT) + "_upgrade";
    }

    public ResourceLocation getPartId() {
        return partId;
    }
}
