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

package net.silentchaos512.gear.parts.type;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartConst;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;

import javax.annotation.Nullable;
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

    @Nullable
    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        return null;
    }

    @Override
    public void addInformation(PartData part, ItemStack gear, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(1, part.getDisplayName(gear));
    }

    @Override
    public boolean isValidFor(ICoreItem gearItem) {
        // TODO: Temp fix. Should define this in JSON...
        if (this.getId().equals(PartConst.MISC_SPOON))
            return gearItem == ModItems.pickaxe;
        return IUpgradePart.super.isValidFor(gearItem);
    }
}
