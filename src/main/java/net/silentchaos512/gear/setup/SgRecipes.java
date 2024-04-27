package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.*;
import net.silentchaos512.gear.crafting.recipe.*;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.FabricCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.GemCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.MetalCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.press.MaterialPressingRecipe;
import net.silentchaos512.gear.crafting.recipe.press.PressingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.CompoundPartSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.GearSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.CoatingSmithingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.UpgradeSmithingRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

import java.util.function.Supplier;

public final class SgRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SilentGear.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, SilentGear.MOD_ID);

    // Types
    public static final DeferredHolder<RecipeType<?>, RecipeType<CompoundingRecipe>> COMPOUNDING_TYPE = registerType(Const.COMPOUNDING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FabricCompoundingRecipe>> COMPOUNDING_FABRIC_TYPE = registerType(Const.COMPOUNDING_FABRIC);
    public static final DeferredHolder<RecipeType<?>, RecipeType<GemCompoundingRecipe>> COMPOUNDING_GEM_TYPE = registerType(Const.COMPOUNDING_GEM);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MetalCompoundingRecipe>> COMPOUNDING_METAL_TYPE = registerType(Const.COMPOUNDING_METAL);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PressingRecipe>> PRESSING_TYPE = registerType(Const.PRESSING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SalvagingRecipe>> SALVAGING_TYPE = registerType(Const.SALVAGING);

    // Serializers
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMBINE_FRAGMENTS = register(Const.COMBINE_FRAGMENTS, () ->
            new SimpleCraftingRecipeSerializer<>(CombineFragmentsRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMPOUND_PART = register(Const.COMPOUND_PART, () ->
            ExtendedShapelessRecipe.Serializer.basic(ShapelessCompoundPartRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMPOUNDING = register(Const.COMPOUNDING, () ->
            new CompoundingRecipe.Serializer<>(CompoundingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMPOUNDING_FABRIC = register(Const.COMPOUNDING_FABRIC, () ->
            new CompoundingRecipe.Serializer<>(GemCompoundingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMPOUNDING_GEM = register(Const.COMPOUNDING_GEM, () ->
            new CompoundingRecipe.Serializer<>(GemCompoundingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMPOUNDING_METAL = register(Const.COMPOUNDING_METAL, () ->
            new CompoundingRecipe.Serializer<>(MetalCompoundingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CONVERSION = register("conversion",
            ConversionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> DAMAGE_ITEM = register(Const.DAMAGE_ITEM,
            SGearDamageItemRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> FILL_REPAIR_KIT = register(Const.FILL_REPAIR_KIT, () ->
            new SimpleCraftingRecipeSerializer<>(FillRepairKitRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> MOD_KIT_REMOVE_PART = register(Const.MOD_KIT_REMOVE_PART, () ->
            new SimpleCraftingRecipeSerializer<>(ModKitRemovePartRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> PRESSING = register(Const.PRESSING, () ->
            ExtendedSingleItemRecipe.Serializer.basic(PRESSING_TYPE.get(), PressingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> PRESSING_MATERIAL = register(Const.PRESSING_MATERIAL, () ->
            ExtendedSingleItemRecipe.Serializer.basic(PRESSING_TYPE.get(), MaterialPressingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> QUICK_REPAIR = register(Const.QUICK_REPAIR, () ->
            new SimpleCraftingRecipeSerializer<>(QuickRepairRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SALVAGING = register(Const.SALVAGING,
            SalvagingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SALVAGING_GEAR = register(Const.SALVAGING_GEAR,
            GearSalvagingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SALVAGING_COMPOUND_PART = register(Const.SALVAGING_COMPOUND_PART,
            CompoundPartSalvagingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SHAPED_GEAR = register(Const.SHAPED_GEAR_CRAFTING, () ->
            ExtendedShapedRecipe.Serializer.basic(ShapedGearRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SHAPELESS_GEAR = register(Const.SHAPELESS_GEAR_CRAFTING, () ->
            ExtendedShapelessRecipe.Serializer.basic(ShapelessGearRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SMITHING_COATING = register(Const.SMITHING_COATING,
            CoatingSmithingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SMITHING_UPGRADE = register(Const.SMITHING_UPGRADE,
            UpgradeSmithingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SWAP_GEAR_PART = register(Const.SWAP_GEAR_PART, () ->
            new SimpleCraftingRecipeSerializer<>(GearPartSwapRecipe::new));

    // This overrides the vanilla crafting grid repair recipe, to prevent it from destroying gear items
    @SuppressWarnings("unused")
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> REPAIR_ITEM_OVERRIDE = register("crafting_special_repairitem", () ->
            new SimpleCraftingRecipeSerializer<>(RepairItemRecipeFix::new));

    private SgRecipes() {
    }

    private static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> register(String name, Supplier<RecipeSerializer<?>> serializer) {
        return register(SilentGear.getId(name), serializer);
    }

    private static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> register(ResourceLocation id, Supplier<RecipeSerializer<?>> serializer) {
        return RECIPE_SERIALIZERS.register(id.getPath(), serializer);
    }

    public static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerType(ResourceLocation name) {
        return RECIPE_TYPES.register(name.getPath(), () -> RecipeType.simple(name));
    }

    public static boolean isRepairMaterial(ItemStack gear, ItemStack materialItem) {
        MaterialInstance mat = MaterialInstance.from(materialItem);
        if (mat != null) {
            return mat.getRepairValue(gear) > 0;
        }

        return false;
    }
}
