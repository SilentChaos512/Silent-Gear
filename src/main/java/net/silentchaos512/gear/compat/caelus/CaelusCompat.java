package net.silentchaos512.gear.compat.caelus;

import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.fml.ModList;
import net.silentchaos512.gear.util.Const;

public final class CaelusCompat {
    private CaelusCompat() {}

    public static void tryAddFlightAttribute(ItemAttributeModifiers.Builder builder) {
        if (ModList.get().isLoaded(Const.CAELUS)) {
            CaelusCompatProxy.addFlightAttribute(builder);
        }
    }
}
