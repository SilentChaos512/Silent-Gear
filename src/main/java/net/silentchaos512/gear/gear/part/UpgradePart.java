/*
 * Silent Gear -- UpgradePart
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

package net.silentchaos512.gear.gear.part;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.utils.Color;

import java.util.List;

public class UpgradePart extends AbstractGearPart implements IUpgradePart {
    public UpgradePart(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public PartType getType() {
        return PartType.MISC_UPGRADE;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.ANY;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.MISC_UPGRADE.getSerializer();
    }

    @Override
    public int getColor(PartData part, ItemStack gear, int layer, int animationFrame) {
        return Color.VALUE_WHITE;
    }

    @Override
    public void addInformation(PartData part, ItemStack gear, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(1, part.getDisplayName(gear));
    }

    @Override
    public boolean canAddToGear(ItemStack gear, PartData part) {
        // TODO: Temp fix. Should define this in JSON...
        if (this.getId().equals(Const.Parts.MISC_SPOON.getId()))
            return gear.getItem() == ModItems.PICKAXE.get();
        return true;
    }
}
