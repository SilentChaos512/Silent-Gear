package net.silentchaos512.gear.item.gear;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.api.item.ICoreWeapon;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CoreSword extends ItemSword implements ICoreWeapon {
    public CoreSword() {
        this(null);
    }

    CoreSword(@Nullable ToolType type) {
        super(ItemTier.DIAMOND, 0, 0, GearHelper.getBuilder(type));
    }

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        return Config.sword;
    }

    @Override
    public String getGearClass() {
        return "sword";
    }

    //region Standard tool overrides

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return GearHelper.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.ENCHANTABILITY);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, GearHelper.calcDamageClamped(stack, damage));
        if (GearHelper.isBroken(stack)) {
            GearData.recalculateStats(null, stack);
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.DURABILITY);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return GearHelper.getRarity(stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return GearHelper.hitEntity(stack, target, attacker);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        return GearHelper.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    //endregion
}
