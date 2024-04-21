package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.*;

public class TargetEffectTraitBuilder extends TraitBuilder {
    private final Map<GearType, Map<Integer, List<MobEffectInstance>>> potions = new LinkedHashMap<>();

    public TargetEffectTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public TargetEffectTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, ApiConst.TARGET_EFFECT_TRAIT_ID);
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
            throw new IllegalStateException("Target effect trait '" + this.getTraitId() + "' has no effects");
        }

        JsonObject json = super.serialize();

        JsonObject effectsJson = new JsonObject();
        this.potions.forEach((gearType, map) -> {
            EffectMap effectMap = new EffectMap(map);
            effectsJson.add(gearType.getName(), effectMap.serialize());
        });
        json.add("effects", effectsJson);

        return json;
    }

    public static class EffectMap {
        private final Map<Integer, List<MobEffectInstance>> effects = new LinkedHashMap<>();

        public EffectMap(Map<Integer, List<MobEffectInstance>> effects) {
            this.effects.putAll(effects);
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();

            for (Map.Entry<Integer, List<MobEffectInstance>> entry : this.effects.entrySet()) {
                int level = entry.getKey();
                List<MobEffectInstance> list = entry.getValue();

                JsonArray array = new JsonArray();

                for (MobEffectInstance inst : list) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("effect", Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(inst.getEffect())).toString());
                    obj.addProperty("amplifier", inst.getAmplifier());
                    obj.addProperty("duration", inst.getDuration() / 20f);
                    array.add(obj);
                }

                json.add(String.valueOf(level), array);
            }

            return json;
        }
    }
}
