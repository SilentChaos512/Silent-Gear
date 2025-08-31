package net.silentchaos512.gear.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.silentchaos512.gear.setup.SgRecipes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber
public class ClientRecipeHelper {
    private static final Multimap<RecipeType<?>, RecipeHolder<?>> MAP_BY_TYPE = ArrayListMultimap.create();
    private static final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> MAP_BY_KEY = new HashMap<>();

    public static Collection<RecipeHolder<?>> getRecipes(RecipeType<?> recipeType) {
        return MAP_BY_TYPE.get(recipeType);
    }

    public static Optional<RecipeHolder<?>> getRecipe(ResourceKey<Recipe<?>> key) {
        return Optional.ofNullable(MAP_BY_KEY.getOrDefault(key, null));
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(
                RecipeType.CRAFTING, // We have to request all crafting recipes in order to get gear crafting recipes...
                SgRecipes.ALLOY_MAKING_FABRIC_TYPE.get(),
                SgRecipes.ALLOY_MAKING_GEM_TYPE.get(),
                SgRecipes.ALLOY_MAKING_METAL_TYPE.get(),
                SgRecipes.ALLOY_MAKING_SUPER_TYPE.get(),
                SgRecipes.COMPOUNDING_TYPE.get(),
                SgRecipes.PRESSING_TYPE.get(),
                SgRecipes.MATERIAL_PRESSING_TYPE.get(),
                SgRecipes.SALVAGING_TYPE.get(),
                SgRecipes.TOOL_ACTION_TYPE.get()
        );
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        clearRecipeMaps();

        for (RecipeHolder<?> recipeHolder : event.getRecipeMap().values()) {
            if (recipeHolder.value().getType().equals(RecipeType.CRAFTING)) {
                // Filter only gear-related crafting recipes
                if (isGearCraftingRecipe(recipeHolder)) {
                    addRecipe(recipeHolder);
                }
            } else {
                // Keep all other types of recipes
                addRecipe(recipeHolder);
            }
        }
    }

    private static void clearRecipeMaps() {
        MAP_BY_TYPE.clear();
        MAP_BY_KEY.clear();
    }

    private static void addRecipe(RecipeHolder<?> recipe) {
        MAP_BY_TYPE.put(recipe.value().getType(), recipe);
        MAP_BY_KEY.put(recipe.id(), recipe);
    }

    private static boolean isGearCraftingRecipe(RecipeHolder<?> recipeHolder) {
        var serializer = recipeHolder.value().getSerializer();
        return serializer == SgRecipes.SHAPED_GEAR.get()
                || serializer == SgRecipes.SHAPELESS_GEAR.get()
                || serializer == SgRecipes.COMPOUND_PART.get();
    }
}
