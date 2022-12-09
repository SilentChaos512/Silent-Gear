package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.*;

public class WielderEffectTraitBuilder extends TraitBuilder {
    private final Map<GearType, List<PotionData>> potions = new LinkedHashMap<>();

    public WielderEffectTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public WielderEffectTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, ApiConst.WIELDER_EFFECT_TRAIT_ID);
    }

    public WielderEffectTraitBuilder(DataResource<ITrait> trait, int maxLevel, ResourceLocation serializerName) {
        this(trait.getId(), maxLevel, serializerName);
    }

    public WielderEffectTraitBuilder(ResourceLocation traitId, int maxLevel, ResourceLocation serializerName) {
        super(traitId, maxLevel, serializerName);
    }

    @Deprecated
    public WielderEffectTraitBuilder addEffect(GearType gearType, boolean requiresFullSet, MobEffect effect, int... levels) {
        this.potions.computeIfAbsent(gearType, gt -> new ArrayList<>())
                .add(PotionData.of(requiresFullSet, effect, levels));
        return this;
    }

    public WielderEffectTraitBuilder addEffect(GearType gearType, LevelType type, MobEffect effect, int... levels) {
        this.potions.computeIfAbsent(gearType, gt -> new ArrayList<>())
                .add(PotionData.of(type, effect, levels));
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.potions.isEmpty()) {
            throw new IllegalStateException("Potion effect trait '" + this.getTraitId() + "' has no effects");
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

    public static class PotionData {
        private LevelType type;
        private ResourceLocation effectId;
        private int duration;
        private int[] levels;

        @Deprecated
        public static PotionData of(boolean requiresFullSet, MobEffect effect, int... levels) {
            return of(requiresFullSet ? LevelType.FULL_SET_ONLY : LevelType.PIECE_COUNT, effect, levels);
        }

        public static PotionData of(LevelType type, MobEffect effect, int... levels) {
            PotionData ret = new PotionData();
            ret.type = type;
            ret.effectId = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect));
            ret.duration = TimeUtils.ticksFromSeconds(getDefaultDuration(ret.effectId));
            ret.levels = levels.clone();
            return ret;
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", this.type.getName());
            json.addProperty("effect", this.effectId.toString());

            JsonArray levelsArray = new JsonArray();
            Arrays.stream(this.levels).forEach(levelsArray::add);
            json.add("level", levelsArray);
            return json;
        }

        private static float getDefaultDuration(ResourceLocation effectId) {
            // Duration in seconds. The .9 should prevent flickering.
            return new ResourceLocation("night_vision").equals(effectId) ? 15.9f : 1.9f;
        }
    }

    public enum LevelType {
        TRAIT_LEVEL,
        PIECE_COUNT,
        FULL_SET_ONLY;

        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
