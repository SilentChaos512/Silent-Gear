package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.gear.trait.condition.GearTypeTraitCondition;
import net.silentchaos512.gear.gear.trait.condition.OrTraitCondition;

import java.util.*;
import java.util.function.Supplier;

public class TraitBuilder {
    private final DataResource<Trait> trait;
    protected final int maxLevel;
    private Component name;
    private Component description;
    private final List<TraitEffect> effects = new ArrayList<>();
    private final List<ITraitCondition> conditions = new ArrayList<>();
    private final List<ResourceLocation> cancelsList = new ArrayList<>();
    private final List<ResourceLocation> overridesList = new ArrayList<>();
    private final List<Component> extraWikiLines = new ArrayList<>();

    public TraitBuilder(DataResource<Trait> trait, int maxLevel) {
        this.trait = trait;
        this.maxLevel = maxLevel;

        this.name = Component.translatable(Util.makeDescriptionId("trait", this.trait.getId()));
        this.description = Component.translatable(Util.makeDescriptionId("trait", this.trait.getId()) + ".desc");
    }

    public static TraitBuilder of(DataResource<Trait> trait, int maxLevel) {
        return new TraitBuilder(trait, maxLevel);
    }

    public DataResource<Trait> getTrait() {
        return trait;
    }

    public TraitBuilder effects(TraitEffect first, TraitEffect... rest) {
        if (!this.effects.isEmpty()) {
            throw new IllegalStateException("Already called 'effects' on this TraitBuilder!");
        }
        this.effects.add(first);
        if (rest.length > 0) {
            this.effects.addAll(Arrays.stream(rest).toList());
        }
        return this;
    }

    public TraitBuilder setName(Component text) {
        this.name = text;
        return this;
    }

    public TraitBuilder setDescription(Component text) {
        this.description = text;
        return this;
    }

    public TraitBuilder withConditions(ITraitCondition... conditions) {
        Collections.addAll(this.conditions, conditions);
        return this;
    }

    @SafeVarargs
    public final TraitBuilder withGearTypeCondition(Supplier<GearType> first, Supplier<GearType>... rest) {
        if (rest.length > 0) {
            Collection<GearType> types = new ArrayList<>(rest.length + 1);
            types.add(first.get());
            Arrays.stream(rest).map(Supplier::get).forEach(types::add);

            GearTypeTraitCondition[] values = types.stream()
                    .map(GearTypeTraitCondition::new)
                    .toArray(GearTypeTraitCondition[]::new);

            return withConditions(new OrTraitCondition(values));
        }
        return withConditions(new GearTypeTraitCondition(first.get()));
    }

    public TraitBuilder cancelsWith(DataResource<Trait> trait) {
        return cancelsWith(trait.getId());
    }

    public TraitBuilder cancelsWith(ResourceLocation trait) {
        this.cancelsList.add(trait);
        return this;
    }

    public TraitBuilder overridesTrait(DataResource<Trait> trait) {
        return overridesTrait(trait.getId());
    }

    public TraitBuilder overridesTrait(ResourceLocation trait) {
        this.overridesList.add(trait);
        return this;
    }

    public TraitBuilder extraWikiLines(String... lines) {
        for (String line : lines) {
            this.extraWikiLines.add(Component.literal(line));
        }
        return this;
    }

    public TraitBuilder extraWikiLines(Component... lines) {
        this.extraWikiLines.addAll(Arrays.asList(lines));
        return this;
    }

    public JsonObject serialize() {
        SilentGear.LOGGER.info("Trying to serialize trait \"{}\"", this.trait.getId());
        var traitObj = new Trait(
                this.maxLevel,
                this.name,
                this.description,
                this.effects,
                this.conditions,
                this.extraWikiLines
        );

        var jsonElementDataResult = Trait.CODEC.encodeStart(JsonOps.INSTANCE, traitObj);
        if (jsonElementDataResult.isError()) {
            SilentGear.LOGGER.error("Something went wrong when serializing trait \"{}\"", this.trait.getId());
        }
        return jsonElementDataResult.getOrThrow().getAsJsonObject();
    }
}
