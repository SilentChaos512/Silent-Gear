package net.silentchaos512.gear.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gnu.trove.map.hash.THashMap;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigOptionEquipment {
    public final ICoreItem item;
    public final String name;

    private boolean canCraft = true;
    private boolean isVisible = true;

    private final Map<ItemStat, StatInstance> modifiers = new THashMap<>();
    private final Map<ItemStat, Float> baseModifiers = new THashMap<>();
    private final Map<String, Integer> recipe = new HashMap<>();

    public <T extends ICoreItem> ConfigOptionEquipment(T item) {
        this.item = item;
        this.name = item.getGearClass();
    }

    public ConfigOptionEquipment loadValue(Configuration config) {
        return loadValue(config, Config.CAT_GEAR + Config.SEP + name);
    }

    public ConfigOptionEquipment loadValue(Configuration config, String category) {
        loadJsonResources();
        return this;
    }

    @Nonnull
    public StatInstance getBaseModifier(ItemStat stat) {
        float value = this.baseModifiers.containsKey(stat) ? this.baseModifiers.get(stat) : 0;
        return new StatInstance(this.item.getGearClass() + "_basemod", value, Operation.ADD);
    }

    @Nonnull
    public StatInstance getStatModifier(ItemStat stat) {
        if (this.modifiers.containsKey(stat))
            return this.modifiers.get(stat);
        return StatInstance.ZERO;
    }

    public Set<PartType> getRequiredPartTypes() {
        return recipe.keySet().stream().map(PartType::get).collect(Collectors.toSet());
    }

    public int getCraftingPartCount(PartType type) {
        return this.recipe.getOrDefault(type.getName(), 0);
    }

    @Deprecated
    public int getHeadCount() {
        return this.recipe.getOrDefault("main", 0);
    }

    @Deprecated
    public int getRodCount() {
        return this.recipe.getOrDefault("rod", 0);
    }

    @Deprecated
    public int getBowstringCount() {
        return this.recipe.getOrDefault("bowstring", 0);
    }

    public boolean canCraft() {
        return this.canCraft;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    private void loadJsonResources() {
        // Main resource file in JAR
        String path = "assets/" + SilentGear.MOD_ID + "/equipment/" + item.getGearClass() + ".json";
        SilentGear.log.info("Loading equipment asset file: " + path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), "UTF-8"))) {
            readResourceFile(reader);
        } catch (Exception e) {
            SilentGear.log.fatal("Error loading resource file! Either Silent screwed up or the JAR has been modified.");
            SilentGear.log.fatal("    item: " + item);
            SilentGear.log.fatal("    item type: " + item.getGearClass());
            e.printStackTrace();
        }

        // Override in config folder
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(Config.INSTANCE.directory.getPath(), "equipment/" + item.getGearClass() + ".json")))) {
            readResourceFile(reader);
        } catch (FileNotFoundException ex) {
            // Ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readResourceFile(BufferedReader reader) {
        JsonElement je = (new GsonBuilder().create()).fromJson(reader, JsonElement.class);
        JsonObject json = je.getAsJsonObject();

        JsonElement elementMods = json.get("modifiers");
        if (elementMods.isJsonArray()) {
            JsonArray array = elementMods.getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.has("name") ? JsonUtils.getString(obj, "name") : "";
                ItemStat stat = ItemStat.ALL_STATS.get(name);
                if (stat != null) {
                    float value = obj.has("value") ? JsonUtils.getFloat(obj, "value") : 0f;
                    Operation op = obj.has("op") ? Operation.byName(JsonUtils.getString(obj, "op")) : Operation.MUL1;
                    String id = this.item.getGearClass() + "_mod_" + stat.getName();
                    this.modifiers.put(stat, new StatInstance(id, value, op));
                }
            }
        }

        JsonElement elementBaseMods = json.get("base_modifiers");
        if (elementBaseMods.isJsonObject()) {
            JsonObject obj = elementBaseMods.getAsJsonObject();
            for (ItemStat stat : ItemStat.ALL_STATS.values()) {
                if (obj.has(stat.getName().getPath())) {
                    float value = obj.get(stat.getName().getPath()).getAsFloat();
                    this.baseModifiers.put(stat, value);
                }
            }
        }

        if (JsonUtils.hasField(json, "crafting")) {
            JsonObject obj = JsonUtils.getJsonObject(json, "crafting");
            for (PartType type : PartType.getValues()) {
                final int amount = getPartCountFromJson(obj, type);
                if (amount > 0)
                    this.recipe.put(type.getName(), amount);
            }
            this.canCraft = JsonUtils.getBoolean(obj, "can_craft", this.canCraft);
            this.isVisible = JsonUtils.getBoolean(obj, "visible", this.isVisible);
        }
    }

    private static int getPartCountFromJson(JsonObject json, PartType type) {
        String[] possibleNames = type == PartType.MAIN
                ? new String[] {type.getName(), type.getName() + "_count", "head_count"}
                : new String[] {type.getName(), type.getName() + "_count"};
        for (String name : possibleNames)
            if (json.has(name))
                return json.get(name).getAsInt();
        return 0;
    }
}
