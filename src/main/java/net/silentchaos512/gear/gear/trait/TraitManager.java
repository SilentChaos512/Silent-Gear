package net.silentchaos512.gear.gear.trait;

import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.core.DataResourceManager;
import net.silentchaos512.gear.gear.TraitJsonException;

public final class TraitManager extends DataResourceManager<Trait> {
    public TraitManager() {
        super(Trait.CODEC, TraitJsonException::new, "trait", "silentgear_traits", "TraitManager", SilentGear.LOGGER);
    }
}
