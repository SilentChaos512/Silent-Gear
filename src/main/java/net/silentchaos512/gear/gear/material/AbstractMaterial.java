package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.material.DefaultMaterialDisplay;
import net.silentchaos512.gear.client.material.GearDisplayManager;
import net.silentchaos512.gear.client.material.MaterialDisplay;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.gear.util.TierHelper;
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
    protected boolean visible = true;
    protected boolean canSalvage = true;
    protected boolean simple = true;

    protected int tier = -1;
    protected Tier harvestTier = Tiers.IRON;
    protected Ingredient ingredient = Ingredient.EMPTY;
    protected Map<PartType, Ingredient> partSubstitutes = Map.of();

    protected final Map<PartType, StatModifierMap> stats = new LinkedHashMap<>();
    protected final Map<PartType, List<TraitInstance>> traits = new LinkedHashMap<>();
    protected final List<String> blacklistedGearTypes = new ArrayList<>();

    protected Component displayName;
    @Nullable protected Component namePrefix = null;
    protected IMaterialDisplay displayProperties = DefaultMaterialDisplay.INSTANCE;

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
    public int getTier(IMaterialInstance material, PartType partType) {
        if (tier < 0 && getParent() != null) {
            return getParent().getTier(material, partType);
        }
        return this.tier;
    }

    @Override
    public Tier getHarvestTier(IMaterialInstance material) {
        if (this.parent != null) {
            IMaterial parent = getParent();
            if (parent != null) {
                return TierHelper.getHigher(this.harvestTier, MaterialInstance.of(parent).getHarvestTier());
            }
        }
        return this.harvestTier;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
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
        return canSalvage;
    }

    @Override
    public IMaterialInstance onSalvage(IMaterialInstance material) {
        return removeEnhancements(material);
    }

    public static IMaterialInstance removeEnhancements(IMaterialInstance material) {
        ItemStack stack = material.getItem().copy();
        for (IMaterialModifierType modifierType : MaterialModifiers.getTypes()) {
            modifierType.removeModifier(stack);
        }
        EnchantmentHelper.setEnchantments(Collections.emptyMap(), stack);

        IMaterial iMaterial = material.get();
        if (iMaterial != null) {
            return MaterialInstance.of(iMaterial, stack);
        } else {
            return material;
        }
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
        StatModifierMap map = this.stats.getOrDefault(type, StatModifierMap.EMPTY_STAT_MAP);
        if (map.isEmpty() && getParent() != null) {
            return getParent().getStatKeys(material, type);
        }
        return map.keySet();
    }

    @Override
    public Collection<TraitInstance> getTraits(IMaterialInstance material, PartGearKey partKey, ItemStack gear) {
        Collection<TraitInstance> ret = new ArrayList<>(traits.getOrDefault(partKey.getPartType(), Collections.emptyList()));
        if (ret.isEmpty() && getParent() != null) {
            ret.addAll(getParent().getTraits(material, partKey, gear));
        }
        return ret;
    }

    @Override
    public boolean isCraftingAllowed(IMaterialInstance material, PartType partType, GearType gearType, @Nullable Container inventory) {
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
    public Component getDisplayName(@Nullable IMaterialInstance material, PartType type, ItemStack gear) {
        return displayName.copy();
    }

    @Nullable
    @Override
    public Component getDisplayNamePrefix(ItemStack gear, PartType partType) {
        return namePrefix != null ? namePrefix.copy() : null;
    }

    @Override
    public int getNameColor(IMaterialInstance material, PartType partType, GearType gearType) {
        IMaterialDisplay model = material.getDisplayProperties();
        int color = model.getLayerColor(gearType, partType, material, 0);
        return Color.blend(color, Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
    }

    @Override
    public IMaterialDisplay getDisplayProperties(IMaterialInstance material) {
        IMaterialDisplay override = GearDisplayManager.get(material);
        if (override != null) {
            return override;
        }
        return displayProperties;
    }

    @Override
    public boolean isVisible(PartType partType) {
        return this.visible;
    }

    @Override
    public void updateIngredient(SyncMaterialCraftingItemsPacket msg) {
        if (msg.isValid()) {
            msg.getIngredient(this.materialId).ifPresent(ing -> this.ingredient = ing);
            this.partSubstitutes = Map.copyOf(msg.getPartSubstitutes(this.materialId));
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

            if (json.has("parent")) {
                ret.parent = new ResourceLocation(GsonHelper.getAsString(json, "parent"));
            }

            deserializeHarvestTier(json, ret);
            deserializeStats(json, ret);
            deserializeTraits(json, ret);
            deserializeCraftingItems(json, ret);
            deserializeNames(json, ret);
            deserializeDisplayProperties(json, ret);
            deserializeAvailability(json, ret);

            return ret;
        }

        void deserializeHarvestTier(JsonObject json, T ret) {
            if (ret.parent != null && !json.has("harvest_tier")) {
                // Will try to inherit harvest tier from parent
                ret.harvestTier = Tiers.WOOD;
                return;
            }
            // TODO: Remove me in 1.21!
            else if (!json.has("harvest_tier")) {
                // guess a harvest tier for outdated files
                hackyDeserializeHarvestTierFromOutdatedFile(json, ret);
                return;
            }

            String harvestTierStr = GsonHelper.getAsString(json, "harvest_tier");
            ResourceLocation harvestTierName = new ResourceLocation(harvestTierStr);
            Tier harvestTier = TierSortingRegistry.byName(harvestTierName);
            if (harvestTier != null) {
                ret.harvestTier = harvestTier;
            } else {
                throw new JsonSyntaxException("Unknown harvest tier: " + harvestTierName);
            }
        }

        @Deprecated // TODO: Remove me!
        void hackyDeserializeHarvestTierFromOutdatedFile(JsonObject json, T ret) {
            // Guess a harvest tier based on an old harvest level stat, but also log a warning about it
            // Remove this method in 1.21 and force harvest tiers
            boolean tierAssigned = false;
            JsonElement elementStats = json.get("stats");
            if (elementStats != null && elementStats.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : elementStats.getAsJsonObject().entrySet()) {
                    ResourceLocation partTypeName = SilentGear.getIdWithDefaultNamespace(entry.getKey());
                    if (partTypeName != null) {
                        JsonObject statsList = entry.getValue().getAsJsonObject();
                        if (statsList.has("harvest_level")) {
                            ret.harvestTier = hackyGuessHarvestTier(statsList.get("harvest_level"));
                            tierAssigned = true;
                            break;
                        } else if (statsList.has("silentgear:harvest_level")) {
                            ret.harvestTier = hackyGuessHarvestTier(statsList.get("silentgear:harvest_level"));
                            tierAssigned = true;
                            break;
                        }
                    }
                }
            }

            if (!tierAssigned) {
                // Nothing found... Make it wood as a failsafe.
                ret.harvestTier = Tiers.WOOD;
            }
            SilentGear.LOGGER.warn("Material has no harvest tier, guessing it as \"{}\"", TierSortingRegistry.getName(ret.harvestTier));
        }

        @Deprecated // TODO: Remove me!
        Tier hackyGuessHarvestTier(JsonElement json) {
            // Helper for hackyDeserializeHarvestTierFromOutdatedFile
            // The key is just a dummy, it doesn't affect reading the value
            StatInstance statInstance = StatInstance.read(StatGearKey.of(ItemStats.DURABILITY, GearType.ALL), json);
            int harvestLevel = Math.round(statInstance.getValue());
            return switch (harvestLevel) {
                case 0 -> Tiers.WOOD;
                case 1 -> Tiers.STONE;
                case 2 -> Tiers.IRON;
                case 3 -> Tiers.DIAMOND;
                default -> Tiers.NETHERITE;
            };
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
            ret.simple = GsonHelper.getAsBoolean(json, "simple", true);

            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();

                deserializeCategories(obj.get("categories"), ret);
                ret.tier = GsonHelper.getAsInt(obj, "tier", ret.tier);
                ret.visible = GsonHelper.getAsBoolean(obj, "visible", ret.visible);
                ret.canSalvage = GsonHelper.getAsBoolean(obj, "can_salvage", ret.canSalvage);

                JsonArray blacklist = GsonHelper.getAsJsonArray(obj, "gear_blacklist", null);
                if (blacklist != null) {
                    ret.blacklistedGearTypes.clear();
                    blacklist.forEach(e -> ret.blacklistedGearTypes.add(e.getAsString()));
                } else if (ret.simple && ret.parent == null) {
                    throw new JsonSyntaxException("Expected 'availability' to be an object");
                }
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
                    throw new JsonSyntaxException("Expected 'categories' to be array or string");
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
                    ret.ingredient = Ingredient.fromJson(main);
                }

                JsonElement subs = craftingItems.getAsJsonObject().get("subs");
                if (subs != null && subs.isJsonObject()) {
                    // Part substitutes
                    JsonObject jo = subs.getAsJsonObject();
                    Map<PartType, Ingredient> map = new HashMap<>();
                    jo.entrySet().forEach(entry -> {
                        PartType partType = PartType.get(new ModResourceLocation(entry.getKey()));
                        Ingredient ingredient = Ingredient.fromJson(entry.getValue());
                        map.put(partType, ingredient);
                    });
                    ret.partSubstitutes = Map.copyOf(map);
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

        private static Component deserializeText(JsonElement json) {
            return Objects.requireNonNull(Component.Serializer.fromJson(json));
        }

        private void deserializeDisplayProperties(JsonObject json, T ret) {
            JsonElement element = json.get("model");
            if (element != null) {
                if (element.isJsonObject()) {
                    ret.displayProperties = MaterialDisplay.deserialize(this.name, element.getAsJsonObject());
                } else {
                    throw new JsonSyntaxException("Expected 'model' to be an object");
                }
            } else {
                SilentGear.LOGGER.warn("Material '{}' has no model in the data file. This may be an outdated data pack or mod.", ret.materialId);
            }
        }

        //endregion

        @Override
        public T read(ResourceLocation id, FriendlyByteBuf buffer) {
            T material = factory.apply(id, buffer.readUtf(PACK_NAME_MAX_LENGTH));

            readBasics(buffer, material);
            readCraftingItems(buffer, material);
            readDisplayProperties(buffer, material);
            readRestrictions(buffer, material);
            readStats(buffer, material);
            readTraits(buffer, material);

            return material;
        }

        //region read methods

        private void readBasics(FriendlyByteBuf buffer, T material) {
            // Parent
            if (buffer.readBoolean()) {
                material.parent = buffer.readResourceLocation();
            }
            material.harvestTier = TierSortingRegistry.byName(buffer.readResourceLocation());
            material.tier = buffer.readByte();
            material.visible = buffer.readBoolean();
            material.canSalvage = buffer.readBoolean();
            material.simple = buffer.readBoolean();

            // Name and Prefix
            material.displayName = buffer.readComponent();
            if (buffer.readBoolean()) {
                material.namePrefix = buffer.readComponent();
            }
        }

        private Ingredient tempReadIngredientFix(FriendlyByteBuf buffer) {
            if (buffer.readBoolean()) {
                buffer.readVarInt(); // Burn Forge's -1 marker...
                return CraftingHelper.getIngredient(buffer.readResourceLocation(), buffer);
            }
            return Ingredient.fromNetwork(buffer);
        }

        private void tempWriteIngredientFix(FriendlyByteBuf buffer, Ingredient ingredient) {
            boolean custom = ingredient.getSerializer() != VanillaIngredientSerializer.INSTANCE;
            buffer.writeBoolean(custom);
            CraftingHelper.write(buffer, ingredient);
        }

        private void readCraftingItems(FriendlyByteBuf buffer, T material) {
            material.ingredient = tempReadIngredientFix(buffer);

            // Part subs
            int subCount = buffer.readByte();
            Map.Entry<PartType, Ingredient>[] subs = new Map.Entry[subCount];
            for (int i = 0; i < subCount; ++i) {
                PartType partType = PartType.get(buffer.readResourceLocation());
                Ingredient ingredient = tempReadIngredientFix(buffer);
                subs[i] = Map.entry(partType, ingredient);
            }
            material.partSubstitutes = Map.ofEntries(subs);
        }

        private void readDisplayProperties(FriendlyByteBuf buf, T material) {
            if (buf.readBoolean()) {
                material.displayProperties = MaterialDisplay.fromNetwork(material.materialId, buf);
            }
        }

        private void readRestrictions(FriendlyByteBuf buffer, T material) {
            // Categories
            int categoryCount = buffer.readByte();
            for (int i = 0; i < categoryCount; ++i) {
                material.categories.add(MaterialCategories.get(buffer.readUtf()));
            }

            // Gear Type Blacklist
            material.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                material.blacklistedGearTypes.add(buffer.readUtf());
            }
        }

        private void readStats(FriendlyByteBuf buffer, T material) {
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

        private void readTraits(FriendlyByteBuf buffer, T material) {
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
        public void write(FriendlyByteBuf buffer, T material) {
            buffer.writeUtf(material.packName.substring(0, Math.min(PACK_NAME_MAX_LENGTH, material.packName.length())), PACK_NAME_MAX_LENGTH);

            writeBasics(buffer, material);
            writeCraftingItems(buffer, material);
            writeDisplayProperties(buffer, material);
            writeRestrictions(buffer, material);
            writeStats(buffer, material);
            writeTraits(buffer, material);
        }

        //region write methods

        private void writeBasics(FriendlyByteBuf buffer, T material) {
            // Parent
            buffer.writeBoolean(material.parent != null);
            if (material.parent != null) {
                buffer.writeResourceLocation(material.parent);
            }

            buffer.writeResourceLocation(TierSortingRegistry.getName(material.harvestTier));
            buffer.writeByte(material.tier);
            buffer.writeBoolean(material.visible);
            buffer.writeBoolean(material.canSalvage);
            buffer.writeBoolean(material.simple);

            // Text
            buffer.writeComponent(material.displayName);
            buffer.writeBoolean(material.namePrefix != null);
            if (material.namePrefix != null) {
                buffer.writeComponent(material.namePrefix);
            }
        }

        private void writeCraftingItems(FriendlyByteBuf buffer, T material) {
            tempWriteIngredientFix(buffer, material.ingredient);

            // Part subs
            buffer.writeByte(material.partSubstitutes.size());
            material.partSubstitutes.forEach((type, ing) -> {
                buffer.writeResourceLocation(type.getName());
                tempWriteIngredientFix(buffer, ing);
            });
        }

        private void writeDisplayProperties(FriendlyByteBuf buf, T material) {
            boolean canWrite = material.displayProperties instanceof MaterialDisplay;
            buf.writeBoolean(canWrite);
            if (canWrite) {
                ((MaterialDisplay) material.displayProperties).toNetwork(buf);
            }
        }

        private void writeRestrictions(FriendlyByteBuf buffer, T material) {
            // Categories
            buffer.writeByte(material.categories.size());
            material.categories.forEach(cat -> buffer.writeUtf(cat.getName()));

            // Gear Type Blacklist
            buffer.writeByte(material.blacklistedGearTypes.size());
            material.blacklistedGearTypes.forEach(buffer::writeUtf);
        }

        private void writeStats(FriendlyByteBuf buffer, T material) {
            buffer.writeByte(material.stats.size());
            //noinspection OverlyLongLambda
            material.stats.forEach((partType, map) -> {
                buffer.writeResourceLocation(partType.getName());
                buffer.writeByte(map.size());
                map.forEach((key, mod) -> {
                    buffer.writeUtf(key.toString());
                    mod.write(buffer);
                });
            });
        }

        private void writeTraits(FriendlyByteBuf buffer, T material) {
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
