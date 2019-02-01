package net.silentchaos512.gear.parts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.api.traits.TraitRegistry;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractGearPart implements IGearPart {
    // Identity
    private final ResourceLocation name;
    PartMaterial materials = new PartMaterial();
    int tier;

    // Stats and Traits
    Multimap<ItemStat, StatInstance> stats = new StatModifierMap();
    Map<Trait, Integer> traits = new LinkedHashMap<>();

    // Display
    ITextComponent displayName;
    final Map<String, PartDisplay> display = new HashMap<>();
    // Model index: re-evaluate
    int modelIndex;
    private static int lastModelIndex;

    public AbstractGearPart(ResourceLocation location) {
        this.name = location;
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public IPartMaterial getMaterials() {
        return materials;
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartData part) {
        List<StatInstance> mods = new ArrayList<>(this.stats.get(stat));
        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public Map<Trait, Integer> getTraits(PartData part) {
        return ImmutableMap.copyOf(traits);
    }

    @Override
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        return stat == CommonItemStats.HARVEST_LEVEL ? StatInstance.Operation.MAX : StatInstance.Operation.ADD;
    }

    @Override
    public float getRepairAmount(RepairContext context) {
        // Base value on material durability
        PartData material = context.getMaterial();
        PartData gearPrimary = GearData.getPrimaryPart(context.getGear());
        // Material tier must be equal to or higher than gear's primary
        if (gearPrimary != null && material.getTier() < gearPrimary.getTier()) return 0;
        Collection<StatInstance> mods = getStatModifiers(CommonItemStats.DURABILITY, material);
        float durability = CommonItemStats.DURABILITY.compute(0f, mods);

        switch (context.getRepairType()) {
            case QUICK:
                return Config.GENERAL.repairFactorQuick.get().floatValue() * durability;
            case ANVIL:
                return Config.GENERAL.repairFactorAnvil.get().floatValue() * durability;
            default:
                throw new IllegalArgumentException("Unknown RepairContext: " + context);
        }
    }

    @Override
    public IPartDisplay getDisplayProperties(PartData part, ItemStack gear, int animationFrame) {
        if (!gear.isEmpty()) {
            String gearType = Objects.requireNonNull(gear.getItem().getRegistryName()).getPath();

            // Gear class-specific override
            if (display.containsKey(gearType))
                return display.get(gearType);
            // Armor override
            if (gear.getItem() instanceof ICoreArmor && display.containsKey("armor"))
                return display.get("armor");
        }
        // Default
        return display.get("all");
    }

    @Nullable
    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        String path = "item/" + gearClass + "/" + position.getTexturePrefix() + "_" + props.getTextureSuffix();
        return new ResourceLocation(props.getTextureDomain(), path);
    }

    @Nullable
    @Override
    public ResourceLocation getBrokenTexture(PartData part, ItemStack gear, String gearClass, IPartPosition position) {
        return getTexture(part, gear, gearClass, position, 0);
    }

    @Override
    public int getColor(PartData part, ItemStack gear, int animationFrame) {
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        if (!gear.isEmpty()) {
            if (GearHelper.isBroken(gear))
                return props.getBrokenColor();
            if (GearHelper.shouldUseFallbackColor(gear, part))
                return props.getFallbackColor();
        }
        return props.getNormalColor();
    }

    @Override
    public ITextComponent getDisplayName(@Nullable PartData part, ItemStack gear) {
        if (displayName == null) return new TextComponentString("<error: missing name>");
        return displayName;
    }

    public int getModelIndex() {
        return modelIndex;
    }

    @Override
    public String getModelIndex(PartData part, int animationFrame) {
        return this.modelIndex + (animationFrame == 3 ? "_3" : "");
    }

    @Override
    public void addInformation(PartData part, ItemStack gear, List<ITextComponent> tooltip, ITooltipFlag flag) { }

    public static class Serializer<T extends AbstractGearPart> implements IPartSerializer<T> {
        private final ResourceLocation serializerId;
        private final Function<ResourceLocation, T> function;

        public Serializer(ResourceLocation serializerId, Function<ResourceLocation, T> function) {
            this.serializerId = serializerId;
            this.function = function;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            T part = function.apply(id);

            // Stats
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
                        StatInstance.Operation op = obj.has("op")
                                ? StatInstance.Operation.byName(JsonUtils.getString(obj, "op"))
                                : part.getDefaultStatOperation(stat);
                        String statId = String.format("mat_%s_%s%d", part.getName(), stat.getName(),
                                statMap.get(stat).size() + 1);
                        statMap.put(stat, new StatInstance(statId, value, op));
                    }
                }

                // Move the newly loaded modifiers into the stat map, replacing existing ones
                statMap.forEach((stat, instance) -> {
                    part.stats.removeAll(stat);
                    part.stats.put(stat, instance);
                });
            }

            // Traits
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

            // Crafting Items
            JsonObject craftingItems = getRequiredObj(json, "crafting_items");
            JsonObject craftingNormal = getRequiredObj(craftingItems, "normal");
            // Normal (required)
            if (!craftingNormal.has("item") && !craftingNormal.has("tag")) {
                throw new JsonParseException("crafting_items.normal must contain either 'item', 'tag', or both");
            }
            if (craftingNormal.has("item")) {
                final ResourceLocation itemName = new ResourceLocation(JsonUtils.getString(craftingNormal, "item"));
                part.materials.item = ForgeRegistries.ITEMS.getValue(itemName);
            }
            if (craftingNormal.has("tag")) {
                final ResourceLocation tagName = new ResourceLocation(JsonUtils.getString(craftingNormal, "tag"));
                part.materials.tag = ItemTags.getCollection().getOrCreate(tagName);
            }
            // Small (optional)
            JsonObject craftingSmall = getOptionalObj(craftingItems, "small");
            if (craftingSmall != null) {
                if (!craftingSmall.has("item") && !craftingSmall.has("tag")) {
                    throw new JsonParseException("crafting_items.small must contain either 'item', 'tag', or both");
                }
                if (craftingSmall.has("item")) {
                    final ResourceLocation itemName = new ResourceLocation(JsonUtils.getString(craftingSmall, "item"));
                    part.materials.itemSmall = ForgeRegistries.ITEMS.getValue(itemName);
                }
                if (craftingSmall.has("tag")) {
                    final ResourceLocation tagName = new ResourceLocation(JsonUtils.getString(craftingSmall, "tag"));
                    part.materials.tagSmall = ItemTags.getCollection().getOrCreate(tagName);
                }
            }

            // Name
            JsonElement elementName = json.get("name");
            if (elementName != null && elementName.isJsonObject()) {
                JsonObject obj = elementName.getAsJsonObject();
                boolean translate = JsonUtils.getBoolean(obj, "translate", false);
                String nameValue = JsonUtils.getString(obj, "name");
                if (translate) {
                    part.displayName = new TextComponentTranslation(nameValue);
                } else {
                    part.displayName = new TextComponentString(nameValue);
                }
            } else if (elementName != null) {
                throw new JsonParseException("Expected 'name' to be an object");
            }

            // Textures
            JsonElement elementDisplay = json.get("textures");
            if (elementDisplay != null && elementDisplay.isJsonObject()) {
                JsonObject obj = elementDisplay.getAsJsonObject();
                PartDisplay defaultProps = part.display.getOrDefault("all", PartDisplay.DEFAULT);

                if (!part.display.containsKey("all")) {
                    part.display.put("all", defaultProps);
                }

                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();

                    if (value.isJsonObject()) {
                        JsonObject jsonObject = value.getAsJsonObject();
                        part.display.put(key, PartDisplay.from(jsonObject, defaultProps));
                    }
                }
            }

            // Availability
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();
                part.tier = JsonUtils.getInt(obj, "tier", part.tier);
            }

            return part;
        }

        private static JsonObject getRequiredObj(JsonObject json, String name) {
            if (!json.has(name))
                throw new JsonParseException("Expected required object '" + name + "', found none");
            JsonElement elem = json.get(name);
            if (!elem.isJsonObject())
                throw new JsonParseException("Expected '" + name + "' to be an object");
            return elem.getAsJsonObject();
        }

        @Nullable
        private static JsonObject getOptionalObj(JsonObject json, String name) {
            if (!json.has(name)) return null;
            JsonElement elem = json.get(name);
            if (!elem.isJsonObject())
                throw new JsonParseException("Expected '" + name + "' to be an object");
            return elem.getAsJsonObject();
        }

        @Override
        public T read(ResourceLocation id, PacketBuffer buffer) {
            T part = function.apply(id);

            // Name
            part.displayName = buffer.readTextComponent();

            // Textures
            int displayCount = buffer.readVarInt();
            for (int i = 0; i < displayCount; ++i) {
                String key = buffer.readString(255);
                PartDisplay display = PartDisplay.read(buffer);
                part.display.put(key, display);
            }

            return part;
        }

        @Override
        public void write(PacketBuffer buffer, T part) {
            // Name
            buffer.writeTextComponent(part.getDisplayName(null, ItemStack.EMPTY));

            // Textures
            buffer.writeVarInt(part.display.size());
            part.display.forEach((s, partDisplay) -> {
                buffer.writeString(s);
                PartDisplay.write(buffer, partDisplay);
            });
        }

        @Override
        public ResourceLocation getName() {
            return serializerId;
        }
    }
}
