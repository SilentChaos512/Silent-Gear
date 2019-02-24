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

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeRepairItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.lib.collection.StackList;

/**
 * This replaces vanilla's item repair recipe. That recipe deletes all NBT, so the results would be
 * disastrous on SGear items. This blocks {@link ICoreItem} from matching. For all others, this is
 * passed back to the vanilla version.
 *
 * @since 0.3.2
 */
public final class RepairItemRecipeFix extends RecipeRepairItem {
    private RepairItemRecipeFix(ResourceLocation p_i48163_1_) {
        super(p_i48163_1_);
        SilentGear.LOGGER.debug("RepairItemRecipeFix init");
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        ItemStack gearStack = StackList.from(inv).firstMatch(s -> s.getItem() instanceof ICoreItem);
        return gearStack.isEmpty() && super.matches(inv, worldIn);
    }

    public static final class Serializer implements IRecipeSerializer<RepairItemRecipeFix> {
        public static final Serializer INSTANCE = new Serializer();
        private static final ResourceLocation NAME = new ResourceLocation(SilentGear.MOD_ID, "repair_item_fix");

        @Override
        public RepairItemRecipeFix read(ResourceLocation recipeId, JsonObject json) {
            SilentGear.LOGGER.debug("RepairItemRecipeFix read json");
            return new RepairItemRecipeFix(recipeId);
        }

        @Override
        public RepairItemRecipeFix read(ResourceLocation recipeId, PacketBuffer buffer) {
            SilentGear.LOGGER.debug("RepairItemRecipeFix read packet");
            return new RepairItemRecipeFix(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, RepairItemRecipeFix recipe) {}

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }
}
