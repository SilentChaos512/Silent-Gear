package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.CodecUtils;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Trait {
    public static final Codec<Trait> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ExtraCodecs.POSITIVE_INT.fieldOf("max_level").forGetter(t -> t.maxLevel),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(t -> t.displayName),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(t -> t.description),
                    Codec.list(TraitEffect.DISPATCH_CODEC).fieldOf("effects").forGetter(t -> t.effects),
                    Codec.list(ITraitCondition.DISPATCH_CODEC).fieldOf("conditions").forGetter(t -> t.conditions),
                    Codec.list(ComponentSerialization.CODEC).optionalFieldOf("extra_wiki_lines", Collections.emptyList()).forGetter(t -> t.wikiLines)
            ).apply(instance, Trait::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Trait> STREAM_CODEC = StreamCodec.of(
            (buf, t) -> {
                var traitId = SgRegistries.TRAIT.getKey(t);
                SilentGear.LOGGER.debug("trait encode {}", traitId);
                buf.writeResourceLocation(traitId);

                ByteBufCodecs.VAR_INT.encode(buf, t.maxLevel);
                ComponentSerialization.STREAM_CODEC.encode(buf, t.displayName);
                ComponentSerialization.STREAM_CODEC.encode(buf, t.description);
                CodecUtils.encodeList(buf, t.effects, TraitEffect.STREAM_CODEC);
                CodecUtils.encodeList(buf, t.conditions, ITraitCondition.STREAM_CODEC);
                CodecUtils.encodeList(buf, t.wikiLines, ComponentSerialization.STREAM_CODEC);
            },
            buf -> {
                var traitId = buf.readResourceLocation();
                SilentGear.LOGGER.debug("trait decode {}", traitId);

                var maxLevel = ByteBufCodecs.VAR_INT.decode(buf);
                var displayName = ComponentSerialization.STREAM_CODEC.decode(buf);
                var description = ComponentSerialization.STREAM_CODEC.decode(buf);
                var effects = CodecUtils.decodeList(buf, TraitEffect.STREAM_CODEC);
                var conditions = CodecUtils.decodeList(buf, ITraitCondition.STREAM_CODEC);
                var wikiLines = CodecUtils.decodeList(buf, ComponentSerialization.STREAM_CODEC);
                return new Trait(maxLevel, displayName, description, effects, conditions, wikiLines);
            }
    );

    private final int maxLevel;
    private final Component displayName;
    private final Component description;
    private final List<TraitEffect> effects;
    private final List<ITraitCondition> conditions;
    private final List<Component> wikiLines;

    public Trait(
            int maxLevel,
            Component displayName,
            Component description,
            List<TraitEffect> effects,
            List<ITraitCondition> conditions,
            List<Component> wikiLines
    ) {
        this.maxLevel = maxLevel;
        this.displayName = displayName;
        this.description = description;
        this.effects = ImmutableList.copyOf(effects);
        this.conditions = ImmutableList.copyOf(conditions);
        this.wikiLines = ImmutableList.copyOf(wikiLines);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<TraitEffect> getEffects() {
        return effects;
    }

    public List<ITraitCondition> getConditions() {
        return conditions;
    }

    public MutableComponent getDisplayName(int level) {
        MutableComponent text = displayName.copy();
        if (level > 0 && maxLevel > 1) {
            text.append(" ").append(Component.translatable("enchantment.level." + level));
        }
        return text;
    }

    public MutableComponent getDescription(int level) {
        return description.copy();
    }

    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        return baseValue;
    }

    public double onCalculateSynergy(double synergy, int traitLevel) {
        double ret = synergy;
        for (TraitEffect effect : this.effects) {
            ret = effect.onCalculateSynergy(ret, traitLevel);
        }
        return ret;
    }

    public int onDurabilityDamage(TraitActionContext context, int damageTaken) {
        int ret = damageTaken;
        for (TraitEffect effect : this.effects) {
            ret = effect.onDurabilityDamage(context, ret);
        }
        return ret;
    }

    public void onGearCrafted(TraitActionContext context) {
        for (TraitEffect effect : this.effects) {
            effect.onGearCrafted(context);
        }
    }

    public void onRecalculatePre(TraitActionContext context) {
        for (TraitEffect effect : this.effects) {
            effect.onRecalculatePre(context);
        }
    }

    public void onRecalculatePost(TraitActionContext context) {
        for (TraitEffect effect : this.effects) {
            effect.onRecalculatePost(context);
        }
    }

    public Collection<GearPropertyValue<?>> getBonusProperties(
            int traitLevel,
            @Nullable Player player,
            GearProperty<?, ?> property,
            GearPropertyValue<?> baseValue,
            float damageRatio
    ) {
        Collection<GearPropertyValue<?>> result = new ArrayList<>();
        for (TraitEffect effect : this.effects) {
            result.addAll(effect.getBonusProperties(traitLevel, player, property, baseValue, damageRatio));
        }
        return result;
    }

    public void onGetAttributeModifiers(TraitActionContext context, ItemAttributeModifiers.Builder builder) {
        for (TraitEffect effect : this.effects) {
            effect.onGetAttributeModifiers(context, builder);
        }
    }

    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        for (TraitEffect effect : this.effects) {
            var result = effect.onItemUse(context, traitLevel);
            if (result.consumesAction()) {
                return result;
            }
        }
        return InteractionResult.PASS;
    }

    public void onItemSwing(ItemStack stack, LivingEntity wielder, int traitLevel) {
        for (TraitEffect effect : this.effects) {
            effect.onItemSwing(stack, wielder, traitLevel);
        }
    }

    public float getMiningSpeedModifier(int traitLevel, BlockState state, float baseSpeed) {
        var total = 0f;
        for (var effect : this.effects) {
            total += effect.getMiningSpeedModifier(traitLevel, state);
        }
        return total;
    }

    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        for (TraitEffect effect : this.effects) {
            effect.onUpdate(context, isEquipped);
        }
    }

    public ItemStack addLootDrops(TraitActionContext context, ItemStack stack) {
        if (this.effects.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = stack.copy();
        for (TraitEffect effect : this.effects) {
            result = effect.addLootDrops(context, result);
        }
        return result;
    }

    public Collection<String> getExtraWikiLines() {
        return this.wikiLines.stream()
                .map(Component::getString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isHidden() {
        return false;
    }

    public boolean showInTooltip(TooltipFlag flag) {
        return !isHidden() || flag.isAdvanced();
    }

    @Deprecated
    public void addInformation(int level, List<Component> tooltip) {
        addInformation(level, tooltip, TooltipFlag.NORMAL);
    }

    /**
     * Add tooltip information for this trait. Normally, this consists of just the trait's
     * translated name and level, but may include a description under certain conditions. If the
     * trait is hidden ({@link #isHidden()}), nothing is shown unless advanced tooltips are
     * enabled.
     *
     * @param level   The trait level
     * @param tooltip The tooltip list
     * @param flag    The tooltip flag
     */
    public void addInformation(int level, List<Component> tooltip, TooltipFlag flag) {
        addInformation(level, tooltip, flag, t -> t);
    }

    /**
     * Add tooltip information for this trait. Normally, this consists of just the trait's
     * translated name and level, but may include a description under certain conditions. If the
     * trait is hidden ({@link #isHidden()}), nothing is shown unless advanced tooltips are
     * enabled.
     *
     * @param level      The trait level
     * @param tooltip    The tooltip list
     * @param flag       The tooltip flag
     * @param affixFirst A function which can be used to make additional changes to the first line
     *                   (display name)
     */
    public void addInformation(int level, List<Component> tooltip, TooltipFlag flag, Function<Component, Component> affixFirst) {
        if (!showInTooltip(flag)) return;

        // Display name
        Component displayName = TextUtil.withColor(this.getDisplayName(level), isHidden() ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY);
        displayName.getStyle().withColor(ChatFormatting.ITALIC);
        tooltip.add(affixFirst.apply(displayName));

        // Description (usually not shown)
        if (KeyTracker.isDisplayTraitsDown() && !Config.Client.vanillaStyleTooltips.get()) {
            Component description = TextUtil.withColor(this.getDescription(level), ChatFormatting.DARK_GRAY);
            tooltip.add(Component.literal("    ").append(description));
        }
    }
}
