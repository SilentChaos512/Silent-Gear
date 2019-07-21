/*
 * Silent Gear -- RepairItemRecipeFix
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

package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RepairItemRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.lib.collection.StackList;

/**
 * This replaces vanilla's item repair recipe. That recipe deletes all NBT, so the results would be
 * disastrous on SGear items. This blocks {@link ICoreItem} from matching. For all others, this is
 * passed back to the vanilla version.
 *
 * @since 0.3.2
 */
public class RepairItemRecipeFix extends RepairItemRecipe {
    public RepairItemRecipeFix(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack gearStack = StackList.from(inv).firstMatch(s -> s.getItem() instanceof ICoreItem);
        return gearStack.isEmpty() && super.matches(inv, worldIn);
    }
}
