package net.silentchaos512.gear.traits;

import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;

/**
 * @deprecated Use {@link net.silentchaos512.gear.gear.trait.TraitSerializers} instead
 */
@Deprecated
public class TraitSerializers {
    public static <S extends ITraitSerializer<T>, T extends ITrait> S register(S serializer) {
        return net.silentchaos512.gear.gear.trait.TraitSerializers.register(serializer);
    }
}
