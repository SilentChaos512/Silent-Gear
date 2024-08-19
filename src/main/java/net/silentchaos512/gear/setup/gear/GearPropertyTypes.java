package net.silentchaos512.gear.setup.gear;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Supplier;

public class GearPropertyTypes {
    public static final DeferredRegister<GearPropertyType<?>> REGISTRAR = DeferredRegister.create(SgRegistries.GEAR_PROPERTY_TYPE, SilentGear.MOD_ID);

    public static final Supplier<GearPropertyType<NumberPropertyValue>> NUMBER = REGISTRAR.register(
            "number",
            () -> new GearPropertyType<>(NumberProperty.CODEC, NumberProperty.STREAM_CODEC)
    );

    public static final Supplier<GearPropertyType<TierPropertyValue>> TIER = REGISTRAR.register(
            "tier",
            () -> new GearPropertyType<>(TierProperty.CODEC, TierProperty.STREAM_CODEC)
    );

    public static final Supplier<GearPropertyType<TraitListPropertyValue>> TRAIT_LIST = REGISTRAR.register(
            "trait_list",
            () -> new GearPropertyType<>(TraitListProperty.CODEC, TraitListProperty.STREAM_CODEC)
    );
}
