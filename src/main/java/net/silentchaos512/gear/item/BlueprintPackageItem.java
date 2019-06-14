/*
 * Silent Gear -- BlueprintPackageItem
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.item.LootContainerItem;

import javax.annotation.Nullable;
import java.util.List;

public class BlueprintPackageItem extends LootContainerItem {
    public BlueprintPackageItem(ResourceLocation defaultLootTable) {
        super(defaultLootTable, new Properties().group(SilentGear.ITEM_GROUP));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.silentgear.blueprint_package.desc1").applyTextStyle(TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent("item.silentgear.blueprint_package.desc2").applyTextStyle(TextFormatting.ITALIC));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
