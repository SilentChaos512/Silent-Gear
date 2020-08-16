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
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.stats.StatModifierMap;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.part.PartPositions;
import net.silentchaos512.gear.gear.part.CompoundPart;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PartBuilder {
    final ResourceLocation id;
    private final ResourceLocation serializerType;
    private final GearType gearType;
    private final PartType partType;
    private final PartPositions position;
    private final int tier = -1;
    private final Ingredient ingredient;
    private boolean visible = true;
    private ITextComponent name;
    @Nullable private ITextComponent namePrefix;

    private final StatModifierMap stats = new StatModifierMap();
    private final List<ITraitInstance> traits = new ArrayList<>();

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, PartPositions position, IItemProvider item) {
        this(id, gearType, partType, position, Ingredient.fromItems(item));
    }

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, PartPositions position, Tag<Item> tag) {
        this(id, gearType, partType, position, Ingredient.fromTag(tag));
    }

    public PartBuilder(ResourceLocation id, GearType gearType, PartType partType, PartPositions position, Ingredient ingredient) {
        this.serializerType = CompoundPart.SERIALIZER.getName();
        this.gearType = gearType;
        this.partType = partType;
        this.position = position;
        this.id = id;
        this.ingredient = ingredient;
    }

    public PartBuilder visible(boolean visible) {
        this.visible = visible;
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

    private PartBuilder trait(ResourceLocation traitId, int level, ITraitCondition... conditions) {
        ITraitInstance inst = TraitInstance.lazy(traitId, level, conditions);
        this.traits.add(inst);
        return this;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.addProperty("type", this.serializerType.toString());
        json.addProperty("gear_type", this.gearType.getName());
        json.addProperty("part_type", this.partType.getName().toString());
        json.addProperty("part_position", this.position.name());

        JsonObject availability = new JsonObject();
        if (this.tier >= 0) {
            availability.addProperty("tier", this.tier);
            availability.addProperty("visible", this.visible);
        }
        if (!availability.entrySet().isEmpty()) {
            json.add("availability", availability);
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
