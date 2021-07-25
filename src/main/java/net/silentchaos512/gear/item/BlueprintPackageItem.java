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

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.item.LootContainerItem;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class BlueprintPackageItem extends LootContainerItem {
    public BlueprintPackageItem(ResourceLocation defaultLootTable) {
        super(defaultLootTable, new Properties().tab(SilentGear.ITEM_GROUP));
    }

    public ResourceLocation getDefaultLootTable() {
        return getLootTable(getStack());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.silentgear.blueprint_package.desc1").withStyle(ChatFormatting.ITALIC));
        tooltip.add(new TranslatableComponent("item.silentgear.blueprint_package.desc2").withStyle(ChatFormatting.ITALIC));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
