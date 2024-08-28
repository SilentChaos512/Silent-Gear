package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ConversionRecipe extends ExtendedShapelessRecipe {
    private final Result result;
    private final ICoreItem item;

    public ConversionRecipe(String pGroup, CraftingBookCategory pCategory, Result pResult, NonNullList<Ingredient> pIngredients) {
        super(pGroup, pCategory, pResult.item.getDefaultInstance(), pIngredients);
        this.result = pResult;

        if (!(this.result.item instanceof ICoreItem)) {
            throw new JsonParseException("result is not a gear item: " + BuiltInRegistries.ITEM.getKey(this.result.item));
        }
        this.item = (ICoreItem) this.result.item;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.CONVERSION.get();
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        ItemStack result = item.construct(getParts());
        ItemStack original = findOriginalItem(inv);
        if (!original.isEmpty()) {
            // Copy relevant data components
            result.setDamageValue(original.getDamageValue());
            if (original.isEnchanted()) {
                // Copy enchantments
                result.set(DataComponents.ENCHANTMENTS, original.getEnchantments());
            }
        }
        return result;
    }

    private static ItemStack findOriginalItem(CraftingInput inv) {
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private Collection<PartInstance> getParts() {
        PartList ret = PartList.of();
        //noinspection OverlyLongLambda
        this.result.parts.forEach(part -> {
            if (part != null) {
                ret.add(part);
            }
        });
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public record Result(Item item, List<PartInstance> parts) {
        public static final Codec<Result> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(r -> r.item),
                        Codec.list(PartInstance.CODEC).fieldOf("parts").forGetter(r -> r.parts)
                ).apply(instance, Result::new)
        );

        public static Result fromNetwork(RegistryFriendlyByteBuf buf) {
            var item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            var parts = new ArrayList<PartInstance>();
            int partListSize = buf.readByte();
            for (int i = 0; i < partListSize; ++i) {
                parts.add(PartInstance.STREAM_CODEC.decode(buf));
            }
            return new Result(item, parts);
        }

        public void toNetwork(RegistryFriendlyByteBuf buf) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item));
            buf.writeByte(parts.size());
            parts.forEach(part -> PartInstance.STREAM_CODEC.encode(buf, part));
        }
    }

    public static class Serializer implements RecipeSerializer<ConversionRecipe> {
        private static final MapCodec<ConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                                CraftingBookCategory.CODEC.fieldOf("group").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                                Result.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Ingredient.CODEC_NONEMPTY
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                recipe -> {
                                                    Ingredient[] aingredient = recipe.toArray(Ingredient[]::new);
                                                    if (aingredient.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                                    } else {
                                                        return aingredient.length > 9
                                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(9))
                                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(p_300975_ -> p_300975_.ingredients)
                        )
                        .apply(instance, ConversionRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ConversionRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<ConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ConversionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static ConversionRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            var group = buf.readUtf();
            var bookCategory = buf.readEnum(CraftingBookCategory.class);
            var result = Result.fromNetwork(buf);
            var ingredients = NonNullList.<Ingredient>create();
            var ingredientListSize = buf.readVarInt();
            for (int i = 0; i < ingredientListSize; ++i) {
                ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }
            return new ConversionRecipe(group, bookCategory, result, ingredients);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, ConversionRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            recipe.result.toNetwork(buf);
            buf.writeVarInt(recipe.ingredients.size());
            recipe.ingredients.forEach(ing -> Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing));
        }
    }
}
