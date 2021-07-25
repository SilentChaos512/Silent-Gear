package net.silentchaos512.gear.data.part;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.SetTag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearTypeMatcher;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.material.MaterialLayerList;
import net.silentchaos512.gear.api.part.IPartSerializer;
import net.silentchaos512.gear.api.part.PartDisplay;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.util.DataResource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class PartBuilder {
    final ResourceLocation id;
    private IPartSerializer<?> serializerType = PartSerializers.COMPOUND_PART;
    private final GearType gearType;
    private final PartType partType;
    private final Ingredient ingredient;
    private Component name;
    @Nullable private Component namePrefix;
    @Nullable private GearTypeMatcher upgradeGearTypes;
    private final Map<PartGearKey, MaterialLayerList> display = new LinkedHashMap<>();
    private final List<GearType> gearBlacklist = new ArrayList<>();

    private final StatModifierMap stats = new StatModifierMap();
    private final List<ITraitInstance> traits = new ArrayList<>();

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, ItemLike item) {
        this(id, gearType, partType, Ingredient.of(item));
    }

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, SetTag<Item> tag) {
        this(id, gearType, partType, Ingredient.of(tag));
    }

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, Ingredient ingredient) {
        this.gearType = gearType;
        this.partType = partType;
        this.id = id;
        this.ingredient = ingredient;

        if (!this.gearType.isGear()) {
            throw new IllegalArgumentException("Part gear type must extend GearType.ALL");
        }
    }

    public PartBuilder serializerType(IPartSerializer<?> serializer) {
        this.serializerType = serializer;
        return this;
    }

    public PartBuilder visible(boolean visible) {
        return this;
    }

    public PartBuilder name(Component text) {
        this.name = text;
        return this;
    }

    public PartBuilder namePrefix(Component text) {
        this.namePrefix = text;
        return this;
    }

    public PartBuilder stat(IItemStat stat, float value) {
        return stat(stat, value, StatInstance.Operation.AVG);
    }

    public PartBuilder stat(IItemStat stat, float value, StatInstance.Operation operation) {
        StatGearKey key = StatGearKey.of(stat, GearType.ALL);
        StatInstance mod = StatInstance.of(value, operation, key);
        this.stats.put(stat, GearType.ALL, mod);
        return this;
    }

    public PartBuilder trait(DataResource<ITrait> trait, int level, ITraitCondition... conditions) {
        ITraitInstance inst = TraitInstance.of(trait, level, conditions);
        this.traits.add(inst);
        return this;
    }

    public PartBuilder blacklistGearType(GearType type) {
        this.gearBlacklist.add(type);
        return this;
    }

    public PartBuilder upgradeGearTypes(GearTypeMatcher matcher) {
        this.upgradeGearTypes = matcher;
        return this;
    }

    public PartBuilder display(GearType gearType, PartType partType, MaterialLayer... layers) {
        return display(gearType, partType, new MaterialLayerList(layers));
    }

    public PartBuilder display(GearType gearType, PartType partType, MaterialLayerList layers) {
        this.display.put(PartGearKey.of(gearType, partType), layers);
        return this;
    }

    public JsonObject serializeModel() {
        return PartDisplay.of(this.display).serialize();
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.addProperty("type", this.serializerType.getName().toString());
        json.addProperty("gear_type", this.gearType.getName());
        json.addProperty("part_type", this.partType.getName().toString());

        JsonObject availability = new JsonObject();
        if (!this.gearBlacklist.isEmpty()) {
            JsonArray array = new JsonArray();
            this.gearBlacklist.forEach(gt -> array.add(gt.getName()));
            availability.add("gear_blacklist", array);
        }
        if (!availability.entrySet().isEmpty()) {
            json.add("availability", availability);
        }

        if (this.upgradeGearTypes != null) {
            json.add("gear_types", this.upgradeGearTypes.serialize());
        }

        json.add("crafting_item", this.ingredient.toJson());

        json.add("name", Component.Serializer.toJsonTree(this.name));

        if (this.namePrefix != null) {
            json.add("name_prefix", Component.Serializer.toJsonTree(this.namePrefix));
        }

        if (!this.stats.isEmpty()) {
            json.add("stats", this.stats.serialize());
        }

        if (!this.traits.isEmpty()) {
            JsonArray array = new JsonArray();
            this.traits.forEach(t -> array.add(t.serialize()));
            json.add("traits", array);
        }

        return json;
    }
}
