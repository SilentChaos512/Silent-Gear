package net.silentchaos512.gear.gear.trait;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NBTTrait extends SimpleTrait {
    static final ITraitSerializer<NBTTrait> SERIALIZER = new Serializer<>(
            ApiConst.NBT_TRAIT_ID,
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
        ItemStack gear = context.gear();
        GearType gearType = GearHelper.getType(gear);

        int traitLevel = context.traitLevel();
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

    private static void readBuffer(NBTTrait trait, FriendlyByteBuf buffer) {
        trait.data.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<DataEntry> list = new ArrayList<>();
            String gearType = buffer.readUtf();
            int dataCount = buffer.readByte();

            for (int dataIndex = 0; dataIndex < dataCount; ++dataIndex) {
                list.add(DataEntry.read(buffer));
            }

            trait.data.put(gearType, list);
        }
    }

    private static void writeBuffer(NBTTrait trait, FriendlyByteBuf buffer) {
        buffer.writeByte(trait.data.size());
        trait.data.forEach((type, list) -> {
            buffer.writeUtf(type);
            buffer.writeByte(list.size());
            list.forEach(e -> e.write(buffer));
        });
    }

    public static class DataEntry {
        private int level;
        private CompoundTag data = new CompoundTag();

        static DataEntry from(JsonObject json) {
            DataEntry ret = new DataEntry();
            ret.level = GsonHelper.getAsInt(json, "level", 1);
            try {
                JsonElement element = json.get("data");
                if (element.isJsonObject())
                    ret.data = TagParser.parseTag(GSON.toJson(element));
                else
                    ret.data = TagParser.parseTag(GsonHelper.getAsString(json, "data"));
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            return ret;
        }

        static DataEntry read(FriendlyByteBuf buffer) {
            DataEntry ret = new DataEntry();
            ret.level = buffer.readByte();
            ret.data = buffer.readNbt();
            return ret;
        }

        void write(FriendlyByteBuf buffer) {
            buffer.writeByte(this.level);
            buffer.writeNbt(this.data);
        }
    }
}
