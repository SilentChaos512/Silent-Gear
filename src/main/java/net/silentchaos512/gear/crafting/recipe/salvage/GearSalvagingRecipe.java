package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.util.GearData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GearSalvagingRecipe extends SalvagingRecipe {
    public static final RecipeSerializer<GearSalvagingRecipe> SERIALIZER = new RecipeSerializer<>(
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient)
                    ).apply(instance, GearSalvagingRecipe::new)
            ),
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                    GearSalvagingRecipe::new
            )
    );

    public GearSalvagingRecipe(Ingredient ingredient) {
        super(ingredient, Collections.emptyList());
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level worldIn) {
        // Block salvaging of stackable items like arrows
        if (input.getItem(0).getMaxStackSize() > 1) {
            return false;
        }
        return super.matches(input, worldIn);
    }

    @Override
    public List<ItemStackTemplate> getPossibleResults(Container inv) {
        ItemStack input = inv.getItem(0);
        // Block salvaging of stackable items like arrows
        if (input.getMaxStackSize() > 1) {
            return List.of();
        }

        List<ItemStackTemplate> ret = new ArrayList<>();
        PartList parts = GearData.getConstruction(input).parts();

        for (PartInstance part : parts) {
            if (Config.Common.salvagerBreakDownPartsWithGear.get()) {
                ret.addAll(salvagePart(part));
            } else {
                ret.addAll(itemToList(part));
            }
        }

        return ret;
    }

    @Override
    public List<ItemStackTemplate> getPossibleResultsForDisplay() {
        // Cannot compute anything without an input item
        return List.of();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }
}
