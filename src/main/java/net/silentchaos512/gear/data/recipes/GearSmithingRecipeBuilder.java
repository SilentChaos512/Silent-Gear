package net.silentchaos512.gear.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class GearSmithingRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final Item gearItem;
    private final Ingredient addition;

    public GearSmithingRecipeBuilder(IRecipeSerializer<?> serializer, Item gearItem, Ingredient addition) {
        this.serializer = serializer;
        this.gearItem = gearItem;
        this.addition = addition;
    }

    public static GearSmithingRecipeBuilder coating(IItemProvider gearItem) {
        return new GearSmithingRecipeBuilder(ModRecipes.COATING_SMITHING, gearItem.asItem(), PartMaterialIngredient.of(PartType.COATING));
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + NameUtils.from(this.gearItem).getPath()));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new GearSmithingRecipeBuilder.Result(recipeId, this));
    }

    public class Result implements IFinishedRecipe {
        private final ResourceLocation recipeId;
        private final GearSmithingRecipeBuilder builder;

        public Result(ResourceLocation recipeId, GearSmithingRecipeBuilder builder) {
            this.recipeId = recipeId;
            this.builder = builder;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("gear", Ingredient.fromItems(builder.gearItem).serialize());
            json.add("addition", builder.addition.serialize());
        }

        @Override
        public ResourceLocation getID() {
            return recipeId;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return builder.serializer;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return null;
        }
    }
}
