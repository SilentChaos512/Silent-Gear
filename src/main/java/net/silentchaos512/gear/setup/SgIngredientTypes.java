package net.silentchaos512.gear.setup;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.*;

public class SgIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> REGISTRAR = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, SilentGear.MOD_ID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<BlueprintIngredient>> BLUEPRINT = register("blueprint", BlueprintIngredient.CODEC, BlueprintIngredient.STREAM_CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<CustomAlloyIngredient>> CUSTOM_ALLOY = register("custom_compound", CustomAlloyIngredient.CODEC, CustomAlloyIngredient.STREAM_CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<GearTypeIngredient>> GEAR_TYPE = register("gear_type", GearTypeIngredient.CODEC, GearTypeIngredient.STREAM_CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<PartMaterialIngredient>> MATERIAL = register("material", PartMaterialIngredient.CODEC, PartMaterialIngredient.STREAM_CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<GearPartIngredient>> PART = register("part_type", GearPartIngredient.CODEC, GearPartIngredient.STREAM_CODEC);

    private static <T extends ICustomIngredient> DeferredHolder<IngredientType<?>, IngredientType<T>> register(
            String name,
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return REGISTRAR.register(name, () -> new IngredientType<>(codec, streamCodec));
    }
}
