package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.config.Config;

import java.util.Iterator;

public class EquipmentHelper {

    // ==========================================================================
    // Mining, using, repairing, etc
    // ==========================================================================

    public static float getDestroySpeed(ItemStack stack, IBlockState state, Material[] extraMaterials) {
        if (isBroken(stack))
            return 0.25f;

        float speed = EquipmentData.getStat(stack, CommonItemStats.HARVEST_SPEED);

        // Tool effective on block?
        if (stack.getItem().canHarvestBlock(state, stack)) {
            return speed;
        }

        for (String type : stack.getItem().getToolClasses(stack)) {
            try {
                if (state.getBlock().isToolEffective(type, state)) {
                    return speed;
                }
            } catch (IllegalArgumentException ex) {
                return 1f;
            }
        }

        if (extraMaterials != null) {
            for (Material material : extraMaterials) {
                if (state.getMaterial() == material) {
                    return speed;
                }
            }
        }

        // Tool ineffective.
        return 1f;
    }

    public static float getMeleeDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 1f;

        float val = EquipmentData.getStat(stack, CommonItemStats.MELEE_DAMAGE);
        // if (stack.getItem() instanceof ICoreTool)
        // val += ((ICoreTool) stack.getItem()).getMeleeDamageModifier();
        return val < 0 ? 0 : val;
    }

    public static float getMagicDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 0f;

        float val = EquipmentData.getStat(stack, CommonItemStats.MAGIC_DAMAGE);
        // if (stack.getItem() instanceof ICoreTool)
        // val += ((ICoreTool) stack.getItem()).getMagicDamageModifier();
        return val < 0 ? 0 : val;
    }

    public static float getAttackSpeedModifier(ItemStack stack) {
        if (!(stack.getItem() instanceof ICoreTool))
            return 0.0f;

        // float base = ((ICoreTool) stack.getItem()).getAttackSpeedModifier();
        float speed = EquipmentData.getStat(stack, CommonItemStats.ATTACK_SPEED);
        if (isBroken(stack))
            speed += 0.7f;
        return /* base + */ speed;
    }

    public static Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        String name = stack.getItem() instanceof ItemTool ? "Tool modifier" : "Weapon modifier";

        @SuppressWarnings("deprecation")
        Multimap<String, AttributeModifier> map = stack.getItem().getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            // Melee Damage
            String key = SharedMonsterAttributes.ATTACK_DAMAGE.getName();
            float value = getMeleeDamageModifier(stack);
            replaceAttributeModifierInMap(map, key, value);

            // Melee Speed
            key = SharedMonsterAttributes.ATTACK_SPEED.getName();
            value = getAttackSpeedModifier(stack);
            replaceAttributeModifierInMap(map, key, value);
        }

        return map;
    }

    private static void replaceAttributeModifierInMap(Multimap<String, AttributeModifier> map, String key, float value) {
        if (map.containsKey(key)) {
            Iterator<AttributeModifier> iter = map.get(key).iterator();
            if (iter.hasNext()) {
                AttributeModifier mod = iter.next();
                map.removeAll(key);
                map.put(key, new AttributeModifier(mod.getID(), mod.getName(), value, mod.getOperation()));
            }
        }
    }

    public static boolean getIsRepairable(ItemStack stack, ItemStack material) {
        ItemPartData data = EquipmentData.getPrimaryPart(stack);
        ItemPartData dataMaterial = ItemPartData.fromStack(material);
        return data != null && dataMaterial != null && data.part.getTier() <= dataMaterial.part.getTier();
    }

    public static void attemptDamageTool(ItemStack stack, int amount, EntityLivingBase entityLiving) {
        if (isUnbreakable(stack) || (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode))
            return;

        if (!Config.toolsBreakPermanently)
            amount = Math.min(stack.getMaxDamage() - stack.getItemDamage(), amount);
        EntityPlayerMP player = entityLiving instanceof EntityPlayerMP ? (EntityPlayerMP) entityLiving : null;
        boolean wouldBreak = stack.attemptDamageItem(amount, SilentGear.random, player);

        if (isBroken(stack)) {
            // The item broke
            entityLiving.renderBrokenItemStack(stack);
            EquipmentData.recalculateStats(stack);
        } else if (Config.toolsBreakPermanently && wouldBreak) {
            entityLiving.renderBrokenItemStack(stack);
            stack.shrink(1);
        }
    }

    public static boolean isBroken(ItemStack stack) {
        // if (tool.getItem() instanceof ItemGemArrow) {
        // // Quick hack for arrow coloring.
        // return true;
        // }

        if (Config.toolsBreakPermanently)
            return false;

        int maxDamage = stack.getMaxDamage();
        if (stack.isEmpty() || maxDamage <= 0)
            return false;
        return stack.getItemDamage() >= maxDamage;
    }

    public static boolean isUnbreakable(ItemStack stack) {
        // TODO: Is this the best solution?
        return stack.getMaxDamage() >= CommonItemStats.DURABILITY.getMaximumValue();
    }

    public static boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        // TODO
        return false;
    }

    public static boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        // TODO
        if (!isBroken(stack))
            attemptDamageTool(stack, 1, entityLiving);
        return true;
    }

    public static boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        // TODO
        boolean isSword = stack.getItem() instanceof ItemSword;
        boolean isShield = stack.getItem() instanceof ItemShield;
        boolean isTool = stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe;
        boolean isBroken = isBroken(stack);

        if (!isBroken) {
            int currentDmg = stack.getItemDamage();
            int maxDmg = stack.getMaxDamage();
            attemptDamageTool(stack, isTool ? 2 : (isSword || isShield ? 1 : 0), attacker);

            if (isBroken(stack))
                attacker.renderBrokenItemStack(stack);
        }

        return !isBroken && isTool;
    }

    public static void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        // TODO
    }

    public static boolean onEntityItemUpdate(EntityItem entityItem) {
        // TODO
        return false;
    }

    public static EnumRarity getRarity(ItemStack stack) {
        int rarity = EquipmentData.getStatInt(stack, CommonItemStats.RARITY);
        if (rarity < 20)
            return EnumRarity.COMMON;
        if (rarity < 40)
            return EnumRarity.UNCOMMON;
        if (rarity < 60)
            return EnumRarity.RARE;
        if (rarity < 80)
            return EnumRarity.EPIC;
        return SilentGear.RARITY_LEGENDARY;
    }

    public static String getItemStackDisplayName(ItemStack stack) {
        ICoreItem item = (ICoreItem) stack.getItem();
        ItemPartData data = EquipmentData.getPrimaryPart(stack);
        if (data == null || data.part == null)
            return SilentGear.localization.getLocalizedString(stack.getUnlocalizedName() + ".name");
        String partName = data.part.getLocalizedName(data, ItemStack.EMPTY);
        return SilentGear.localization.getItemSubText(item.getItemClassName(), "nameProper", partName);
    }

    public static class EventHandler {

        public static EventHandler INSTANCE = new EventHandler();

        // TODO
    }
}
