/*
 * Silent Gear -- PartHighlight
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

package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PartHighlight extends ItemPart {
    public PartHighlight(ResourceLocation registryName, PartOrigins origin) {
        super(registryName, origin);
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
    public void addInformation(ItemPartData part, ItemStack gear, World world, List<String> tooltip, boolean advanced) { }

    @Override
    public String getTypeName() {
        return "highlight";
    }

    @Nullable
    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        String frameStr = "bow".equals(gearClass) && animationFrame == 3 ? "_3" : "";
        PartDisplayProperties props = getDisplayProperties(part, gear, animationFrame);
        String path = "items/" + gearClass + "/_" + props.textureSuffix + frameStr;
        return new ResourceLocation(props.textureDomain, path);
    }
}
