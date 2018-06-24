package net.silentchaos512.gear.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.inventory.InventoryCraftingStation;
import net.silentchaos512.gear.item.blueprint.Blueprint;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.lib.recipe.RecipeBaseSL;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.Collection;

public class RecipeBlueprintCrafting extends RecipeBaseSL {

    public Item outputType;

    public RecipeBlueprintCrafting(Item outputType) {
        this.outputType = outputType;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        StackList list = StackHelper.getNonEmptyStacks(inv);
        ItemStack blueprint = list.firstOfType(IBlueprint.class);
        list.remove(blueprint);
        return ((IBlueprint) blueprint.getItem()).getCraftingResult(blueprint, list);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        StackList list = StackHelper.getNonEmptyStacks(inv);
        ItemStack blueprint = list.uniqueOfType(Blueprint.class);

        // Only one blueprint
        if (blueprint.isEmpty()) {
            return false;
        }

        Collection<ItemStack> materials = list.allMatches(s -> PartRegistry.get(s) != null);
        int materialCount = materials.size();
        IBlueprint blueprintItem = (IBlueprint) blueprint.getItem();

        // Right number of materials and nothing else?
        if (materialCount + 1 != list.size() || materialCount != blueprintItem.getOutputInfo(blueprint).cost) {
            return false;
        }

        // Inventory allows mixing?
        return inventoryAllowsMixedMaterial(inv) || materials.stream().map(PartRegistry::get).distinct().count() == 1;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(outputType);
    }

    private boolean inventoryAllowsMixedMaterial(InventoryCrafting inv) {
        return inv instanceof InventoryCraftingStation;
    }
}
