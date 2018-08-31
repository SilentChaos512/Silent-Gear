/*
 * Silent Gear -- BlueprintPackage
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.item.ItemLootContainer;

import javax.annotation.Nullable;
import java.util.List;

public class BlueprintPackage extends ItemLootContainer {
    public BlueprintPackage(ResourceLocation defaultLootTable) {
        super(defaultLootTable);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.ITALIC + SilentGear.i18n.subText(this, "desc1"));
        tooltip.add(TextFormatting.ITALIC + SilentGear.i18n.subText(this, "desc2"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
