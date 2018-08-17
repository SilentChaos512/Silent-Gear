package net.silentchaos512.gear.init;

import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.recipe.RecipeBlueprintCrafting;
import net.silentchaos512.gear.recipe.RecipeModularItem;
import net.silentchaos512.gear.recipe.RecipeQuickRepair;
import net.silentchaos512.gear.recipe.RecipeUpgradeModularItem;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.registry.SRegistry;

public class ModRecipes {
    public static void registerAll(SRegistry reg) {
        RecipeMaker recipes = reg.getRecipeMaker();
        for (ICoreItem item : ModItems.toolClasses.values()) {
            recipes.addCustomRecipe("core_" + item.getGearClass(), new RecipeModularItem(item));
        }
        recipes.addCustomRecipe("head_blueprint", new RecipeBlueprintCrafting(ModItems.toolHead));
        recipes.addCustomRecipe("upgrade_core_item", new RecipeUpgradeModularItem());
        recipes.addCustomRecipe("quick_gear_repair", new RecipeQuickRepair());
    }
}
