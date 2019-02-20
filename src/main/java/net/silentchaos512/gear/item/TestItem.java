/*
 * Silent Gear -- TestItem
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

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.item.IColoredItem;

public class TestItem extends ItemPickaxe implements IColoredItem {
    public TestItem() {
        super(ItemTier.IRON, 0, 0, new Properties().group(SilentGear.ITEM_GROUP));
    }

    @Override
    public IItemColor getColorHandler() {
        return (stack, tintIndex) -> {
            if (tintIndex == 0) return 0xFFFFFF;
            else if (tintIndex == 1) return 3949738;
            else if (tintIndex == 2) return 0xFFFFFF;
            return 0xFFFFFF;
        };
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (playerIn instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
            LibTriggers.GENERIC_INT.trigger(playerMP, new ResourceLocation(SilentGear.MOD_ID, "test_val"), 42);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
    }
}
