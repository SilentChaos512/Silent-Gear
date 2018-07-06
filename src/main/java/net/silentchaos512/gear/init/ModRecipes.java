package net.silentchaos512.gear.init;

import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.recipe.RecipeBlueprintCrafting;
import net.silentchaos512.gear.recipe.RecipeModularItem;
import net.silentchaos512.gear.recipe.RecipeUpgradeModularItem;
import net.silentchaos512.lib.registry.IRegistrationHandler;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.registry.SRegistry;

public class ModRecipes implements IRegistrationHandler<IRecipe> {

    public static final ModRecipes INSTANCE = new ModRecipes();

    @Override
    public void registerAll(SRegistry reg) {
        RecipeMaker recipes = reg.recipes;
        for (ICoreItem item : ModItems.toolClasses.values()) {
            recipes.addCustomRecipe("core_" + item.getGearClass(), new RecipeModularItem(item));
        }
        recipes.addCustomRecipe("head_blueprint", new RecipeBlueprintCrafting(ModItems.toolHead));
        recipes.addCustomRecipe("upgrade_core_item", new RecipeUpgradeModularItem());
    }

    public void preInitOreDict() {
        OreDictionary.registerOre("flint", Items.FLINT);
    }
}
