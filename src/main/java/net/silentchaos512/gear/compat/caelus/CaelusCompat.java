package net.silentchaos512.gear.compat.caelus;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

    public static void tryAddFlightAttribute(Multimap<Holder<Attribute>, AttributeModifier> attributeMap) {
        if (ModList.get().isLoaded(Const.CAELUS)) {
            CaelusCompatProxy.addFlightAttribute(attributeMap);
        }
    }
}
