package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

public class SgRecipeBookCategories {
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, SilentGear.MOD_ID);

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> ALLOY_FORGE = register("alloy_forge");
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> METAL_PRESS = register("metal_press");
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> SALVAGER = register("salvager");
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> TOOL_ACTION = register("tool_action");

    private static DeferredHolder<RecipeBookCategory, RecipeBookCategory> register(String path) {
        return RECIPE_BOOK_CATEGORIES.register(path, RecipeBookCategory::new);
    }
}
