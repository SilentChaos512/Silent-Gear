package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonObject;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.TargetEffectTrait;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TargetEffectTraitBuilder extends TraitBuilder {
    private final Map<GearType, Map<Integer, List<MobEffectInstance>>> potions = new LinkedHashMap<>();

    public TargetEffectTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public TargetEffectTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, TargetEffectTrait.SERIALIZER);
    }

    public TargetEffectTraitBuilder addEffect(GearType gearType, int traitLevel, MobEffect effect, int amplifier, float durationInSeconds) {
        this.potions
                .computeIfAbsent(gearType, t -> new LinkedHashMap<>())
                .computeIfAbsent(traitLevel, l -> new ArrayList<>())
                .add(new MobEffectInstance(effect, TimeUtils.ticksFromSeconds(durationInSeconds), amplifier));
        return this;
    }

    public TargetEffectTraitBuilder withDurationByLevel(GearType gearType, MobEffect effect, int amplifier, float baseDurationInSeconds) {
        for (int i = 1; i <= this.maxLevel; ++i) {
            this.addEffect(gearType, i, effect, amplifier, i * baseDurationInSeconds);
        }
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.potions.isEmpty()) {
            throw new IllegalStateException("Target effect trait '" + this.traitId + "' has no effects");
        }

        JsonObject json = super.serialize();

        JsonObject effectsJson = new JsonObject();
        this.potions.forEach((gearType, map) -> {
            TargetEffectTrait.EffectMap effectMap = new TargetEffectTrait.EffectMap(map);
            effectsJson.add(gearType.getName(), effectMap.serialize());
        });
        json.add("effects", effectsJson);

        return json;
    }
}
