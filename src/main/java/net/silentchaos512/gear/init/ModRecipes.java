package net.silentchaos512.gear.init;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.recipe.*;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.registry.SRegistry;

import java.util.HashMap;
import java.util.Map;

public class ModRecipes {
    public static final Map<String, RecipeModularItem> gearCrafting = new HashMap<>();

    public static void registerAll(SRegistry reg) {
        RecipeMaker recipes = reg.getRecipeMaker();
        for (ICoreItem item : ModItems.toolClasses.values()) {
            final RecipeModularItem recipe = new RecipeModularItem(item);
            gearCrafting.put(item.getGearClass(), recipe);
            recipes.addCustomRecipe("core_" + item.getGearClass(), recipe);
        }
        recipes.addCustomRecipe("head_blueprint", new RecipeBlueprintCrafting(ModItems.toolHead));
        recipes.addCustomRecipe("upgrade_core_item", new RecipeUpgradeModularItem());
        recipes.addCustomRecipe("quick_gear_repair", new RecipeQuickRepair());

        // Repair recipe "fix" - prevents gear items from being destroyed by vanilla
        SilentGear.log.info("Replacing vanilla repair recipe");
        IRecipe rec = new RecipeRepairItemFix();
        rec.setRegistryName(new ResourceLocation("minecraft", "repairitem"));
        ForgeRegistries.RECIPES.register(rec);
    }
}
