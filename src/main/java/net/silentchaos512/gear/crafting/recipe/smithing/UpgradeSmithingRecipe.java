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
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class UpgradeSmithingRecipe extends GearSmithingRecipe {
    public UpgradeSmithingRecipe(ItemStack gearItem, Ingredient template, Ingredient addition) {
        super(gearItem, template, addition);
    }

    @Override
    protected ItemStack applyUpgrade(ItemStack gear, ItemStack upgradeItem) {
        PartInstance part = PartInstance.from(upgradeItem);
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
        public static final MapCodec<UpgradeSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        ItemStack.SINGLE_ITEM_CODEC.fieldOf("gear").forGetter(r -> r.gearItem),
                        Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
                        Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, UpgradeSmithingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, r -> r.gearItem,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.template,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.addition,
                UpgradeSmithingRecipe::new
        );

        @Override
        public MapCodec<UpgradeSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, UpgradeSmithingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
