/*
 * Silent Gear -- PartBinding
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

package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;

import java.util.List;

public class PartBinding extends ItemPart implements IUpgradePart {
    public PartBinding(ResourceLocation registryName, PartOrigins origin) {
        super(registryName, origin);
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
    public void addInformation(ItemPartData part, ItemStack gear, World world, List<String> tooltip, boolean advanced) { }

    @Override
    public String getTypeName() {
        return "binding";
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
