package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.gear.trait.SimpleTrait;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.gear.trait.condition.GearTypeTraitCondition;
import net.silentchaos512.gear.gear.trait.condition.OrTraitCondition;
import net.silentchaos512.gear.util.DataResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class TraitBuilder {
    protected final ResourceLocation traitId;
    protected final int maxLevel;
    protected final ResourceLocation type;

    private ITextComponent name;
    private ITextComponent description;

    private final Collection<ITraitCondition> conditions = new ArrayList<>();
    private final Collection<ResourceLocation> cancelsList = new ArrayList<>();
    private final Collection<ResourceLocation> overridesList = new ArrayList<>();
    private final Collection<ITextComponent> extraWikiLines = new ArrayList<>();

    private Consumer<JsonObject> extraData = json -> {};

    public TraitBuilder(DataResource<ITrait> trait, int maxLevel, ITraitSerializer<?> serializer) {
        this(trait.getId(), maxLevel, serializer);
    }

    public TraitBuilder(ResourceLocation traitId, int maxLevel, ITraitSerializer<?> serializer) {
        this.traitId = traitId;
        this.type = serializer.getName();
        this.maxLevel = maxLevel;

        this.name = new TranslationTextComponent(Util.makeDescriptionId("trait", traitId));
        this.description = new TranslationTextComponent(Util.makeDescriptionId("trait", traitId) + ".desc");
    }

    public static TraitBuilder simple(DataResource<ITrait> trait, int maxLevel) {
        return simple(trait.getId(), maxLevel);
    }

    public static TraitBuilder simple(ResourceLocation traitId, int maxLevel) {
        return new TraitBuilder(traitId, maxLevel, SimpleTrait.SERIALIZER);
    }

    public TraitBuilder setName(ITextComponent text) {
        this.name = text;
        return this;
    }

    public TraitBuilder setDescription(ITextComponent text) {
        this.description = text;
        return this;
    }

    public TraitBuilder withConditions(ITraitCondition... conditions) {
        Collections.addAll(this.conditions, conditions);
        return this;
    }

    public TraitBuilder withGearTypeCondition(GearType first, GearType... rest) {
        if (rest.length > 0) {
            Collection<GearType> types = new ArrayList<>(rest.length + 1);
            types.add(first);
            Collections.addAll(types, rest);

            GearTypeTraitCondition[] values = types.stream()
                    .map(GearTypeTraitCondition::new)
                    .toArray(GearTypeTraitCondition[]::new);

            return withConditions(new OrTraitCondition(values));
        }
        return withConditions(new GearTypeTraitCondition(first));
    }

    public TraitBuilder cancelsWith(DataResource<ITrait> trait) {
        return cancelsWith(trait.getId());
    }

    public TraitBuilder cancelsWith(ResourceLocation trait) {
        this.cancelsList.add(trait);
        return this;
    }

    public TraitBuilder overridesTrait(DataResource<ITrait> trait) {
        return overridesTrait(trait.getId());
    }

    public TraitBuilder overridesTrait(ResourceLocation trait) {
        this.overridesList.add(trait);
        return this;
    }

    public TraitBuilder extraWikiLines(String... lines) {
        for (String line : lines) {
            this.extraWikiLines.add(new StringTextComponent(line));
        }
        return this;
    }

    public TraitBuilder extraWikiLines(ITextComponent... lines) {
        this.extraWikiLines.addAll(Arrays.asList(lines));
        return this;
    }

    /**
     * The "I don't feel like extending the class for this basic trait" method. Use it to append
     * small amounts of extra properties to JSON.
     *
     * @param consumer The consumer
     * @return The trait builder
     */
    public TraitBuilder extraData(Consumer<JsonObject> consumer) {
        this.extraData = consumer;
        return this;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();

        json.addProperty("type", this.type.toString());
        json.addProperty("max_level", this.maxLevel);

        if (!this.conditions.isEmpty()) {
            JsonArray array = new JsonArray();
            this.conditions.forEach(c -> array.add(TraitSerializers.serializeCondition(c)));
            json.add("conditions", array);
        }

        json.add("name", ITextComponent.Serializer.toJsonTree(this.name));
        json.add("description", ITextComponent.Serializer.toJsonTree(this.description));

        if (!this.cancelsList.isEmpty()) {
            JsonArray cancelsArray = new JsonArray();
            this.cancelsList.forEach(id -> cancelsArray.add(id.toString()));
            json.add("cancels_with", cancelsArray);
        }

        if (!this.overridesList.isEmpty()) {
            JsonArray overridesArray = new JsonArray();
            this.overridesList.forEach(id -> overridesArray.add(id.toString()));
            json.add("overrides", overridesArray);
        }

        if (!this.extraWikiLines.isEmpty()) {
            JsonArray array = new JsonArray();
            this.extraWikiLines.forEach(t -> array.add(ITextComponent.Serializer.toJsonTree(t)));
            json.add("extra_wiki_lines", array);
        }

        this.extraData.accept(json);

        return json;
    }
}
