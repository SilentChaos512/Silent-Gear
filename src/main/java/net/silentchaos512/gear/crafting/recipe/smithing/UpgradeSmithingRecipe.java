package net.silentchaos512.gear.crafting.recipe.smithing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class UpgradeSmithingRecipe extends GearSmithingRecipe {
    public UpgradeSmithingRecipe(ItemStack gearItem, Ingredient template, Ingredient addition) {
        super(gearItem, template, addition);
    }

    @Override
    protected ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem) {
        PartData part = PartData.from(upgradeItem);
        if (part != null) {
            GearType gearType = GearHelper.getType(gear);
            if (gearType.isGear() && part.get().canAddToGear(gear, part) && !GearData.hasPart(gear, part.get())) {
                ItemStack result = gear.copy();
                GearData.addPart(result, part);
                GearData.recalculateStats(result, CommonHooks.getCraftingPlayer());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgRecipes.SMITHING_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<UpgradeSmithingRecipe> {
        public static final Codec<UpgradeSmithingRecipe> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        ItemStack.SINGLE_ITEM_CODEC.fieldOf("gear").forGetter(r -> r.gearItem),
                        Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
                        Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, UpgradeSmithingRecipe::new)
        );

        @Override
        public Codec<UpgradeSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public UpgradeSmithingRecipe fromNetwork(FriendlyByteBuf buffer) {
            ItemStack gearItem = buffer.readItem();
            Ingredient template = Ingredient.fromNetwork(buffer);
            Ingredient addition = Ingredient.fromNetwork(buffer);
            return new UpgradeSmithingRecipe(gearItem, template, addition);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, UpgradeSmithingRecipe recipe) {
            buffer.writeItem(recipe.gearItem);
            recipe.template.toNetwork(buffer);
            recipe.addition.toNetwork(buffer);
        }
    }
}
