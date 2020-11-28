package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.gear.part.PartTextureSet;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.*;

public final class PartMaterial implements IMaterial {
    private static final StatModifierMap EMPTY_STAT_MAP = new StatModifierMap();

    private final ResourceLocation materialId;
    @Nullable private ResourceLocation parent;
    private final String packName;
    private final Collection<IMaterialCategory> categories = new ArrayList<>();
    private Ingredient ingredient = Ingredient.EMPTY;
    private final Map<PartType, Ingredient> partSubstitutes = new HashMap<>();
    private boolean visible = true;
    private int tier = -1;
    private boolean canSalvage = true;

    private final Map<PartType, StatModifierMap> stats = new LinkedHashMap<>();
    private final Map<PartType, List<TraitInstance>> traits = new LinkedHashMap<>();

    private ITextComponent displayName;
    @Nullable private ITextComponent namePrefix = null;
    // Keys are part_type/gear_type
    private final Map<String, MaterialLayerList> display = new HashMap<>();
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
    public Collection<IMaterialCategory> getCategories() {
        if (this.categories.isEmpty() && getParent() != null) {
            return getParent().getCategories();
        }
        return Collections.unmodifiableCollection(this.categories);
    }

    @Override
    public int getTier(PartType partType) {
        if (tier < 0 && getParent() != null) {
            return getParent().getTier(partType);
        }
        return this.tier;
    }

    @Override
    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public Optional<Ingredient> getPartSubstitute(PartType partType) {
        return Optional.ofNullable(this.partSubstitutes.get(partType));
    }

    @Override
    public boolean hasPartSubstitutes() {
        return !this.partSubstitutes.isEmpty();
    }

    @Override
    public boolean canSalvage() {
        return this.canSalvage;
    }

    @Override
    public Set<PartType> getPartTypes() {
        // Grab the part types from this part and its parent(s)
        return Sets.union(stats.keySet(), getParentOptional()
                .<Set<PartType>>map(m -> new LinkedHashSet<>(m.getPartTypes())).orElse(Collections.emptySet()));
    }

    @Override
    public boolean allowedInPart(PartType partType) {
        return stats.containsKey(partType) || (getParent() != null && getParent().allowedInPart(partType));
    }

