package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;

import java.util.*;

public class TargetEffectTrait extends SimpleTrait {
    public static final ITraitSerializer<TargetEffectTrait> SERIALIZER = new Serializer<>(
            ApiConst.TARGET_EFFECT_TRAIT_ID,
            TargetEffectTrait::new,
            TargetEffectTrait::deserialize,
            TargetEffectTrait::read,
            TargetEffectTrait::write
    );

    private final Map<String, EffectMap> effects = new LinkedHashMap<>();

    public TargetEffectTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        GearType type = GearHelper.getType(context.gear());
        for (String typeStr : this.effects.keySet()) {
            if (type.matches(typeStr)) {
                this.effects.get(typeStr).applyTo(target, context.traitLevel());
            }
        }
        return super.onAttackEntity(context, target, baseValue);
    }

    private static void deserialize(TargetEffectTrait trait, JsonObject json) {
        if (!json.has("effects")) {
            throw new JsonParseException("Target effect trait '" + trait.getId() + "' is missing 'effects' object");
        }

        // Parse effects map
        JsonObject jsonEffects = json.getAsJsonObject("effects");
        for (Map.Entry<String, JsonElement> entry : jsonEffects.entrySet()) {
            String gearTypeKey = entry.getKey();
            JsonElement element = entry.getValue();

            if (!element.isJsonObject()) {
                throw new JsonParseException("Expected object, found " + element.getClass().getSimpleName());
            }

            EffectMap list = EffectMap.deserialize(element.getAsJsonObject());
            trait.effects.put(gearTypeKey, list);
        }
    }

    private static void read(TargetEffectTrait trait, FriendlyByteBuf buffer) {
        trait.effects.clear();
        int mapSize = buffer.readByte();
        for (int i = 0; i < mapSize; ++i) {
            String key = buffer.readUtf();
            EffectMap list = EffectMap.read(buffer);
            trait.effects.put(key, list);
        }
    }

    private static void write(TargetEffectTrait trait, FriendlyByteBuf buffer) {
        buffer.writeByte(trait.effects.size());
        for (Map.Entry<String, EffectMap> entry : trait.effects.entrySet()) {
            buffer.writeUtf(entry.getKey());
            entry.getValue().write(buffer);
        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        this.effects.forEach((type, map) -> {
            ret.add("  - " + type);
            ret.addAll(map.getWikiLines());
        });
        return ret;
    }

    public static class EffectMap {
        private final Map<Integer, List<MobEffectInstance>> effects = new LinkedHashMap<>();

        public EffectMap(Map<Integer, List<MobEffectInstance>> effects) {
            this.effects.putAll(effects);
        }

        public void applyTo(LivingEntity target, int traitLevel) {
            if (this.effects.containsKey(traitLevel)) {
                for (MobEffectInstance effect : this.effects.get(traitLevel)) {
                    MobEffectInstance copy = new MobEffectInstance(effect);
                    target.addEffect(copy);
                }
            }
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();

            for (Map.Entry<Integer, List<MobEffectInstance>> entry : this.effects.entrySet()) {
                int level = entry.getKey();
                List<MobEffectInstance> list = entry.getValue();

                JsonArray array = new JsonArray();

                for (MobEffectInstance inst : list) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("effect", Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(inst.getEffect())).toString());
                    obj.addProperty("amplifier", inst.getAmplifier());
                    obj.addProperty("duration", inst.getDuration() / 20f);
                    array.add(obj);
                }

                json.add(String.valueOf(level), array);
            }

            return json;
        }

        static EffectMap deserialize(JsonObject json) {
            Map<Integer, List<MobEffectInstance>> map = new LinkedHashMap<>();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                int level = Integer.parseInt(entry.getKey());
                JsonElement element = entry.getValue();

                if (element.isJsonArray()) {
                    List<MobEffectInstance> list = new ArrayList<>();
                    for (JsonElement effectJson : element.getAsJsonArray()) {
                        list.add(deserialize(effectJson));
                    }
                    map.put(level, list);
                } else if (element.isJsonObject()) {
                    map.put(level, Collections.singletonList(deserialize(element)));
                } else {
                    throw new JsonParseException("Expected effects for level element to be either array or object");
                }
            }

            return new EffectMap(map);
        }

        private static MobEffectInstance deserialize(JsonElement jsonElement) {
            if (!jsonElement.isJsonObject()) {
                throw new JsonParseException("Expected effect instance element to be an object");
            }

            JsonObject json = jsonElement.getAsJsonObject();
            ResourceLocation effectId = new ResourceLocation(GsonHelper.getAsString(json, "effect"));
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
            if (effect == null) {
                throw new JsonParseException("Unknown effect ID: " + effectId);
            }
            int level = GsonHelper.getAsInt(json, "amplifier", 0);
            float duration = GsonHelper.getAsFloat(json, "duration", 5f);

            return new MobEffectInstance(effect, (int) duration * 20, level);
        }

        public static EffectMap read(FriendlyByteBuf buffer) {
            int mapSize = buffer.readByte();
            Map<Integer, List<MobEffectInstance>> result = new LinkedHashMap<>();

            for (int i = 0; i < mapSize; ++i) {
                int level = buffer.readByte();
                int listSize = buffer.readByte();
                List<MobEffectInstance> list = new ArrayList<>(listSize);

                for (int j = 0; j < listSize; ++j) {
                    ResourceLocation effectId = buffer.readResourceLocation();
                    MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
                    if (effect == null) {
                        throw new JsonParseException("Unknown effect ID: " + effectId);
                    }
                    int amplifier = buffer.readByte();
                    int duration = buffer.readVarInt();
                    list.add(new MobEffectInstance(effect, duration, amplifier));
                }

                result.put(level, list);
            }

            return new EffectMap(result);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(this.effects.size());

            for (Map.Entry<Integer, List<MobEffectInstance>> entry : this.effects.entrySet()) {
                buffer.writeByte(entry.getKey());
                buffer.writeByte(entry.getValue().size());

                for (MobEffectInstance effect : entry.getValue()) {
                    buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect())));
                    buffer.writeByte(effect.getAmplifier());
                    buffer.writeVarInt(effect.getDuration());
                }
            }
        }

        public Collection<String> getWikiLines() {
            Collection<String> ret = new ArrayList<>();
            effects.forEach((level, list) -> {
                ret.add("    - Level " + level + ":");
                list.forEach(effect -> {
                    ret.add("      - " + effect);
                });
            });
            return ret;
        }
    }
}
