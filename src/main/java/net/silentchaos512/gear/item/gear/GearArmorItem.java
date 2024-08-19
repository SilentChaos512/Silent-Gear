package net.silentchaos512.gear.item.gear;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.material.TextureType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class GearArmorItem extends ArmorItem implements ICoreArmor {
    // Just copied from ArmorItem, access transformers are too flaky
    private static final UUID[] ARMOR_MODIFIERS = {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    // sum = 1, starts with boots
    private static final float[] ABSORPTION_RATIO_BY_SLOT = {3f / 20f, 6f / 20f, 8f / 20f, 3f / 20f};

    // Caches armor colors by model key to speed up armor rendering
    private static final Cache<String, Integer> ARMOR_COLORS = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final Supplier<GearType> gearType;

    public GearArmorItem(Supplier<GearType> gearType, ArmorItem.Type armorType) {
        super(GearHelper.DEFAULT_DUMMY_ARMOR_MATERIAL, armorType, GearHelper.getBaseItemProperties());
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.getType().getSlot().getName().equalsIgnoreCase(slot);
    }

    //region Stats and attributes

    @Override
    public float getRepairModifier(ItemStack stack) {
        return getGearType().armorDurabilityMultiplier();
    }

    public float getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.ARMOR);
    }

    public float getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.ARMOR_TOUGHNESS);
    }

    public float getArmorMagicProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getProperties(stack).getNumber(GearProperties.MAGIC_ARMOR);
    }

    private static float getGenericArmorProtection(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof GearArmorItem)
            return ((GearArmorItem) item).getArmorProtection(stack);
        else if (item instanceof ArmorItem)
            return ((ArmorItem) item).getDefense();
        return 0;
    }

    private static int getPlayerTotalArmorValue(LivingEntity player) {
        float total = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            total += getGenericArmorProtection(armor);
        }
        return Math.round(total);
    }

    @Override
    public ItemAttributeModifiers getAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
        if (slot == this.getType().getSlot()) {
            UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
            var equipmentSlotGroup = EquipmentSlotGroup.bySlot(slot);
            builder.add(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", getArmorProtection(stack), AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", getArmorToughness(stack), AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            float knockbackResistance = GearData.getProperties(stack).getNumber(GearProperties.KNOCKBACK_RESISTANCE) / 10f;
            if (knockbackResistance > 0) {
                builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", knockbackResistance, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
            }
            GearHelper.addAttributeModifiers(stack, builder);
        }
        return builder.build();
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Runnable onBroken) {
        return GearHelper.damageItem(stack, amount, entity, () -> {
            GearHelper.onBroken(stack, entity instanceof Player ? (Player) entity : null, this.getType().getSlot());
            onBroken.run();
        });
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }

    //endregion

    //region Client-side methods and rendering horrors


    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        // Empty texture if broken
        if (GearHelper.isBroken(stack)) {
            return SilentGear.getId("textures/models/armor/empty.png");
        }

        var primaryPart = GearData.getConstruction(stack).getCoatingOrMainPart();
        if (primaryPart != null) {
            var primaryMaterial = primaryPart.getMaterialNullable();
            if (primaryMaterial != null) {
                var mainTextureType = primaryMaterial.getMainTextureType();
                return mainTextureType.getArmorTexture(innerModel);
            }
        }

        return TextureType.HIGH_CONTRAST.getArmorTexture(innerModel);
    }

    private static int getArmorColor(ItemStack stack) {
        // Gets the outermost (coating or main) part and compute its color
        var data = GearData.getConstruction(stack);
        var part = data.getCoatingOrMainPart();
        if (part != null) {
            return part.getColor(stack);
        }
        return Color.VALUE_WHITE;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, tooltipContext, tooltip, flagIn);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    //endregion
}
