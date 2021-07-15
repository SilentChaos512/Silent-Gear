package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.item.CompoundPartItem;

import javax.annotation.Nullable;
import java.util.*;

public class SalvagingRecipe implements IRecipe<IInventory> {
    private final ResourceLocation recipeId;
    protected Ingredient ingredient;
    private final List<ItemStack> results = new ArrayList<>();

    public SalvagingRecipe(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getPossibleResults(IInventory inv) {
        return new ArrayList<>(results);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return ingredient.test(inv.getItem(0));
    }

    @Deprecated
    @Override
    public ItemStack assemble(IInventory inv) {
        // DO NOT USE
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getResultItem() {
        // DO NOT USE
        return !results.isEmpty() ? results.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SALVAGING.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.SALVAGING_TYPE;
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
                    ret.add(ModItems.FRAGMENT.get().create(material, frags));
                }
            }
            return ret;
        }
        return Collections.singletonList(part.getItem());
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SalvagingRecipe> {
        @Override
        public SalvagingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            SalvagingRecipe recipe = new SalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromJson(json.get("ingredient"));
            JsonArray resultsArray = json.getAsJsonArray("results");
            for (JsonElement element : resultsArray) {
                if (element.isJsonObject()) {
                    Item item = JSONUtils.getAsItem(element.getAsJsonObject(), "item");
                    int count = JSONUtils.getAsInt(element.getAsJsonObject(), "count", 1);
                    recipe.results.add(new ItemStack(item, count));
                } else {
                    recipe.results.add(new ItemStack(JSONUtils.convertToItem(element, "item")));
                }
            }
            return recipe;
        }

        @Nullable
        @Override
        public SalvagingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            SalvagingRecipe recipe = new SalvagingRecipe(recipeId);
            recipe.ingredient = Ingredient.fromNetwork(buffer);
            int resultCount = buffer.readByte();
            for (int i = 0; i < resultCount; ++i) {
                recipe.results.add(buffer.readItem());
            }
            return recipe;
        }

        @Override
        public void toNetwork(PacketBuffer buffer, SalvagingRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeByte(recipe.results.size());
            recipe.results.forEach(buffer::writeItem);
        }
    }
}
