package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.gear.trait.WielderEffectTrait;
import net.silentchaos512.gear.util.DataResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PotionTraitBuilder extends TraitBuilder {
    private final Map<GearType, List<WielderEffectTrait.PotionData>> potions = new LinkedHashMap<>();

    public PotionTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public PotionTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, WielderEffectTrait.SERIALIZER);
    }

    public PotionTraitBuilder(DataResource<ITrait> trait, int maxLevel, ITraitSerializer<? extends WielderEffectTrait> serializer) {
        this(trait.getId(), maxLevel, serializer);
    }

    public PotionTraitBuilder(ResourceLocation traitId, int maxLevel, ITraitSerializer<? extends WielderEffectTrait> serializer) {
        super(traitId, maxLevel, serializer);
    }

    @Deprecated
    public PotionTraitBuilder addEffect(GearType gearType, boolean requiresFullSet, MobEffect effect, int... levels) {
        this.potions.computeIfAbsent(gearType, gt -> new ArrayList<>())
                .add(WielderEffectTrait.PotionData.of(requiresFullSet, effect, levels));
        return this;
    }

    public PotionTraitBuilder addEffect(GearType gearType, WielderEffectTrait.LevelType type, MobEffect effect, int... levels) {
        this.potions.computeIfAbsent(gearType, gt -> new ArrayList<>())
                .add(WielderEffectTrait.PotionData.of(type, effect, levels));
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.potions.isEmpty()) {
            throw new IllegalStateException("Potion effect trait '" + this.traitId + "' has no effects");
        }

        JsonObject json = super.serialize();

        JsonObject effectsJson = new JsonObject();
        this.potions.forEach(((gearType, effects) -> {
            JsonArray array = new JsonArray();
            effects.forEach(e -> array.add(e.serialize()));
            effectsJson.add(gearType.getName(), array);
        }));
        json.add("potion_effects", effectsJson);

        return json;
    }
}
