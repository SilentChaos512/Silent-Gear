package net.silentchaos512.gear.setup;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.*;

public class SgIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, SilentGear.MOD_ID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<BlueprintIngredient>> BLUEPRINT = register("blueprint", BlueprintIngredient.CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<CustomAlloyIngredient>> CUSTOM_ALLOY = register("custom_compound", CustomAlloyIngredient.CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<GearTypeIngredient>> GEAR_TYPE = register("gear_type", GearTypeIngredient.CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<PartMaterialIngredient>> MATERIAL = register("material", PartMaterialIngredient.CODEC);
    public static final DeferredHolder<IngredientType<?>, IngredientType<GearPartIngredient>> PART = register("part_type", GearPartIngredient.CODEC);

    private static <T extends Ingredient> DeferredHolder<IngredientType<?>, IngredientType<T>> register(String name, Codec<T> codec) {
        return REGISTER.register(name, () -> new IngredientType<>(codec));
    }
}
