package net.silentchaos512.gear.api.material.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.util.StatGearKey;

import java.util.List;

public interface IMaterialModifier {
    IMaterialModifierType<?> getType();

    List<StatInstance> modifyStats(IMaterialInstance material, PartType partType, StatGearKey key, List<StatInstance> statMods);

    // TODO: modifyTraits method?

    void appendTooltip(List<Component> tooltip);

    MutableComponent modifyMaterialName(MutableComponent name);
}
