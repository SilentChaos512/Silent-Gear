package net.silentchaos512.gear.compat.caelus;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import top.theillusivec4.caelus.api.CaelusApi;

import java.util.UUID;

final class CaelusCompatProxy {
    private static final AttributeModifier ELYTRA_MOD = new AttributeModifier(
            UUID.fromString("f6fdf316-7223-4b29-a92c-2bead65c8776"),
            "Elytra modifier",
            1,
            AttributeModifier.Operation.ADD_VALUE);

    private CaelusCompatProxy() {}

    static void addFlightAttribute(ItemAttributeModifiers.Builder builder) {
        builder.add(CaelusApi.getInstance().getFlightAttribute(), ELYTRA_MOD, EquipmentSlotGroup.BODY);
    }
}
