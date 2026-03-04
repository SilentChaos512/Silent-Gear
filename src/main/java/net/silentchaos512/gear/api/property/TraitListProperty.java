package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;
import net.silentchaos512.gear.client.tooltip.GearTooltipStyle;
import net.silentchaos512.gear.client.util.GearTooltipFlag;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TextUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TraitListProperty extends GearProperty<List<TraitInstance>, TraitListPropertyValue> implements CustomTooltipProperty {
    public static final Codec<TraitListPropertyValue> CODEC = Codec.list(TraitInstance.CODEC)
            .xmap(
                    TraitListPropertyValue::new,
                    GearPropertyValue::value
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, TraitListPropertyValue> STREAM_CODEC = StreamCodec.of(
            (buf, val) -> CodecUtils.encodeList(buf, val.value, TraitInstance.STREAM_CODEC),
            buf -> new TraitListPropertyValue(CodecUtils.decodeList(buf, TraitInstance.STREAM_CODEC))
    );

    public TraitListProperty(Builder<List<TraitInstance>> builder) {
        super(builder);
    }

    @Override
    public Codec<TraitListPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, TraitListPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public TraitListPropertyValue valueOf(List<TraitInstance> value) {
        return new TraitListPropertyValue(value);
    }

    @Override
    public List<TraitInstance> compute(ComputeContext context, List<TraitInstance> baseValue, boolean filterConditions, GearType itemType, GearType statType, Collection<TraitListPropertyValue> modifiers) {
        return computeForGear(context, baseValue, filterConditions, itemType, statType, modifiers, List.of());
    }

    @Override
    public List<TraitInstance> computeForGear(ComputeContext context, List<TraitInstance> baseValue, boolean filterConditions, GearType itemType, GearType statType, Collection<TraitListPropertyValue> modifiers, List<PartInstance> parts) {
        if (modifiers.isEmpty()) {
            return baseValue;
        }

        List<TraitInstance> list = new ArrayList<>();
        for (var mod : modifiers) {
            list.addAll(mod.value);
        }
        return computeTraits(context, filterConditions, itemType, baseValue, list, parts);
    }

    public List<TraitInstance> computeTraits(ComputeContext context, boolean filterConditions, GearType itemType, List<TraitInstance> baseValue, Collection<TraitInstance> traits, List<PartInstance> parts) {
        if (traits.isEmpty()) {
            return baseValue;
        }

        Map<Trait, Integer> map = new LinkedHashMap<>();
        Map<Trait, Integer> count = new HashMap<>();
        Map<Trait, Set<ITraitCondition>> conditions = new HashMap<>();

        for (var traitInstance : traits) {
            if (traitInstance.isValid()) {
                map.merge(traitInstance.getTrait(), traitInstance.getLevel(), Integer::sum);
                count.merge(traitInstance.getTrait(), 1, Integer::sum);
                for (ITraitCondition condition : traitInstance.conditions()) {
                    conditions.computeIfAbsent(traitInstance.getTrait(), traitIn -> new LinkedHashSet<>()).add(condition);
                }
            }
        }

        Trait[] keys = map.keySet().toArray(new Trait[0]);

        for (Trait trait : keys) {
            final int matsWithTrait = count.get(trait);
            final float divisor = Math.min(traits.size() / 2f, matsWithTrait);
            final int value = Math.round(map.get(trait) / divisor);
            map.put(trait, Mth.clamp(value, 1, trait.getMaxLevel()));
        }

        // TODO: Trait cancelling? Events?

        List<TraitInstance> ret = new ArrayList<>();
        map.forEach((trait, level) -> {
            Set<ITraitCondition> traitConditions = conditions.computeIfAbsent(trait, traitIn -> Collections.emptySet());
            var instance = TraitInstance.of(trait, level, traitConditions);
            var transformed = instance.getTrait().transformTrait(context, level);
            transformed.ifPresent(ret::add);
        });
        if (filterConditions) {
            // Remove if the conditions don't match the gear
            ret.removeIf(trait -> !trait.conditionsMatch(context));
        }
        return ret;
    }

    @Override
    public List<TraitListPropertyValue> compressModifiers(ComputeContext context, Collection<TraitListPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        return List.copyOf(modifiers);
    }

    @Override
    public List<TraitListPropertyValue> reduce(ComputeContext context, Collection<TraitListPropertyValue> modifiers) {
        List<TraitListPropertyValue> result = new ArrayList<>();
        for (TraitListPropertyValue traitList : modifiers) {
            List<TraitInstance> newList = new ArrayList<>();
            for (TraitInstance inst : traitList.value) {
                inst.reduceConditions(context).ifPresent(newList::add);
            }
            if (!newList.isEmpty()) {
                result.add(new TraitListPropertyValue(newList));
            }
        }
        return result;
    }

    @Override
    public List<TraitInstance> getZeroValue() {
        return List.of();
    }

    @Override
    public boolean isZero(List<TraitInstance> value) {
        return value.isEmpty();
    }

    @Override
    public MutableComponent formatValueWithColor(TraitListPropertyValue value, FormatContext formatContext, FormatColorScheme colorScheme) {
        return formatValue(value, formatContext, FormatColorScheme.NO_COLORS).plainCopy();
    }

    @Override
    public Component formatValue(TraitListPropertyValue value, FormatContext formatContext, FormatColorScheme colorScheme) {
        return Component.literal(
                value.value.stream()
                        .map(traitInstance -> traitInstance.getDisplayName(formatContext))
                        .map(Component::getString)
                        .collect(Collectors.joining(", "))
        );
    }

    @Override
    public List<TraitListPropertyValue> sortForDisplay(Collection<TraitListPropertyValue> mods) {
        return List.of(valueOf(compute(ComputeContext.empty(), this.baseValue, false, GearTypes.ALL.get(), mods)));
    }

    @Override
    public void buildTooltip(TextListBuilder listBuilder, TraitListPropertyValue value, ItemStack gearItemStack, GearTooltipFlag flag, FormatColorScheme colorScheme) {
        var propertyName = TextUtil.withColor(getDisplayName(), this.nameColor);

        if (value.value.isEmpty()) {
            var noneText = Component.translatable("misc.silentgear.tooltip.none");
            listBuilder.add(Component.translatable("property.silentgear.displayFormat", propertyName, noneText));
            return;
        }

        listBuilder.add(propertyName);
        listBuilder.indent();
        var displayDescriptions = KeyTracker.isDisplayTraitDescriptionsDown();
        for (TraitInstance trait : value.value) {
            var text = trait.getDisplayName(FormatContext.GEAR);
            if (displayDescriptions) {
                var descriptionColored = TextUtil.withColor(trait.getDescription(), ChatFormatting.GRAY);
                var textWithDescription = Component.translatable("property.silentgear.traits.displayWithDescription", text, descriptionColored);
                listBuilder.add(textWithDescription);
            } else {
                listBuilder.add(text);
            }
        }
        listBuilder.unindent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, V extends GearPropertyValue<T>> boolean addCustomTooltip(GearTooltipStyle format, FormatColorScheme valueColorScheme, Collection<V> modifiers, TextListBuilder builder) {
        if (format.compactStylePreferred()) {
            return false;
        }

        var traitList = this.compute(ComputeContext.empty(), getZeroValue(), false, GearTypes.ALL.get(), (Collection<TraitListPropertyValue>) modifiers);
        if (traitList.isEmpty()) {
            return true;
        }

        var headerText = Component.translatable("property.silentgear.traits");
        builder.add(format.colorPropertyName() ? headerText.withColor(this.nameColor.getColor()) : headerText);
        builder.indent();
        for (TraitInstance trait : traitList) {
            builder.add(trait.getDisplayName(FormatContext.ANY));
            builder.indent();
            builder.add(trait.getDescription().withStyle(ChatFormatting.ITALIC));
            if (!trait.conditions().isEmpty()) {
                builder.setBullet("*");
                for (ITraitCondition condition : trait.conditions()) {
                    var text = condition.getDisplayText();
                    var strippedText = text.getString().replaceAll("^\\(|\\)$", "");
                    builder.add(Component.literal(strippedText).withStyle(ChatFormatting.ITALIC), ChatFormatting.DARK_GRAY);
                }
            }
            builder.unindent();
        }
        builder.unindent();

        return true;
    }
}
