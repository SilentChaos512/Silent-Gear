package net.silentchaos512.gear.crafting.ingredient;

import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import java.util.Optional;

public interface IGearIngredient {
    PartType getPartType();

    default GearType getGearType() {
        return GearType.ALL;
    }

    default Optional<Component> getJeiHint() {
        return Optional.empty();
    }
}
