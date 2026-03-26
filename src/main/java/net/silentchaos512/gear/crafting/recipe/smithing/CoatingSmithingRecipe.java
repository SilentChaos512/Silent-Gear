package net.silentchaos512.gear.crafting.recipe.smithing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Objects;
import java.util.Optional;

/** @noinspection OptionalUsedAsFieldOrParameterType*/
public class CoatingSmithingRecipe extends GearSmithingRecipe {
    public static final RecipeSerializer<CoatingSmithingRecipe> SERIALIZER = createSerializer(CoatingSmithingRecipe::new);

    public CoatingSmithingRecipe(Ingredient gearItem, Optional<Ingredient> template, Optional<Ingredient> addition) {
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
        return SERIALIZER;
    }
}
