package net.silentchaos512.gear.setup;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.recipe.*;
import net.silentchaos512.gear.crafting.recipe.alloy.*;
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
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyRecipe>> COMPOUNDING_TYPE = registerType(Const.ALLOY_MAKING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrudeAlloyRecipe>> ALLOY_MAKING_CRUDE_TYPE = registerType(Const.ALLOY_MAKING_CRUDE);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FabricAlloyRecipe>> ALLOY_MAKING_FABRIC_TYPE = registerType(Const.ALLOY_MAKING_FABRIC);
    public static final DeferredHolder<RecipeType<?>, RecipeType<GemAlloyRecipe>> ALLOY_MAKING_GEM_TYPE = registerType(Const.ALLOY_MAKING_GEM);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MetalAlloyRecipe>> ALLOY_MAKING_METAL_TYPE = registerType(Const.ALLOY_MAKING_METAL);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SuperAlloyRecipe>> ALLOY_MAKING_SUPER_TYPE = registerType(Const.ALLOY_MAKING_SUPER);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PressingRecipe>> PRESSING_TYPE = registerType(Const.PRESSING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MaterialPressingRecipe>> MATERIAL_PRESSING_TYPE = registerType(Const.PRESSING_MATERIAL);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SalvagingRecipe>> SALVAGING_TYPE = registerType(Const.SALVAGING);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ToolActionRecipe>> TOOL_ACTION_TYPE = registerType(Const.TOOL_ACTION);

    // Serializers
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessCompoundPartRecipe>> COMPOUND_PART = register(Const.COMPOUND_PART,
            () -> ExtendedShapelessRecipe.basicSerializer(ShapelessCompoundPartRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyRecipe>> COMPOUNDING = register(Const.ALLOY_MAKING,
            () -> AlloyRecipe.createSerializer(AlloyRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrudeAlloyRecipe>> ALLOY_MAKING_CRUDE = register(Const.ALLOY_MAKING_CRUDE,
            () -> AlloyRecipe.createSerializer(CrudeAlloyRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FabricAlloyRecipe>> ALLOY_MAKING_FABRIC = register(Const.ALLOY_MAKING_FABRIC,
            () -> AlloyRecipe.createSerializer(FabricAlloyRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GemAlloyRecipe>> ALLOY_MAKING_GEM = register(Const.ALLOY_MAKING_GEM,
            () -> AlloyRecipe.createSerializer(GemAlloyRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MetalAlloyRecipe>> ALLOY_MAKING_METAL = register(Const.ALLOY_MAKING_METAL,
            () -> AlloyRecipe.createSerializer(MetalAlloyRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SuperAlloyRecipe>> ALLOY_MAKING_SUPER = register(Const.ALLOY_MAKING_SUPER,
            () -> AlloyRecipe.createSerializer(SuperAlloyRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ConversionRecipe>> CONVERSION = register(Const.CONVERSION,
            () -> ConversionRecipe.SERIALIZER
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FillRepairKitRecipe>> FILL_REPAIR_KIT = register(Const.FILL_REPAIR_KIT,
            () -> emptyCustomRecipe(FillRepairKitRecipe.INSTANCE)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ModKitRemovePartRecipe>> MOD_KIT_REMOVE_PART = register(Const.MOD_KIT_REMOVE_PART,
            () -> emptyCustomRecipe(ModKitRemovePartRecipe.INSTANCE)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PressingRecipe>> PRESSING = register(Const.PRESSING,
            () -> new RecipeSerializer<>(
                    SingleItemRecipe.simpleMapCodec(PressingRecipe::new),
                    SingleItemRecipe.simpleStreamCodec(PressingRecipe::new)
            )
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaterialPressingRecipe>> PRESSING_MATERIAL = register(Const.PRESSING_MATERIAL,
            () -> new RecipeSerializer<>(
                    SingleItemRecipe.simpleMapCodec(MaterialPressingRecipe::new),
                    SingleItemRecipe.simpleStreamCodec(MaterialPressingRecipe::new)
            )
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<QuickRepairRecipe>> QUICK_REPAIR = register(Const.QUICK_REPAIR,
            () -> emptyCustomRecipe(QuickRepairRecipe.INSTANCE)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SalvagingRecipe>> SALVAGING = register(Const.SALVAGING,
            () -> SalvagingRecipe.SERIALIZER
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GearSalvagingRecipe>> SALVAGING_GEAR = register(Const.SALVAGING_GEAR,
            () -> GearSalvagingRecipe.SERIALIZER
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CompoundPartSalvagingRecipe>> SALVAGING_COMPOUND_PART = register(Const.SALVAGING_COMPOUND_PART,
            () -> CompoundPartSalvagingRecipe.SERIALIZER
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedGearRecipe>> SHAPED_GEAR = register(Const.SHAPED_GEAR_CRAFTING,
            () -> ExtendedShapedRecipe.basicSerializer(ShapedGearRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessGearRecipe>> SHAPELESS_GEAR = register(Const.SHAPELESS_GEAR_CRAFTING,
            () -> ExtendedShapelessRecipe.basicSerializer(ShapelessGearRecipe::new)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CoatingSmithingRecipe>> SMITHING_COATING = register(Const.SMITHING_COATING,
            () -> CoatingSmithingRecipe.SERIALIZER
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeSmithingRecipe>> SMITHING_UPGRADE = register(Const.SMITHING_UPGRADE,
            () -> UpgradeSmithingRecipe.SERIALIZER
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GearPartSwapRecipe>> SWAP_GEAR_PART = register(Const.SWAP_GEAR_PART,
            () -> emptyCustomRecipe(GearPartSwapRecipe.INSTANCE)
    );
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ToolActionRecipe>> TOOL_ACTION = register(Const.TOOL_ACTION,
            () -> ToolActionRecipe.SERIALIZER
    );

    private SgRecipes() {
    }

    private static <T extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> register(Identifier id, Supplier<RecipeSerializer<T>> serializer) {
        return RECIPE_SERIALIZERS.register(id.getPath(), serializer);
    }

    public static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerType(Identifier name) {
        return RECIPE_TYPES.register(name.getPath(), () -> RecipeType.simple(name));
    }

    public static boolean isRepairMaterial(ItemStack gear, ItemStack materialItem) {
        MaterialInstance mat = MaterialInstance.from(materialItem);
        if (mat != null) {
            return mat.getRepairValue(gear) > 0;
        }

        return false;
    }

    private static <R extends CustomRecipe> RecipeSerializer<R> emptyCustomRecipe(R instance) {
        return new RecipeSerializer<>(MapCodec.unit(instance), StreamCodec.unit(instance));
    }
}