    @Override
    public Collection<StatInstance> getStatModifiers(IMaterialInstance material, ItemStat stat, PartType partType, ItemStack gear) {
        Collection<StatInstance> ret = new ArrayList<>(stats.getOrDefault(partType, EMPTY_STAT_MAP).get(stat));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getStatModifiers(material, stat, partType, gear));
        }
        return ret;
    }

    @Override
    public List<TraitInstance> getTraits(PartType partType, ItemStack gear) {
        List<TraitInstance> ret = new ArrayList<>(traits.getOrDefault(partType, Collections.emptyList()));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getTraits(partType, gear));
        }
        return ret;
    }

    @Override
    public boolean isCraftingAllowed(IMaterialInstance material, PartType partType, GearType gearType) {
        if (isGearTypeBlacklisted(gearType) || !allowedInPart(partType)) {
            return false;
        }

        if (stats.containsKey(partType) || (getParent() != null && getParent().isCraftingAllowed(material, partType, gearType))) {
            if (partType == PartType.MAIN) {
                ItemStat stat = gearType == GearType.ARMOR ? ItemStats.ARMOR_DURABILITY : ItemStats.DURABILITY;
                return !getStatModifiers(material, stat, partType).isEmpty() && getStatUnclamped(material, stat, partType) > 0;
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
    public int getPrimaryColor(ItemStack gear, PartType partType) {
        return getMaterialDisplay(gear, partType).getPrimaryColor();
    }

    @Override
    public PartTextureSet getTexture(PartType partType, ItemStack gear) {
        return getMaterialDisplay(gear, partType).getTexture();
    }

    @Deprecated
    @Override
    public IMaterialLayerList getMaterialDisplay(ItemStack gear, PartType partType) {
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
        return display.getOrDefault(partType.getName().getPath() + "/all", display.getOrDefault(partType.getName() + "/all", MaterialLayerList.DEFAULT));
    }

    @Override
    public IFormattableTextComponent getDisplayName(PartType partType, ItemStack gear) {
        return displayName.deepCopy();
    }

    @Nullable
    @Override
    public IFormattableTextComponent getDisplayNamePrefix(ItemStack gear, PartType partType) {
        return namePrefix != null ? namePrefix.deepCopy() : null;
    }

    @Override
    public int getNameColor(PartType partType, GearType gearType) {
        IMaterialDisplay model = MaterialDisplayManager.get(this);
        int color = model.getLayerColor(gearType, partType, 0);
        return Color.blend(color, Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
    }

    @Override
    public boolean isVisible(PartType partType) {
        return this.visible;
    }

    @Override
    public void updateIngredient(SyncMaterialCraftingItemsPacket msg) {
        if (msg.isValid()) {
            msg.getIngredient(this.materialId).ifPresent(ing -> this.ingredient = ing);
            this.partSubstitutes.clear();
            msg.getPartSubstitutes(this.materialId).forEach(this.partSubstitutes::put);
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
                        StatModifierMap statMods = StatModifierMap.deserialize(entry.getValue());
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
                        List<TraitInstance> list = new ArrayList<>();
                        entry.getValue().getAsJsonArray().forEach(e -> list.add(TraitInstance.deserialize(e.getAsJsonObject())));
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

                JsonElement subs = craftingItems.getAsJsonObject().get("subs");
                if (subs != null && subs.isJsonObject()) {
                    // Part substitutes
                    JsonObject jo = subs.getAsJsonObject();
                    Map<PartType, Ingredient> map = new HashMap<>();
                    jo.entrySet().forEach(entry -> {
                        PartType partType = PartType.get(new ModResourceLocation(entry.getKey()));
                        Ingredient ingredient = Ingredient.deserialize(entry.getValue());
                        map.put(partType, ingredient);
                    });
                    ret.partSubstitutes.putAll(map);
                }
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

        @Deprecated
        private static void deserializeDisplayProps(JsonObject json, PartMaterial ret) {
            JsonElement elementDisplay = json.get("display");
            if (elementDisplay != null && elementDisplay.isJsonObject()) {
                JsonObject obj = elementDisplay.getAsJsonObject();
                MaterialLayerList defaultProps = ret.display.getOrDefault("all", MaterialLayerList.DEFAULT);

                if (!ret.display.containsKey("all")) {
                    ret.display.put("all", defaultProps);
                }

                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();
                    ret.display.put(key, MaterialLayerList.deserialize(value, defaultProps));
                }
            }
        }

        private static void deserializeAvailability(JsonObject json, PartMaterial ret) {
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();

                deserializeCategories(obj.get("categories"), ret);
                ret.tier = JSONUtils.getInt(obj, "tier", ret.tier);
                ret.visible = JSONUtils.getBoolean(obj, "visible", ret.visible);
                ret.canSalvage = JSONUtils.getBoolean(obj, "can_salvage", ret.canSalvage);

                JsonArray blacklist = JSONUtils.getJsonArray(obj, "gear_blacklist", null);
                if (blacklist != null) {
                    ret.blacklistedGearTypes.clear();
                    blacklist.forEach(e -> ret.blacklistedGearTypes.add(e.getAsString()));
                }
            } else if (ret.parent == null) {
                throw new JsonSyntaxException("Expected 'availability' to be an object");
            }
        }

        private static void deserializeCategories(@Nullable JsonElement json, PartMaterial material) {
            if (json != null) {
                if (json.isJsonArray()) {
                    JsonArray array = json.getAsJsonArray();
                    for (JsonElement elem : array) {
                        material.categories.add(MaterialCategories.get(elem.getAsString()));
                    }
                } else if (json.isJsonPrimitive()) {
                    material.categories.add(MaterialCategories.get(json.getAsString()));
                } else {
                    throw new JsonParseException("Expected 'categories' to be array or string");
                }
            }
        }

        private static ITextComponent deserializeText(JsonElement json) {
            return Objects.requireNonNull(ITextComponent.Serializer.getComponentFromJson(json));
        }

        // endregion

        // region read/write

        @Override
        public PartMaterial read(ResourceLocation id, PacketBuffer buffer) {
            PartMaterial material = new PartMaterial(id, buffer.readString(PACK_NAME_MAX_LENGTH));

            if (buffer.readBoolean())
                material.parent = buffer.readResourceLocation();

            int categoryCount = buffer.readByte();
            for (int i = 0; i < categoryCount; ++i) {
                material.categories.add(MaterialCategories.get(buffer.readString()));
            }

            material.displayName = buffer.readTextComponent();
            if (buffer.readBoolean())
                material.namePrefix = buffer.readTextComponent();

            material.tier = buffer.readByte();
            material.visible = buffer.readBoolean();
            material.canSalvage = buffer.readBoolean();
            material.ingredient = Ingredient.read(buffer);

            int subCount = buffer.readByte();
            for (int i = 0; i < subCount; ++i) {
                PartType partType = PartType.get(buffer.readResourceLocation());
                Ingredient ingredient = Ingredient.read(buffer);
                material.partSubstitutes.put(partType, ingredient);
            }

            material.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                material.blacklistedGearTypes.add(buffer.readString());
            }

            // Textures
            int displayCount = buffer.readVarInt();
            for (int i = 0; i < displayCount; ++i) {
                String key = buffer.readString(255);
                MaterialLayerList display = MaterialLayerList.read(buffer);
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

            buffer.writeByte(material.categories.size());
            material.categories.forEach(cat -> buffer.writeString(cat.getName()));

            buffer.writeTextComponent(material.displayName);
            buffer.writeBoolean(material.namePrefix != null);
            if (material.namePrefix != null)
                buffer.writeTextComponent(material.namePrefix);

            buffer.writeByte(material.tier);
            buffer.writeBoolean(material.visible);
            buffer.writeBoolean(material.canSalvage);
            material.ingredient.write(buffer);

            buffer.writeByte(material.partSubstitutes.size());
            material.partSubstitutes.forEach((type, ing) -> {
                buffer.writeResourceLocation(type.getName());
                ing.write(buffer);
            });

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
                List<TraitInstance> list = new ArrayList<>();
                for (int j = 0; j < traitCount; ++j) {
                    list.add(TraitInstance.read(buffer));
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
