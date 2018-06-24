package net.silentchaos512.gear.api.parts;

import com.google.common.collect.Multimap;
import com.google.common.primitives.UnsignedInts;
import com.google.gson.*;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatInstance.Operation;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.EquipmentHelper;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
public abstract class ItemPart {

    protected static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(SilentGear.MOD_ID, "items/blank");
    private static final Gson GSON = (new GsonBuilder()).create();

    protected ResourceLocation key;
    protected ItemStack craftingStack = ItemStack.EMPTY;
    protected String craftingOreDictName = "";
    protected ItemStack craftingStackSmall = ItemStack.EMPTY;
    protected String craftingOreDictNameSmall = "";
    protected int tier = 0;
    protected boolean enabled = true;
    protected boolean hidden = false;
    protected String textureSuffix;
    protected int textureColor = 0xFFFFFF;
    protected int brokenColor = 0xFFFFFF;
    // Unused?
    protected TextFormatting nameColor = TextFormatting.GRAY;

    /**
     * Numerical index for model caching. This value could change any time the mod updates or new materials are added, so
     * don't use it for persistent data! Also good for identifying subtypes in JEI.
     */
    @Getter(value = AccessLevel.NONE)
    protected int modelIndex;
    private static int lastModelIndex = -1;

    @Getter(value = AccessLevel.NONE)
    protected Multimap<ItemStat, StatInstance> stats = new StatModifierMap();

    public ItemPart(ResourceLocation resource) {
        this.key = resource;
        this.textureSuffix = resource.getResourcePath().replaceFirst("[a-z]+_", "");
        this.modelIndex = ++lastModelIndex;
    }

    public void init() {
        loadJsonResources();
    }

    // ===========================
    // = Stats and Miscellaneous =
    // ===========================

    public Collection<StatInstance> getStatModifiers(ItemStat stat, ItemStack partRep) {
        Collection<StatInstance> mods = new ArrayList<>(this.stats.get(stat));
        GetStatModifierEvent event = new GetStatModifierEvent(stat, mods, partRep);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    /**
     * Default operation to use if the resource file does not specify on operation for the given stat
     */
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        if (stat == CommonItemStats.HARVEST_LEVEL)
            return StatInstance.Operation.MAX;
        else if (this instanceof ItemPartMain)
            return StatInstance.Operation.AVG;
        else if (stat == CommonItemStats.ATTACK_SPEED || stat == CommonItemStats.RARITY)
            return StatInstance.Operation.ADD;
        else if (this instanceof ToolPartRod)
            return StatInstance.Operation.MUL2;
        else if (this instanceof ToolPartTip)
            return StatInstance.Operation.ADD;

        // TODO
        return StatInstance.Operation.ADD;
    }

    public int getRepairAmount(ItemStack equipment, ItemPartData part) {
        // TODO
        // float durability = part.getStatModifier(CommonItemStats.DURABILITY).getValue();
        // return (int) (durability / 2);
        return 1;
    }

    // ============
    // = Crafting =
    // ============

    public boolean matchesForCrafting(ItemStack partRep, boolean matchOreDict) {
        if (StackHelper.isEmpty(partRep))
            return false;
        if (partRep.isItemEqual(this.craftingStack))
            return true;
        if (matchOreDict)
            return StackHelper.matchesOreDict(partRep, this.craftingOreDictName);
        return false;
    }

    public boolean matchesForDecorating(ItemStack partRep, boolean matchOreDict) {
        if (!craftingOreDictName.isEmpty()) {
            String nuggetName = craftingOreDictName.replaceFirst("gem|ingot", "nugget");
            // TODO?
        }
        return matchesForCrafting(partRep, matchOreDict);
    }

    public boolean isBlacklisted() {
        return isBlacklisted(this.craftingStack);
    }

    public boolean isBlacklisted(ItemStack partStack) {
        return !this.enabled;
    }

    // ===================================
    // = Display (textures and tooltips) =
    // ===================================

    /**
     * Gets a texture to use based on the item class
     *
     * @param equipment The equipment item (tool/weapon/armor)
     * @param toolClass The tool class string (pickaxe/sword/etc.)
     */
    public abstract ResourceLocation getTexture(ItemStack equipment, String toolClass, int animationFrame);

    public ResourceLocation getTexture(ItemStack equipment, String toolClass) {
        return getTexture(equipment, toolClass, 0);
    }

    /**
     * Gets a texture to use for a broken item based on the item class
     *
     * @param equipment The equipment item (tool/weapon/armor)
     * @param toolClass The tool class string (pickaxe/sword/etc.)
     */
    public ResourceLocation getBrokenTexture(ItemStack equipment, String toolClass) {
        return getTexture(equipment, toolClass);
    }

    /**
     * Used for model caching. Be sure to include the animation frame if it matters!
     */
    public String getModelIndex(int animationFrame) {
        return this.modelIndex + (animationFrame == 3 ? "_3" : "");
    }

    public int getColor(ItemStack equipment, int animationFrame) {
        if (!equipment.isEmpty() && EquipmentHelper.isBroken(equipment))
            return this.brokenColor;
        return this.textureColor;
    }

    /**
     * Adds information to the tooltip of an equipment item
     *
     * @param data      The data of the part
     * @param equipment The equipment (tool/weapon/armor) stack
     * @param tooltip   Current tooltip lines
     */
    public void addInformation(ItemPartData data, ItemStack equipment, World world, List<String> tooltip, boolean advanced) {
    }

    /**
     * Gets a key suitable for localization
     */
    public String getUnlocalizedName() {
        return "material." + this.key.toString() + ".name";
    }

