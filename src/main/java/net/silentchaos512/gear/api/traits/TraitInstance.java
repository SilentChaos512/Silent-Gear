package net.silentchaos512.gear.api.traits;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.api.property.ComputeContext;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nonnull;
import java.util.*;

public record TraitInstance(
        DataResource<Trait> trait,
        int level,
        ImmutableList<ITraitCondition> conditions
) {
    public static final Codec<TraitInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    DataResource.TRAIT_CODEC.fieldOf("trait").forGetter(t -> t.trait),
                    ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(t -> t.level),
                    Codec.list(ITraitCondition.DISPATCH_CODEC).optionalFieldOf("conditions").forGetter(t -> Optional.of(new ArrayList<>(t.conditions)))
            ).apply(instance, (id, level, conditions) ->
                    new TraitInstance(id, level, conditions.orElse(Collections.emptyList()))
            )
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, TraitInstance> STREAM_CODEC = StreamCodec.composite(
            DataResource.TRAIT_STREAM_CODEC, t -> t.trait,
            ByteBufCodecs.VAR_INT, t -> t.level,
            ITraitCondition.STREAM_CODEC.apply(ByteBufCodecs.list()), t -> new ArrayList<>(t.conditions),
            TraitInstance::new
    );

    private TraitInstance(Trait trait, int level, ITraitCondition... conditions) {
        this(DataResource.trait(SgRegistries.TRAIT.getKey(trait)), level, conditions);
    }

    private TraitInstance(Trait trait, int level, Collection<ITraitCondition> conditions) {
        this(DataResource.trait(SgRegistries.TRAIT.getKey(trait)), level, conditions);
    }

    private TraitInstance(DataResource<Trait> trait, int level, ITraitCondition... conditions) {
        this(trait, level, Arrays.asList(conditions));
    }

    private TraitInstance(DataResource<Trait> trait, int level, Collection<ITraitCondition> conditions) {
        this(
                trait,
                level,
                createFilteredConditionsList(trait, conditions)
        );
    }

    private static ImmutableList<ITraitCondition> createFilteredConditionsList(DataResource<Trait> trait, Collection<ITraitCondition> conditions) {
        // Use a set to filter out duplicates, then create the immutable list
        var set = new LinkedHashSet<ITraitCondition>();
        if (trait.isPresent()) {
            set.addAll(trait.get().getConditions());
        }
        set.addAll(conditions);
        return ImmutableList.copyOf(set);
    }

    public static TraitInstance of(DataResource<Trait> trait, int level, ITraitCondition... conditions) {
        return new TraitInstance(trait, level, conditions);
    }

    public static TraitInstance of(Trait trait, int level, ITraitCondition... conditions) {
        return new TraitInstance(trait, level, conditions);
    }

    public static TraitInstance of(Trait trait, int level, Collection<ITraitCondition> conditions) {
        return new TraitInstance(trait, level, conditions);
    }

    public boolean isValid() {
        return trait.isPresent() && level > 0;
    }

    public ResourceLocation getTraitId() {
        return trait.getId();
    }

    @Nonnull
    public Trait getTrait() {
        return trait.get();
    }

    public int getLevel() {
        return level;
    }

    public Collection<ITraitCondition> getConditions() {
        return conditions;
    }

    public MutableComponent getDisplayName() {
        return getDisplayName(GearProperty.FormatContext.ANY);
    }

    public MutableComponent getDisplayName(GearProperty.FormatContext formatContext) {
        if (!isValid()) {
            return Component.literal("INVALID (" + this.trait.getId() + ")");
        }
        MutableComponent text = this.trait.get().getDisplayName(this.level).copy();
        if (formatContext != GearProperty.FormatContext.GEAR && !conditions.isEmpty()) {
            text.append("*");
        }
        return text;
    }

    public MutableComponent getDescription() {
        return this.trait.get().getDescription(this.level);
    }

    public void addInformation(List<Component> tooltip, TooltipFlag flag) {
        if (!this.trait.get().showInTooltip(flag)) return;

        // Display name
        MutableComponent displayName = this.getDisplayName().withStyle(ChatFormatting.ITALIC);
        tooltip.add(trait.get().isHidden() ? TextUtil.withColor(displayName, ChatFormatting.DARK_GRAY) : displayName);

        // Description (usually not shown)
        if (KeyTracker.isAltDown()) {
            Component description = TextUtil.withColor(this.trait.get().getDescription(level), ChatFormatting.DARK_GRAY);
            tooltip.add(Component.literal("    ").append(description));
        }
    }

    public boolean conditionsMatch(ComputeContext context) {
        if (!isValid()) {
            return false;
        }

        Trait trait = getTrait();

        for (ITraitCondition condition : getConditions()) {
            if (!condition.matches(trait, context)) {
                return false;
            }
        }

        return true;
    }

    public Optional<TraitInstance> reduceConditions(ComputeContext context) {
        if (conditionsMatch(context)) {
            List<ITraitCondition> filteredConditions = new ArrayList<>();
            for (ITraitCondition condition : conditions) {
                condition.reduce(this.trait.get(), context).ifPresent(filteredConditions::add);
            }
            return Optional.of(TraitInstance.of(this.trait.get(), this.level, filteredConditions));
        }
        return Optional.empty();
    }

    public MutableComponent getConditionsText() {
        // Basically the same as AndTraitCondition
        return getConditions().stream()
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "and")).append(t2))
                .orElseGet(() -> Component.literal(""));
    }

    @Override
    public String toString() {
        return getDisplayName().getString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TraitInstance other)) return false;

        if (!(this.trait.equals(other.trait)
                && this.level == other.level
                && this.conditions.size() == other.conditions.size())) {
            return false;
        }

        for (int i = 0; i < this.conditions.size(); ++i) {
            if (!this.conditions.get(i).equals(other.conditions.get(i))) {
                return false;
            }
        }

        return true;
    }
}
