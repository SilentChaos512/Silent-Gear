package net.silentchaos512.gear.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.crafting.recipe.*;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.registry.SRegistry;

import java.util.HashMap;
import java.util.Map;

public final class ModRecipes {
    public static final Map<String, RecipeModularItem> gearCrafting = new HashMap<>();

    private ModRecipes() {}

    public static void registerAll(SRegistry reg) {
        // Gear recipes TODO: move to JSON
        RecipeMaker recipes = reg.getRecipeMaker();
        for (ICoreItem item : ModItems.toolClasses.values()) {
            final RecipeModularItem recipe = new RecipeModularItem(item);
            gearCrafting.put(item.getGearClass(), recipe);
            recipes.addCustomRecipe("core_" + item.getGearClass(), recipe);
        }

        // Smelting recipes
        recipes.addSmelting(ModBlocks.crimsonIronOre, new ItemStack(CraftingItems.CRIMSON_IRON_INGOT.getItem()), 0.6f);

        // Repair recipe "fix" - prevents gear items from being destroyed by vanilla
        SilentGear.log.info("Replacing vanilla repair recipe");
        IRecipe rec = new RepairItemRecipeFix();
        rec.setRegistryName(new ResourceLocation("minecraft", "repairitem"));
        ForgeRegistries.RECIPES.register(rec);
    }
}
