package net.silentchaos512.gear.gear.part;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.IPartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;

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
    Component displayName;
    @Nullable Component namePrefix = null;

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
    public Collection<StatInstance> getStatModifiers(IPartData part, PartType partType, StatGearKey key, ItemStack gear) {
        List<StatInstance> mods = new ArrayList<>(this.stats.get(key));
        GetStatModifierEvent event = new GetStatModifierEvent((PartData) part, (ItemStat) key.getStat(), mods);
        NeoForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public Collection<TraitInstance> getTraits(IPartData part, PartGearKey partKey, ItemStack gear) {
        return Collections.unmodifiableList(traits);
    }

    @Override
    public boolean isCraftingAllowed(IPartData part, PartType partType, GearType gearType, @Nullable Container inventory) {
        if (!gearType.matches(GearType.ALL)) return true;
        for (String blacklistedGearType : blacklistedGearTypes) {
            if (gearType.matches(blacklistedGearType)) {
                return false;
            }
        }
        return IGearPart.super.isCraftingAllowed(part, partType, gearType, inventory);
    }

    @Override
    public void onAddToGear(ItemStack gear, PartData part) {
        IGearPart.super.onAddToGear(gear, part);
        // Transfer durability from main parts
        if (part.getType() == PartType.MAIN) {
             gear.setDamageValue(part.getItem().getDamageValue());
        }
    }

    @Override
    public Component getDisplayName(@Nullable PartData part, ItemStack gear) {
        if (displayName == null) return Component.literal("<error: missing name>");
        return displayName.copy();
    }

    @Override
    public Component getDisplayName(@Nullable IPartData part, PartType type, ItemStack gear) {
        if (displayName == null) return Component.literal("<error: missing name>");
        return displayName.copy();
    }

    @Override
    public Component getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        return namePrefix != null ? namePrefix.copy() : null;
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    public void addInformation(PartData part, ItemStack gear, List<Component> tooltip, TooltipFlag flag) {}

    /**
     * List of blacklisted {@link GearType}s, mostly used for part tooltips. To know whether of not
     * a part may be used in crafting, use {@link #isCraftingAllowed(IPartData, PartType, GearType, Container)} instead.
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
                StatModifierMap statMap = StatModifierMap.deserialize(elementStats);
                // Move the newly loaded modifiers into the stat map, replacing existing ones
                statMap.keySet().forEach(key -> part.stats.removeAll(key));
                statMap.forEach((key, mod) -> part.stats.put(key, mod));
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
                part.ingredient = Ingredient.fromJson(craftingItem);
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
                part.tier = GsonHelper.getAsInt(obj, "tier", part.tier);
                part.visible = GsonHelper.getAsBoolean(obj, "visible", part.visible);

                JsonArray blacklist = getGearBlacklist(obj);
                if (blacklist != null) {
                    part.blacklistedGearTypes.clear();
                    blacklist.forEach(e -> part.blacklistedGearTypes.add(e.getAsString()));
                }
            }

            return part;
        }

        private static Component deserializeText(JsonElement json) {
            // Handle the old style
            if (json.isJsonObject() && json.getAsJsonObject().has("name")) {
                boolean translate = GsonHelper.getAsBoolean(json.getAsJsonObject(), "translate", false);
                String name = GsonHelper.getAsString(json.getAsJsonObject(), "name");
                return translate ? Component.translatable(name) : Component.literal(name);
            }

            // Deserialize use vanilla serializer
            return Objects.requireNonNull(Component.Serializer.fromJson(json));
        }

        @Nullable
        private static JsonArray getGearBlacklist(JsonObject json) {
            if (json.has("gear_blacklist"))
                return GsonHelper.getAsJsonArray(json, "gear_blacklist");
            else if (json.has("tool_blacklist"))
                return GsonHelper.getAsJsonArray(json, "tool_blacklist");
            return null;
        }

        @Override
        public T read(ResourceLocation id, FriendlyByteBuf buffer) {
            T part = function.apply(id);

            part.packName = buffer.readUtf();

            part.displayName = buffer.readComponent();
            if (buffer.readBoolean())
                part.namePrefix = buffer.readComponent();
            part.ingredient = Ingredient.fromNetwork(buffer);
            part.tier = buffer.readByte();
            part.visible = buffer.readBoolean();

            part.blacklistedGearTypes.clear();
            int blacklistSize = buffer.readByte();
            for (int i = 0; i < blacklistSize; ++i) {
                part.blacklistedGearTypes.add(buffer.readUtf());
            }

            // Stats and traits
            part.stats = StatModifierMap.read(buffer);
            readTraits(buffer, part);

            return part;
        }

        @Override
        public void write(FriendlyByteBuf buffer, T part) {
            buffer.writeUtf(part.packName);
            buffer.writeComponent(part.getDisplayName(null, ItemStack.EMPTY));
            buffer.writeBoolean(part.namePrefix != null);
            if (part.namePrefix != null)
                buffer.writeComponent(part.namePrefix);
            part.ingredient.toNetwork(buffer);
            buffer.writeByte(part.getTier());
            buffer.writeBoolean(part.visible);

            buffer.writeByte(part.blacklistedGearTypes.size());
            part.blacklistedGearTypes.forEach(buffer::writeUtf);

            // Stats and traits
            part.stats.write(buffer);
            writeTraits(buffer, part);
        }

        private void readTraits(FriendlyByteBuf buffer, T part) {
            part.traits.clear();
            int traitCount = buffer.readVarInt();
            for (int i = 0; i < traitCount; ++i) {
                part.traits.add(TraitInstance.read(buffer));
            }
        }

        private void writeTraits(FriendlyByteBuf buffer, T part) {
            buffer.writeVarInt(part.traits.size());
            part.traits.forEach(inst -> inst.write(buffer));
        }

        @Override
        public ResourceLocation getName() {
            return serializerId;
        }
    }
}
