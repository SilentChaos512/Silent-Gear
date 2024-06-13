package net.silentchaos512.gear.crafting.recipe.smithing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.setup.SgRecipes;
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

                PartType.COATING.getCompoundPartItem(gearType).ifPresent(cpi -> {
                    ItemStack partItem = cpi.create(material, 1);
                    // Unfortunately this deletes the old part; can't get a player here
                    GearData.addOrReplacePart(result, Objects.requireNonNull(PartData.from(partItem)));
                });

                result.setDamageValue(0);
                GearData.removeExcessParts(result, PartType.COATING);
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
        public static final Codec<CoatingSmithingRecipe> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ItemStack.SINGLE_ITEM_CODEC.fieldOf("gear").forGetter(r -> r.gearItem),
                        Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
                        Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, CoatingSmithingRecipe::new)
        );

        @Override
        public Codec<CoatingSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public CoatingSmithingRecipe fromNetwork(FriendlyByteBuf buffer) {
            ItemStack gearItem = buffer.readItem();
            Ingredient template = Ingredient.fromNetwork(buffer);
            Ingredient addition = Ingredient.fromNetwork(buffer);
            return new CoatingSmithingRecipe(gearItem, template, addition);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CoatingSmithingRecipe recipe) {
            buffer.writeItem(recipe.gearItem);
            recipe.template.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
        }
    }
}
