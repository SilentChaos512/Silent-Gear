package net.silentchaos512.gear.crafting.recipe.salvage;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRecipeBookCategories;
import net.silentchaos512.gear.setup.SgRecipes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SalvagingRecipe implements Recipe<SingleRecipeInput> {
    public static final RecipeSerializer<SalvagingRecipe> SERIALIZER = new RecipeSerializer<>(
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                            Codec.list(ItemStackTemplate.CODEC).fieldOf("results").forGetter(r -> r.results)
                    ).apply(instance, SalvagingRecipe::new)
            ),
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                    ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)), r -> r.results,
                    SalvagingRecipe::new
            )
    );

    protected final Ingredient ingredient;
    private final List<ItemStackTemplate> results;
    @Nullable private PlacementInfo placementInfo = null;

    public SalvagingRecipe(Ingredient ingredient, List<ItemStackTemplate> results) {
        this.ingredient = ingredient;
        this.results = ImmutableList.copyOf(results);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ItemStackTemplate> getPossibleResults(Container inv) {
        return this.results;
    }

    public List<ItemStackTemplate> getPossibleResultsForDisplay() {
        return getPossibleResults(new SimpleContainer(1));
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level worldIn) {
        return ingredient.test(input.getItem(0));
    }

    @Deprecated
    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        // DO NOT USE
        return !results.isEmpty() ? results.getFirst().create() : ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return SgRecipes.SALVAGING_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredient);
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return SgRecipeBookCategories.SALVAGER.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    /**
     * Salvages parts into their respective material items. This may not necessarily give back the original item used
     * for the material, but an item that matches it.
     *
     * @param part The part
     * @return The list of items to return
     */
    public static List<ItemStackTemplate> salvagePart(PartInstance part) {
        ItemStack partStack = part.copyItem();
        if (canSalvagePart(part)) {
            List<MaterialInstance> materialsInPart = part.getItemData(SgDataComponents.MATERIAL_LIST, List.of());
            if (materialsInPart.isEmpty()) {
                SilentGear.LOGGER.warn("Compound part contains no materials? {}", part);
                return itemToList(part);
            }

            List<ItemStackTemplate> result = new ArrayList<>();
            var partMaterials = part.getMaterials();
            for (var material : partMaterials) {
                var salvagedMaterial = material.onSalvage();
                result.addAll(itemToList(salvagedMaterial));
            }
            return result;
        }
        return itemToList(part);
    }

    static List<ItemStackTemplate> itemToList(GearComponentInstance<?> instance) {
        return instance.getItem() != null ? List.of(instance.getItem()) : List.of();
    }

    private static boolean canSalvagePart(PartInstance part) {
        var partItem = part.getItem();
        return part.isValid() && part.get() instanceof CoreGearPart
                && partItem != null
                && partItem.item().value() instanceof CompoundPartItem
                && partItem.getMaxStackSize() == 1;
    }
}
