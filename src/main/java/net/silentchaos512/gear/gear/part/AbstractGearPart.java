package net.silentchaos512.gear.gear.part;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
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
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.util.GearData;

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
    String packName = "UNKNOWN PACK";
    // Crafting items
    Ingredient ingredient = Ingredient.EMPTY;
    // Availability
    boolean visible = true;
    int tier = -1;
    List<String> blacklistedGearTypes = new ArrayList<>();

    // Stats and Traits
    protected StatModifierMap stats = new StatModifierMap();
    List<TraitInstance> traits = new ArrayList<>();

    // Display
    ITextComponent displayName;
    @Nullable ITextComponent namePrefix = null;

    public AbstractGearPart(ResourceLocation location) {
        this.name = location;
    }

    @Override
    public ResourceLocation getId() {
        return name;
    }

    @Override
    public int getTier(PartData part) {
        return tier;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public String getPackName() {
        return packName;
    }

    public void updateCraftingItems(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartData part, ItemStack gear) {
        List<StatInstance> mods = new ArrayList<>(this.stats.get(stat));
        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, mods);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public List<TraitInstance> getTraits(PartData part, ItemStack gear) {
        return Collections.unmodifiableList(traits);
    }

    @Override
    public float getRepairAmount(RepairContext context) {
        PartData material = context.getMaterial();
        if (material.getType() != PartType.MAIN || !(context.getGear().getItem() instanceof ICoreItem))
            return 0;

        // Material tier must be equal to or higher than gear's primary
        if (material.getTier() < GearData.getTier(context.getGear())) return 0;

        // Base repair values on the appropriate durability stat
        ICoreItem gearItem = (ICoreItem) context.getGear().getItem();
        ItemStat durabilityStat = gearItem.getDurabilityStat();
        Collection<StatInstance> mods = getStatModifiers(durabilityStat, material, context.getGear());
        float durability = durabilityStat.compute(0f, mods);
        float multiplier = durabilityStat == ItemStats.ARMOR_DURABILITY ? 12f : 1f;

        switch (context.getRepairType()) {
            case QUICK:
                return Config.Common.repairFactorQuick.get().floatValue() * multiplier * durability;
            case ANVIL:
                return Config.Common.repairFactorAnvil.get().floatValue() * multiplier * durability;
            default:
                throw new IllegalArgumentException("Unknown RepairContext: " + context);
        }
    }

    @Override
    public boolean isCraftingAllowed(PartData part, GearType gearType, @Nullable CraftingInventory inventory) {
        if (!gearType.matches(GearType.ALL)) return true;
        return blacklistedGearTypes.stream().noneMatch(gearType::matches) && IGearPart.super.isCraftingAllowed(part, gearType, inventory);
    }

    @Override
    public ITextComponent getDisplayName(@Nullable PartData part, ItemStack gear) {
        if (displayName == null) return new StringTextComponent("<error: missing name>");
        return displayName.deepCopy();
    }

    @Override
    public ITextComponent getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        return namePrefix != null ? namePrefix.deepCopy() : null;
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    public void addInformation(PartData part, ItemStack gear, List<ITextComponent> tooltip, ITooltipFlag flag) {}

    /**
     * List of blacklisted {@link GearType}s, mostly used for part tooltips. To know whether of not
     * a part may be used in crafting, use {@link #isCraftingAllowed(PartData, GearType,
     * CraftingInventory)} instead.
     *
     * @return The List of GearTypes the part may not be used to craft (may be empty)
     */
    public List<GearType> getBlacklistedGearTypes() {
        return blacklistedGearTypes.stream()
                .map(GearType::get)
                .filter(GearType::isGear)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return "AbstractGearPart{" +
                "id=" + this.name +
                ", partType=" + this.getType() +
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
            return read(id, json, true);
        }

        @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
        protected T read(ResourceLocation id, JsonObject json, boolean failOnMissingElement) {
            T part = function.apply(id);

            // Stats
            JsonElement elementStats = json.get("stats");
            if (elementStats != null) {
                Multimap<IItemStat, StatInstance> statMap = StatModifierMap.deserialize(elementStats);
                // Move the newly loaded modifiers into the stat map, replacing existing ones
                statMap.keySet().forEach(stat -> part.stats.removeAll(stat));
                statMap.forEach((stat, mod) -> part.stats.put(stat, mod));
            }

            // Traits
            JsonElement elementTraits = json.get("traits");
            if (elementTraits != null && elementTraits.isJsonArray()) {
                JsonArray array = elementTraits.getAsJsonArray();
                Collection<TraitInstance> traitsList = new ArrayList<>();

                for (JsonElement element : array) {
                    traitsList.add(TraitInstance.deserialize(element.getAsJsonObject()));
                }

                if (!traitsList.isEmpty()) {
                    part.traits.clear();
                    part.traits.addAll(traitsList);
                }
            }

            // Crafting Items
            JsonElement craftingItem = json.get("crafting_item");
            if (craftingItem != null) {
                part.ingredient = Ingredient.deserialize(craftingItem);
            } else if (failOnMissingElement) {
                throw new JsonSyntaxException("Missing 'crafting_item'");
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
            } else if (failOnMissingElement) {
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
            return Objects.requireNonNull(ITextComponent.Serializer.func_240641_a_(json));
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

            part.packName = buffer.readString();

            part.displayName = buffer.readTextComponent();
            if (buffer.readBoolean())
                part.namePrefix = buffer.readTextComponent();
            part.ingredient = Ingredient.read(buffer);
            part.tier = buffer.readByte();
            part.visible = buffer.readBoolean();

            part.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                part.blacklistedGearTypes.add(buffer.readString());
            }

            // Stats and traits
            part.stats = StatModifierMap.read(buffer);
            readTraits(buffer, part);

            return part;
        }

        @Override
        public void write(PacketBuffer buffer, T part) {
            buffer.writeString(part.packName);
            buffer.writeTextComponent(part.getDisplayName(null, ItemStack.EMPTY));
            buffer.writeBoolean(part.namePrefix != null);
            if (part.namePrefix != null)
                buffer.writeTextComponent(part.namePrefix);
            part.ingredient.write(buffer);
            buffer.writeByte(part.getTier());
            buffer.writeBoolean(part.visible);

            buffer.writeByte(part.blacklistedGearTypes.size());
            part.blacklistedGearTypes.forEach(buffer::writeString);

            // Stats and traits
            part.stats.write(buffer);
            writeTraits(buffer, part);
        }

        private void readTraits(PacketBuffer buffer, T part) {
            part.traits.clear();
            int traitCount = buffer.readVarInt();
            for (int i = 0; i < traitCount; ++i) {
                part.traits.add(TraitInstance.read(buffer));
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
