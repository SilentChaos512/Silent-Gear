package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundPartSalvagingRecipe extends SalvagingRecipe {
    public static final CompoundPartSalvagingRecipe INSTANCE = new CompoundPartSalvagingRecipe();
    public static final RecipeSerializer<CompoundPartSalvagingRecipe> SERIALIZER = new RecipeSerializer<>(
            MapCodec.unit(INSTANCE),
            StreamCodec.unit(INSTANCE)
    );

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
    public boolean matches(SingleRecipeInput input, Level level) {
        var stack = input.getItem(0);
        return stack.getMaxStackSize() == 1 && stack.getItem() instanceof CompoundPartItem && PartInstance.from(stack) != null;

    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }
}
