package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRecipes;

import java.util.ArrayList;
import java.util.List;

public class SalvagingRecipe implements Recipe<SingleRecipeInput> {
    protected final Ingredient ingredient;
    private final List<ItemStack> results = new ArrayList<>();

    public SalvagingRecipe(Ingredient ingredient, List<ItemStack> results) {
        this.ingredient = ingredient;
        this.results.addAll(results);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getPossibleResults(Container inv) {
        return new ArrayList<>(results);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level worldIn) {
        return ingredient.test(input.getItem(0));
    }

    @Deprecated
    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registryAccess) {
        // DO NOT USE
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        // DO NOT USE
        return !results.isEmpty() ? results.getFirst() : ItemStack.EMPTY;
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
    public static List<ItemStack> salvagePart(PartInstance part) {
        ItemStack partStack = part.getItem();
        if (part.get() instanceof CoreGearPart && partStack.getItem() instanceof CompoundPartItem compoundPartItem) {
            List<MaterialInstance> materialsInPart = CompoundPartItem.getMaterials(partStack);
            int craftedCount = materialsInPart.size();
            if (craftedCount < 1) {
                SilentGear.LOGGER.warn("Compound part's crafted count is less than 1? {}", partStack);
                return List.of(partStack);
            }

            List<ItemStack> result = new ArrayList<>();
            var partMaterials = part.getMaterials();
            for (var material : partMaterials) {
                var salvagedMaterial = material.onSalvage();
                result.add(salvagedMaterial.getItem());
            }
            return result;
        }
        return List.of(partStack);
    }

    public static class Serializer implements RecipeSerializer<SalvagingRecipe> {
        public static final MapCodec<SalvagingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                        Codec.list(ItemStack.CODEC).fieldOf("results").forGetter(r -> r.results)
                ).apply(instance, SalvagingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SalvagingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                ItemStack.LIST_STREAM_CODEC, r -> r.results,
                SalvagingRecipe::new
        );

        @Override
        public MapCodec<SalvagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SalvagingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
