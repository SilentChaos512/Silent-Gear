package net.silentchaos512.gear.item.gear;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.item.component.UseEffects;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearWeapon;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class GearSpearItem extends BasicGearItem implements GearWeapon {
    private final Supplier<GearType> gearType;

    public GearSpearItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(properties);
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        GearWeapon.super.onRecalculatePost(gear, player, finalProperties);

        var primaryPart = GearData.getConstruction(gear).getPrimaryPart();
        var primaryMaterial = primaryPart != null ? primaryPart.getPrimaryMaterial() : null;
        var isWood = primaryMaterial != null && (primaryMaterial.is(Const.Materials.WOOD) || primaryMaterial.parentIs(Const.Materials.WOOD));

        gear.set(DataComponents.DAMAGE_TYPE, new EitherHolder<>(DamageTypes.SPEAR));
        /*gear.set(
                DataComponents.KINETIC_WEAPON,
                new KineticWeapon(
                        10,
                        (int) (delay * 20F),
                        KineticWeapon.Condition.ofAttackerSpeed((int)(dismountMaxDuration * 20.0F), dismountMinSpeed),
                        KineticWeapon.Condition.ofAttackerSpeed((int)(knockbackMaxDuration * 20.0F), knockbackMinSpeed),
                        KineticWeapon.Condition.ofRelativeSpeed((int)(damageMaxDuration * 20.0F), damageMinSpeed),
                        0.38F,
                        damageMultiplier,
                        Optional.of(isWood ? SoundEvents.SPEAR_WOOD_USE : SoundEvents.SPEAR_USE),
                        Optional.of(isWood ? SoundEvents.SPEAR_WOOD_HIT : SoundEvents.SPEAR_HIT)
                )
        );*/
        gear.set(
                DataComponents.PIERCING_WEAPON,
                new PiercingWeapon(
                        true,
                        false,
                        Optional.of(isWood ? SoundEvents.SPEAR_WOOD_ATTACK : SoundEvents.SPEAR_ATTACK),
                        Optional.of(isWood ? SoundEvents.SPEAR_WOOD_HIT : SoundEvents.SPEAR_HIT)
                )
        );
        gear.set(DataComponents.ATTACK_RANGE, new AttackRange(2.0F, 4.5F, 2.0F, 6.5F, 0.125F, 0.5F));
        gear.set(DataComponents.MINIMUM_ATTACK_CHARGE, 1.0F);
//        gear.set(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.STAB, (int) (swingDuration * 20.0F)));
        gear.set(DataComponents.USE_EFFECTS, new UseEffects(true, false, 1.0F));
    }
}
