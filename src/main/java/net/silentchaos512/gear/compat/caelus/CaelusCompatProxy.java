package net.silentchaos512.gear.compat.caelus;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.silentchaos512.gear.SilentGear;
import top.theillusivec4.caelus.api.CaelusApi;

final class CaelusCompatProxy {
    private static final AttributeModifier ELYTRA_MOD = new AttributeModifier(
            SilentGear.getId("elytra_flight"),
            1,
            AttributeModifier.Operation.ADD_VALUE
    );

    private CaelusCompatProxy() {}

    static void addFlightAttribute(ItemAttributeModifiers.Builder builder) {
        builder.add(CaelusApi.getInstance().getFlightAttribute(), ELYTRA_MOD, EquipmentSlotGroup.BODY);
    }
}
