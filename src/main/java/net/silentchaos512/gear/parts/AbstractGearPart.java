package net.silentchaos512.gear.parts;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base class for all gear parts. Mods should prefer to extend this instead of just implementing
 * {@link IGearPart}.
 */
public abstract class AbstractGearPart implements IGearPart {
    // Identity
    // Part ID
    private final ResourceLocation name;
    // Crafting items
    PartMaterial materials = new PartMaterial();
    // Availability
    boolean visible = true;
    int tier = -1;
    List<String> blacklistedGearTypes = new ArrayList<>();

    // Stats and Traits
    StatModifierMap stats = new StatModifierMap();
    List<PartTraitInstance> traits = new ArrayList<>();

    // Display
    ITextComponent displayName;
    @Nullable ITextComponent namePrefix = null;
    // Textures, colors, etc.
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
    public void retainData(@Nullable IGearPart oldPart) {
        if (oldPart instanceof AbstractGearPart) {
            // Copy trait instances, the client doesn't need to know conditions
            this.traits.clear();
            this.traits.addAll(((AbstractGearPart) oldPart).traits);
        }
    }

    public void updateCraftingItems(PartMaterial material) {
        this.materials = material;
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat, PartData part) {
        List<StatInstance> mods = new ArrayList<>(this.stats.get(stat));
        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public List<PartTraitInstance> getTraits(ItemStack gear, PartData part) {
        return Collections.unmodifiableList(traits);
    }

    @Override
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        return stat == ItemStats.HARVEST_LEVEL ? StatInstance.Operation.MAX : StatInstance.Operation.ADD;
    }

    @Override
    public float getRepairAmount(RepairContext context) {
        // Base value on material durability
        PartData material = context.getMaterial();
        if (material.getType() != PartType.MAIN) return 0;

        // Material tier must be equal to or higher than gear's primary
        if (material.getTier() < GearData.getTier(context.getGear())) return 0;
        Collection<StatInstance> mods = getStatModifiers(context.getGear(), ItemStats.DURABILITY, material);
        float durability = ItemStats.DURABILITY.compute(0f, mods);

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
    public boolean isCraftingAllowed(@Nullable GearType gearType) {
        if (gearType == null) return true;
        return blacklistedGearTypes.stream().noneMatch(gearType::matches) && IGearPart.super.isCraftingAllowed(gearType);
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
        return display.getOrDefault("all", PartDisplay.DEFAULT);
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

    @Override
    public ITextComponent getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        return namePrefix;
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

    /**
     * List of blacklisted {@link GearType}s, mostly used for part tooltips. To know whether of not
     * a part may be used in crafting, use {@link #isCraftingAllowed(GearType)} instead.
     *
     * @return The List of GearTypes the part may not be used to craft (may be empty)
     */
    public List<GearType> getBlacklistedGearTypes() {
        return blacklistedGearTypes.stream()
                .map(GearType::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

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
                Collection<PartTraitInstance> traitsList = new ArrayList<>();

                for (JsonElement element : array) {
                    traitsList.add(PartTraitInstance.deserialize(element.getAsJsonObject()));
                }

                if (!traitsList.isEmpty()) {
                    part.traits.clear();
                    part.traits.addAll(traitsList);
                }
            }

            // Crafting Items
            JsonElement craftingItems = json.get("crafting_items");
            if (craftingItems != null && craftingItems.isJsonObject()) {
                part.materials = PartMaterial.deserialize(id, craftingItems.getAsJsonObject());
            } else {
                throw new JsonSyntaxException("Expected 'crafting_items' to be an object");
            }

            // Name
            JsonElement elementName = json.get("name");
            if (elementName != null && elementName.isJsonObject()) {
                part.displayName = deserializeText(elementName);
            } else {
                throw new JsonSyntaxException("Expected 'name' element");
            }

            // Name Prefix
            JsonElement elementNamePrefix = json.get("name_prefix");
            if (elementNamePrefix != null) {
                part.namePrefix = deserializeText(elementNamePrefix);
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
            } else {
                throw new JsonSyntaxException("Expected 'textures' to be an object");
            }

            // Availability
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();
                part.tier = JSONUtils.getInt(obj, "tier", part.tier);
                part.visible = JSONUtils.getBoolean(obj, "visible", part.visible);

                JsonArray blacklist = getGearBlacklist(obj);
                if (blacklist != null) {
                    part.blacklistedGearTypes.clear();
                    blacklist.forEach(e -> part.blacklistedGearTypes.add(e.getAsString()));
                }
            } else {
                throw new JsonSyntaxException("Expected 'availability' to be an object");
            }

            return part;
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

        @Override
        public T read(ResourceLocation id, PacketBuffer buffer) {
            T part = function.apply(id);

            part.displayName = buffer.readTextComponent();
            if (buffer.readBoolean())
                part.namePrefix = buffer.readTextComponent();
            part.materials = PartMaterial.read(buffer);
            part.tier = buffer.readByte();
            part.visible = buffer.readBoolean();

            part.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                part.blacklistedGearTypes.add(buffer.readString());
            }

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
            buffer.writeBoolean(part.namePrefix != null);
            if (part.namePrefix != null)
                buffer.writeTextComponent(part.namePrefix);
            part.materials.write(buffer);
            buffer.writeByte(part.getTier());
            buffer.writeBoolean(part.visible);

            buffer.writeByte(part.blacklistedGearTypes.size());
            part.blacklistedGearTypes.forEach(buffer::writeString);

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
                part.traits.add(PartTraitInstance.read(buffer));
            }
        }

        private void writeTraits(PacketBuffer buffer, T part) {
            buffer.writeVarInt(part.traits.size());
            part.traits.forEach(inst -> inst.write(buffer));
        }

        @Override
        public ResourceLocation getName() {
            return serializerId;
        }
    }
}
