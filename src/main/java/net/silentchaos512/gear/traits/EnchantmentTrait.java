package net.silentchaos512.gear.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnchantmentTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("enchantment_trait");
    static final ITraitSerializer<EnchantmentTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            EnchantmentTrait::new,
            EnchantmentTrait::readJson,
            EnchantmentTrait::readBuffer,
            EnchantmentTrait::writeBuffer
    );

    private final Map<String, List<EnchantmentData>> enchantments = new HashMap<>();

    private EnchantmentTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onGearCrafted(TraitActionContext context) {
        ItemStack gear = context.getGear();
        GearType gearType = GearHelper.getType(gear);
        if (gearType == null) {
            SilentGear.LOGGER.error("Unknown gear type for item {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }

        int traitLevel = context.getTraitLevel();
        enchantments.forEach((type, list) -> {
            if (gearType.matches(type) || "all".equals(type)) {
                addEnchantments(gear, traitLevel, list);
            }
        });
    }

    private static void addEnchantments(ItemStack gear, int traitLevel, Iterable<EnchantmentData> list) {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(gear);

        for (EnchantmentData data : list) {
            Enchantment enchantment = data.getEnchantment();
            if (enchantment != null) {
                boolean compatible = true;
                for (Enchantment current : enchants.keySet()) {
                    if (!current.isCompatibleWith(enchantment)) {
                        compatible = false;
                        break;
                    }
                }

                if (compatible) {
                    enchants.put(enchantment, data.getLevel(traitLevel));
                }
            }
        }

        EnchantmentHelper.setEnchantments(enchants, gear);
    }

    private static void readJson(EnchantmentTrait trait, JsonObject json) {
        if (!json.has("enchantments")) {
            throw new JsonParseException("Enchantment trait '" + trait.getId() + "' is missing 'enchantments' object");
        }

        // Parse potion effects array
        JsonObject jsonEffects = json.getAsJsonObject("enchantments");
        for (Map.Entry<String, JsonElement> entry : jsonEffects.entrySet()) {
            // Key (gear type)
            String key = entry.getKey();
            // Array of EnchantmentData objects
            JsonElement element = entry.getValue();

            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array, found " + element.getClass().getSimpleName());
            }

            JsonArray array = element.getAsJsonArray();
            List<EnchantmentData> list = new ArrayList<>();
            for (JsonElement elem : array) {
                if (!elem.isJsonObject()) {
                    throw new JsonParseException("Expected object, found " + elem.getClass().getSimpleName());
                }
                list.add(EnchantmentData.from(elem.getAsJsonObject()));
            }

            if (!list.isEmpty()) {
                trait.enchantments.put(key, list);
            }
        }
    }

    private static void readBuffer(EnchantmentTrait trait, PacketBuffer buffer) {
        trait.enchantments.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<EnchantmentData> list = new ArrayList<>();
            String gearType = buffer.readString();
            int dataCount = buffer.readByte();

            for (int dataIndex = 0; dataIndex < dataCount; ++dataIndex) {
                list.add(EnchantmentData.read(buffer));
            }

            trait.enchantments.put(gearType, list);
        }
    }

    private static void writeBuffer(EnchantmentTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.enchantments.size());
        for (Map.Entry<String, List<EnchantmentData>> entry : trait.enchantments.entrySet()) {
            buffer.writeString(entry.getKey());
            buffer.writeByte(entry.getValue().size());

            for (EnchantmentData data : entry.getValue()) {
                data.write(buffer);
            }
        }
    }

    public static class EnchantmentData {
        private ResourceLocation enchantmentId;
        private int[] levels;

        static EnchantmentData from(JsonObject json) {
            EnchantmentData ret = new EnchantmentData();
            // Enchantment ID, get actual enchantment only when needed
            ret.enchantmentId = new ResourceLocation(JSONUtils.getString(json, "enchantment", "unknown"));

            // Level int or array
            JsonElement elementLevel = json.get("level");
            if (elementLevel == null) {
                throw new JsonParseException("level element not found, should be either int or array");
            }
            if (elementLevel.isJsonPrimitive()) {
                // Single level
                ret.levels = new int[]{JSONUtils.getInt(json, "level", 1)};
            } else if (elementLevel.isJsonArray()) {
                // Levels
                JsonArray array = elementLevel.getAsJsonArray();
                ret.levels = new int[array.size()];
                for (int i = 0; i < ret.levels.length; ++i) {
                    ret.levels[i] = array.get(i).getAsInt();
                }
            } else {
                throw new JsonParseException("Expected level to be int or array, was " + elementLevel.getClass().getSimpleName());
            }

            return ret;
        }

        static EnchantmentData read(PacketBuffer buffer) {
            EnchantmentData ret = new EnchantmentData();
            ret.enchantmentId = buffer.readResourceLocation();
            ret.levels = buffer.readVarIntArray();
            return ret;
        }

        void write(PacketBuffer buffer) {
            buffer.writeResourceLocation(enchantmentId);
            buffer.writeVarIntArray(levels);
        }

        @Nullable
        Enchantment getEnchantment() {
            return ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
        }

        int getLevel(int traitLevel) {
            int index = MathHelper.clamp(traitLevel, 1, levels.length) - 1;
            return levels[index];
        }
    }
}
