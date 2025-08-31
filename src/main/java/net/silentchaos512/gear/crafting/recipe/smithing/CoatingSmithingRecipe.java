package net.silentchaos512.gear.crafting.recipe.smithing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CoatingSmithingRecipe extends GearSmithingRecipe {
    public CoatingSmithingRecipe(ItemStack gearItem, Optional<Ingredient> template, Optional<Ingredient> addition) {
        super(gearItem, template, addition);
    }

    @Override
    protected void applyUpgrade(ItemStack stackToModify, ItemStack upgradeItem) {
        if (GearData.getPartOfType(stackToModify, PartTypes.MAIN.get()) == null) {
            return;
        }

        MaterialInstance material = MaterialInstance.from(upgradeItem);
        if (material != null) {
            GearType gearType = GearHelper.getType(stackToModify);
            if (gearType.isGear()) {
                ItemStack result = stackToModify.copy();

                PartTypes.COATING.get().getCompoundPartItem(gearType).ifPresent(cpi -> {
                    ItemStack partItem = cpi.create(material, 1);
                    // Unfortunately this deletes the old part; can't get a player here
                    GearData.addOrReplacePart(result, Objects.requireNonNull(PartInstance.from(partItem)));
                });

                result.setDamageValue(0);
                GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer()); // Crafting player is always null?
            }
        }
    }

    @Override
    public RecipeSerializer<CoatingSmithingRecipe> getSerializer() {
        return SgRecipes.SMITHING_COATING.get();
    }

    public static class Serializer implements RecipeSerializer<CoatingSmithingRecipe> {
        public static final MapCodec<CoatingSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ItemStack.SINGLE_ITEM_CODEC.fieldOf("gear").forGetter(r -> r.gearItem),
                        Ingredient.CODEC.optionalFieldOf("template").forGetter(r -> r.template),
                        Ingredient.CODEC.optionalFieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, CoatingSmithingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CoatingSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, r -> r.gearItem,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> r.template,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> r.addition,
                CoatingSmithingRecipe::new
        );

        @Override
        public MapCodec<CoatingSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CoatingSmithingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
