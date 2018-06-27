package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.config.Config;

import javax.annotation.Nullable;
import java.util.*;

public class GearHelper {

    //region Attribute modifiers

    public static float getMeleeDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 1f;

        float val = GearData.getStat(stack, CommonItemStats.MELEE_DAMAGE);
        return val < 0 ? 0 : val;
    }

    public static float getMagicDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 0f;

        float val = GearData.getStat(stack, CommonItemStats.MAGIC_DAMAGE);
        return val < 0 ? 0 : val;
    }

    public static float getAttackSpeedModifier(ItemStack stack) {
        if (!(stack.getItem() instanceof ICoreTool))
            return 0.0f;

        float speed = GearData.getStat(stack, CommonItemStats.ATTACK_SPEED);
        if (isBroken(stack))
            speed += 0.7f;
        return speed;
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

    //endregion


    //region Damage and repair

    public static boolean getIsRepairable(ItemStack stack, ItemStack material) {
        ItemPartData data = GearData.getPrimaryPart(stack);
        ItemPartData dataMaterial = ItemPartData.fromStack(material);
        return data != null && dataMaterial != null && data.part.getTier() <= dataMaterial.part.getTier();
    }

    public static void attemptDamage(ItemStack stack, int amount, EntityLivingBase entityLiving) {
        if (isUnbreakable(stack) || (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode))
            return;

        if (!Config.toolsBreakPermanently)
            amount = Math.min(stack.getMaxDamage() - stack.getItemDamage(), amount);
        EntityPlayerMP player = entityLiving instanceof EntityPlayerMP ? (EntityPlayerMP) entityLiving : null;
        boolean wouldBreak = stack.attemptDamageItem(amount, SilentGear.random, player);

        if (isBroken(stack)) {
            // The item broke
            entityLiving.renderBrokenItemStack(stack);
            GearData.recalculateStats(stack);
        } else if (Config.toolsBreakPermanently && wouldBreak) {
            entityLiving.renderBrokenItemStack(stack);
            stack.shrink(1);
        }
    }

    public static boolean isBroken(ItemStack stack) {
        // if (gear.getItem() instanceof ItemGemArrow) {
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

    //endregion

    public static float getDestroySpeed(ItemStack stack, IBlockState state, @Nullable Set<Material> extraMaterials) {
        if (isBroken(stack))
            return 0.25f;

        float speed = GearData.getStat(stack, CommonItemStats.HARVEST_SPEED);

        // Tool effective on block?
        if (stack.getItem().canHarvestBlock(state, stack)) {
            return speed;
        }

        // Check tool classes
        for (String type : stack.getItem().getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state)) {
                return speed;
            }
        }

        // Check extra materials
        if (extraMaterials != null && extraMaterials.contains(state.getMaterial())) {
            return speed;
        }

        // Tool ineffective.
        return 1f;
    }

    @Deprecated
    public static boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        // Used for statistics
        return false;
    }

    public static boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!isBroken(stack) && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnBlockBreak(stack, world, state, pos);
            attemptDamage(stack, damage, entityLiving);
        }
        return true;
    }

    public static boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean isBroken = isBroken(stack);
        if (!isBroken && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnHitEntity(stack, target, attacker);
            attemptDamage(stack, damage, attacker);
        }
        return !isBroken;
    }

    public static void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        // TODO
    }

    public static boolean onEntityItemUpdate(EntityItem entityItem) {
        // TODO
        return false;
    }

    private static Map<String, List<ItemStack>> subItemCache = new HashMap<>();

    public static void getSubItems(ICoreItem item, CreativeTabs tab, NonNullList<ItemStack> subitems) {
        if (!subItemCache.containsKey(item.getGearClass())) {
            List<ItemStack> list = new ArrayList<>();
            // TODO: How should we handle gear subitems?
            for (PartMain part : PartRegistry.getVisibleMains()) {
                ItemStack stack = item.construct(item.getItem(), part.getCraftingStack());
                GearData.setExampleTag(stack, true);
                list.add(stack);
            }
            subItemCache.put(item.getGearClass(), list);
        }
        subitems.addAll(subItemCache.get(item.getGearClass()));
    }

    public static void resetSubItemsCache() {
        subItemCache.clear();
    }

    public static String getItemStackDisplayName(ItemStack stack) {
        ICoreItem item = (ICoreItem) stack.getItem();
        ItemPartData data = GearData.getPrimaryPart(stack);
        if (data == null || data.part == null)
            return SilentGear.localization.getLocalizedString(stack.getUnlocalizedName() + ".name");
        String partName = data.part.getLocalizedName(data, ItemStack.EMPTY);
        return SilentGear.localization.getItemSubText(item.getGearClass(), "nameProper", partName);
    }

    public static class EventHandler {

        public static EventHandler INSTANCE = new EventHandler();

        // TODO
    }
}
