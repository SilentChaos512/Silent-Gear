package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.init.SgRecipes;
import net.silentchaos512.gear.item.CompoundPartItem;

import javax.annotation.Nullable;
import java.util.*;

public class SalvagingRecipe implements Recipe<Container> {

    private final ResourceLocation recipeId;
    protected Ingredient ingredient;
    private final List<ItemStack> results = new ArrayList<>();

    public SalvagingRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getPossibleResults(Container inv) {
        return new ArrayList<>(results);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return ingredient.test(inv.getItem(0));
    }

    @Deprecated
    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        // DO NOT USE
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        // DO NOT USE
        return !results.isEmpty() ? results.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SgRecipes.SALVAGING_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Salvages parts into their respective material items, or fragments if appropriate. This does
     * not necessarily give back the original item used for the material, but an item that matches
     * it.
     *
     * @param part The part
     * @return The list of items to return
     */
    public static List<ItemStack> salvage(PartData part) {
        if (part.get() instanceof CompoundPart && part.getItem().getItem() instanceof CompoundPartItem) {
            int craftedCount = ((CompoundPartItem) part.getItem().getItem()).getCraftedCount(part.getItem());
            if (craftedCount < 1) {
                SilentGear.LOGGER.warn("Compound part's crafted count is less than 1? {}", part.getItem());
                return Collections.singletonList(part.getItem());
            }

            List<IMaterialInstance> materials = part.getMaterials();
            Map<IMaterialInstance, Integer> fragments = new LinkedHashMap<>();

            for (IMaterialInstance material : materials) {
                int fragmentCount = 8 / craftedCount;
                fragments.merge(material.onSalvage(), fragmentCount, Integer::sum);
            }

            List<ItemStack> ret = new ArrayList<>();
            for (Map.Entry<IMaterialInstance, Integer> entry : fragments.entrySet()) {
                IMaterialInstance material = entry.getKey();
                int count = entry.getValue();
                int fulls = count / 8;
                int frags = count % 8;
                if (fulls > 0) {
                    ret.add(material.getItem());
                }
                if (frags > 0) {
                    ret.add(SgItems.FRAGMENT.get().create(material, frags));
                }
            }
            return ret;
        }
        return Collections.singletonList(part.getItem());
    }

    public static class Serializer implements RecipeSerializer<SalvagingRecipe> {
        @Override
        public SalvagingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            SalvagingRecipe recipe = new SalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            JsonArray resultsArray = json.getAsJsonArray("results");
            for (JsonElement element : resultsArray) {
                if (element.isJsonObject()) {
                    Item item = GsonHelper.getAsItem(element.getAsJsonObject(), "item");
                    int count = GsonHelper.getAsInt(element.getAsJsonObject(), "count", 1);
                    recipe.results.add(new ItemStack(item, count));
                } else {
                    recipe.results.add(new ItemStack(GsonHelper.convertToItem(element, "item")));
                }
            }
            return recipe;
        }

        @Nullable
        @Override
        public SalvagingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            SalvagingRecipe recipe = new SalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            int resultCount = buffer.readByte();
            for (int i = 0; i < resultCount; ++i) {
                recipe.results.add(buffer.readItem());
            }
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SalvagingRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.results.size());
            recipe.results.forEach(buffer::writeItem);
        }
    }
}
