package net.silentchaos512.gear.traits;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;

import java.util.*;

public class AttributeTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("attribute_trait");
    static final ITraitSerializer<AttributeTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            AttributeTrait::new,
            AttributeTrait::readJson,
            AttributeTrait::readBuffer,
            AttributeTrait::writeBuffer
    );

    private final Map<String, List<ModifierData>> modifiers = new HashMap<>();

    public AttributeTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onGetAttributeModifiers(TraitActionContext context, Multimap<String, AttributeModifier> map, EquipmentSlotType slot) {
        int traitLevel = context.getTraitLevel();
        for (Map.Entry<String, List<ModifierData>> entry : this.modifiers.entrySet()) {
            String key = entry.getKey();
            List<ModifierData> mods = entry.getValue();

            if (gearMatchesKey(context.getGear(), key, slot)) {
                mods.forEach(d -> {
                    String modName = String.format("%s_%s_%d_%s_%s", this.getId().getNamespace(), this.getId().getPath(), traitLevel, d.name, key);
                    float modValue = d.values[MathHelper.clamp(traitLevel - 1, 0, d.values.length - 1)];
                    map.put(d.name, new AttributeModifier(d.uuid, modName, modValue, d.operation));
                });
            }
        }
    }

    private static boolean gearMatchesKey(ItemStack gear, String key, EquipmentSlotType slotType) {
        String[] parts = key.split("/");
        if (parts.length != 2) {
            // Invalid key
            return false;
        }

        EquipmentSlotType slot = null;
        for (EquipmentSlotType type : EquipmentSlotType.values()) {
            if (type.getName().equalsIgnoreCase(parts[1])) {
                slot = type;
            }
        }
        if (slot == null) {
            // Invalid slot
            return false;
        }

        if (gear.getItem() instanceof ArmorItem && ((ArmorItem) gear.getItem()).getEquipmentSlot() != slotType) {
            return false;
        }

        GearType gearType = GearHelper.getType(gear);
        return gearType != null && gearType.matches(parts[0]) && slotType == slot;
    }

    private static void readJson(AttributeTrait trait, JsonObject json) {
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

    private static void readBuffer(AttributeTrait trait, PacketBuffer buffer) {
        trait.modifiers.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<ModifierData> list = new ArrayList<>();
            String gearType = buffer.readString();
            int dataCount = buffer.readByte();

            for (int dataIndex = 0; dataIndex < dataCount; ++dataIndex) {
                list.add(ModifierData.read(buffer));
            }

            trait.modifiers.put(gearType, list);
        }
    }

    private static void writeBuffer(AttributeTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.modifiers.size());
        for (Map.Entry<String, List<ModifierData>> entry : trait.modifiers.entrySet()) {
            buffer.writeString(entry.getKey());
            buffer.writeByte(entry.getValue().size());

            for (ModifierData data : entry.getValue()) {
                data.write(buffer);
            }
        }
    }

    public static class ModifierData {
        private String name;
        private float[] values;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;
        private UUID uuid;

        static ModifierData from(JsonObject json) {
            ModifierData ret = new ModifierData();
            ret.uuid = UUID.randomUUID();

            if (!json.has("attribute")) {
                throw new JsonParseException("attribute element not found, should be string");
            }
            ret.name = JSONUtils.getString(json, "attribute");

            JsonElement element = json.get("value");
            if (element.isJsonPrimitive()) {
                ret.values = new float[]{JSONUtils.getFloat(json, "value")};
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                ret.values = new float[array.size()];
                for (int i = 0; i < ret.values.length; ++i) {
                    ret.values[i] = array.get(i).getAsFloat();
                }
            } else {
                throw new JsonParseException("value element not found, should be either float or array");
            }

            ret.operation = AttributeModifier.Operation.byId(JSONUtils.getInt(json, "operation", 0));

            return ret;
        }

        static ModifierData read(PacketBuffer buffer) {
            ModifierData ret = new ModifierData();
            ret.name = buffer.readString();
            ret.values = new float[(int) buffer.readByte()];
            for (int i = 0; i < ret.values.length; ++i) {
                ret.values[i] = buffer.readFloat();
            }
            ret.operation = buffer.readEnumValue(AttributeModifier.Operation.class);
            ret.uuid = buffer.readUniqueId();
            return ret;
        }

        void write(PacketBuffer buffer) {
            buffer.writeString(name);
            buffer.writeByte(values.length);
            for (float f : values) {
                buffer.writeFloat(f);
            }
            buffer.writeEnumValue(operation);
            buffer.writeUniqueId(uuid);
        }
    }
}
