package net.silentchaos512.gear.init;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.*;
import net.silentchaos512.gear.crafting.recipe.*;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.GemCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.MetalCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.press.PressingRecipe;
import net.silentchaos512.gear.crafting.recipe.press.MaterialPressingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.CompoundPartSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.GearSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.CoatingSmithingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.UpgradeSmithingRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedSingleItemRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final IRecipeType<CompoundingRecipe> COMPOUNDING_TYPE = IRecipeType.register(Const.COMPOUNDING.toString());
    public static final IRecipeType<GemCompoundingRecipe> COMPOUNDING_GEM_TYPE = IRecipeType.register(Const.COMPOUNDING_GEM.toString());
    public static final IRecipeType<MetalCompoundingRecipe> COMPOUNDING_METAL_TYPE = IRecipeType.register(Const.COMPOUNDING_METAL.toString());
    public static final IRecipeType<PressingRecipe> PRESSING_TYPE = IRecipeType.register(Const.PRESSING.toString());
    public static final IRecipeType<SalvagingRecipe> SALVAGING_TYPE = IRecipeType.register(Const.SALVAGING.toString());

    public static final RegistryObject<IRecipeSerializer<?>> COMBINE_FRAGMENTS = register(Const.COMBINE_FRAGMENTS, () -> new SpecialRecipeSerializer<>(CombineFragmentsRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> COMPOUND_PART = register(Const.COMPOUND_PART, () -> ExtendedShapelessRecipe.Serializer.basic(ShapelessCompoundPartRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> COMPOUNDING = register(Const.COMPOUNDING, () -> new CompoundingRecipe.Serializer<>(CompoundingRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> COMPOUNDING_GEM = register(Const.COMPOUNDING_GEM, () -> new CompoundingRecipe.Serializer<>(GemCompoundingRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> COMPOUNDING_METAL = register(Const.COMPOUNDING_METAL, () -> new CompoundingRecipe.Serializer<>(MetalCompoundingRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> CONVERSION = register("conversion", ConversionRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> DAMAGE_ITEM = register(Const.DAMAGE_ITEM, SGearDamageItemRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> FILL_REPAIR_KIT = register(Const.FILL_REPAIR_KIT, () -> new SpecialRecipeSerializer<>(FillRepairKitRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> MOD_KIT_REMOVE_PART = register(Const.MOD_KIT_REMOVE_PART, () -> new SpecialRecipeSerializer<>(ModKitRemovePartRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> PRESSING = register(Const.PRESSING, () ->
            ExtendedSingleItemRecipe.Serializer.basic(PRESSING_TYPE, PressingRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> PRESSING_MATERIAL = register(Const.PRESSING_MATERIAL, () ->
            ExtendedSingleItemRecipe.Serializer.basic(PRESSING_TYPE, MaterialPressingRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> QUICK_REPAIR = register(Const.QUICK_REPAIR, () -> new SpecialRecipeSerializer<>(QuickRepairRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> SALVAGING = register(Const.SALVAGING, SalvagingRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> SALVAGING_GEAR = register(Const.SALVAGING_GEAR, GearSalvagingRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> SALVAGING_COMPOUND_PART = register(Const.SALVAGING_COMPOUND_PART, CompoundPartSalvagingRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> SHAPED_GEAR = register(Const.SHAPED_GEAR_CRAFTING, () -> ExtendedShapedRecipe.Serializer.basic(ShapedGearRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> SHAPELESS_GEAR = register(Const.SHAPELESS_GEAR_CRAFTING, () -> ExtendedShapelessRecipe.Serializer.basic(ShapelessGearRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> SMITHING_COATING = register(Const.SMITHING_COATING, CoatingSmithingRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> SMITHING_UPGRADE = register(Const.SMITHING_UPGRADE, UpgradeSmithingRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> SWAP_GEAR_PART = register(Const.SWAP_GEAR_PART, () -> new SpecialRecipeSerializer<>(GearPartSwapRecipe::new));

    // This overrides the vanilla crafting grid repair recipe, to prevent it from destroying gear items
    @SuppressWarnings("unused")
    public static final RegistryObject<IRecipeSerializer<?>> REPAIR_ITEM_OVERRIDE = register("crafting_special_repairitem", () -> new SpecialRecipeSerializer<>(RepairItemRecipeFix::new));

    private ModRecipes() {}

    static void register() {
        // Ingredient serializers
        CraftingHelper.register(BlueprintIngredient.Serializer.NAME, BlueprintIngredient.Serializer.INSTANCE);
        CraftingHelper.register(ExclusionIngredient.Serializer.NAME, ExclusionIngredient.Serializer.INSTANCE);
        CraftingHelper.register(GearPartIngredient.Serializer.NAME, GearPartIngredient.Serializer.INSTANCE);
        CraftingHelper.register(GearTypeIngredient.Serializer.NAME, GearTypeIngredient.Serializer.INSTANCE);
        CraftingHelper.register(PartMaterialIngredient.Serializer.NAME, PartMaterialIngredient.Serializer.INSTANCE);
        CraftingHelper.register(CustomCompoundIngredient.Serializer.NAME, CustomCompoundIngredient.Serializer.INSTANCE);
    }

    private static RegistryObject<IRecipeSerializer<?>> register(String name, Supplier<IRecipeSerializer<?>> serializer) {
        return register(SilentGear.getId(name), serializer);
    }

    private static RegistryObject<IRecipeSerializer<?>> register(ResourceLocation id, Supplier<IRecipeSerializer<?>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(id.getPath(), serializer);
    }

    public static boolean isRepairMaterial(ItemStack gear, ItemStack materialItem) {
        MaterialInstance mat = MaterialInstance.from(materialItem);
        if (mat != null) {
            return mat.getRepairValue(gear) > 0;
        }

        return false;
    }
}
