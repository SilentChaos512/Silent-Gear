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
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractGearPart implements IGearPart {
    // Identity
    private final ResourceLocation name;
    PartMaterial materials = new PartMaterial();
    int tier = -1;

    // Stats and Traits
    StatModifierMap stats = new StatModifierMap();
    Map<ITrait, Integer> traits = new LinkedHashMap<>();

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
    public ResourceLocation getId() {
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
    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat, PartData part) {
        List<StatInstance> mods = new ArrayList<>(this.stats.get(stat));
        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public Map<ITrait, Integer> getTraits(ItemStack gear, PartData part) {
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
        if (material.getType() != PartType.MAIN) return 0;

        // Material tier must be equal to or higher than gear's primary
        if (material.getTier() < GearData.getTier(context.getGear())) return 0;
        Collection<StatInstance> mods = getStatModifiers(context.getGear(), CommonItemStats.DURABILITY, material);
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
            GearType gearType = ((ICoreItem) gear.getItem()).getGearType();

            // Gear class-specific override
            String typeName = gearType.getName();
            if (display.containsKey(typeName)) {
                return display.get(typeName);
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

    @Nullable
    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        String path = "item/" + gearClass + "/" + position.getTexturePrefix() + "_" + props.getTextureSuffix();
        return new ResourceLocation(props.getTextureDomain(), path);
    }

    @Nullable
    @Override
    public ResourceLocation getBrokenTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position) {
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
        if (displayName == null) return new StringTextComponent("<error: missing name>");
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

    @Override
    public String toString() {
        return "AbstractGearPart{" +
                this.name +
                "}";
    }

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
                    String name = JSONUtils.getString(obj, "name", "");
                    ItemStat stat = ItemStat.ALL_STATS.get(name);

                    if (stat != null) {
                        float value = JSONUtils.getFloat(obj, "value", 0f);
                        StatInstance.Operation op = obj.has("op")
                                ? StatInstance.Operation.byName(JSONUtils.getString(obj, "op"))
                                : part.getDefaultStatOperation(stat);
                        String statId = String.format("mat_%s_%s%d", part.getId(), stat.getName(),
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
                Map<ITrait, Integer> traitsMap = new HashMap<>();
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = JSONUtils.getString(obj, "name", "");
                    ITrait trait = TraitManager.get(name);

                    if (trait != null) {
                        int level = MathHelper.clamp(JSONUtils.getInt(obj, "level", 1), 1, trait.getMaxLevel());
                        if (level > 0) {
                            traitsMap.put(trait, level);
                            SilentGear.LOGGER.debug("Add trait {} level {} to part {}",
                                    trait.getId(), level, part.getId());
                        }
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
                final ResourceLocation itemName = new ResourceLocation(JSONUtils.getString(craftingNormal, "item"));
                part.materials.item = ForgeRegistries.ITEMS.getValue(itemName);
            }
            if (craftingNormal.has("tag")) {
                final ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(craftingNormal, "tag"));
                part.materials.tag = ItemTags.getCollection().getOrCreate(tagName);
            }
            // Small (optional)
            JsonObject craftingSmall = getOptionalObj(craftingItems, "small");
            if (craftingSmall != null) {
                if (!craftingSmall.has("item") && !craftingSmall.has("tag")) {
                    throw new JsonParseException("crafting_items.small must contain either 'item', 'tag', or both");
                }
                if (craftingSmall.has("item")) {
                    final ResourceLocation itemName = new ResourceLocation(JSONUtils.getString(craftingSmall, "item"));
                    part.materials.itemSmall = ForgeRegistries.ITEMS.getValue(itemName);
                }
                if (craftingSmall.has("tag")) {
                    final ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(craftingSmall, "tag"));
                    part.materials.tagSmall = ItemTags.getCollection().getOrCreate(tagName);
                }
            }

            // Name
            JsonElement elementName = json.get("name");
            if (elementName != null && elementName.isJsonObject()) {
                JsonObject obj = elementName.getAsJsonObject();
                boolean translate = JSONUtils.getBoolean(obj, "translate", false);
                String nameValue = JSONUtils.getString(obj, "name");
                if (translate) {
                    part.displayName = new TranslationTextComponent(nameValue);
                } else {
                    part.displayName = new StringTextComponent(nameValue);
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
                part.tier = JSONUtils.getInt(obj, "tier", part.tier);
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

            part.displayName = buffer.readTextComponent();
            part.materials = PartMaterial.read(buffer);
            part.tier = buffer.readByte();

            // Textures
            int displayCount = buffer.readVarInt();
            for (int i = 0; i < displayCount; ++i) {
                String key = buffer.readString(255);
                PartDisplay display = PartDisplay.read(buffer);
                part.display.put(key, display);
            }

            // Stats and traits
            part.stats = StatModifierMap.read(buffer);
            readTraits(buffer, part);

            return part;
        }

        @Override
        public void write(PacketBuffer buffer, T part) {
            buffer.writeTextComponent(part.getDisplayName(null, ItemStack.EMPTY));
            part.materials.write(buffer);
            buffer.writeByte(part.getTier());

            // Textures
            buffer.writeVarInt(part.display.size());
            part.display.forEach((s, partDisplay) -> {
                buffer.writeString(s);
                PartDisplay.write(buffer, partDisplay);
            });

            // Stats and traits
            part.stats.write(buffer);
            writeTraits(buffer, part);
        }

        private void readTraits(PacketBuffer buffer, T part) {
            part.traits.clear();
            int traitCount = buffer.readVarInt();
            for (int i = 0; i < traitCount; ++i) {
                ResourceLocation traitId = buffer.readResourceLocation();
                ITrait trait = TraitManager.get(traitId);
                int level = buffer.readByte();
                if (trait != null) {
                    part.traits.put(trait, level);
                } else {
                    SilentGear.LOGGER.warn("Read unknown trait from server: {}", traitId);
                }
            }
        }

        private void writeTraits(PacketBuffer buffer, T part) {
            buffer.writeVarInt(part.traits.size());
            part.traits.forEach((trait, level) -> {
                buffer.writeResourceLocation(trait.getId());
                buffer.writeByte(level);
            });
        }

        @Override
        public ResourceLocation getName() {
            return serializerId;
        }
    }
}
