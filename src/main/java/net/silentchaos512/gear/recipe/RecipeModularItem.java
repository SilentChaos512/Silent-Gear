package net.silentchaos512.gear.recipe;

import com.google.common.base.Predicate;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.lib.recipe.RecipeBaseSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecipeModularItem extends RecipeBaseSL {

    public final ICoreItem item;

    public RecipeModularItem(ICoreItem item) {
        this.item = item;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = getRecipeOutput();
        Collection<ItemStack> parts = getComponents(inv);
        List<ItemPartData> data = new ArrayList<>();
        ItemStack toolHeadDebug = ItemStack.EMPTY;

        boolean foundRod = false;
        boolean foundBowstring = false;
        boolean foundTip = false;
        for (ItemStack stack : parts) {
            ItemPart part = PartRegistry.get(stack);
            if (stack.getItem() instanceof ToolHead) {
                toolHeadDebug = stack;
                ToolHead itemToolHead = (ToolHead) stack.getItem();
                if (!itemToolHead.getToolClass(stack).equals(this.item.getItemClassName()))
                    return ItemStack.EMPTY;
                data.addAll(itemToolHead.getAllParts(stack));
            } else if (part instanceof ToolPartRod) {
                if (!foundRod)
                    data.add(ItemPartData.fromStack(stack));
                foundRod = true;
            } else if (part instanceof BowPartString) {
                if (!foundBowstring)
                    data.add(ItemPartData.fromStack(stack));
                foundBowstring = true;
            } else if (part instanceof ToolPartTip) {
                if (!foundTip)
                    data.add(ItemPartData.fromStack(stack));
                foundTip = true;
            }
            // TODO
        }

        return this.item.construct((Item) this.item, data);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        Collection<ItemStack> parts = getComponents(inv);
        return item.matchesRecipe(parts);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack((Item) this.item);
    }

    private Collection<ItemStack> getComponents(InventoryCrafting inv) {
        List<ItemStack> parts = new ArrayList<>();
        parts.addAll(getComponents(inv, s -> s.getItem() instanceof ToolHead));
        parts.addAll(getComponents(inv, s -> PartRegistry.get(s) instanceof ToolPartRod));
        parts.addAll(getComponents(inv, s -> PartRegistry.get(s) instanceof BowPartString));
        parts.addAll(getComponents(inv, s -> PartRegistry.get(s) instanceof ToolPartTip));
        return parts;
    }

    private Collection<ItemStack> getComponents(InventoryCrafting inv, Predicate<ItemStack> predicate) {
        List<ItemStack> parts = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && predicate.apply(stack))
                parts.add(stack);
        }
        return parts;
    }
}
