/*
 * Silent Gear -- BindingPart
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

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartPositions;

public class BindingPart extends AbstractGearPart implements IUpgradePart {
    public BindingPart(ResourceLocation location) {
        super(location);
    }

    @Override
    public PartType getType() {
        return PartType.BINDING;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.BINDING;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.BINDING.getSerializer();
    }

    @Override
    public boolean isValidFor(ICoreItem gearItem) {
        return gearItem instanceof ICoreTool;
    }

    @Override
    public boolean replacesExisting() {
        return true;
    }
}
