/*
package net.silentchaos512.gear.compat.caelus;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.caelus.api.CaelusApi;

import java.util.UUID;

final class CaelusCompatProxy {
    private static final AttributeModifier ELYTRA_MOD = new AttributeModifier(
            UUID.fromString("f6fdf316-7223-4b29-a92c-2bead65c8776"),
            "Elytra modifier",
            1,
            AttributeModifier.Operation.ADDITION);

    private CaelusCompatProxy() {}

    static void addFlightAttribute(Multimap<Attribute, AttributeModifier> map) {
        map.put(CaelusApi.getInstance().getFlightAttribute(), ELYTRA_MOD);
    }
}
*/
