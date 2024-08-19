package net.silentchaos512.gear.crafting.recipe.smithing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Objects;

public class CoatingSmithingRecipe extends GearSmithingRecipe {
    public CoatingSmithingRecipe(ItemStack gearItem, Ingredient template, Ingredient addition) {
        super(gearItem, template, addition);
    }

    @Override
    protected ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem) {
        MaterialInstance material = MaterialInstance.from(upgradeItem);
        if (material != null) {
            GearType gearType = GearHelper.getType(gear);
            if (gearType.isGear()) {
                ItemStack result = gear.copy();

                PartTypes.COATING.get().getCompoundPartItem(gearType).ifPresent(cpi -> {
                    ItemStack partItem = cpi.create(material, 1);
                    // Unfortunately this deletes the old part; can't get a player here
                    GearData.addOrReplacePart(result, Objects.requireNonNull(PartInstance.from(partItem)));
                });

                result.setDamageValue(0);
                GearData.recalculateStats(result, CommonHooks.getCraftingPlayer()); // Crafting player is always null?
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SMITHING_COATING.get();
    }

    public static class Serializer implements RecipeSerializer<CoatingSmithingRecipe> {
        public static final MapCodec<CoatingSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ItemStack.SINGLE_ITEM_CODEC.fieldOf("gear").forGetter(r -> r.gearItem),
                        Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
                        Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, CoatingSmithingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CoatingSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, r -> r.gearItem,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.template,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.addition,
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