    /**
     * Gets a localized name for the part, suitable for display
     *
     * @param data      The data of the part
     * @param equipment The equipment (tool/weapon/armor) stack
     */
    public String getLocalizedName(ItemPartData data, ItemStack equipment) {
        return /* nameColor + */ SilentGear.localization.getLocalizedString(getUnlocalizedName());
    }

    /**
     * Gets a string that represents the type of part (main, rod, tip, etc.)
     */
    public abstract String getTypeName();

    @Override
    public String toString() {
        // TODO: Update ItemPart#toString
        String str = "ItemPart{";
        str += "Key: " + this.key + ", ";
        str += "CraftingStack: " + this.craftingStack + ", ";
        str += "CraftingOreDictName: '" + this.craftingOreDictName + "', ";
        // str += "Tier: " + getTier();
        str += "}";
        return str;
    }

    // ====================================
    // = Resource file and NBT management =
    // ====================================

    /**
     * Get the location of the resource file that contains material information
     */
    protected String getResourceFileLocation() {
        return "assets/" + this.key.getResourceDomain() + "/materials/" + this.key.getResourcePath() + ".json";
    }

    private void loadJsonResources() {
        // Main resource file in JAR
        String path = getResourceFileLocation();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), "UTF-8"))) {
            readResourceFile(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Override in config folder
        File file = new File(Config.INSTANCE.getDirectory().getPath(), "materials/" + this.key.getResourcePath() + ".json");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            readResourceFile(reader);
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads material information from a JSON file. Does not handle file IO exceptions.
     */
    private void readResourceFile(BufferedReader reader) {
        JsonElement je = GSON.fromJson(reader, JsonElement.class);
        JsonObject json = je.getAsJsonObject();
        processJson(json);
    }

    /**
     * Process the JSON from a loaded resource file. Override if you need to load extra data.
     *
     * @param json The root JsonObject from the current file
     */
    protected void processJson(JsonObject json) {
        // Read stats
        JsonElement elementStats = json.get("stats");
        if (elementStats.isJsonArray()) {
            JsonArray array = elementStats.getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.has("name") ? JsonUtils.getString(obj, "name") : "";
                ItemStat stat = ItemStat.ALL_STATS.get(name);

                if (stat != null) {
                    float value = obj.has("value") ? JsonUtils.getFloat(obj, "value") : 0f;
                    Operation op = obj.has("op") ? Operation.byName(JsonUtils.getString(obj, "op")) : getDefaultStatOperation(stat);
                    String id = "mat_" + this.getUnlocalizedName() + "_" + stat.getUnlocalizedName() + (this.stats.get(stat).size() + 1);
                    this.stats.put(stat, new StatInstance(id, value, op));
                }
            }
        }

        // Read crafting item data
        JsonElement elementCraftingItems = json.get("crafting_items");
        if (elementCraftingItems.isJsonObject()) {
            JsonObject objTop = elementCraftingItems.getAsJsonObject();
            // Normal item (ingot, gem)
            if (objTop.has("normal") && objTop.get("normal").isJsonObject()) {
                JsonObject obj = objTop.get("normal").getAsJsonObject();
                craftingStack = readItemData(obj);
                if (obj.has("oredict"))
                    craftingOreDictName = JsonUtils.getString(obj, "oredict");
            }
            // Small item (nugget, shard)
            if (objTop.has("small") && objTop.get("small").isJsonObject()) {
                JsonObject obj = objTop.get("small").getAsJsonObject();
                craftingStackSmall = readItemData(obj);
                if (obj.has("oredict"))
                    craftingOreDictNameSmall = JsonUtils.getString(obj, "oredict");
            }
        }

        // Display properties
        JsonElement elementDisplay = json.get("display");
        if (elementDisplay.isJsonObject()) {
            JsonObject obj = elementDisplay.getAsJsonObject();
        if (obj.has("hidden"))
            this.hidden = JsonUtils.getBoolean(obj, "hidden");
        if (obj.has("texture_color"))
            this.textureColor = readColorCode(JsonUtils.getString(obj, "texture_color"));
        if (obj.has("broken_color"))
            this.brokenColor = readColorCode(JsonUtils.getString(obj, "broken_color"));
        if (obj.has("name_color"))
            this.nameColor = TextFormatting.getValueByName(obj.get("name_color").getAsString());
    }

    // Availability (enabled, tier, blacklisting)
    JsonElement elementAvailability = json.get("availability");
        if (elementAvailability.isJsonObject()) {
        JsonObject obj = elementAvailability.getAsJsonObject();
        this.enabled = obj.has("enabled") ? JsonUtils.getBoolean(obj, "enabled") : this.enabled;
        this.tier = obj.has("tier") ? JsonUtils.getInt(obj, "tier") : this.tier;
        // TODO: blacklist
    }
}

    protected int readColorCode(String str) {
        try {
            return UnsignedInts.parseUnsignedInt(str, 16);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 0xFFFFFF;
        }
    }

    /**
     * Parse ItemStack data from a JSON object
     */
    protected ItemStack readItemData(JsonObject json) {
        if (!json.has("item"))
            return ItemStack.EMPTY;

        String itemName = JsonUtils.getString(json, "item");
        Item item = Item.getByNameOrId(itemName);
        if (item == null)
            return ItemStack.EMPTY;
        int meta = json.has("data") ? JsonUtils.getInt(json, "data") : 0;

        return new ItemStack(item, 1, meta);
    }

    public void writeToNBT(@Nonnull NBTTagCompound tags) {
        tags.setString("Key", this.key.toString());
    }

    @Nullable
    public static ItemPart fromNBT(@Nonnull NBTTagCompound tags) {
        String key = tags.getString("Key");
        return PartRegistry.get(key);
    }
}
