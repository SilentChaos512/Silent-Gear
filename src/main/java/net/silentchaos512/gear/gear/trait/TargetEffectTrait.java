package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.NameUtils;

import java.util.*;

public class TargetEffectTrait extends SimpleTrait {
    public static final ITraitSerializer<TargetEffectTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("target_effect"),
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
        GearType type = GearHelper.getType(context.getGear());
        for (String typeStr : this.effects.keySet()) {
            if (type.matches(typeStr)) {
                this.effects.get(typeStr).applyTo(target, context.getTraitLevel());
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

    private static void read(TargetEffectTrait trait, PacketBuffer buffer) {
        trait.effects.clear();
        int mapSize = buffer.readByte();
        for (int i = 0; i < mapSize; ++i) {
            String key = buffer.readString();
            EffectMap list = EffectMap.read(buffer);
            trait.effects.put(key, list);
        }
    }

    private static void write(TargetEffectTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.effects.size());
        for (Map.Entry<String, EffectMap> entry : trait.effects.entrySet()) {
            buffer.writeString(entry.getKey());
            entry.getValue().write(buffer);
        }
    }

    public static class EffectMap {
        private final Map<Integer, List<EffectInstance>> effects = new LinkedHashMap<>();

        public EffectMap(Map<Integer, List<EffectInstance>> effects) {
            this.effects.putAll(effects);
        }

        public void applyTo(LivingEntity target, int traitLevel) {
            if (this.effects.containsKey(traitLevel)) {
                for (EffectInstance effect : this.effects.get(traitLevel)) {
                    EffectInstance copy = new EffectInstance(effect);
                    target.addPotionEffect(copy);
                }
            }
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();

            for (Map.Entry<Integer, List<EffectInstance>> entry : this.effects.entrySet()) {
                int level = entry.getKey();
                List<EffectInstance> list = entry.getValue();

                JsonArray array = new JsonArray();

                for (EffectInstance inst : list) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("effect", NameUtils.from(inst.getPotion()).toString());
                    obj.addProperty("amplifier", inst.getAmplifier());
                    obj.addProperty("duration", inst.getDuration() / 20f);
                    array.add(obj);
                }

                json.add(String.valueOf(level), array);
            }

            return json;
        }

        static EffectMap deserialize(JsonObject json) {
            Map<Integer, List<EffectInstance>> map = new LinkedHashMap<>();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                int level = Integer.parseInt(entry.getKey());
                JsonElement element = entry.getValue();

                if (element.isJsonArray()) {
                    List<EffectInstance> list = new ArrayList<>();
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

        private static EffectInstance deserialize(JsonElement jsonElement) {
            if (!jsonElement.isJsonObject()) {
                throw new JsonParseException("Expected effect instance element to be an object");
            }

            JsonObject json = jsonElement.getAsJsonObject();
            ResourceLocation effectId = new ResourceLocation(JSONUtils.getString(json, "effect"));
            Effect effect = ForgeRegistries.POTIONS.getValue(effectId);
            if (effect == null) {
                throw new JsonParseException("Unknown effect ID: " + effectId);
            }
            int level = JSONUtils.getInt(json, "amplifier", 0);
            float duration = JSONUtils.getFloat(json, "duration", 5f);

            return new EffectInstance(effect, (int) duration * 20, level);
        }

        public static EffectMap read(PacketBuffer buffer) {
            int mapSize = buffer.readByte();
            Map<Integer, List<EffectInstance>> result = new LinkedHashMap<>();

            for (int i = 0; i < mapSize; ++i) {
                int level = buffer.readByte();
                int listSize = buffer.readByte();
                List<EffectInstance> list = new ArrayList<>(listSize);

                for (int j = 0; j < listSize; ++j) {
                    ResourceLocation effectId = buffer.readResourceLocation();
                    Effect effect = ForgeRegistries.POTIONS.getValue(effectId);
                    if (effect == null) {
                        throw new JsonParseException("Unknown effect ID: " + effectId);
                    }
                    int amplifier = buffer.readByte();
                    int duration = buffer.readVarInt();
                    list.add(new EffectInstance(effect, duration, amplifier));
                }

                result.put(level, list);
            }

            return new EffectMap(result);
        }

        public void write(PacketBuffer buffer) {
            buffer.writeByte(this.effects.size());

            for (Map.Entry<Integer, List<EffectInstance>> entry : this.effects.entrySet()) {
                buffer.writeByte(entry.getKey());
                buffer.writeByte(entry.getValue().size());

                for (EffectInstance effect : entry.getValue()) {
                    buffer.writeResourceLocation(NameUtils.from(effect.getPotion()));
                    buffer.writeByte(effect.getAmplifier());
                    buffer.writeVarInt(effect.getDuration());
                }
            }
        }
    }
}
