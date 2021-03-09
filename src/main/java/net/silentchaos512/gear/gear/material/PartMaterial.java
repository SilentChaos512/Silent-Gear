package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.crafting.ingredient.CustomCompoundIngredient;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public class PartMaterial implements IMaterial { // TODO: Extend AbstractMaterial
    private final ResourceLocation materialId;
    @Nullable ResourceLocation parent;
    final String packName;
    final Collection<IMaterialCategory> categories = new ArrayList<>();
    Ingredient ingredient = Ingredient.EMPTY;
    final Map<PartType, Ingredient> partSubstitutes = new HashMap<>();
    boolean visible = true;
    int tier = -1;
    boolean canSalvage = true;
    boolean simple = true;

    final Map<PartType, StatModifierMap> stats = new LinkedHashMap<>();
    final Map<PartType, List<TraitInstance>> traits = new LinkedHashMap<>();

    ITextComponent displayName;
    @Nullable ITextComponent namePrefix = null;
    // Keys are part_type/gear_type
    final Map<String, MaterialLayerList> display = new HashMap<>();
    final List<String> blacklistedGearTypes = new ArrayList<>();

    public PartMaterial(ResourceLocation id, String packName) {
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
    public Collection<IMaterialCategory> getCategories(IMaterialInstance material) {
        if (this.categories.isEmpty() && getParent() != null) {
            return getParent().getCategories(material);
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
    public boolean isSimple() {
        return this.simple;
    }

    @Override
    public Set<PartType> getPartTypes(IMaterialInstance material) {
        // Grab the part types from this part and its parent(s)
        return Sets.union(stats.keySet(), getParentOptional()
                .<Set<PartType>>map(m -> new LinkedHashSet<>(m.getPartTypes(material))).orElse(Collections.emptySet()));
    }

    @Override
    public boolean allowedInPart(IMaterialInstance material, PartType partType) {
        return stats.containsKey(partType) || (getParent() != null && getParent().allowedInPart(material, partType));
    }

    @Override
    public Collection<StatInstance> getStatModifiers(IMaterialInstance material, PartType partType, StatGearKey key, ItemStack gear) {
        Collection<StatInstance> ret = new ArrayList<>(stats.getOrDefault(partType, StatModifierMap.EMPTY_STAT_MAP).get(key));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getStatModifiers(material, partType, key, gear));
        }
        return ret;
    }

    @Override
    public Collection<StatGearKey> getStatKeys(IMaterialInstance material, PartType type) {
        return this.stats.getOrDefault(type, StatModifierMap.EMPTY_STAT_MAP).keySet();
    }

    @Override
    public Collection<TraitInstance> getTraits(IMaterialInstance instance, PartGearKey partKey, ItemStack gear) {
        List<TraitInstance> ret = new ArrayList<>(traits.getOrDefault(partKey, Collections.emptyList()));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getTraits(instance, partKey, gear));
        }
        return ret;
    }

    @Override
    public boolean isCraftingAllowed(IMaterialInstance material, PartType partType, GearType gearType, @Nullable IInventory inventory) {
        if (isGearTypeBlacklisted(gearType) || !allowedInPart(material, partType)) {
            return false;
        }

        if (stats.containsKey(partType) || (getParent() != null && getParent().isCraftingAllowed(material, partType, gearType, inventory))) {
            if (partType == PartType.MAIN) {
                ItemStat stat = gearType.getDurabilityStat();
                StatGearKey key = StatGearKey.of(stat, gearType);
                return !getStatModifiers(material, partType, key).isEmpty() && getStatUnclamped(material, partType, key, ItemStack.EMPTY) > 0;
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
    public ITextComponent getDisplayName(@Nullable IMaterialInstance material, PartType type, ItemStack gear) {
        return displayName.deepCopy();
    }

    @Nullable
    @Override
    public ITextComponent getDisplayNamePrefix(ItemStack gear, PartType partType) {
        return namePrefix != null ? namePrefix.deepCopy() : null;
    }

    @Override
    public int getNameColor(IMaterialInstance material, PartType partType, GearType gearType) {
        IMaterialDisplay model = MaterialDisplayManager.get(material);
        int color = model.getLayerColor(gearType, partType, material, 0);
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

    public static final class Serializer<T extends PartMaterial> implements IMaterialSerializer<T> {
        static final int PACK_NAME_MAX_LENGTH = 32;

        private final ResourceLocation id;
        private final BiFunction<ResourceLocation, String, T> factory;

        public Serializer(ResourceLocation id, BiFunction<ResourceLocation, String, T> factory) {
            this.id = id;
            this.factory = factory;
        }

        //region deserialize

        @Override
        public T deserialize(ResourceLocation id, String packName, JsonObject json) {
            T ret = this.factory.apply(id, packName);

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

        void deserializeStats(JsonObject json, T ret) {
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

            sanitizeStats(ret);
        }

        private static <T extends PartMaterial> void sanitizeStats(T ret) {
            for (PartType partType : ret.stats.keySet()) {
                StatModifierMap statMap = ret.stats.get(partType);

                for (IItemStat stat : ItemStats.allStatsOrdered()) {
                    if (stat instanceof SplitItemStat) {
                        sanitizeSplitStat((SplitItemStat) stat, statMap);
                    }
                }
            }
        }

        private static void sanitizeSplitStat(SplitItemStat stat, StatModifierMap statMap) {
            // Creates new gear type-specific modifiers for split stats, if they were missing
            StatGearKey all = StatGearKey.of(stat, GearType.ALL);

            for (GearType type : stat.getSplitTypes()) {
                StatGearKey key = StatGearKey.of(stat, type);

                if (!statMap.containsKey(key)) {
                    Collection<StatInstance> mods = statMap.get(all);

                    for (StatInstance mod : mods) {
                        statMap.put(key, getSplitStatMod(stat, key, mod));
                    }
                }
            }
        }

        @Nonnull
        private static StatInstance getSplitStatMod(SplitItemStat stat, StatGearKey key, StatInstance mod) {
            if (mod.getOp() == StatInstance.Operation.AVG) {
                // AVG mods should be adjusted to fit the split values
                float value = stat.compute(stat.getBaseValue(),
                        true,
                        key.getGearType(),
                        mod.getKey().getGearType(),
                        Collections.singleton(mod));
                return StatInstance.of(value, mod.getOp(), key);
            } else {
                // Others can just be copied as-is (but with updated key)
                return StatInstance.of(mod.getValue(), mod.getOp(), key);
            }
        }

        private void deserializeTraits(JsonObject json, T ret) {
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

                JsonElement customCompound = craftingItems.getAsJsonObject().get("custom_compound");
                if (customCompound != null && customCompound.isJsonObject()) {
                    ResourceLocation itemId = new ResourceLocation(JSONUtils.getString(customCompound.getAsJsonObject(), "item"));
                    Item item = ForgeRegistries.ITEMS.getValue(itemId);
                    if (!(item instanceof CustomMaterialItem)) {
                        throw new JsonParseException("Item '" + itemId + "' is not a CustomMaterialItem");
                    }
                    ret.ingredient = CustomCompoundIngredient.of((CustomMaterialItem) item, ret.materialId);
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
                    PartGearKey key = PartGearKey.read(entry.getKey());
                    JsonElement value = entry.getValue();
                    ret.display.put(key.toString(), MaterialLayerList.deserialize(key, value, defaultProps));
                }
            }
        }

        private static void deserializeAvailability(JsonObject json, PartMaterial ret) {
            ret.simple = JSONUtils.getBoolean(json, "simple", true);

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
        public T read(ResourceLocation id, PacketBuffer buffer) {
            T material = this.factory.apply(id, buffer.readString(PACK_NAME_MAX_LENGTH));

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
            material.simple = buffer.readBoolean();
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
        public void write(PacketBuffer buffer, T material) {
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
            buffer.writeBoolean(material.simple);
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
            return id;
        }

        private static void readStats(PacketBuffer buffer, PartMaterial material) {
            material.stats.clear();
            int typeCount = buffer.readByte();
            for (int i = 0; i < typeCount; ++i) {
                PartType partType = PartType.get(buffer.readResourceLocation());
                int statCount = buffer.readByte();
                StatModifierMap map = new StatModifierMap();
                for (int j = 0; j < statCount; ++j) {
                    StatGearKey key = StatGearKey.read(buffer);
                    StatInstance mod = StatInstance.read(key, buffer);
                    map.put(key, mod);
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
                map.forEach((key, mod) -> {
                    buffer.writeString(key.toString());
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
