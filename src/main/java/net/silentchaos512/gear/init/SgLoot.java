package net.silentchaos512.gear.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;

import java.util.function.Supplier;

public final class SgLoot {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, SilentGear.MOD_ID);
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, SilentGear.MOD_ID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SilentGear.MOD_ID);

    // Conditions
    public static final RegistryObject<LootItemConditionType> HAS_TRAIT =
            registerCondition("has_trait", () -> new LootItemConditionType(HasTraitCondition.SERIALIZER));

    // Functions
    public static final RegistryObject<LootItemFunctionType> SELECT_TIER =
            registerFunction("select_tier", () -> new LootItemFunctionType(SelectGearTierLootFunction.SERIALIZER));
    public static final RegistryObject<LootItemFunctionType> SET_PARTS =
            registerFunction("set_parts", () -> new LootItemFunctionType(SetPartsFunction.SERIALIZER));

    // Modifiers
    public static final RegistryObject<Codec<BonusDropsTraitLootModifier>> BONUS_DROPS_TRAIT =
            registerModifier("bonus_drops_trait", BonusDropsTraitLootModifier.CODEC);
    public static final RegistryObject<Codec<MagmaticTraitLootModifier>> MAGMATIC_SMELTING =
            registerModifier("magmatic_smelting", MagmaticTraitLootModifier.CODEC);

    private SgLoot() {
    }

    private static <T extends LootItemConditionType> RegistryObject<T> registerCondition(String name, Supplier<T> condition) {
        return LOOT_CONDITIONS.register(name, condition);
    }

    private static <T extends LootItemFunctionType> RegistryObject<T> registerFunction(String name, Supplier<T> condition) {
        return LOOT_FUNCTIONS.register(name, condition);
    }

    private static <T extends IGlobalLootModifier> RegistryObject<Codec<T>> registerModifier(String name, Supplier<Codec<T>> codec) {
        return LOOT_MODIFIERS.register(name, codec);
    }
}
