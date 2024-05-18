package net.silentchaos512.gear.crafting.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.*;

public final class ConversionRecipe extends ExtendedShapelessRecipe {
    private final Map<PartType, List<IMaterialInstance>> resultMaterials = new LinkedHashMap<>();
    private final ICoreItem item;

    private ConversionRecipe(String pGroup, CraftingBookCategory pCategory, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pGroup, pCategory, pResult, pIngredients);

        Item resultItem = pResult.getItem();
        if (!(resultItem instanceof ICoreItem)) {
            throw new JsonParseException("result is not a gear item: " + pResult);
        }
        this.item = (ICoreItem) resultItem;
    }

    private static void deserializeMaterials(JsonObject json, ConversionRecipe recipe) {
        JsonObject resultJson = json.getAsJsonObject("result");
        for (Map.Entry<String, JsonElement> entry : resultJson.getAsJsonObject("materials").entrySet()) {
            PartType partType = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(entry.getKey())));
            JsonElement element = entry.getValue();
            if (element.isJsonArray()) {
                List<IMaterialInstance> list = new ArrayList<>();
                for (JsonElement e : element.getAsJsonArray()) {
                    list.add(LazyMaterialInstance.deserialize(e.getAsJsonObject()));
                }
                recipe.resultMaterials.put(partType, list);
            } else {
                recipe.resultMaterials.put(partType, Collections.singletonList(LazyMaterialInstance.deserialize(element.getAsJsonObject())));
            }
        }
    }

    private static void readMaterials(FriendlyByteBuf buffer, ConversionRecipe recipe) {
        int typeCount = buffer.readByte();
        for (int i = 0; i < typeCount; ++i) {
            PartType partType = PartType.get(buffer.readResourceLocation());

            int matCount = buffer.readByte();
            List<IMaterialInstance> list = new ArrayList<>(matCount);
            for (int j = 0; j < matCount; ++j) {
                list.add(LazyMaterialInstance.read(buffer));
            }

            recipe.resultMaterials.put(partType, list);
        }
    }

    private static void writeMaterials(FriendlyByteBuf buffer, ConversionRecipe recipe) {
        buffer.writeByte(recipe.resultMaterials.size());
        recipe.resultMaterials.forEach((partType, list) -> {
            buffer.writeResourceLocation(partType.getName());
            buffer.writeByte(list.size());
            list.forEach(mat -> mat.write(buffer));
        });
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
        this.resultMaterials.forEach((partType, list) -> {
            partType.getCompoundPartItem(item.getGearType()).ifPresent(partItem -> {
                PartData part = PartData.from(partItem.create(list));
                if (part != null) {
                    ret.add(part);
                }
            });
        });
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ConversionRecipe> {
        private static final Codec<ConversionRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
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
        public ConversionRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ConversionRecipe pRecipe) {

        }
    }
}
