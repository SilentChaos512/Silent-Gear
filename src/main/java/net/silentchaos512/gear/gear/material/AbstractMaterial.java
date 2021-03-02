package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public abstract class AbstractMaterial implements IMaterial {
    protected final ResourceLocation materialId;
    @Nullable ResourceLocation parent;
    protected final String packName;
    protected final Collection<IMaterialCategory> categories = new ArrayList<>();
    protected Ingredient ingredient = Ingredient.EMPTY;
    protected boolean visible = true;
    protected boolean canSalvage = true;
    protected boolean simple = true;

    protected final Map<PartType, StatModifierMap> stats = new LinkedHashMap<>();
    protected final Map<PartType, List<TraitInstance>> traits = new LinkedHashMap<>();
    protected final List<String> blacklistedGearTypes = new ArrayList<>();

    protected ITextComponent displayName;
    @Nullable protected ITextComponent namePrefix = null;

    protected AbstractMaterial(ResourceLocation materialId, String packName) {
        this.materialId = materialId;
        this.packName = packName;
    }

    @Override
    public String getPackName() {
        return packName;
    }

    @Override
    public ResourceLocation getId() {
        return materialId;
    }

    @Nullable
    @Override
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
        return new HashSet<>(categories);
    }

    @Override
    public int getTier(PartType partType) {
        return 0;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public Optional<Ingredient> getPartSubstitute(PartType partType) {
        return Optional.empty();
    }

    @Override
    public boolean hasPartSubstitutes() {
        return false;
    }

    @Override
    public boolean canSalvage() {
        return canSalvage;
    }

    @Override
    public boolean isSimple() {
        return simple;
    }

    @Override
    public Set<PartType> getPartTypes(IMaterialInstance material) {
        // Grab the part types from this part and its parent(s)
        return Sets.union(stats.keySet(), getParentOptional()
                .<Set<PartType>>map(m -> new LinkedHashSet<>(m.getPartTypes(material))).orElse(Collections.emptySet()));
    }

    @Override
    public boolean allowedInPart(IMaterialInstance material, PartType partType) {
        return getPartTypes(material).contains(partType);
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
    public Collection<TraitInstance> getTraits(IMaterialInstance material, PartType partType, GearType gearType, ItemStack gear) {
        List<TraitInstance> ret = new ArrayList<>(traits.getOrDefault(partType, Collections.emptyList()));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getTraits(material, partType, gearType, gear));
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
        }
    }

    @Override
    public String toString() {
        return "AbstractMaterial{" +
                "id=" + materialId +
                '}';
    }

    public static class Serializer<T extends AbstractMaterial> implements IMaterialSerializer<T> {
        static final int PACK_NAME_MAX_LENGTH = 32;

        private final ResourceLocation name;
        private final BiFunction<ResourceLocation, String, T> factory;

        public Serializer(ResourceLocation name, BiFunction<ResourceLocation, String, T> factory) {
            this.name = name;
            this.factory = factory;
        }

        @Override
        public T deserialize(ResourceLocation id, String packName, JsonObject json) {
            T ret = factory.apply(id, packName);

            deserializeStats(json, ret);
            deserializeTraits(json, ret);
            deserializeCraftingItems(json, ret);
            deserializeNames(json, ret);
            deserializeAvailability(json, ret);

            return ret;
        }

        //region deserialize methods

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

        private void sanitizeStats(T ret) {
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

        private void deserializeAvailability(JsonObject json, T ret) {
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();

                deserializeCategories(obj.get("categories"), ret);
                ret.visible = JSONUtils.getBoolean(obj, "visible", ret.visible);
                ret.canSalvage = JSONUtils.getBoolean(obj, "can_salvage", ret.canSalvage);
            }
        }

        private void deserializeCategories(@Nullable JsonElement json, T material) {
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

        private void deserializeCraftingItems(JsonObject json, T ret) {
            JsonElement craftingItems = json.get("crafting_items");
            if (craftingItems != null && craftingItems.isJsonObject()) {
                JsonElement main = craftingItems.getAsJsonObject().get("main");
                if (main != null) {
                    ret.ingredient = Ingredient.deserialize(main);
                }
            } else {
                throw new JsonSyntaxException("Expected 'crafting_items' to be an object");
            }
        }

        private void deserializeNames(JsonObject json, T ret) {
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

        private static ITextComponent deserializeText(JsonElement json) {
            return Objects.requireNonNull(ITextComponent.Serializer.getComponentFromJson(json));
        }

        //endregion

        @Override
        public T read(ResourceLocation id, PacketBuffer buffer) {
            T material = factory.apply(id, buffer.readString(PACK_NAME_MAX_LENGTH));

            readBasics(buffer, material);
            readRestrictions(buffer, material);
            readStats(buffer, material);
            readTraits(buffer, material);

            return material;
        }

        //region read methods

        private void readBasics(PacketBuffer buffer, T material) {
            // Parent
            if (buffer.readBoolean()) {
                material.parent = buffer.readResourceLocation();
            }
            material.visible = buffer.readBoolean();
            material.canSalvage = buffer.readBoolean();
            material.simple = buffer.readBoolean();
            material.ingredient = Ingredient.read(buffer);

            // Text
            material.displayName = buffer.readTextComponent();
            if (buffer.readBoolean()) {
                material.namePrefix = buffer.readTextComponent();
            }
        }

        private void readRestrictions(PacketBuffer buffer, T material) {
            // Categories
            int categoryCount = buffer.readByte();
            for (int i = 0; i < categoryCount; ++i) {
                material.categories.add(MaterialCategories.get(buffer.readString()));
            }

            // Gear Type Blacklist
            material.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                material.blacklistedGearTypes.add(buffer.readString());
            }
        }

        private void readStats(PacketBuffer buffer, T material) {
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

        private void readTraits(PacketBuffer buffer, T material) {
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

        //endregion

        @Override
        public void write(PacketBuffer buffer, T material) {
            buffer.writeString(material.packName.substring(0, Math.min(PACK_NAME_MAX_LENGTH, material.packName.length())), PACK_NAME_MAX_LENGTH);

            writeBasics(buffer, material);
            writeRestrictions(buffer, material);
            writeStats(buffer, material);
            writeTraits(buffer, material);
        }

        //region write methods

        private void writeBasics(PacketBuffer buffer, T material) {
            // Parent
            buffer.writeBoolean(material.parent != null);
            if (material.parent != null) {
                buffer.writeResourceLocation(material.parent);
            }

            buffer.writeBoolean(material.visible);
            buffer.writeBoolean(material.canSalvage);
            buffer.writeBoolean(material.simple);
            material.ingredient.write(buffer);

            // Text
            buffer.writeTextComponent(material.displayName);
            buffer.writeBoolean(material.namePrefix != null);
            if (material.namePrefix != null) {
                buffer.writeTextComponent(material.namePrefix);
            }
        }

        private void writeRestrictions(PacketBuffer buffer, T material) {
            // Categories
            buffer.writeByte(material.categories.size());
            material.categories.forEach(cat -> buffer.writeString(cat.getName()));

            // Gear Type Blacklist
            buffer.writeByte(material.blacklistedGearTypes.size());
            material.blacklistedGearTypes.forEach(buffer::writeString);
        }

        private void writeStats(PacketBuffer buffer, T material) {
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

        private void writeTraits(PacketBuffer buffer, T material) {
            buffer.writeByte(material.traits.size());
            material.traits.forEach((partType, list) -> {
                buffer.writeResourceLocation(partType.getName());
                buffer.writeByte(list.size());
                list.forEach(trait -> trait.write(buffer));
            });
        }

        //endregion

        @Override
        public ResourceLocation getName() {
            return name;
        }
    }
}
