package net.silentchaos512.gear.setup;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialSerializer;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SilentGear.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SgRegistries {
    public static final ResourceKey<Registry<GearType>> GEAR_TYPE_KEY = createRegistryKey("gear_type");
    public static final ResourceKey<Registry<PartType>> PART_TYPE_KEY = createRegistryKey("part_type");
    public static final ResourceKey<Registry<GearProperty<?, ? extends GearPropertyValue<?>>>> GEAR_PROPERTY_KEY = createRegistryKey("property");
    public static final ResourceKey<Registry<TraitConditionSerializer<?>>> TRAIT_CONDITION_KEY = createRegistryKey("trait_condition");
    public static final ResourceKey<Registry<TraitEffectType<?>>> TRAIT_EFFECT_TYPE_KEY = createRegistryKey("trait_effect_type");
    public static final ResourceKey<Registry<MaterialSerializer<?>>> MATERIAL_SERIALIZER_KEY = createRegistryKey("material_serializer");
    public static final ResourceKey<Registry<IMaterialModifierType<?>>> MATERIAL_MODIFIER_TYPE_KEY = createRegistryKey("material_modifier_type");
    public static final ResourceKey<Registry<PartSerializer<?>>> PART_SERIALIZER_KEY = createRegistryKey("part_serializer");

    public static final Registry<GearType> GEAR_TYPE = new RegistryBuilder<>(GEAR_TYPE_KEY)
            .sync(true)
            .defaultKey(SilentGear.getId("none"))
            .create();
    public static final Registry<PartType> PART_TYPE = new RegistryBuilder<>(PART_TYPE_KEY)
            .sync(true)
            .defaultKey(SilentGear.getId("none"))
            .create();
    public static final Registry<GearProperty<?, ? extends GearPropertyValue<?>>> GEAR_PROPERTY = new RegistryBuilder<>(GEAR_PROPERTY_KEY)
            .sync(true)
            .create();
    public static final Registry<TraitConditionSerializer<?>> TRAIT_CONDITION = new RegistryBuilder<>(TRAIT_CONDITION_KEY)
            .sync(true)
            .create();
    public static final Registry<TraitEffectType<?>> TRAIT_EFFECT_TYPE = new RegistryBuilder<>(TRAIT_EFFECT_TYPE_KEY)
            .sync(true)
            .create();
    public static final Registry<MaterialSerializer<?>> MATERIAL_SERIALIZER = new RegistryBuilder<>(MATERIAL_SERIALIZER_KEY)
            .sync(true)
            .create();
    public static final Registry<IMaterialModifierType<?>> MATERIAL_MODIFIER_TYPE = new RegistryBuilder<>(MATERIAL_MODIFIER_TYPE_KEY)
            .sync(true)
            .create();
    public static final Registry<PartSerializer<?>> PART_SERIALIZER = new RegistryBuilder<>(PART_SERIALIZER_KEY)
            .sync(true)
            .create();

    public static final TraitManager TRAIT = new TraitManager();
    public static final MaterialManager MATERIAL = new MaterialManager();
    public static final PartManager PART = new PartManager();

    private static @NotNull <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(SilentGear.getId(name));
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(GEAR_TYPE);
        event.register(PART_TYPE);
        event.register(GEAR_PROPERTY);
        event.register(TRAIT_CONDITION);
        event.register(TRAIT_EFFECT_TYPE);
        event.register(MATERIAL_SERIALIZER);
        event.register(MATERIAL_MODIFIER_TYPE);
        event.register(PART_SERIALIZER);
    }
}
