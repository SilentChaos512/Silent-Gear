package net.silentchaos512.gear.compat.caelus;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.caelus.api.CaelusApi;

final class CaelusCompatProxy {
    private CaelusCompatProxy() {}

    static void addFlightAttribute(Multimap<Attribute, AttributeModifier> map) {
        CaelusApi.ELYTRA_FLIGHT.ifPresent(attr -> map.put(attr, CaelusApi.ELYTRA_MODIFIER));
    }
}
