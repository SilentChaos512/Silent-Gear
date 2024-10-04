package net.silentchaos512.gear.compat.caelus;

import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.world.entity.EquipmentSlotGroup;
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
}
