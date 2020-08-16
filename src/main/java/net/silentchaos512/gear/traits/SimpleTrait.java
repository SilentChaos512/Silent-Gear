package net.silentchaos512.gear.traits;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.traits.ITraitSerializer;

/**
 * @deprecated Use {@link net.silentchaos512.gear.gear.trait.SimpleTrait} instead
 */
@Deprecated
public class SimpleTrait extends net.silentchaos512.gear.gear.trait.SimpleTrait {
    public SimpleTrait(ResourceLocation id) {
        super(id);
    }

    public SimpleTrait(ResourceLocation id, ITraitSerializer<?> serializer) {
        super(id, serializer);
    }
}
