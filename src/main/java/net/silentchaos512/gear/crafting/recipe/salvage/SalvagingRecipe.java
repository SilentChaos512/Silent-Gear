package net.silentchaos512.gear.crafting.recipe.salvage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRecipes;

import java.util.*;

public class SalvagingRecipe implements Recipe<Container> {
    protected final Ingredient ingredient;
    private final List<ItemStack> results = new ArrayList<>();

    public SalvagingRecipe(Ingredient ingredient, List<ItemStack> results) {
        this.ingredient = ingredient;
        this.results.addAll(results);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<ItemStack> getPossibleResults(Container inv) {
        return new ArrayList<>(results);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return ingredient.test(inv.getItem(0));
    }

    @Deprecated
    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        // DO NOT USE
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        // DO NOT USE
        return !results.isEmpty() ? results.get(0) : ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SALVAGING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SgRecipes.SALVAGING_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Salvages parts into their respective material items, or fragments if appropriate. This does
     * not necessarily give back the original item used for the material, but an item that matches
     * it.
     *
     * @param part The part
     * @return The list of items to return
     */
    public static List<ItemStack> salvage(PartData part) {
        if (part.get() instanceof CompoundPart && part.getItem().getItem() instanceof CompoundPartItem) {
            int craftedCount = ((CompoundPartItem) part.getItem().getItem()).getCraftedCount(part.getItem());
            if (craftedCount < 1) {
                SilentGear.LOGGER.warn("Compound part's crafted count is less than 1? {}", part.getItem());
                return Collections.singletonList(part.getItem());
            }

            List<IMaterialInstance> materials = part.getMaterials();
            Map<IMaterialInstance, Integer> fragments = new LinkedHashMap<>();

            for (IMaterialInstance material : materials) {
                int fragmentCount = 8 / craftedCount;
                fragments.merge(material.onSalvage(), fragmentCount, Integer::sum);
            }

            List<ItemStack> ret = new ArrayList<>();
            for (Map.Entry<IMaterialInstance, Integer> entry : fragments.entrySet()) {
                IMaterialInstance material = entry.getKey();
                int count = entry.getValue();
                int fulls = count / 8;
                int frags = count % 8;
                if (fulls > 0) {
                    ret.add(material.getItem());
                }
                if (frags > 0) {
                    ret.add(SgItems.FRAGMENT.get().create(material, frags));
                }
            }
            return ret;
        }
        return Collections.singletonList(part.getItem());
    }

    public static class Serializer implements RecipeSerializer<SalvagingRecipe> {
        public static final Codec<SalvagingRecipe> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient),
                        Codec.list(ItemStack.ITEM_WITH_COUNT_CODEC).fieldOf("results").forGetter(r -> r.results)
                ).apply(instance, SalvagingRecipe::new)
        );

        @Override
        public Codec<SalvagingRecipe> codec() {
            return CODEC;
        }

        @Override
        public SalvagingRecipe fromNetwork(FriendlyByteBuf buf) {
            var ingredient = Ingredient.fromNetwork(buf);
            var results = new ArrayList<ItemStack>();
            int resultCount = buf.readByte();
            for (int i = 0; i < resultCount; ++i) {
                results.add(buf.readItem());
            }
            return new SalvagingRecipe(ingredient, results);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SalvagingRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeByte(recipe.results.size());
            recipe.results.forEach(buf::writeItem);
        }
    }
}
