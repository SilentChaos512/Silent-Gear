package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.gear.part.PartData;
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

        PartData part = PartData.from(input);
        if (part != null) {
            ret.addAll(salvage(part));
        }

        return ret;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (!(inv.getItem(0).getItem() instanceof CompoundPartItem))
            return false;

        return PartData.from(inv.getItem(0)) != null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING_COMPOUND_PART.get();
    }

    public static class Serializer implements RecipeSerializer<CompoundPartSalvagingRecipe> {
        public static final Codec<CompoundPartSalvagingRecipe> CODEC = Codec.of(Encoder.empty(), Decoder.unit(CompoundPartSalvagingRecipe::new)).codec();

        @Override
        public Codec<CompoundPartSalvagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public CompoundPartSalvagingRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            return new CompoundPartSalvagingRecipe();
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CompoundPartSalvagingRecipe pRecipe) {
            // Nothing to write
        }
    }
}
