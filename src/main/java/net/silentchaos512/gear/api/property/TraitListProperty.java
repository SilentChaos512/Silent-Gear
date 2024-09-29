package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.client.util.GearTooltipFlag;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.lib.util.Color;

import java.util.*;
import java.util.stream.Collectors;

public class TraitListProperty extends GearProperty<List<TraitInstance>, TraitListPropertyValue> {
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
    public List<TraitInstance> compute(List<TraitInstance> baseValue, boolean filterConditions, GearType itemType, GearType statType, Collection<TraitListPropertyValue> modifiers) {
        return computeForGear(baseValue, filterConditions, itemType, statType, modifiers, List.of());
    }

    @Override
    public List<TraitInstance> computeForGear(List<TraitInstance> baseValue, boolean filterConditions, GearType itemType, GearType statType, Collection<TraitListPropertyValue> modifiers, List<PartInstance> parts) {
        if (modifiers.isEmpty()) {
            return baseValue;
        }

        List<TraitInstance> list = new ArrayList<>();
        for (var mod : modifiers) {
            list.addAll(mod.value);
        }
        return computeTraits(filterConditions, itemType, baseValue, list, parts);
    }

    public List<TraitInstance> computeTraits(boolean filterConditions, GearType itemType, List<TraitInstance> baseValue, Collection<TraitInstance> traits, List<PartInstance> parts) {
        if (traits.isEmpty()) {
            return baseValue;
        }

        Map<Trait, Integer> map = new LinkedHashMap<>();
        Map<Trait, Integer> count = new HashMap<>();

        for (var traitInstance : traits) {
            map.merge(traitInstance.getTrait(), traitInstance.getLevel(), Integer::sum);
            count.merge(traitInstance.getTrait(), 1, Integer::sum);
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
        map.forEach((trait, level) -> ret.add(TraitInstance.of(trait, level)));
        // Remove if the conditions don't match the gear
        if (filterConditions) {
            ret.removeIf(trait -> !trait.conditionsMatch(PartGearKey.of(itemType, PartTypes.NONE.get()), parts));
        }
        return ret;
    }

    @Override
    public List<TraitListPropertyValue> compressModifiers(Collection<TraitListPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        return List.copyOf(modifiers);
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
    public MutableComponent formatValueWithColor(TraitListPropertyValue value, boolean addColor) {
        return formatValue(value).plainCopy();
    }

    @Override
    public Component formatValue(TraitListPropertyValue value) {
        return Component.literal(
                value.value.stream()
                        .map(TraitInstance::getDisplayName)
                        .map(Component::getString)
                        .collect(Collectors.joining(", "))
        );
    }

    @Override
    public List<TraitListPropertyValue> sortForDisplay(Collection<TraitListPropertyValue> mods) {
        return List.of(valueOf(compute(this.baseValue, false, GearTypes.ALL.get(), mods)));
    }

    @Override
    public List<Component> getTooltipLines(TraitListPropertyValue value, GearTooltipFlag flag) {
        List<Component> result = new ArrayList<>();
        var traits = value.value;
        var displayIndex = getTraitDisplayIndex(traits.size(), flag);

        MutableComponent textTraits = TextUtil.withColor(TextUtil.misc("tooltip.traits"), Color.GOLD);
        if (displayIndex < 0) {
            if (!Config.Client.vanillaStyleTooltips.get()) {
                result.add(textTraits);
            }
        }

        int i = 0;
        for (var trait : traits) {
            if (displayIndex < 0 || displayIndex == i) {
                final int level = trait.getLevel();
                trait.getTrait().addInformation(level, result, flag, text -> {
                    if (Config.Client.vanillaStyleTooltips.get()) {
                        var bullet = Component.literal(TextListBuilder.VANILLA_BULLET + " ");
                        return TextUtil.withColor(bullet, Color.GRAY).append(text);
                    }
                    if (displayIndex >= 0) {
                        var colon = Component.literal(": ");
                        return textTraits.append(TextUtil.withColor(colon, ChatFormatting.GRAY).append(text));
                    }
                    return Component.literal(TextListBuilder.BULLETS[0] + " ").append(text);
                });
            }
            ++i;
        }

        return result;
    }

    private static int getTraitDisplayIndex(int numTraits, GearTooltipFlag flag) {
        if (Config.Client.vanillaStyleTooltips.get() || KeyTracker.isDisplayTraitsDown() || numTraits == 0)
            return -1;
        return ClientTicks.ticksInGame() / 20 % numTraits;
    }
}
