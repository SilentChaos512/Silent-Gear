package net.silentchaos512.gear.api.parts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.UnsignedInts;
import com.google.gson.*;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.api.traits.TraitRegistry;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

// TODO: javadoc
@Getter(AccessLevel.PUBLIC)
public abstract class ItemPart {
    private static final Pattern REGEX_TEXTURE_SUFFIX_REPLACE = Pattern.compile("[a-z]+_");

    protected ResourceLocation registryName;

    public static final String NBT_KEY = "Key";
    protected static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "items/blank");
    private static final Gson GSON = (new GsonBuilder()).create();

    @Getter(AccessLevel.NONE) protected Supplier<ItemStack> craftingStack = () -> ItemStack.EMPTY;
    protected String craftingOreDictName = "";
    @Getter(AccessLevel.NONE) protected Supplier<ItemStack> craftingStackSmall = () -> ItemStack.EMPTY;
    protected String craftingOreDictNameSmall = "";
    protected int tier = 0;
    protected boolean enabled = true;
    protected boolean hidden = false;
    //    protected String textureDomain;
//    protected String textureSuffix;
//    @Getter(AccessLevel.NONE) protected Map<String, Integer> textureColor = new HashMap<>();
//    @Getter(AccessLevel.NONE) protected Map<String, Integer> brokenColor = new HashMap<>();
//    @Getter(AccessLevel.NONE) protected Map<String, Integer> fallbackColor = new HashMap<>();
    @Getter(AccessLevel.NONE) private Map<String, PartDisplayProperties> display = new HashMap<>();
    protected TextFormatting nameColor = TextFormatting.GRAY;
    protected String localizedNameOverride = "";
    private final PartOrigins origin;

    /**
     * Numerical index for model caching. This value could change any time the mod updates or new
     * materials are added, so don't use it for persistent data! Also good for identifying subtypes
     * in JEI.
     */
    @Getter(AccessLevel.NONE) protected int modelIndex;
    private static int lastModelIndex = -1;

    @Getter(AccessLevel.NONE) protected Multimap<ItemStat, StatInstance> stats = new StatModifierMap();
    @Getter(AccessLevel.NONE) protected Map<Trait, Integer> traits = new LinkedHashMap<>();

    public ItemPart(ResourceLocation registryName, PartOrigins origin) {
        this.registryName = registryName;
        String suffix = REGEX_TEXTURE_SUFFIX_REPLACE.matcher(registryName.getPath()).replaceFirst("");
        display.put("all", new PartDisplayProperties(registryName.getNamespace(), suffix));
        this.modelIndex = ++lastModelIndex;
        this.origin = origin;
        loadJsonResources();
    }

    // ===========================
    // = Stats and Miscellaneous =
    // ===========================

    public abstract PartType getType();

    public ItemStack getCraftingStack() {
        return craftingStack.get();
    }

    public ItemStack getCraftingStackSmall() {
        return craftingStackSmall.get();
    }

    public Collection<StatInstance> getStatModifiers(ItemStat stat, ItemPartData part) {
        List<StatInstance> mods = new ArrayList<>(this.stats.get(stat));
        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    public Map<Trait, Integer> getTraits(ItemPartData part) {
        return ImmutableMap.copyOf(traits);
    }

    /**
     * Default operation to use if the resource file does not specify on operation for the given
     * stat
     */
    public Operation getDefaultStatOperation(ItemStat stat) {
        return stat == CommonItemStats.HARVEST_LEVEL ? Operation.MAX : Operation.ADD;
    }

    @Deprecated
    public int getRepairAmount(ItemStack gear, ItemPartData part) {
        return (int) this.getRepairAmount(gear, part, RepairContext.QUICK);
    }

    public float getRepairAmount(ItemStack gear, ItemPartData part, RepairContext context) {
        // Base value on material durability
        ItemPartData gearPrimary = GearData.getPrimaryPart(gear);
        if (gearPrimary != null && part.part.tier < gearPrimary.part.tier) return 0;
        Collection<StatInstance> mods = getStatModifiers(CommonItemStats.DURABILITY, part);
        float durability = CommonItemStats.DURABILITY.compute(0f, mods);

        switch (context) {
            case QUICK:
                return Config.quickRepairFactor * durability;
            case ANVIL:
                return Config.anvilRepairFactor * durability;
            default:
                throw new IllegalArgumentException("Unknown RepairContext: " + context);
        }
    }

    public float computeStatValue(ItemStat stat) {
        return computeStatValue(stat, ItemPartData.instance(this));
    }

    public float computeStatValue(ItemStat stat, ItemPartData part) {
        return stat.compute(0, getStatModifiers(stat, part));
    }

    // ============
    // = Crafting =
    // ============

    public boolean matchesForCrafting(ItemStack partRep, boolean matchOreDict) {
        if (partRep.isEmpty())
            return false;
        if (!matchOreDict && partRep.isItemEqual(this.craftingStack.get()))
            return true;
        if (matchOreDict && !this.craftingOreDictName.isEmpty()) {
            for (int id : OreDictionary.getOreIDs(partRep)) {
                if (this.craftingOreDictName.equals(OreDictionary.getOreName(id))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the position the part occupies. Mainly used to prevent duplicate upgrades of one type
     * (tips, grips). Consider returning {@link PartPositions#ANY} if position is not relevant.
     *
     * @return The part position (never null)
     */
    public abstract IPartPosition getPartPosition();

    public boolean isBlacklisted() {
        return isBlacklisted(this.craftingStack.get());
    }

    public boolean isBlacklisted(ItemStack partRep) {
        return !this.enabled;
    }

    // ===================================
    // = Display (textures and tooltips) =
    // ===================================

    /**
     * Gets a texture to use based on the item class
     *
     * @param part           The part
     * @param gear           The equipment item (tool/weapon/armor)
     * @param gearClass      The gear class string (pickaxe/sword/etc.)
     * @param animationFrame Animation frame, usually 0
     */
    @Nullable
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        PartDisplayProperties props = getDisplayProperties(part, gear, animationFrame);
        String path = "items/" + gearClass + "/" + position.getTexturePrefix() + "_" + props.textureSuffix;
        return new ResourceLocation(props.textureDomain, path);
    }

    /**
     * Gets a texture to use for a broken item based on the item class
     *
     * @param part      The part
     * @param gear      The equipment item (tool/weapon/armor)
     * @param gearClass The gear class string (pickaxe/sword/etc.)
     */
    @Nullable
    public ResourceLocation getBrokenTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position) {
        return getTexture(part, gear, gearClass, position, 0);
    }

    /**
     * Used for model caching. Be sure to include the animation frame if it matters!
     */
    public String getModelIndex(ItemPartData part, int animationFrame) {
        return this.modelIndex + (animationFrame == 3 ? "_3" : "");
    }

    public int getColor(ItemPartData part, ItemStack gear, int animationFrame) {
        PartDisplayProperties props = getDisplayProperties(part, gear, animationFrame);

        if (!gear.isEmpty()) {
            if (GearHelper.isBroken(gear))
                return props.brokenColor;
            if (GearHelper.shouldUseFallbackColor(gear, part))
                return props.fallbackColor;
        }

        return props.textureColor;
    }

    public PartDisplayProperties getDisplayProperties(ItemPartData part, ItemStack gear, int animationFrame) {
        if (!gear.isEmpty()) {
            GearType gearType = gear.getItem() instanceof ToolHead
                    ? GearType.get(ToolHead.getToolClass(gear))
                    : ((ICoreItem) gear.getItem()).getGearType();
            if (gearType == null) return display.get("all"); // TODO: Remove this line when tool heads are gone

            // Gear class-specific override
            if (display.containsKey(gearType.getName())) {
                return display.get(gearType.getName());
            }
            // Parent type overrides, like "armor"
            for (String key : display.keySet()) {
                if (gearType.matches(key)) {
                    return display.get(key);
                }
            }
        }
        // Default
        return display.get("all");
    }

    /**
     * Adds information to the tooltip of an equipment item
     *
     * @param part    The part
     * @param gear    The equipment (tool/weapon/armor) stack
     * @param tooltip Current tooltip lines
     */
    public abstract void addInformation(ItemPartData part, ItemStack gear, World world, List<String> tooltip, boolean advanced);

    /**
     * Gets a translation key for the part
     */
    public String getTranslationKey(@Nullable ItemPartData part) {
        return String.format("material.%s.%s.name", this.registryName.getNamespace(), this.registryName.getPath());
    }

    /**
     * Gets a translated name for the part, suitable for display
     *
     * @param part The part
     * @param gear The equipment (tool/weapon/armor) stack
     * @deprecated Use {@link #getDisplayName(ItemPartData, ItemStack)} instead
     */
    @Deprecated
    public String getTranslatedName(@Nullable ItemPartData part, ItemStack gear) {
        if (!localizedNameOverride.isEmpty())
            return localizedNameOverride;
        return /* nameColor + */ SilentGear.i18n.translate(this.getTranslationKey(part));
    }

    /**
     * Gets a user-friendly name for use in tooltips.
     *
     * @param part The part data (or null if not available)
     * @param gear The gear item
     * @return A text component
     */
    public ITextComponent getDisplayName(@Nullable ItemPartData part, ItemStack gear) {
        if (!localizedNameOverride.isEmpty())
            return new TextComponentString(localizedNameOverride);
        return new TextComponentTranslation(getTranslationKey(part));
    }

    /**
     * Gets a string that represents the type of part (main, rod, tip, etc.) Used for localization
     * of part type/class, not the individual part.
     *
     * @deprecated Use {@link #getType()}, then {@link PartType#getName()}
     */
    @Deprecated
    public abstract String getTypeName();

    @Override
    public String toString() {
        String str = "ItemPart[" + this.getType().getDebugSymbol() + "]{";
        str += "Key: " + this.registryName + ", ";
        str += "Origin: " + this.origin + ", ";
        str += "CraftingItem: " + this.craftingStack.get() + ", ";
        str += "CraftingOreDict: '" + this.craftingOreDictName + "', ";
        str += "Tier: " + this.tier;
        str += "}";
        return str;
    }

    // ====================================
    // = Resource file and NBT management =
    // ====================================

    /**
     * Get the location of the resource file that contains material information
     */
    private String getResourceFileLocation() {
        return String.format("assets/%s/materials/%s.json", this.registryName.getNamespace(), this.registryName.getPath());
    }

    private void loadJsonResources() {
        // Main resource file in JAR
        String path = getResourceFileLocation();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (resourceAsStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"))) {
                readResourceFile(reader);
            } catch (Exception e) {
                SilentGear.log.warn("Error reading part file '{}'", path);
                SilentGear.log.catching(e);
            }
        } else if (origin.isBuiltin()) {
            SilentGear.log.error("ItemPart '{}' is missing its data file!", this.registryName);
        }

        // Override in config folder
        File file = new File(Config.INSTANCE.getDirectory().getPath(), "materials/" + this.registryName.getPath() + ".json");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            readResourceFile(reader);
        } catch (FileNotFoundException e) {
            // Ignore, overrides are not required
        } catch (Exception e) {
            SilentGear.log.warn("Error reading part override '{}'", file.getAbsolutePath());
            SilentGear.log.catching(e);
        }
    }

    /**
     * Loads material information from a JSON file. Does not handle file IO exceptions.
     */
    private void readResourceFile(BufferedReader reader) {
        JsonElement je = GSON.fromJson(reader, JsonElement.class);
        JsonObject json = je.getAsJsonObject();
        Loader.processJson(this, json);
    }

    public void writeToNBT(NBTTagCompound tags) {
        tags.setString("Key", this.registryName.toString());
    }

    @Nullable
    public static ItemPart fromNBT(NBTTagCompound tags) {
        String key = tags.getString(NBT_KEY);
        return PartRegistry.get(key);
    }

    public void postInitChecks() {
        // Crafting item missing? Try to acquire an item from the ore dictionary.
        if (getCraftingStack().isEmpty()) {
            ItemStack fromOredict = getCraftingItemFromOreDict();
            if (!fromOredict.isEmpty())
                craftingStack = () -> fromOredict;
            else
                SilentGear.log.error("Part \"{}\" ({}) has no crafting item.", this.registryName, this.origin);
        }

        // Confirm that add-ons are using correct origin (should be BUILTIN_ADDON, not BUILTIN_CORE)
        if (this.origin == PartOrigins.BUILTIN_CORE && !SilentGear.MOD_ID.equals(this.registryName.getNamespace()))
            throw new IllegalArgumentException(String.format("Part \"%s\" has origin %s, but should be %s",
                    this.registryName, PartOrigins.BUILTIN_CORE, PartOrigins.BUILTIN_ADDON));
    }

    @SuppressWarnings("MethodWithMultipleReturnPoints")
    private ItemStack getCraftingItemFromOreDict() {
        // Attempts to get a crafting item based on the part's oredict key.
        if (craftingOreDictName.isEmpty()) {
            SilentGear.log.error("No crafting item or ore dictionary key for part: {}", this);
            return ItemStack.EMPTY;
        }
        if (!OreDictionary.doesOreNameExist(craftingOreDictName)) {
            SilentGear.log.error("Ore dictionary key '{}' does not exist. Part: {}", craftingOreDictName, this);
            return ItemStack.EMPTY;
        }

        NonNullList<ItemStack> stacks = OreDictionary.getOres(craftingOreDictName, false);
        if (!stacks.isEmpty()) {
            ItemStack itemStack = stacks.get(0);
            SilentGear.log.debug("Acquire crafting item from oredict, item={}, part={}", itemStack, this);
            return itemStack;
        } else return ItemStack.EMPTY;
    }

    /**
     * Handles most aspects of loading part properties from their JSON file
     */
    private static class Loader {
        private static void processJson(ItemPart part, JsonObject json) {
            readStats(part, json);
            readTraits(part, json);
            readCraftingItems(part, json);
            readDisplayProperties(part, json);
            readAvailability(part, json);
        }

        private static void readStats(ItemPart part, JsonObject json) {
            JsonElement elementStats = json.get("stats");
            if (elementStats != null && elementStats.isJsonArray()) {
                JsonArray array = elementStats.getAsJsonArray();
                Multimap<ItemStat, StatInstance> statMap = new StatModifierMap();
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = JsonUtils.getString(obj, "name", "");
                    ItemStat stat = ItemStat.ALL_STATS.get(name);

                    if (stat != null) {
                        float value = JsonUtils.getFloat(obj, "value", 0f);
                        Operation op = obj.has("op") ? Operation.byName(JsonUtils.getString(obj, "op"))
                                : part.getDefaultStatOperation(stat);
                        String id = String.format("mat_%s_%s%d", part.getTranslationKey(null), stat.getName(),
                                statMap.get(stat).size() + 1);
                        statMap.put(stat, new StatInstance(id, value, op));
                    }
                }

                // Move the newly loaded modifiers into the stat map, replacing existing ones
                statMap.forEach((stat, instance) -> {
                    part.stats.removeAll(stat);
                    part.stats.put(stat, instance);
                });
            }
        }

        private static void readTraits(ItemPart part, JsonObject json) {
            JsonElement elementTraits = json.get("traits");
            if (elementTraits != null && elementTraits.isJsonArray()) {
                JsonArray array = elementTraits.getAsJsonArray();
                Map<Trait, Integer> traitsMap = new HashMap<>();
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = JsonUtils.getString(obj, "name", "");
                    Trait trait = TraitRegistry.get(name);

                    if (trait != null) {
                        int level = MathHelper.clamp(JsonUtils.getInt(obj, "level", 1), 1, trait.getMaxLevel());
                        if (level > 0)
                            traitsMap.put(trait, level);
                    }
                }

                if (!traitsMap.isEmpty()) {
                    part.traits.clear();
                    part.traits.putAll(traitsMap);
                }
            }
        }

        private static void readCraftingItems(ItemPart part, JsonObject json) {
            JsonElement elementCraftingItems = json.get("crafting_items");
            if (elementCraftingItems != null && elementCraftingItems.isJsonObject()) {
                JsonObject objTop = elementCraftingItems.getAsJsonObject();
                // Normal item (ingot, gem)
                if (objTop.has("normal") && objTop.get("normal").isJsonObject()) {
                    JsonObject obj = objTop.get("normal").getAsJsonObject();
                    part.craftingStack = readItemData(obj);
                    part.craftingOreDictName = JsonUtils.getString(obj, "oredict", part.craftingOreDictName);
                }
                // Small item (nugget, shard)
                if (objTop.has("small") && objTop.get("small").isJsonObject()) {
                    JsonObject obj = objTop.get("small").getAsJsonObject();
                    part.craftingStackSmall = readItemData(obj);
                    part.craftingOreDictNameSmall = JsonUtils.getString(obj, "oredict", part.craftingOreDictNameSmall);
                }
            }
        }

        private static void readDisplayProperties(ItemPart part, JsonObject json) {
            JsonElement elementDisplay = json.get("display");
            if (elementDisplay != null && elementDisplay.isJsonObject()) {
                JsonObject obj = elementDisplay.getAsJsonObject();
                PartDisplayProperties defaultProps = part.display.getOrDefault("all", PartDisplayProperties.DEFAULT);

                if (!part.display.containsKey("all")) {
                    part.display.put("all", defaultProps);
                }

                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();

                    if (value.isJsonObject()) {
                        JsonObject jsonObject = value.getAsJsonObject();
                        part.display.put(key, PartDisplayProperties.from(jsonObject, defaultProps));
                    }
                }

                part.hidden = JsonUtils.getBoolean(obj, "hidden", part.hidden);

                if (obj.has("name_color")) {
                    TextFormatting format = TextFormatting.getValueByName(JsonUtils.getString(obj, "name_color"));
                    part.nameColor = format != null ? format : part.nameColor;
                }

                if (obj.has("override_localization"))
                    part.localizedNameOverride = JsonUtils.getString(obj, "override_localization");
                else if (obj.has("override_translation"))
                    part.localizedNameOverride = JsonUtils.getString(obj, "override_translation");

                // Old properties

                if (obj.has("texture_color")) {
                    String str = JsonUtils.getString(obj, "texture_color");
                    defaultProps.textureColor = PartDisplayProperties.readColorCode(str);
                }
                if (obj.has("broken_color")) {
                    String str = JsonUtils.getString(obj, "broken_color");
                    defaultProps.brokenColor = PartDisplayProperties.readColorCode(str);
                }
                if (obj.has("fallback_color")) {
                    String str = JsonUtils.getString(obj, "fallback_color");
                    defaultProps.fallbackColor = PartDisplayProperties.readColorCode(str);
                }

                if (obj.has("texture_domain")) {
                    defaultProps.textureDomain = JsonUtils.getString(obj, "texture_domain");
                }
                if (obj.has("texture_suffix")) {
                    defaultProps.textureSuffix = JsonUtils.getString(obj, "texture_suffix");
                }
            }
        }

        private static void readAvailability(ItemPart part, JsonObject json) {
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();
                part.enabled = JsonUtils.getBoolean(obj, "enabled", part.enabled);
                part.tier = JsonUtils.getInt(obj, "tier", part.tier);
                // TODO: blacklist
            }
        }

        @Deprecated
        private static void readColorMap(Map<String, Integer> map, int fallback, JsonElement jsonElement) {
            if (jsonElement.isJsonPrimitive()) {
                int color = readColorCode(jsonElement.getAsString());
                map.put("all", color);
            } else if (jsonElement.isJsonObject()) {
                JsonObject json = jsonElement.getAsJsonObject();

                json.entrySet().forEach(entry -> {
                    int color = readColorCode(entry.getValue().getAsString());
                    map.put(entry.getKey(), color);
                });
            } else {
                SilentGear.log.error("Could not read color map, unknown element type: " + jsonElement);
            }

            if (!map.containsKey("all")) {
                map.put("all", fallback);
            }
        }

        @Deprecated
        private static int readColorCode(String str) {
            try {
                return UnsignedInts.parseUnsignedInt(str, 16);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return Color.VALUE_WHITE;
            }
        }

        /**
         * Parse ItemStack data from a JSON object
         */
        private static Supplier<ItemStack> readItemData(JsonObject json) {
            if (!json.has("item"))
                return () -> ItemStack.EMPTY;

            final String itemName = JsonUtils.getString(json, "item");
            final int meta = JsonUtils.getInt(json, "data", 0);
            // Item likely does not exist when the ItemPart is constructed, so we need to get it lazily
            return () -> {
                Item item = Item.getByNameOrId(itemName);
                return item == null ? ItemStack.EMPTY : new ItemStack(item, 1, meta);
            };
        }
    }
}
