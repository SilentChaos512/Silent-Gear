package net.silentchaos512.gear.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gnu.trove.map.hash.THashMap;
import jline.internal.InputStreamReader;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigOptionEquipment {

    public static final String[] RECIPE_INGREDIENTS = {"head_count", "rod_count", "bowstring_count"};

    public final ICoreItem item;
    public final String name;
    public boolean enabled = true;

    private Map<ItemStat, StatInstance> modifiers = new THashMap<>();
    private Map<ItemStat, Float> baseModifiers = new THashMap<>();
    private Map<String, Integer> recipe = new HashMap<>();
    @Getter(value = AccessLevel.PUBLIC)
    private float repairMultiplier = 1f;

    public <T extends ICoreItem> ConfigOptionEquipment(T item) {
        this.item = item;
        this.name = item.getItemClassName();
    }

    public ConfigOptionEquipment loadValue(Configuration config) {
        return loadValue(config, Config.CAT_TOOLS + Config.SEP + name);
    }

    @Nonnull
    public StatInstance getBaseModifier(ItemStat stat) {
        float value = this.baseModifiers.containsKey(stat) ? this.baseModifiers.get(stat) : 0;
        return new StatInstance(this.item.getItemClassName() + "_basemod", value, Operation.ADD);
    }

    @Nonnull
    public StatInstance getStatModifier(ItemStat stat) {
        if (this.modifiers.containsKey(stat))
            return this.modifiers.get(stat);
        return StatInstance.ZERO;
    }

    public ConfigOptionEquipment loadValue(Configuration config, String category) {
        enabled = config.get(category, "Enabled", true).getBoolean();
        loadJsonResources();
        return this;
    }

    public int getHeadCount() {
        return this.recipe.getOrDefault("head_count", 0);
    }

    public int getRodCount() {
        return this.recipe.getOrDefault("rod_count", 0);
    }

    public int getBowstringCount() {
        return this.recipe.getOrDefault("bowstring_count", 0);
    }

    private void loadJsonResources() {
        // Main resource file in JAR
        String path = "assets/" + SilentGear.MOD_ID + "/equipment/" + item.getItemClassName() + ".json";
        SilentGear.log.info("Loading equipment asset file: " + path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), "UTF-8"))) {
            readResourceFile(reader);
        } catch (Exception e) {
            SilentGear.log.severe("Error loading resource file! Either Silent screwed up or the JAR has been modified.");
            SilentGear.log.severe("    item: " + item);
            SilentGear.log.severe("    item type: " + item.getItemClassName());
            e.printStackTrace();
        }

        // Override in config folder
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(Config.INSTANCE.directory.getPath(), "equipment/" + item.getItemClassName() + ".json")))) {
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
                    String id = this.item.getItemClassName() + "_mod_" + stat.getUnlocalizedName();
                    this.modifiers.put(stat, new StatInstance(id, value, op));
                }
            }
        }

        JsonElement elementBaseMods = json.get("base_modifiers");
        if (elementBaseMods.isJsonObject()) {
            JsonObject obj = elementBaseMods.getAsJsonObject();
            for (ItemStat stat : ItemStat.ALL_STATS.values()) {
                if (obj.has(stat.getUnlocalizedName())) {
                    float value = obj.get(stat.getUnlocalizedName()).getAsFloat();
                    this.baseModifiers.put(stat, value);
                }
            }
        }

        JsonElement elementProperties = json.get("crafting");
        if (elementProperties.isJsonObject()) {
            JsonObject obj = elementProperties.getAsJsonObject();
            for (String type : RECIPE_INGREDIENTS)
                if (obj.has(type))
                    this.recipe.put(type, obj.get(type).getAsInt());
        }
    }
}
