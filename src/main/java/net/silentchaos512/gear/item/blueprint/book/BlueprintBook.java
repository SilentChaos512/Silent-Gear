/*
 * Silent Gear -- BlueprintBook
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

package net.silentchaos512.gear.item.blueprint.book;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.gui.GuiHandlerSilentGear;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.lib.item.IColoredItem;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class BlueprintBook extends Item implements IBlueprint, IColoredItem {
    // TODO: Store blueprints (container/GUI). Try to pick correct blueprint when crafting.

    public static final int INVENTORY_SIZE = 27;

    private static final int DEFAULT_COLOR = Color.VALUE_WHITE;

    private static final String NBT_ROOT = "SGear_BlueprintBook";
    private static final String NBT_COLOR = "Color";
    private static final String NBT_INVENTORY = "Inventory";
    private static final String NBT_SELECTED = "Selected";

    public BlueprintBook() {
        setContainerItem(this);
    }

    @Override
    public ItemStack getCraftingResult(ItemStack blueprint, Collection<ItemStack> parts) {
        // TODO
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaterialCost(ItemStack blueprint) {
        // TODO
        return 0;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        int subtype = handIn == EnumHand.OFF_HAND ? 1 : 0;
        GuiHandlerSilentGear.GuiType.BLUEPRINT_BOOK.open(playerIn, worldIn, subtype);

        return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(SilentGear.i18n.subText(this, "desc"));
        tooltip.add(SilentGear.i18n.subText(this, "color", String.format("%06x", getCoverColor(stack))));
        tooltip.add(SilentGear.i18n.subText(this, "selected", "not implemented"));
    }

    @Nullable
    static IItemHandler getInventory(ItemStack stack) {
        if (stack.isEmpty()) return null;

        NBTTagCompound tags = getTags(stack);
        if (!tags.hasKey(NBT_INVENTORY))
            tags.setTag(NBT_INVENTORY, new NBTTagList());

        ItemStackHandler stackHandler = new ItemStackHandler(INVENTORY_SIZE);
        NBTTagList tagList = tags.getTagList(NBT_INVENTORY, 10);
        for (int i = 0; i < tagList.tagCount(); ++i)
            stackHandler.setStackInSlot(i, new ItemStack(tagList.getCompoundTagAt(i)));

        return stackHandler;
    }

    static void updateInventory(ItemStack stack, @Nullable IItemHandler itemHandler, EntityPlayer playerIn) {
        if (itemHandler == null) return;

        NBTTagCompound tags = getTags(stack);

        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            tagList.appendTag(itemStack.serializeNBT());
        }

        tags.setTag(NBT_INVENTORY, tagList);
    }

    private static NBTTagCompound getTags(ItemStack stack) {
        return stack.getOrCreateSubCompound(NBT_ROOT);
    }

    @Override
    public IItemColor getColorHandler() {
        return (stack, tintIndex) -> tintIndex == 0 ? getCoverColor(stack) : Color.VALUE_WHITE;
    }

    private static int getCoverColor(ItemStack stack) {
        NBTTagCompound tags = getTags(stack);
        return tags.hasKey(NBT_COLOR) ? tags.getInteger(NBT_COLOR) : DEFAULT_COLOR;
    }
}
