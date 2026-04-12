package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.lib.util.NameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ConversionRecipe extends ExtendedShapelessRecipe {
    public static final RecipeSerializer<ConversionRecipe> SERIALIZER = new RecipeSerializer<>(
            RecordCodecBuilder.mapCodec(
                    i -> i.group(
                            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                            Result.CODEC.fieldOf("result").forGetter(o -> o.result),
                            Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, ShapedRecipePattern.getMaxWidth() * ShapedRecipePattern.getMaxHeight())).fieldOf("ingredients").forGetter(o -> o.ingredients)
                    ).apply(i, ConversionRecipe::new)
            ),
            StreamCodec.composite(
                    Recipe.CommonInfo.STREAM_CODEC, o -> o.commonInfo,
                    CraftingRecipe.CraftingBookInfo.STREAM_CODEC, o -> o.bookInfo,
                    Result.STREAM_CODEC, o -> o.result,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.ingredients,
                    ConversionRecipe::new
            )
    );

    private final Result result;
    private final GearItem item;

    public ConversionRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, Result result, List<Ingredient> ingredients) {
        super(commonInfo, bookInfo, new ItemStackTemplate(result.item), ingredients);
        this.result = result;

        if (!(this.result.item instanceof GearItem)) {
            throw new JsonParseException("result is not a gear item: " + NameUtils.fromItem(this.result.item));
        }
        this.item = (GearItem) this.result.item;
    }

    @Override
    public RecipeSerializer<? extends ConversionRecipe> getSerializer() {
        return SERIALIZER;
    }

    private static boolean allowedInConfig() {
        return Config.Common.isLoaded() && Config.Common.allowConversionRecipes.getAsBoolean();
    }

    @Override
    public boolean matches(CraftingInput pInv, Level pLevel) {
        return allowedInConfig() && super.matches(pInv, pLevel);
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        ItemStack result = item.construct(getParts());
        ItemStack original = findOriginalItem(inv);
        if (!original.isEmpty()) {
            // Copy data components
            result.applyComponents(original.getComponents());
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
        PartList ret = PartList.mutable();
        this.result.parts.forEach(part -> {
            if (part.isValid()) {
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

        public static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.of(
                (buf, r) -> r.toNetwork(buf),
                Result::fromNetwork
        );

        public static Result fromNetwork(RegistryFriendlyByteBuf buf) {
            var item = BuiltInRegistries.ITEM.get(buf.readIdentifier()).orElseThrow().value();
            var parts = new ArrayList<PartInstance>();
            int partListSize = buf.readByte();
            for (int i = 0; i < partListSize; ++i) {
                parts.add(PartInstance.STREAM_CODEC.decode(buf));
            }
            return new Result(item, parts);
        }

        public void toNetwork(RegistryFriendlyByteBuf buf) {
            buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(item));
            buf.writeByte(parts.size());
            parts.forEach(part -> PartInstance.STREAM_CODEC.encode(buf, part));
        }
    }
}
