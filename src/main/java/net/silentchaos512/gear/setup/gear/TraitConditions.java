package net.silentchaos512.gear.setup.gear;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.gear.trait.condition.*;
import net.silentchaos512.gear.setup.SgRegistries;

public class TraitConditions {
    public static final DeferredRegister<TraitConditionSerializer<?>> REGISTRAR =
            DeferredRegister.create(SgRegistries.TRAIT_CONDITION, SilentGear.MOD_ID);

    public static final DeferredHolder<TraitConditionSerializer<?>, TraitConditionSerializer<AndTraitCondition>> AND =
            REGISTRAR.register("and", () -> AndTraitCondition.SERIALIZER);
    public static final DeferredHolder<TraitConditionSerializer<?>, TraitConditionSerializer<GearTypeTraitCondition>> GEAR_TYPE =
            REGISTRAR.register("gear_type", () -> GearTypeTraitCondition.SERIALIZER);
    public static final DeferredHolder<TraitConditionSerializer<?>, TraitConditionSerializer<MaterialCountTraitCondition>> MATERIAL_COUNT =
            REGISTRAR.register("material_count", () -> MaterialCountTraitCondition.SERIALIZER);
    public static final DeferredHolder<TraitConditionSerializer<?>, TraitConditionSerializer<MaterialRatioTraitCondition>> MATERIAL_RATIO =
            REGISTRAR.register("material_ratio", () -> MaterialRatioTraitCondition.SERIALIZER);
    public static final DeferredHolder<TraitConditionSerializer<?>, TraitConditionSerializer<NotTraitCondition>> NOT =
            REGISTRAR.register("not", () -> NotTraitCondition.SERIALIZER);
    public static final DeferredHolder<TraitConditionSerializer<?>, TraitConditionSerializer<OrTraitCondition>> OR =
            REGISTRAR.register("or", () -> OrTraitCondition.SERIALIZER);

}
