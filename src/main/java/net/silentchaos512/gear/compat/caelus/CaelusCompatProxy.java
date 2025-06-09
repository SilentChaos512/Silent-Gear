package net.silentchaos512.gear.compat.caelus;

import com.google.common.collect.Multimap;
import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.SilentGear;

final class CaelusCompatProxy {
    private static final AttributeModifier ELYTRA_MOD = new AttributeModifier(
            SilentGear.getId("elytra_flight"),
            1,
            AttributeModifier.Operation.ADD_VALUE
    );

    private CaelusCompatProxy() {}

    static void addFlightAttribute(ItemAttributeModifiers.Builder builder) {
        builder.add(CaelusApi.getInstance().getFallFlyingAttribute(), ELYTRA_MOD, EquipmentSlotGroup.BODY);
    }

    public static void addFlightAttribute(Multimap<Holder<Attribute>, AttributeModifier> attributeMap) {
        attributeMap.put(CaelusApi.getInstance().getFallFlyingAttribute(), new AttributeModifier(SilentGear.getId("gliding"), 1, AttributeModifier.Operation.ADD_VALUE));
    }
}
