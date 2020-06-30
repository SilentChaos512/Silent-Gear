package net.silentchaos512.gear.item.gear;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.parts.IPartDisplay;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CoreArmor extends DyeableArmorItem implements ICoreArmor {
    // Just copied from ArmorItem, access transformers are too flaky
    private static final UUID[] ARMOR_MODIFIERS = {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    // sum = 1, starts with boots
    private static final float[] ABSORPTION_RATIO_BY_SLOT = {0.175f, 0.3f, 0.4f, 0.125f};
    // Same values as in ArmorItem.
    private static final int[] MAX_DAMAGE_ARRAY = {13, 15, 16, 11};

    public CoreArmor(EquipmentSlotType slot) {
        super(ArmorMaterial.DIAMOND, slot, GearHelper.getBuilder(null));
    }

    @Deprecated
    public CoreArmor(EquipmentSlotType slot, String name) {
        this(slot);
    }

    @Override
    public GearType getGearType() {
        switch (this.getEquipmentSlot()) {
            case HEAD:
                return GearType.HELMET;
            case CHEST:
                return GearType.CHESTPLATE;
            case LEGS:
                return GearType.LEGGINGS;
            case FEET:
                return GearType.BOOTS;
            default:
                return GearType.ARMOR;
        }
    }

    //region Stats and attributes


    @Override
    public ItemStat getDurabilityStat() {
        return ItemStats.ARMOR_DURABILITY;
    }

    public double getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return ABSORPTION_RATIO_BY_SLOT[this.getEquipmentSlot().getIndex()] * GearData.getStat(stack, ItemStats.ARMOR);
    }

    public double getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getStat(stack, ItemStats.ARMOR_TOUGHNESS) / 4;
    }

    private static double getGenericArmorProtection(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof CoreArmor)
            return ((CoreArmor) item).getArmorProtection(stack);
        else if (item instanceof ArmorItem)
            return ((ArmorItem) item).getDamageReduceAmount();
        return 0;
    }

    private static int getPlayerTotalArmorValue(LivingEntity player) {
        float total = 0;
        for (ItemStack armor : player.getArmorInventoryList()) {
            total += getGenericArmorProtection(armor);
        }
        return Math.round(total);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = LinkedHashMultimap.create();
        if (slot == this.getEquipmentSlot()) {
            UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(uuid, "Armor modifier", getArmorProtection(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(uuid, "Armor toughness", getArmorToughness(stack), AttributeModifier.Operation.ADDITION));
            return GearHelper.getAttributeModifiers(slot, stack, multimap);
        }
        return multimap;
    }

    //endregion

    //region Item overrides

    @Override
    public int getMaxDamage(ItemStack stack) {
        int maxDamageFactor = GearData.getStatInt(stack, getDurabilityStat());
        return MAX_DAMAGE_ARRAY[this.getEquipmentSlot().getIndex()] * maxDamageFactor;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack))
            return;
        if (!Config.Server.gearBreaksPermanently.get())
            damage = MathHelper.clamp(damage, 0, getMaxDamage(stack));
        super.setDamage(stack, damage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int value;
        if (GearHelper.isUnbreakable(stack)) {
            value = 0;
        } else if (!Config.Server.gearBreaksPermanently.get()) {
            value = MathHelper.clamp(amount, 0, stack.getMaxDamage() - stack.getDamage() - 1);
        } else {
            value = super.damageItem(stack, amount, entity, onBroken);
        }
        GearHelper.damageParts(stack, value);
        return value;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, ItemStats.ENCHANTABILITY);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        GearHelper.inventoryTick(stack, world, player, 0, true);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    //endregion

    //region Client-side methods and rendering horrors

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        // In order for colors to work, it seems the following must be true:
        // 1. Armor texture must be named using the vanilla convention
        // 2. Return value of this may NOT be cached... wat? Not a big deal I guess.
        // 3. You got lucky. The tiniest change can break everything for no apparent reason.

        // Empty texture if broken
        if (GearHelper.isBroken(stack)) return SilentGear.MOD_ID + ":textures/models/armor/empty.png";

        int layer = slot == EquipmentSlotType.LEGS ? 2 : 1;
        // Overlay - default to a blank texture
        if ("overlay".equals(type))
            return SilentGear.MOD_ID + ":textures/models/armor/all_layer_" + layer + "_overlay.png";

        PartData part = GearData.getPrimaryRenderPartFast(stack);
        if (part == null) {
            part = PartData.ofNullable(PartType.MAIN.getFallbackPart());
        }
        if (part == null) {
            return "silentgear:textures/models/armor/generic_hc_layer_" + layer + (type != null ? "_" + type : "") + ".png";
        }

        // Actual armor texture
        IPartDisplay props = part.getPart().getDisplayProperties(part, stack, 0);
        return props.getTextureDomain() + ":textures/models/armor/"
                + props.getArmorTexturePrefix()
                + "_layer_" + layer
                + (type != null ? "_" + type : "")
                + ".png";
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        return true;
    }

    @Override
    public int getColor(ItemStack stack) {
        return GearData.getArmorColor(stack);
    }

    @Override
    public void removeColor(ItemStack stack) {}

    @Override
    public void setColor(ItemStack stack, int color) {}

    @Override
    public boolean hasEffect(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    //endregion
}
