package net.silentchaos512.gear.setup;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyType;
import net.silentchaos512.gear.api.traits.TraitConditionSerializer;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = SilentGear.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SgRegistries {
    public static final ResourceKey<Registry<GearPropertyType<?, ? extends GearProperty<?>>>> GEAR_PROPERTIES_KEY = createRegistryKey("gear_property");
    public static final ResourceKey<Registry<GearType>> GEAR_TYPES_KEY = createRegistryKey("gear_type");
    public static final ResourceKey<Registry<PartType>> PART_TYPES_KEY = createRegistryKey("part_type");
    public static final ResourceKey<Registry<TraitConditionSerializer<?>>> TRAIT_CONDITIONS_KEY = createRegistryKey("trait_condition");

    public static final Registry<GearPropertyType<?, ? extends GearProperty<?>>> GEAR_PROPERTIES = new RegistryBuilder<>(GEAR_PROPERTIES_KEY)
            .sync(true)
            .create();
    public static final Registry<GearType> GEAR_TYPES = new RegistryBuilder<>(GEAR_TYPES_KEY)
            .sync(true)
            .defaultKey(SilentGear.getId("none"))
            .create();
    public static final Registry<PartType> PART_TYPES = new RegistryBuilder<>(PART_TYPES_KEY)
            .sync(true)
            .defaultKey(SilentGear.getId("none"))
            .create();
    public static final Registry<TraitConditionSerializer<?>> TRAIT_CONDITIONS = new RegistryBuilder<>(TRAIT_CONDITIONS_KEY)
            .sync(true)
            .create();

    private static @NotNull <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(SilentGear.getId(name));
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(GEAR_PROPERTIES);
        event.register(GEAR_TYPES);
        event.register(PART_TYPES);
        event.register(TRAIT_CONDITIONS);
    }
}
