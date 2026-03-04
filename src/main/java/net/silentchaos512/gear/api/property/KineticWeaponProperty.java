package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.component.KineticWeapon;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.tooltip.FormatColorScheme;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class KineticWeaponProperty extends GearProperty<KineticWeapon, KineticWeaponPropertyValue> {
    public static final KineticWeapon ZERO = new KineticWeapon(0, 0, Optional.empty(), Optional.empty(), Optional.empty(), 0f, 0f, Optional.empty(), Optional.empty());

    public static final Codec<KineticWeaponPropertyValue> CODEC = GearPropertyValue.createSimpleValueCodec(
            KineticWeapon.CODEC,
            KineticWeaponPropertyValue::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, KineticWeaponPropertyValue> STREAM_CODEC = GearPropertyValue.createSimpleStreamCodec(
            KineticWeapon.STREAM_CODEC,
            KineticWeaponPropertyValue::new
    );

    public KineticWeaponProperty(Builder<KineticWeapon> builder) {
        super(builder);
    }

    @Override
    public Codec<KineticWeaponPropertyValue> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, KineticWeaponPropertyValue> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public KineticWeaponPropertyValue valueOf(KineticWeapon value) {
        return new KineticWeaponPropertyValue(value);
    }

    @Override
    public KineticWeapon compute(ComputeContext context, KineticWeapon baseValue, boolean clampResult, GearType itemType, GearType statType, Collection<KineticWeaponPropertyValue> modifiers) {
        if (modifiers.isEmpty()) {
            return getZeroValue();
        }

        int totalContactCooldownTicks = 0;
        int totalDelayTicks = 0;
        Optional<KineticWeapon.Condition> dismountConditions = Optional.empty();
        Optional<KineticWeapon.Condition> knockbackConditions = Optional.empty();
        Optional<KineticWeapon.Condition> damageConditions = Optional.empty();
        float totalForwardMovement = 0f;
        float totalDamageMultiplier = 0f;
        Optional<Holder<SoundEvent>> sound = Optional.empty();
        Optional<Holder<SoundEvent>> hitSound = Optional.empty();

        for (KineticWeaponPropertyValue value : modifiers) {
            totalContactCooldownTicks += value.value.contactCooldownTicks();
            totalDelayTicks += value.value.delayTicks();
            if (dismountConditions.isEmpty()) {
                dismountConditions = value.value.dismountConditions();
            }
            if (knockbackConditions.isEmpty()) {
                knockbackConditions = value.value.knockbackConditions();
            }
            if (damageConditions.isEmpty()) {
                damageConditions = value.value.damageConditions();
            }
            totalForwardMovement += value.value.forwardMovement();
            totalDamageMultiplier += value.value.damageMultiplier();
            if (sound.isEmpty()) {
                sound = value.value.sound();
            }
            if (hitSound.isEmpty()) {
                hitSound = value.value.hitSound();
            }
        }

        return new KineticWeapon(
                totalContactCooldownTicks / modifiers.size(),
                totalDelayTicks / modifiers.size(),
                dismountConditions,
                knockbackConditions,
                damageConditions,
                totalForwardMovement / modifiers.size(),
                totalDamageMultiplier / modifiers.size(),
                sound,
                hitSound
        );
    }

    @Override
    public KineticWeapon getZeroValue() {
        return ZERO;
    }

    @Override
    public boolean isZero(KineticWeapon value) {
        return value.equals(getZeroValue());
    }

    @Override
    public List<KineticWeaponPropertyValue> compressModifiers(ComputeContext context, Collection<KineticWeaponPropertyValue> modifiers, PartGearKey key, List<? extends GearComponentInstance<?>> components) {
        return List.of(new KineticWeaponPropertyValue(compute(context, getBaseValue(), true, key.gearType(), modifiers)));
    }

    @Override
    public Component formatValue(KineticWeaponPropertyValue value, FormatContext formatContext, FormatColorScheme colorScheme) {
        return Component.literal(
                String.format(
                        "KineticWeapon(cct=%d,dt=%d,p1=%s,p2=%s,p3=%s,fm=%.2f,dmg=%.2f)",
                        value.value.contactCooldownTicks(),
                        value.value.delayTicks(),
                        value.value.dismountConditions().isPresent() ? formatKineticWeaponCondition(value.value.dismountConditions().get()) : "None",
                        value.value.knockbackConditions().isPresent() ? formatKineticWeaponCondition(value.value.knockbackConditions().get()) : "None",
                        value.value.damageConditions().isPresent() ? formatKineticWeaponCondition(value.value.damageConditions().get()) : "None",
                        value.value.forwardMovement(),
                        value.value.damageMultiplier()
                )
        );
    }

    private String formatKineticWeaponCondition(KineticWeapon.Condition condition) {
        return String.format(
                "(dt=%dt,ms=%.2f,rs=%.2f)",
                condition.maxDurationTicks(),
                condition.minSpeed(),
                condition.minRelativeSpeed()
        );
    }

    @Override
    public MutableComponent formatValueWithColor(KineticWeaponPropertyValue value, FormatContext formatContext, FormatColorScheme colorScheme) {
        return formatValue(value, formatContext, colorScheme).plainCopy();
    }
}
