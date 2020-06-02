package net.silentchaos512.gear.gear.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.api.material.MaterialDisplay;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.parts.PartTextureType;

import javax.annotation.Nullable;
import java.util.*;

public class PartMaterial implements IPartMaterial {
    private final ResourceLocation materialId;
    private Ingredient ingredient = Ingredient.EMPTY;
    boolean visible = true;
    int tier = -1;

    Map<PartType, StatModifierMap> stats = new HashMap<>();
    Map<PartType, List<PartTraitInstance>> traits = new HashMap<>();

    ITextComponent displayName;
    @Nullable ITextComponent namePrefix = null;
    // Keys are part_type/gear_type
    final Map<String, MaterialDisplay> display = new HashMap<>();

    public PartMaterial(ResourceLocation id) {
        this.materialId = id;
    }

    @Override
    public ResourceLocation getId() {
        return this.materialId;
    }

    @Override
    public int getTier(PartType partType) {
        return this.tier;
    }

    @Override
    public Ingredient getIngredient(PartType partType) {
        return this.ingredient;
    }

    @Override
    public boolean allowedInPart(PartType partType) {
        return stats.containsKey(partType);
    }

    @Override
    public void retainData(@Nullable IPartMaterial oldMaterial) {
        if (oldMaterial instanceof PartMaterial) {
            // Copy trait instances, the client doesn't need to know conditions
            this.traits.clear();
            ((PartMaterial) oldMaterial).traits.forEach((partType, list) -> this.traits.put(partType, list));
        }
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat, PartType partType) {
        return stats.get(partType).get(stat);
    }

    @Override
    public Collection<PartTraitInstance> getTraits(ItemStack gear, PartType partType) {
        return Collections.unmodifiableList(traits.getOrDefault(partType, Collections.emptyList()));
    }

    @Override
    public int getColor(ItemStack gear, PartType partType) {
        return getMaterialDisplay(gear, partType).getColor();
    }

    @Override
    public PartTextureType getTexture(ItemStack gear, PartType partType) {
        return getMaterialDisplay(gear, partType).getTexture();
    }

    private MaterialDisplay getMaterialDisplay(ItemStack gear, PartType partType) {
        if (!gear.isEmpty()) {
            GearType gearType = ((ICoreItem) gear.getItem()).getGearType();

            // Gear class-specific override
            String gearTypeKey = partType + "/" + gearType.getName();
            if (display.containsKey(gearTypeKey)) {
                return display.get(gearTypeKey);
            }
            // Parent type overrides, like "armor"
            for (String key : display.keySet()) {
                if (gearType.matches(key, false)) {
                    return display.get(key);
                }
            }
        }
        return display.getOrDefault(partType + "/all", MaterialDisplay.DEFAULT);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack gear, PartType partType) {
        return displayName;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public String toString() {
        return "PartMaterial{" +
                "id=" + materialId +
                ", tier=" + tier +
                ", ingredient=" + ingredient +
                '}';
    }

    public static final class Serializer {
        private Serializer() {throw new IllegalAccessError("Utility class");}

        public static PartMaterial deserialize(ResourceLocation id, JsonObject json) {
            PartMaterial ret = new PartMaterial(id);

            // Stats
            readStats(json, ret);

            // Traits
            JsonElement elementTraits = json.get("traits");
            if (elementTraits != null && elementTraits.isJsonArray()) {
                JsonArray array = elementTraits.getAsJsonArray();
                Collection<PartTraitInstance> traitsList = new ArrayList<>();

                for (JsonElement element : array) {
                    traitsList.add(PartTraitInstance.deserialize(element.getAsJsonObject()));
                }

                if (!traitsList.isEmpty()) {
//                    ret.traits.addAll(traitsList);
                }
            }

            // Crafting Items
            JsonElement craftingItems = json.get("crafting_items");
            if (craftingItems != null && craftingItems.isJsonObject()) {
                JsonElement main = craftingItems.getAsJsonObject().get("main");
                if (main != null) {
                    ret.ingredient = Ingredient.deserialize(main);
                }
                // TODO: Rod substitutes?
            } else {
                throw new JsonSyntaxException("Expected 'crafting_items' to be an object");
            }

            // Name
            JsonElement elementName = json.get("name");
            if (elementName != null && elementName.isJsonObject()) {
                ret.displayName = deserializeText(elementName);
            } else {
                throw new JsonSyntaxException("Expected 'name' element");
            }

            // Name Prefix
            JsonElement elementNamePrefix = json.get("name_prefix");
            if (elementNamePrefix != null) {
                ret.namePrefix = deserializeText(elementNamePrefix);
            }

            // Display Properties
            JsonElement elementDisplay = json.get("display");
            if (elementDisplay != null && elementDisplay.isJsonObject()) {
                JsonObject obj = elementDisplay.getAsJsonObject();
                MaterialDisplay defaultProps = ret.display.getOrDefault("all", MaterialDisplay.DEFAULT);

                if (!ret.display.containsKey("all")) {
                    ret.display.put("all", defaultProps);
                }

                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();

                    if (value.isJsonObject()) {
                        JsonObject jsonObject = value.getAsJsonObject();
                        ret.display.put(key, MaterialDisplay.deserialize(jsonObject, defaultProps));
                    }
                }
            } else {
                throw new JsonSyntaxException("Expected 'display' to be an object");
            }

            // Availability
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();
                ret.tier = JSONUtils.getInt(obj, "tier", ret.tier);
                ret.visible = JSONUtils.getBoolean(obj, "visible", ret.visible);

                // FIXME
//                JsonArray blacklist = getGearBlacklist(obj);
//                if (blacklist != null) {
//                    ret.blacklistedGearTypes.clear();
//                    blacklist.forEach(e -> ret.blacklistedGearTypes.add(e.getAsString()));
//                }
            } else {
                throw new JsonSyntaxException("Expected 'availability' to be an object");
            }

            return ret;
        }

        public static void readStats(JsonObject json, PartMaterial ret) {
            JsonElement elementStats = json.get("stats");
            if (elementStats != null && elementStats.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : elementStats.getAsJsonObject().entrySet()) {
                    ResourceLocation partTypeName = SilentGear.getIdWithDefaultNamespace(entry.getKey());
                    if (partTypeName != null) {
                        PartType partType = PartType.get(partTypeName);
                        StatModifierMap statMods = StatModifierMap.read(entry.getValue());
                        ret.stats.put(partType, statMods);
                    }
                }
            }
        }

        private static ITextComponent deserializeText(JsonElement json) {
            // Handle the old style
            if (json.isJsonObject() && json.getAsJsonObject().has("name")) {
                boolean translate = JSONUtils.getBoolean(json.getAsJsonObject(), "translate", false);
                String name = JSONUtils.getString(json.getAsJsonObject(), "name");
                return translate ? new TranslationTextComponent(name) : new StringTextComponent(name);
            }

            // Deserialize use vanilla serializer
            return Objects.requireNonNull(ITextComponent.Serializer.fromJson(json));
        }

        @Nullable
        private static JsonArray getGearBlacklist(JsonObject json) {
            if (json.has("gear_blacklist"))
                return JSONUtils.getJsonArray(json, "gear_blacklist");
            else if (json.has("tool_blacklist"))
                return JSONUtils.getJsonArray(json, "tool_blacklist");
            return null;
        }
    }
}
