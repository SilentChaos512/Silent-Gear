package net.silentchaos512.gear.traits;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NBTTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("nbt_trait");
    static final ITraitSerializer<NBTTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            NBTTrait::new,
            NBTTrait::readJson,
            NBTTrait::readBuffer,
            NBTTrait::writeBuffer
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<String, List<DataEntry>> data = new HashMap<>();

    private NBTTrait(ResourceLocation id) {
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
        data.forEach((type, list) -> {
            if (gearType.matches(type) || "all".equals(type)) {
                list.stream().filter(e -> e.level == traitLevel).forEach(e -> gear.getOrCreateTag().merge(e.data));
            }
        });
    }

    private static void readJson(NBTTrait trait, JsonObject json) {
        if (!json.has("nbt")) {
            throw new JsonParseException("NBT trait '" + trait.getId() + "' is missing 'nbt' object");
        }

        // Parse potion effects array
        JsonObject jsonNbt = json.getAsJsonObject("nbt");
        for (Map.Entry<String, JsonElement> entry : jsonNbt.entrySet()) {
            // Key (gear type)
            String key = entry.getKey();
            // Array of DataEntry objects
            JsonElement element = entry.getValue();

            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array, found " + element.getClass().getSimpleName());
            }

            JsonArray array = element.getAsJsonArray();
            List<DataEntry> list = new ArrayList<>();
            for (JsonElement elem : array) {
                if (!elem.isJsonObject()) {
                    throw new JsonParseException("Expected object, found " + elem.getClass().getSimpleName());
                }
                list.add(DataEntry.from(elem.getAsJsonObject()));
            }

            if (!list.isEmpty()) {
                trait.data.put(key, list);
            }
        }
    }

    private static void readBuffer(NBTTrait trait, PacketBuffer buffer) {
        trait.data.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<DataEntry> list = new ArrayList<>();
            String gearType = buffer.readString();
            int dataCount = buffer.readByte();

            for (int dataIndex = 0; dataIndex < dataCount; ++dataIndex) {
                list.add(DataEntry.read(buffer));
            }

            trait.data.put(gearType, list);
        }
    }

    private static void writeBuffer(NBTTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.data.size());
        trait.data.forEach((type, list) -> {
            buffer.writeString(type);
            buffer.writeByte(list.size());
            list.forEach(e -> e.write(buffer));
        });
    }

    public static class DataEntry {
        private int level;
        private CompoundNBT data = new CompoundNBT();

        static DataEntry from(JsonObject json) {
            DataEntry ret = new DataEntry();
            ret.level = JSONUtils.getInt(json, "level", 1);
            try {
                JsonElement element = json.get("data");
                if (element.isJsonObject())
                    ret.data = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    ret.data = JsonToNBT.getTagFromJson(JSONUtils.getString(json, "data"));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            return ret;
        }

        static DataEntry read(PacketBuffer buffer) {
            DataEntry ret = new DataEntry();
            ret.level = buffer.readByte();
            ret.data = buffer.readCompoundTag();
            return ret;
        }

        void write(PacketBuffer buffer) {
            buffer.writeByte(this.level);
            buffer.writeCompoundTag(this.data);
        }
    }
}
