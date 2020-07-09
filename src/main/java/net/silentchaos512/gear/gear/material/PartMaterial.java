package net.silentchaos512.gear.gear.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialSerializer;
import net.silentchaos512.gear.api.material.MaterialDisplay;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.parts.PartTextureType;

import javax.annotation.Nullable;
import java.util.*;

public final class PartMaterial implements IMaterial {
    private static final StatModifierMap EMPTY_STAT_MAP = new StatModifierMap();

    private final ResourceLocation materialId;
    @Nullable private ResourceLocation parent;
    private final String packName;
    private Ingredient ingredient = Ingredient.EMPTY;
    private boolean visible = true;
    private int tier = -1;

    private Map<PartType, StatModifierMap> stats = new HashMap<>();
    private Map<PartType, List<PartTraitInstance>> traits = new HashMap<>();

    private ITextComponent displayName;
    @Nullable private ITextComponent namePrefix = null;
    // Keys are part_type/gear_type
    private final Map<String, MaterialDisplay> display = new HashMap<>();
    private final List<String> blacklistedGearTypes = new ArrayList<>();

    private PartMaterial(ResourceLocation id, String packName) {
        this.materialId = id;
        this.packName = packName;
    }

    @Override
    public String getPackName() {
        return packName;
    }

    @Override
    public ResourceLocation getId() {
        return this.materialId;
    }

    @Override
    public IMaterialSerializer<?> getSerializer() {
        return MaterialSerializers.STANDARD;
    }

    @Override
    @Nullable
    public IMaterial getParent() {
        if (parent != null) {
            return MaterialManager.get(parent);
        }
        return null;
    }

    @Override
    public int getTier(PartType partType) {
        if (tier < 0 && getParent() != null) {
            return getParent().getTier(partType);
        }
        return this.tier;
    }

    @Override
    public Ingredient getIngredient(PartType partType) {
        return this.ingredient;
    }

    @Override
    public boolean allowedInPart(PartType partType) {
        return stats.containsKey(partType) || (getParent() != null && getParent().allowedInPart(partType));
    }

    @Override
    public void retainData(@Nullable IMaterial oldMaterial) {
        if (oldMaterial instanceof PartMaterial) {
            // Copy trait instances, the client doesn't need to know conditions
            this.traits.clear();
            ((PartMaterial) oldMaterial).traits.forEach((partType, list) -> this.traits.put(partType, list));
        }
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType, ItemStack gear) {
        Collection<StatInstance> ret = new ArrayList<>(stats.getOrDefault(partType, EMPTY_STAT_MAP).get(stat));
        if (getParent() != null) {
            ret.addAll(getParent().getStatModifiers(stat, partType, gear));
        }
        return ret;
    }

    @Override
    public List<PartTraitInstance> getTraits(PartType partType, ItemStack gear) {
        List<PartTraitInstance> ret = new ArrayList<>(traits.getOrDefault(partType, Collections.emptyList()));
        if (getParent() != null) {
            ret.addAll(getParent().getTraits(partType, gear));
        }
        return ret;
    }

    @Override
    public boolean isCraftingAllowed(PartType partType, GearType gearType) {
        if (isGearTypeBlacklisted(gearType)) {
            return false;
        }

        if (stats.containsKey(partType) || (getParent() != null && getParent().isCraftingAllowed(partType, gearType))) {
            if (partType == PartType.MAIN) {
                ItemStat stat = gearType == GearType.ARMOR ? ItemStats.ARMOR_DURABILITY : ItemStats.DURABILITY;
                return !getStatModifiers(stat, partType).isEmpty() && getStatUnclamped(stat, partType) > 0;
            }
            return true;
        }
        return false;
    }

