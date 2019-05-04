package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.lib.collection.StackList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ShapedGearCrafting extends ShapedRecipes {
    private final ICoreItem item;

    public ShapedGearCrafting(ICoreItem item, String... lines) {
        super("", lines[0].length(), lines.length, getIngredients(lines), new ItemStack(item.getItem()));
        this.item = item;
    }

    private static NonNullList<Ingredient> getIngredients(String... lines) {
        NonNullList<Ingredient> list = NonNullList.create();
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                if (c == '#') {
                    list.add(new GearPartIngredient(PartType.MAIN));
                } else if (c == '/') {
                    list.add(new GearPartIngredient(PartType.ROD));
                } else if (c == '~') {
                    list.add(new GearPartIngredient(PartType.BOWSTRING));
                } else {
                    list.add(Ingredient.EMPTY);
                }
            }
        }
        return list;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        StackList stackList = StackList.fromInventory(inv);

        // Make sure the rods match
        ItemStack rod = stackList.firstMatch(s -> typeOf(s) == PartType.ROD);
        for (ItemStack stack : stackList.allMatches(s -> typeOf(s) == PartType.ROD)) {
            if (!rod.isItemEqual(stack)) {
                return false;
            }
        }
        // Make sure bowstrings match (if there are any)
        ItemStack bowstring = stackList.firstMatch(s -> typeOf(s) == PartType.BOWSTRING);
        if (!bowstring.isEmpty()) {
            for (ItemStack stack : stackList.allMatches(s -> typeOf(s) == PartType.BOWSTRING)) {
                if (!bowstring.isItemEqual(stack)) {
                    return false;
                }
            }
        }

        return super.matches(inv, worldIn);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        StackList stackList = StackList.fromInventory(inv);
        Collection<ItemStack> items = new ArrayList<>();
        items.addAll(stackList.allMatches(s -> typeOf(s) == PartType.MAIN));
        items.add(stackList.firstMatch(s -> typeOf(s) == PartType.ROD));
        ItemStack bowstring = stackList.firstMatch(s -> typeOf(s) == PartType.BOWSTRING);
        if (!bowstring.isEmpty()) {
            items.add(bowstring);
        }

        List<ItemPartData> parts = items.stream()
                .map(ItemPartData::fromStack)
                .collect(Collectors.toList());
        return this.item.construct(this.item.getItem(), parts);
    }

    @Nullable
    private static PartType typeOf(ItemStack stack) {
        ItemPartData part = ItemPartData.fromStack(stack);
        if (part != null) {
            return part.getPart().getType();
        }
        return null;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
