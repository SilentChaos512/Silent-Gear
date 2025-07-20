package net.silentchaos512.gear.item.gear;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GearShieldItem extends BasicGearItem {
    private final Supplier<GearType> gearType;

    public GearShieldItem(Supplier<GearType> gearType) {
        super(GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        super.onRecalculatePost(gear, player, finalProperties);
        if (!GearHelper.isBroken(gear)) {
            gear.set(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
            gear.set(
                    DataComponents.EQUIPPABLE,
                    Equippable.builder(EquipmentSlot.OFFHAND)
                            .setSwappable(false)
                            .build()
            );
            gear.set(
                    DataComponents.BLOCKS_ATTACKS,
                    new BlocksAttacks(
                            0.25F,
                            1.0F,
                            List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                            new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                            Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                            Optional.of(SoundEvents.SHIELD_BLOCK),
                            Optional.of(SoundEvents.SHIELD_BREAK)
                    )
            );
            gear.set(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
        }
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return ImmutableList.of(PartTypes.MAIN.get(), PartTypes.ROD.get());
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, GearHelper.calcDamageClamped(stack, damage));
        if (GearHelper.isBroken(stack)) {
            GearData.recalculateGearData(stack, null);
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        var armorDurability = GearData.getProperties(stack).getNumber(GearProperties.ARMOR_DURABILITY);
        return Math.round(getGearType().armorDurabilityMultiplier() * armorDurability);
    }
}