    private boolean isGearTypeBlacklisted(GearType gearType) {
        for (String s : this.blacklistedGearTypes) {
            if (gearType.matches(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getColor(ItemStack gear, PartType partType) {
        return getMaterialDisplay(gear, partType).getColor();
    }

    @Override
    public PartTextureType getTexture(PartType partType, ItemStack gear) {
        return getMaterialDisplay(gear, partType).getTexture();
    }

    private MaterialDisplay getMaterialDisplay(ItemStack gear, PartType partType) {
        if (!gear.isEmpty()) {
            GearType gearType = ((ICoreItem) gear.getItem()).getGearType();

            // Gear class-specific override
            String gearTypeKey = partType.getName() + "/" + gearType.getName();
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
        return display.getOrDefault(partType.getName().getPath() + "/all", display.getOrDefault(partType.getName() + "/all", MaterialDisplay.DEFAULT));
    }

    @Override
    public ITextComponent getDisplayName(PartType partType, ItemStack gear) {
        return displayName.deepCopy();
    }

    @Override
    public boolean isVisible(PartType partType) {
        return this.visible;
    }

    @Override
    public void updateIngredient(SyncMaterialCraftingItemsPacket msg) {
        Ingredient ing = msg.getIngredient(this.materialId);
        if (ing != null) {
            this.ingredient = ing;
        }
    }

    @Override
    public String toString() {
        return "PartMaterial{" +
                "id=" + materialId +
                ", tier=" + tier +
                ", ingredient=" + ingredient +
                '}';
    }

    public static final class Serializer implements IMaterialSerializer<PartMaterial> {
        static final int PACK_NAME_MAX_LENGTH = 32;

        //region deserialize

        @Override
        public PartMaterial deserialize(ResourceLocation id, String packName, JsonObject json) {
            PartMaterial ret = new PartMaterial(id, packName);

            if (json.has("parent")) {
                ret.parent = new ResourceLocation(JSONUtils.getString(json, "parent"));
            }

            deserializeStats(json, ret);
            deserializeTraits(json, ret);
            deserializeCraftingItems(json, ret);
            deserializeNames(json, ret);
            deserializeDisplayProps(json, ret);
            deserializeAvailability(json, ret);

            return ret;
        }

        static void deserializeStats(JsonObject json, PartMaterial ret) {
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

        private static void deserializeTraits(JsonObject json, PartMaterial ret) {
            JsonElement elementTraits = json.get("traits");
            if (elementTraits != null && elementTraits.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : elementTraits.getAsJsonObject().entrySet()) {
                    PartType partType = PartType.get(Objects.requireNonNull(SilentGear.getIdWithDefaultNamespace(entry.getKey())));
                    if (partType != null) {
                        List<PartTraitInstance> list = new ArrayList<>();
                        entry.getValue().getAsJsonArray().forEach(e -> list.add(PartTraitInstance.deserialize(e.getAsJsonObject())));
                        ret.traits.put(partType, list);
                    }
                }
            }
        }

        private static void deserializeCraftingItems(JsonObject json, PartMaterial ret) {
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
        }

        private static void deserializeNames(JsonObject json, PartMaterial ret) {
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
        }

        private static void deserializeDisplayProps(JsonObject json, PartMaterial ret) {
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
        }

        private static void deserializeAvailability(JsonObject json, PartMaterial ret) {
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();
                ret.tier = JSONUtils.getInt(obj, "tier", ret.tier);
                ret.visible = JSONUtils.getBoolean(obj, "visible", ret.visible);

                JsonArray blacklist = JSONUtils.getJsonArray(obj, "gear_blacklist", null);
                if (blacklist != null) {
                    ret.blacklistedGearTypes.clear();
                    blacklist.forEach(e -> ret.blacklistedGearTypes.add(e.getAsString()));
                }
            } else if (ret.parent == null) {
                throw new JsonSyntaxException("Expected 'availability' to be an object");
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

        // endregion

        // region read/write

        @Override
        public PartMaterial read(ResourceLocation id, PacketBuffer buffer) {
            PartMaterial material = new PartMaterial(id, buffer.readString(PACK_NAME_MAX_LENGTH));

            if (buffer.readBoolean())
                material.parent = buffer.readResourceLocation();

            material.displayName = buffer.readTextComponent();
            if (buffer.readBoolean())
                material.namePrefix = buffer.readTextComponent();
            material.ingredient = Ingredient.read(buffer);
            material.tier = buffer.readByte();
            material.visible = buffer.readBoolean();

            material.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                material.blacklistedGearTypes.add(buffer.readString());
            }

            // Textures
            int displayCount = buffer.readVarInt();
            for (int i = 0; i < displayCount; ++i) {
                String key = buffer.readString(255);
                MaterialDisplay display = MaterialDisplay.read(buffer);
                material.display.put(key, display);
            }

            // Stats and traits
            readStats(buffer, material);
            readTraits(buffer, material);

            return material;
        }

        @Override
        public void write(PacketBuffer buffer, PartMaterial material) {
            buffer.writeString(material.packName.substring(0, Math.min(PACK_NAME_MAX_LENGTH, material.packName.length())), PACK_NAME_MAX_LENGTH);

            buffer.writeBoolean(material.parent != null);
            if (material.parent != null)
                buffer.writeResourceLocation(material.parent);

            buffer.writeTextComponent(material.displayName);
            buffer.writeBoolean(material.namePrefix != null);
            if (material.namePrefix != null)
                buffer.writeTextComponent(material.namePrefix);
            material.ingredient.write(buffer);
            buffer.writeByte(material.tier);
            buffer.writeBoolean(material.visible);

            buffer.writeByte(material.blacklistedGearTypes.size());
            material.blacklistedGearTypes.forEach(buffer::writeString);

            // Textures
            buffer.writeVarInt(material.display.size());
            material.display.forEach((s, display) -> {
                buffer.writeString(s);
                display.write(buffer);
            });

            // Stats and traits
            writeStats(buffer, material);
            writeTraits(buffer, material);
        }

        @Override
        public ResourceLocation getName() {
            return SilentGear.getId("standard");
        }

        private static void readStats(PacketBuffer buffer, PartMaterial material) {
            material.stats.clear();
            int typeCount = buffer.readByte();
            for (int i = 0; i < typeCount; ++i) {
                PartType partType = PartType.get(buffer.readResourceLocation());
                int statCount = buffer.readByte();
                StatModifierMap map = new StatModifierMap();
                for (int j = 0; j < statCount; ++j) {
                    ItemStat stat = ItemStats.REGISTRY.get().getValue(buffer.readResourceLocation());
                    StatInstance mod = StatInstance.read(buffer);
                    map.put(stat, mod);
                }
                material.stats.put(partType, map);
            }
        }

        private static void writeStats(PacketBuffer buffer, PartMaterial material) {
            buffer.writeByte(material.stats.size());
            //noinspection OverlyLongLambda
            material.stats.forEach((partType, map) -> {
                buffer.writeResourceLocation(partType.getName());
                buffer.writeByte(map.size());
                map.forEach((stat, mod) -> {
                    buffer.writeResourceLocation(Objects.requireNonNull(stat.getStatId()));
                    mod.write(buffer);
                });
            });
        }

        private static void readTraits(PacketBuffer buffer, PartMaterial material) {
            material.traits.clear();
            int typeCount = buffer.readByte();
            for (int i = 0; i < typeCount; ++i) {
                PartType partType = PartType.get(buffer.readResourceLocation());
                int traitCount = buffer.readByte();
                List<PartTraitInstance> list = new ArrayList<>();
                for (int j = 0; j < traitCount; ++j) {
                    PartTraitInstance trait = PartTraitInstance.read(buffer);
                    list.add(trait);
                }
                material.traits.put(partType, list);
            }
        }

        private static void writeTraits(PacketBuffer buffer, PartMaterial material) {
            buffer.writeByte(material.traits.size());
            material.traits.forEach((partType, list) -> {
                buffer.writeResourceLocation(partType.getName());
                buffer.writeByte(list.size());
                list.forEach(trait -> trait.write(buffer));
            });
        }

        //endregion
    }
}
