package net.silentchaos512.gear.trait;

import com.google.gson.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.lib.ResourceOrigin;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NBTTrait extends Trait {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<String, List<DataEntry>> data = new HashMap<>();

    public NBTTrait(ResourceLocation name, ResourceOrigin origin) {
        super(name, origin);
    }

    @Override
    public void onGearCrafted(@Nullable EntityPlayer player, int level, ItemStack gear) {
        GearType gearType = GearHelper.getType(gear);
        if (gearType == null) {
            SilentGear.LOGGER.error("Unknown gear type for item {}", gear);
            SilentGear.LOGGER.catching(new IllegalArgumentException());
            return;
        }

        data.forEach((type, list) -> {
            if (gearType.matches(type) || "all".equals(type)) {
                list.stream().filter(e -> e.level == level).forEach(e -> gear.getTagCompound().merge(e.data));
            }
        });
    }

    @Override
    protected void processExtraJson(JsonObject json) {
        readJson(this, json);
    }

    private static void readJson(NBTTrait trait, JsonObject json) {
        if (!json.has("nbt")) {
            throw new JsonParseException("NBT trait '" + trait.getName() + "' is missing 'nbt' object");
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

    public static class DataEntry {
        private int level;
        private NBTTagCompound data = new NBTTagCompound();

        static DataEntry from(JsonObject json) {
            DataEntry ret = new DataEntry();
            ret.level = JsonUtils.getInt(json, "level", 1);
            try {
                JsonElement element = json.get("data");
                if (element.isJsonObject())
                    ret.data = JsonToNBT.getTagFromJson(GSON.toJson(element));
                else
                    ret.data = JsonToNBT.getTagFromJson(JsonUtils.getString(json, "data"));
            } catch (NBTException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }
}
