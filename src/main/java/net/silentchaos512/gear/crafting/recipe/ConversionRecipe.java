package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = item.construct(getParts());
        ItemStack original = findOriginalItem(inv);
        if (!original.isEmpty()) {
            // Copy relevant NBT
            result.setDamageValue(original.getDamageValue());
            if (original.isEnchanted()) {
                // Copy enchantments
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(original);
                EnchantmentHelper.setEnchantments(enchantments, result);
            }
        }
        return result;
    }

    private static ItemStack findOriginalItem(Container inv) {
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private Collection<? extends IPartData> getParts() {
        PartDataList ret = PartDataList.of();
        //noinspection OverlyLongLambda
        this.result.parts.forEach(lazy -> {
            PartData part = PartData.from(lazy.getItem());
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

    public record Result(Item item, List<LazyPartData> parts) {
        public static final Codec<Result> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(r -> r.item),
                        Codec.list(LazyPartData.CODEC).fieldOf("parts").forGetter(r -> r.parts)
                ).apply(instance, Result::new)
        );

        public static Result fromNetwork(FriendlyByteBuf buf) {
            var item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
            var parts = new ArrayList<LazyPartData>();
            int partListSize = buf.readByte();
            for (int i = 0; i < partListSize; ++i) {
                parts.add(LazyPartData.fromNetwork(buf));
            }
            return new Result(item, parts);
        }

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item));
            buf.writeByte(parts.size());
            parts.forEach(p -> p.toNetwork(buf));
        }
    }

    public static class Serializer implements RecipeSerializer<ConversionRecipe> {
        private static final Codec<ConversionRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
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

        @Override
        public Codec<ConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public ConversionRecipe fromNetwork(FriendlyByteBuf buf) {
            var group = buf.readUtf();
            var bookCategory = buf.readEnum(CraftingBookCategory.class);
            var result = Result.fromNetwork(buf);
            var ingredients = NonNullList.<Ingredient>create();
            var ingredientListSize = buf.readByte();
            for (int i = 0; i < ingredientListSize; ++i) {
                ingredients.add(Ingredient.fromNetwork(buf));
            }
            return new ConversionRecipe(group, bookCategory, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ConversionRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            recipe.result.toNetwork(buf);
            buf.writeByte(recipe.ingredients.size());
            recipe.ingredients.forEach(ing -> ing.toNetwork(buf));
        }
    }
}
