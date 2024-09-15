package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRecipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundPartSalvagingRecipe extends SalvagingRecipe {
    public CompoundPartSalvagingRecipe() {
        super(Ingredient.of(SgItems.getItems(CompoundPartItem.class).toArray(new CompoundPartItem[0])), Collections.emptyList());
    }

    @Override
    public List<ItemStack> getPossibleResults(Container inv) {
        ItemStack input = inv.getItem(0);
        List<ItemStack> ret = new ArrayList<>();

        PartInstance part = PartInstance.from(input);
        if (part != null) {
            ret.addAll(salvagePart(part));
        }

        return ret;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level worldIn) {
        var itemStack = input.getItem(0);
        return itemStack.getItem() instanceof CompoundPartItem && PartInstance.from(itemStack) != null;

    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING_COMPOUND_PART.get();
    }

    public static class Serializer implements RecipeSerializer<CompoundPartSalvagingRecipe> {
        public static final MapCodec<CompoundPartSalvagingRecipe> CODEC = Codec.of(
                Encoder.empty(),
                Decoder.unit(CompoundPartSalvagingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CompoundPartSalvagingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> {},
                buf -> new CompoundPartSalvagingRecipe()
        );

        @Override
        public MapCodec<CompoundPartSalvagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CompoundPartSalvagingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
