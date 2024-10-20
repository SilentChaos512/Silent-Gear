package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GearSalvagingRecipe extends SalvagingRecipe {
    public GearSalvagingRecipe(Ingredient ingredient) {
        super(ingredient, Collections.emptyList());
    }

    @Override
    public List<ItemStack> getPossibleResults(Container inv) {
        ItemStack input = inv.getItem(0);
        List<ItemStack> ret = new ArrayList<>();

        PartList parts = GearData.getConstruction(input).parts();
        for (PartInstance part : parts) {
            ret.add(part.getItem());
            //ret.addAll(salvage(part));
        }

        return ret;
    }

    @Override
    public List<ItemStack> getPossibleResultsForDisplay() {
        // Cannot compute anything without an input item
        return List.of();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING_GEAR.get();
    }

    public static class Serializer implements RecipeSerializer<GearSalvagingRecipe> {
        public static final MapCodec<GearSalvagingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient)
                ).apply(instance, GearSalvagingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, GearSalvagingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                GearSalvagingRecipe::new
        );

        @Override
        public MapCodec<GearSalvagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GearSalvagingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
