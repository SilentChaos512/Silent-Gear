package net.silentchaos512.gear.api.traits;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface ITraitInstance {
    ResourceLocation getTraitId();

    @Nullable
    Trait getTrait();

    int getLevel();

    Collection<ITraitCondition> getConditions();

    default boolean conditionsMatch(PartGearKey key, ItemStack gear, List<? extends GearComponentInstance<?>> components) {
        Trait trait = getTrait();
        if (trait == null) return true;

        for (ITraitCondition condition : getConditions()) {
            if (!condition.matches(trait, key, gear, components)) {
                return false;
            }
        }

        return true;
    }

    default MutableComponent getConditionsText() {
        // Basically the same as AndTraitCondition
        return getConditions().stream()
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "and")).append(t2))
                .orElseGet(() -> Component.literal(""));
    }
}
