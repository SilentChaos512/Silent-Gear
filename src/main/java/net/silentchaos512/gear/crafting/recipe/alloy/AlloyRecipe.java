package net.silentchaos512.gear.crafting.recipe.alloy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.block.compounder.CompoundMakerBlockEntity;
import net.silentchaos512.gear.block.compounder.CompoundMakerInfo;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.setup.SgRecipes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class AlloyRecipe implements Recipe<CompoundMakerBlockEntity<?>> {
    final List<Ingredient> ingredients = new ArrayList<>();
    final Result result;

    public AlloyRecipe(Result result, List<Ingredient> ingredients) {
        this.result = result;
        this.ingredients.addAll(ingredients);
    }

    public static <R extends AlloyRecipe> R makeExample(CompoundMakerInfo<?> info, int count, BiFunction<ItemStack, List<Ingredient>, R> recipeFactory) {
        IMaterialCategory[] cats = info.getCategories().toArray(new IMaterialCategory[0]);
        List<Ingredient> list = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            list.add(PartMaterialIngredient.of(PartType.MAIN, GearType.ALL, cats));
        }
        ItemStack result = new ItemStack(info.getOutputItem(), count);
        return recipeFactory.apply(result, list);
    }

    @Override
    public boolean matches(CompoundMakerBlockEntity<?> inv, Level worldIn) {
        Set<Integer> matches = new HashSet<>();
        int inputs = 0;

        for (int i = 0; i < inv.getInputSlotCount(); ++i) {
            if (!inv.getItem(i).isEmpty()) {
                ++inputs;
            }
        }

        for (Ingredient ingredient : this.ingredients) {
            boolean found = false;

            for (int i = 0; i < inv.getInputSlotCount(); ++i) {
                ItemStack stack = inv.getItem(i);

                if (!stack.isEmpty() && ingredient.test(stack)) {
                    found = true;
                    matches.add(i);
                }
            }

            if (!found) {
                return false;
            }
        }

        int matchCount = matches.size();
        return matchCount == inputs && matchCount == this.ingredients.size();
    }

    @Override
    public ItemStack assemble(CompoundMakerBlockEntity<?> inv, RegistryAccess registryAccess) {
        return this.result.getResult();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height <= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result.getResult();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ret = NonNullList.create();
        ret.addAll(ingredients);
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.COMPOUNDING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SgRecipes.COMPOUNDING_TYPE.get();
    }

    public record Result(Item item, int count, DataResource<IMaterial> material) {
        public static final Codec<Result> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(r -> r.item),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count),
                        DataResource.MATERIAL_CODEC.fieldOf("material").forGetter(r -> r.material)
                ).apply(instance, Result::new)
        );

        public static Result fromNetwork(FriendlyByteBuf buf) {
            var item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            var count = buf.readByte();
            var material = DataResource.material(buf.readResourceLocation());
            return new Result(item, count, material);
        }

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item));
            buf.writeByte(count);
            buf.writeResourceLocation(material.getId());
        }

        public ItemStack getResult() {
            if (item instanceof CustomMaterialItem customMaterialItem) {
                return customMaterialItem.create(LazyMaterialInstance.of(material), count);
            }
            return new ItemStack(item);
        }
    }

    public static class Serializer<T extends AlloyRecipe> implements RecipeSerializer<T> {
        private final BiFunction<Result, List<Ingredient>, T> factory;
        private final Codec<T> codec;

        public static <R extends AlloyRecipe> Codec<R> makeCodec(BiFunction<Result, List<Ingredient>, R> factory) {
            return RecordCodecBuilder.create(
                    instance -> instance.group(
                            Result.CODEC.fieldOf("result").forGetter(r -> r.result),
                            Codec.list(Ingredient.CODEC_NONEMPTY).fieldOf("ingredients").forGetter(r -> r.ingredients)
                    ).apply(instance, factory)
            );
        }

        public Serializer(BiFunction<Result, List<Ingredient>, T> factory) {
            this.factory = factory;
            this.codec = makeCodec(factory);
        }

        @Override
        public Codec<T> codec() {
            return codec;
        }

        @Override
        public T fromNetwork(FriendlyByteBuf buf) {
            var result = Result.fromNetwork(buf);
            var ingredientsSize = buf.readByte();
            var ingredients = new ArrayList<Ingredient>();
            for (int i = 0; i < ingredientsSize; ++i) {
                ingredients.add(Ingredient.fromNetwork(buf));
            }
            return factory.apply(result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            recipe.result.toNetwork(buf);
            buf.writeByte(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient -> ingredient.toNetwork(buf));
        }
    }
}
