package net.silentchaos512.gear.data.part;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.util.DataResource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PartBuilder {
    final ResourceLocation id;
    private IPartSerializer<?> serializerType = PartSerializers.COMPOUND_PART;
    private final GearType gearType;
    private final PartType partType;
    private final Ingredient ingredient;
    private ITextComponent name;
    @Nullable private ITextComponent namePrefix;
    @Nullable private GearTypeMatcher upgradeGearTypes;
    private final Map<GearType, MaterialLayerList> display = new LinkedHashMap<>();
    private final List<GearType> gearBlacklist = new ArrayList<>();

    private final StatModifierMap stats = new StatModifierMap();
    private final List<ITraitInstance> traits = new ArrayList<>();

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, IItemProvider item) {
        this(id, gearType, partType, Ingredient.fromItems(item));
    }

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, Tag<Item> tag) {
        this(id, gearType, partType, Ingredient.fromTag(tag));
    }

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, Ingredient ingredient) {
        this.gearType = gearType;
        this.partType = partType;
        this.id = id;
        this.ingredient = ingredient;
    }

    public PartBuilder serializerType(IPartSerializer<?> serializer) {
        this.serializerType = serializer;
        return this;
    }

    public PartBuilder visible(boolean visible) {
        return this;
    }

    public PartBuilder name(ITextComponent text) {
        this.name = text;
        return this;
    }

    public PartBuilder namePrefix(ITextComponent text) {
        this.namePrefix = text;
        return this;
    }

    public PartBuilder stat(IItemStat stat, float value) {
        return stat(stat, value, StatInstance.Operation.AVG);
    }

    public PartBuilder stat(IItemStat stat, float value, StatInstance.Operation operation) {
        StatInstance mod = StatInstance.of(value, operation);
        this.stats.put(stat, mod);
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

    public PartBuilder display(GearType gearType, MaterialLayer... layers) {
        return display(gearType, new MaterialLayerList(layers));
    }

    public PartBuilder display(GearType gearType, MaterialLayerList layers) {
        this.display.put(gearType, layers);
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

        json.add("crafting_item", this.ingredient.serialize());

        json.add("name", ITextComponent.Serializer.toJsonTree(this.name));

        if (this.namePrefix != null) {
            json.add("name_prefix", ITextComponent.Serializer.toJsonTree(this.namePrefix));
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
