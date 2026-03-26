package net.silentchaos512.gear.crafting.recipe.smithing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Optional;

/** @noinspection OptionalUsedAsFieldOrParameterType*/
public class UpgradeSmithingRecipe extends GearSmithingRecipe {
    public static final RecipeSerializer<UpgradeSmithingRecipe> SERIALIZER = createSerializer(UpgradeSmithingRecipe::new);

    public UpgradeSmithingRecipe(Ingredient gearItem, Optional<Ingredient> template, Optional<Ingredient> addition) {
        super(gearItem, template, addition);
    }

    @Override
    protected void applyUpgrade(ItemStack stackToModify, ItemStack upgradeItem) {
        PartInstance part = PartInstance.from(upgradeItem);
        if (part != null) {
            GearType gearType = GearHelper.getType(stackToModify);
            if (gearType.isGear() && part.isValid() && part.get().canAddToGear(stackToModify, part) && !GearData.hasPart(stackToModify, part.get())) {
                ItemStack result = stackToModify.copy();
                GearData.addPart(result, part);
                GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
            }
        }
    }

    @Override
    public RecipeSerializer<UpgradeSmithingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
