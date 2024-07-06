package net.silentchaos512.gear.crafting.ingredient;

import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.setup.gear.GearTypes;

import java.util.Optional;

public interface IGearIngredient {
    PartType getPartType();

    default GearType getGearType() {
        return GearTypes.ALL.get();
    }

    default Optional<Component> getJeiHint() {
        return Optional.empty();
    }
}
