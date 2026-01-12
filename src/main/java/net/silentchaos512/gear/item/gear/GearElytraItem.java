package net.silentchaos512.gear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.item.GearArmor;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearElytraItem extends BasicGearItem implements GearArmor {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableSet.of(
            PartTypes.MAIN.get(),
            PartTypes.BINDING.get()
    ));

    private final Supplier<GearType> gearType;

    public GearElytraItem(Supplier<GearType> gearType, Item.Properties properties) {
        super(properties);
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        super.onRecalculatePost(gear, player, finalProperties);
        gear.set(DataComponents.GLIDER, Unit.INSTANCE);
        gear.set(
                DataComponents.EQUIPPABLE,
                Equippable.builder(EquipmentSlot.CHEST)
                        .setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
                        .setAsset(EquipmentAssets.ELYTRA)
                        .setDamageOnHurt(false)
                        .build()
        );

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        float armor = GearData.getProperties(gear).getNumber(GearProperties.ARMOR);
        if (armor > 0) {
            var armorType = ArmorType.CHESTPLATE;
            var equipmentSlotGroup = EquipmentSlotGroup.bySlot(armorType.getSlot());
            var id = Identifier.withDefaultNamespace("armor." + armorType.getName());
            builder.add(Attributes.ARMOR, new AttributeModifier(id, armor, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }
        this.buildAttributes(gear, builder);
        gear.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public boolean supportsPart(ItemStack gear, PartInstance part) {
        if (!part.isValid()) return false;
        PartType type = part.getType();
        boolean canAdd = part.get().canAddToGear(gear, part);
        boolean supported = (requiresPartOfType(part.getType()) && canAdd) || canAdd;
        return (type == PartTypes.MAIN.get() && supported)
                || type == PartTypes.LINING.get()
                || supported;
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        var durability = GearData.getProperties(stack).getNumber(getDurabilityStat());
        return (int) (getGearType().armorDurabilityMultiplier() * durability);
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
            var player = entity instanceof Player ? (Player) entity : null;
            GearHelper.onBroken(stack, player, this.getEquipmentSlot(stack));
            onBroken.accept(item);
        });
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }
}
