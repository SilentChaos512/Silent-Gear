/*
 * Silent Gear -- HighlightPart
 * Copyright (C) 2019 SilentChaos512
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

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IPartDisplay;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;

import javax.annotation.Nullable;

public class HighlightPart extends AbstractGearPart {
    public HighlightPart(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public PartType getType() {
        return PartType.HIGHLIGHT;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.HIGHLIGHT;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.HIGHLIGHT.getSerializer();
    }

    @Nullable
    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        String frameStr = gearClass == GearType.BOW && animationFrame == 3 ? "_3" : "";
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        String path = "items/" + gearClass + "/_" + props.getTextureSuffix() + frameStr;
        return new ResourceLocation(props.getTextureDomain(), path);
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
