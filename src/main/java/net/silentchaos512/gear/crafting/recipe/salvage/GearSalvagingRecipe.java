package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.gear.part.PartData;
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

        PartDataList parts = GearData.getConstructionParts(input);
        for (PartData part : parts) {
            ret.add(part.getItem());
            //ret.addAll(salvage(part));
        }

        return ret;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING_GEAR.get();
    }

    public static class Serializer implements RecipeSerializer<GearSalvagingRecipe> {
        public static final Codec<GearSalvagingRecipe> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient)
                ).apply(instance, GearSalvagingRecipe::new)
        );
        @Override
        public Codec<GearSalvagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public GearSalvagingRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            var ingredient = Ingredient.fromNetwork(pBuffer);
            return new GearSalvagingRecipe(ingredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, GearSalvagingRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
        }
    }
}
