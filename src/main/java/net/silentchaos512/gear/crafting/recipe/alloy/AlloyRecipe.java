package net.silentchaos512.gear.crafting.recipe.alloy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.block.compounder.CompoundMakerBlockEntity;
import net.silentchaos512.gear.block.compounder.CompoundMakerInfo;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public class AlloyRecipe implements Recipe<CompoundMakerBlockEntity<?>> {
    final List<Ingredient> ingredients = new ArrayList<>();
    final Result result;

    public AlloyRecipe(Result result, List<Ingredient> ingredients) {
        this.result = result;
        this.ingredients.addAll(ingredients);
    }

    public static <R extends AlloyRecipe> R makeExample(CompoundMakerInfo<?> info, int count, BiFunction<Result, List<Ingredient>, R> recipeFactory) {
        IMaterialCategory[] cats = info.getCategories().toArray(new IMaterialCategory[0]);
        List<Ingredient> list = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            var partMaterialIngredient = PartMaterialIngredient.of(PartTypes.MAIN.get(), GearTypes.ALL.get(), cats);
            list.add(new Ingredient(partMaterialIngredient));
        }
        return recipeFactory.apply(new Result(info.getOutputItem(), count, Const.Materials.EXAMPLE), list);
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
    public ItemStack assemble(CompoundMakerBlockEntity<?> inv, HolderLookup.Provider registryAccess) {
        return this.result.getResult();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height <= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
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

    public record Result(
            Item item,
            int count,
            @Nullable DataResource<IMaterial> material
    ) {
        public static final Codec<Result> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(r -> r.item),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.count),
                        DataResource.MATERIAL_CODEC.optionalFieldOf("material").forGetter(r -> Optional.ofNullable(r.material))
                ).apply(instance, (item, count, material) -> new Result(item, count, material.orElse(null)))
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM), r -> r.item,
                ByteBufCodecs.VAR_INT, r -> r.count,
                DataResource.MATERIAL_STREAM_CODEC, r -> r.material,
                Result::new
        );

        public ItemStack getResult() {
            if (item instanceof CustomMaterialItem customMaterialItem && material != null) {
                return customMaterialItem.create(LazyMaterialInstance.of(material), count);
            }
            return new ItemStack(item);
        }
    }

    @FunctionalInterface
    public interface Factory<R extends AlloyRecipe> {
        R create(Result result, List<Ingredient> ingredients);
    }

    public static class Serializer<T extends AlloyRecipe> implements RecipeSerializer<T> {
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(Factory<T> factory) {
            this.codec = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Result.CODEC.fieldOf("result").forGetter(r -> r.result),
                            Codec.list(Ingredient.CODEC_NONEMPTY).fieldOf("ingredients").forGetter(r -> r.ingredients)
                    ).apply(instance, factory::create)
            );
            this.streamCodec = StreamCodec.of(
                    (buf, r) -> {
                        Result.STREAM_CODEC.encode(buf, r.result);
                        CodecUtils.encodeList(buf, r.ingredients, Ingredient.CONTENTS_STREAM_CODEC);
                    },
                    buf -> {
                        var result = Result.STREAM_CODEC.decode(buf);
                        var ingredients = CodecUtils.decodeList(buf, Ingredient.CONTENTS_STREAM_CODEC);
                        return factory.create(result, ingredients);
                    }
            );
        }

        @Override
        public MapCodec<T> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec;
        }
    }
}
