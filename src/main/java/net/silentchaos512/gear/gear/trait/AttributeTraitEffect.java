package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;

public class AttributeTraitEffect extends TraitEffect {
    public static final ITraitSerializer<AttributeTraitEffect> SERIALIZER = new Serializer<>(
            ApiConst.ATTRIBUTE_TRAIT_ID,
            AttributeTraitEffect::new,
            AttributeTraitEffect::readJson,
            AttributeTraitEffect::readBuffer,
            AttributeTraitEffect::writeBuffer
    );

    private final Map<String, List<ModifierData>> modifiers = new HashMap<>();

    public AttributeTraitEffect(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onGetAttributeModifiers(TraitActionContext context, Multimap<Attribute, AttributeModifier> map, String slot) {
        int traitLevel = context.traitLevel();
        for (Map.Entry<String, List<ModifierData>> entry : this.modifiers.entrySet()) {
            String key = entry.getKey();
            List<ModifierData> mods = entry.getValue();

            if (gearMatchesKey(context.gear(), key, slot)) {
                for (ModifierData mod : mods) {
                    Attribute attribute = mod.getAttribute();
                    if (attribute != null) {
                        String modName = String.format("%s_%s_%d_%s_%s", this.getId().getNamespace(), this.getId().getPath(), traitLevel, mod.name, key);
                        float modValue = mod.values[Mth.clamp(traitLevel - 1, 0, mod.values.length - 1)];
                        map.put(attribute, new AttributeModifier(mod.getUuid(slot), modName, modValue, mod.operation));
                    }
                }
            }
        }
    }

    private static boolean gearMatchesKey(ItemStack gear, String key, String slotType) {
        String[] parts = key.split("/");
        if (parts.length > 2) {
            // Invalid key
            return false;
        }

        GearType gearType = GearHelper.getType(gear);
        return gearType.matches(parts[0])
                && (parts.length < 2 || slotType.equals(parts[1]))
                && GearHelper.isValidSlot(gear, slotType);
    }

    private static void readJson(AttributeTraitEffect trait, JsonObject json) {
        if (!json.has("attribute_modifiers")) {
            throw new JsonParseException("Attribute trait '" + trait.getId() + "' is missing 'attribute_modifiers' object");
        }

        JsonObject jsonModifiers = json.getAsJsonObject("attribute_modifiers");
        for (Map.Entry<String, JsonElement> entry : jsonModifiers.entrySet()) {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array, found " + element.getClass().getSimpleName());
            }

            JsonArray array = element.getAsJsonArray();
            List<ModifierData> list = new ArrayList<>();
            for (JsonElement elem : array) {
                if (!elem.isJsonObject()) {
                    throw new JsonParseException("Expected object, found " + elem.getClass().getSimpleName());
                }
                list.add(ModifierData.from(elem.getAsJsonObject()));
            }

            if (!list.isEmpty()) {
                trait.modifiers.put(key, list);
            }
        }
    }

    private static void readBuffer(AttributeTraitEffect trait, FriendlyByteBuf buffer) {
        trait.modifiers.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<ModifierData> list = new ArrayList<>();
            String gearType = buffer.readUtf();
            int dataCount = buffer.readByte();

            for (int dataIndex = 0; dataIndex < dataCount; ++dataIndex) {
                list.add(ModifierData.read(buffer));
            }

            trait.modifiers.put(gearType, list);
        }
    }

    private static void writeBuffer(AttributeTraitEffect trait, FriendlyByteBuf buffer) {
        buffer.writeByte(trait.modifiers.size());
        for (Map.Entry<String, List<ModifierData>> entry : trait.modifiers.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeByte(entry.getValue().size());

            for (ModifierData data : entry.getValue()) {
                data.write(buffer);
            }
        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        this.modifiers.forEach((type, list) -> {
            ret.add("  - " + type);
            list.forEach(mod -> {
                ret.add("    - " + mod.getWikiLine());
            });
        });
        return ret;
    }

    public static class ModifierData {
        private ResourceLocation name;
        private float[] values;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_VALUE;
        private final Map<String, UUID> uuidMap = new HashMap<>();

        @SuppressWarnings("TypeMayBeWeakened")
        public static ModifierData of(Attribute attribute, AttributeModifier.Operation operation, float... values) {
            ModifierData ret = new ModifierData();
            ret.name = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
            ret.operation = operation;
            ret.values = values.clone();
            return ret;
        }

        public UUID getUuid(String slot) {
            return uuidMap.computeIfAbsent(slot, slot1 -> new UUID(this.name.hashCode(), slot1.hashCode()));
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();

            json.addProperty("attribute", name.toString());
            json.addProperty("operation", operation.toValue());

            JsonArray array = new JsonArray();
            for (float f : this.values) {
                array.add(f);
            }
            json.add("value", array);

            return json;
        }

        static ModifierData from(JsonObject json) {
            ModifierData ret = new ModifierData();

            if (!json.has("attribute")) {
                throw new JsonParseException("attribute element not found, should be string");
            }
            ret.name = new ResourceLocation(GsonHelper.getAsString(json, "attribute"));

            JsonElement element = json.get("value");
            if (element.isJsonPrimitive()) {
                ret.values = new float[]{GsonHelper.getAsFloat(json, "value")};
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                ret.values = new float[array.size()];
                for (int i = 0; i < ret.values.length; ++i) {
                    ret.values[i] = array.get(i).getAsFloat();
                }
            } else {
                throw new JsonParseException("value element not found, should be either float or array");
            }

            ret.operation = AttributeModifier.Operation.fromValue(GsonHelper.getAsInt(json, "operation", 0));

            return ret;
        }

        static ModifierData read(FriendlyByteBuf buffer) {
            ModifierData ret = new ModifierData();
            ret.name = buffer.readResourceLocation();
            ret.values = new float[(int) buffer.readByte()];
            for (int i = 0; i < ret.values.length; ++i) {
                ret.values[i] = buffer.readFloat();
            }
            ret.operation = buffer.readEnum(AttributeModifier.Operation.class);
            return ret;
        }

        void write(FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(name);
            buffer.writeByte(values.length);
            for (float f : values) {
                buffer.writeFloat(f);
            }
            buffer.writeEnum(operation);
        }

        @Nullable
        public Attribute getAttribute() {
            return BuiltInRegistries.ATTRIBUTE.get(this.name);
        }

        private String getWikiLine() {
            String[] valueText = new String[values.length];
            for (int i = 0; i < values.length; ++i) {
                valueText[i] = Float.toString(values[i]);
            }
            return name + ": " + operation.name() + " [" + String.join(", ", valueText) + "]";
        }
    }
}
