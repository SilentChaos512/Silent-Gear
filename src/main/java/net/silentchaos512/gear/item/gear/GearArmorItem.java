package net.silentchaos512.gear.item.gear;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearArmor;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.SgAttributes;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearArmorItem extends BasicGearItem implements GearArmor {
    private final Supplier<GearType> gearType;
    private final ArmorType armorType;

    public GearArmorItem(Supplier<GearType> gearType, ArmorType armorType, Item.Properties properties) {
        super(properties);
        this.gearType = gearType;
        this.armorType = armorType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        super.onRecalculatePost(gear, player, finalProperties);
        if (!GearHelper.isBroken(gear)) {
            // Set equippable data
            var construction = GearData.getConstruction(gear);
            var primaryMaterial = construction.getMainTextureMaterialOrPlaceholder();
            if (primaryMaterial.isValid()) {
                var equippableInfo = primaryMaterial.get().getEquippableInfo();
                gear.set(
                        DataComponents.EQUIPPABLE,
                        Equippable.builder(this.armorType.getSlot())
                                .setEquipSound(equippableInfo.equipSound())
                                .setAsset(equippableInfo.assetId())
                                .build()
                );
            }

            // Attach armor color
            var primaryPart = construction.getCoatingOrMainPart();
            if (primaryPart != null) {
                gear.set(DataComponents.DYED_COLOR, new DyedItemColor(primaryPart.getColor(gear)));
            }

            // Set attribute modifiers for armor
            var properties = GearData.getProperties(gear);
            float armor = properties.getNumber(GearProperties.ARMOR);
            float toughness = properties.getNumber(GearProperties.ARMOR_TOUGHNESS);
            float knockbackResistance = properties.getNumber(GearProperties.KNOCKBACK_RESISTANCE) / 10f;
            float magicArmor = properties.getNumber(GearProperties.MAGIC_ARMOR);
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(this.armorType.getSlot());
            ResourceLocation id = ResourceLocation.withDefaultNamespace("armor." + this.armorType.getName());
            builder.add(Attributes.ARMOR, new AttributeModifier(id, armor, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            if (knockbackResistance > 0f) {
                builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, knockbackResistance, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }
            if (magicArmor > 0f) {
                builder.add(SgAttributes.MAGIC_ARMOR, new AttributeModifier(id, magicArmor, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }
            gear.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
        }
    }

    //region Stats and attributes

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    //endregion

    //region Item overrides

    @Override
    public int getMaxDamage(ItemStack stack) {
        int maxDamageFactor = (int) GearData.getProperties(stack).getNumber(getDurabilityStat());
        return (int) getGearType().armorDurabilityMultiplier() * maxDamageFactor;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack))
            return;
        if (!(Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get()))
            damage = Mth.clamp(damage, 0, getMaxDamage(stack));
        super.setDamage(stack, damage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, item -> {
            GearHelper.onBroken(stack, entity instanceof Player ? (Player) entity : null, this.armorType.getSlot());
            onBroken.accept(item);
        });
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return this.getEquipmentSlot(stack) == EquipmentSlot.FEET && TraitHelper.hasTrait(stack, Const.Traits.SNOW_WALKER);
    }

    //endregion

    //region Client-side methods and rendering horrors

    public static int getArmorColor(ItemStack stack) {
        // Gets the outermost (coating or main) part and compute its color
        var data = GearData.getConstruction(stack);
        var part = data.getCoatingOrMainPart();
        if (part != null) {
            return part.getColor(stack);
        }
        return Color.VALUE_WHITE;
    }

    //endregion
}
