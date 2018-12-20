/*
 * Silent Gear -- GearPartIngredient
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

package net.silentchaos512.gear.crafting.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.parts.PartType;

import javax.annotation.Nullable;

public class GearPartIngredient extends Ingredient {
    private final PartType type;

    public GearPartIngredient(PartType type) {
        this.type = type;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ItemPart part = PartRegistry.get(stack);
        return part != null && part.getType().equals(type);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return super.getMatchingStacks();
    }
}
