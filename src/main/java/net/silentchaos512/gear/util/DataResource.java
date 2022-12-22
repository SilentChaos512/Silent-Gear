package net.silentchaos512.gear.util;

import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitManager;

import java.util.function.Function;

// Deprecated: use api package class instead
@Deprecated
public class DataResource<T> extends net.silentchaos512.gear.api.util.DataResource<T> {
    public DataResource(ResourceLocation id, Function<ResourceLocation, T> getter) {
        super(id, getter);
    }

    public static net.silentchaos512.gear.api.util.DataResource<IMaterial> material(String modPath) {
        return material(SilentGear.getId(modPath));
    }

    public static net.silentchaos512.gear.api.util.DataResource<IMaterial> material(ResourceLocation id) {
        return new net.silentchaos512.gear.api.util.DataResource<>(id, MaterialManager::get);
    }

    public static net.silentchaos512.gear.api.util.DataResource<IGearPart> part(String modPath) {
        return part(SilentGear.getId(modPath));
    }

    public static net.silentchaos512.gear.api.util.DataResource<IGearPart> part(ResourceLocation id) {
        return new net.silentchaos512.gear.api.util.DataResource<>(id, PartManager::get);
    }

    public static net.silentchaos512.gear.api.util.DataResource<ITrait> trait(String modPath) {
        return trait(SilentGear.getId(modPath));
    }

    public static net.silentchaos512.gear.api.util.DataResource<ITrait> trait(ResourceLocation id) {
        return new net.silentchaos512.gear.api.util.DataResource<>(id, TraitManager::get);
    }
}
