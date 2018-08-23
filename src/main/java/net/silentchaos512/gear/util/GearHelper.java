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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.MiscUpgrades;
import net.silentchaos512.gear.item.ToolRods;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nullable;
import java.util.*;

public class GearHelper {
    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("5e889b20-a8bd-43df-9ece-88a9f9be7530");

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

            // Reach distance
            map.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER_UUID, "Gear reach",
                    GearData.getStat(stack, CommonItemStats.REACH_DISTANCE), 0));
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
        return data != null && dataMaterial != null && data.getPart().getTier() <= dataMaterial.getPart().getTier();
    }

    public static void attemptDamage(ItemStack stack, int amount, EntityLivingBase entityLiving) {
        if (isUnbreakable(stack) || (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode))
            return;
        final boolean canBreakPermanently = Config.toolsBreakPermanently || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPart());

        if (!canBreakPermanently)
            amount = Math.min(stack.getMaxDamage() - stack.getItemDamage(), amount);
        EntityPlayerMP player = entityLiving instanceof EntityPlayerMP ? (EntityPlayerMP) entityLiving : null;
        boolean wouldBreak = stack.attemptDamageItem(amount, SilentGear.random, player);

        if (isBroken(stack)) {
            // The item "broke" (can still be repaired)
            entityLiving.renderBrokenItemStack(stack);
            GearData.incrementBrokenCount(stack);
            GearData.recalculateStats(stack);
        } else if (canBreakPermanently && wouldBreak) {
            // Item is gone forever, rest in pieces
            entityLiving.renderBrokenItemStack(stack);
            stack.shrink(1);
        }
    }

    public static boolean isBroken(ItemStack stack) {
        // if (gear.getItem() instanceof ItemGemArrow) {
        // // Quick hack for arrow coloring.
        // return true;
        // }

        if (Config.toolsBreakPermanently || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPart()))
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

    public static boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!isBroken(stack) && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnBlockBreak(stack, world, state, pos);
            attemptDamage(stack, damage, entityLiving);
        }
//        GearStatistics.incrementStat(stack, GearStatistics.BLOCKS_MINED);
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
        if (world.getTotalWorldTime() % 20 == 0) {
            // Any ungraded parts get a random grade
            if (!GearData.isRandomGradingDone(stack)) {
                // Select D, C, or B as median
                MaterialGrade median = SilentGear.random.nextInt(100) < 20 ? MaterialGrade.D : SilentGear.random.nextInt(100) < 40 ? MaterialGrade.B : MaterialGrade.C;
                PartDataList parts = PartDataList.of();
                for (ItemPartData data : GearData.getConstructionParts(stack)) {
                    if (data.getGrade() == MaterialGrade.NONE) {
                        MaterialGrade grade = MaterialGrade.selectRandom(SilentGear.random, median, 1.5, MaterialGrade.S);
                        parts.add(ItemPartData.instance(data.getPart(), grade, data.getCraftingItem()));
                    } else {
                        parts.add(data);
                    }
                }
                GearData.writeConstructionParts(stack, parts);
                GearData.setRandomGradingDone(stack, true);
                GearData.recalculateStats(stack);
            }
        }
    }

    public static boolean onEntityItemUpdate(EntityItem entityItem) {
        // TODO
        return false;
    }

    public static EnumRarity getRarity(ItemStack stack) {
        int rarity = GearData.getStatInt(stack, CommonItemStats.RARITY);
        if (stack.isItemEnchanted())
            rarity += 20;

        if (rarity < 20)
            return EnumRarity.COMMON;
        if (rarity < 40)
            return EnumRarity.UNCOMMON;
        if (rarity < 70)
            return EnumRarity.RARE;
        if (rarity < 110)
            return EnumRarity.EPIC;
        return SilentGear.RARITY_LEGENDARY;
    }

    private static Map<String, List<ItemStack>> subItemCache = new HashMap<>();

    public static void getSubItems(ICoreItem item, CreativeTabs tab, NonNullList<ItemStack> subitems) {
        boolean inTab = false;
        for (CreativeTabs tabInList : item.getItem().getCreativeTabs()) {
            if (tabInList == tab) {
                inTab = true;
                break;
            }
        }
        if (!inTab) {
            return;
        }

        if (!subItemCache.containsKey(item.getGearClass())) {
            List<ItemStack> list = new ArrayList<>();
            // Create a few samples of each tool type, because rendering performance is a problem on many machines.
            for (int i = 1; i <= PartRegistry.getHighestMainPartTier(); ++i) {
                ItemStack stack = createSampleItem(item, i);
                if (!stack.isEmpty())
                    list.add(stack);
            }
            subItemCache.put(item.getGearClass(), list);
        }
        subitems.addAll(subItemCache.get(item.getGearClass()));
    }

    private static ItemStack createSampleItem(ICoreItem item, int tier) {
        ItemStack result = GearGenerator.create(item, tier);
        GearData.setExampleTag(result, true);
        return result;
    }

    public static void resetSubItemsCache() {
        subItemCache.clear();
    }

    public static String getItemStackDisplayName(ItemStack stack) {
        ICoreItem item = (ICoreItem) stack.getItem();
        ItemPartData data = GearData.getPrimaryPart(stack);
        if (data == null)
            return SilentGear.i18n.translate(stack.getTranslationKey() + ".name");
        String partName = data.getTranslatedName(ItemStack.EMPTY);
        return SilentGear.i18n.subText(item.getItem(), "nameProper", partName);
    }

    public static Collection<IRecipe> getExampleRecipes(ICoreItem item) {
        RecipeMaker recipes = SilentGear.registry.getRecipeMaker();
        Collection<IRecipe> list = new ArrayList<>();

        for (PartMain part : PartRegistry.getVisibleMains()) {
            ItemStack result = "sword".equals(item.getGearClass())
                    ? item.construct(item.getItem(), part.getCraftingStack(), part.getCraftingStack())
                    : item.construct(item.getItem(), part.getCraftingStack());
            GearData.setExampleTag(result, true);

            ConfigOptionEquipment config = item.getConfig();
            List<Object> inputs = new ArrayList<>();
            inputs.add(ModItems.toolHead.getStack(item.getGearClass(), part, false));
            for (int i = 0; i < config.getRodCount(); ++i)
                inputs.add(ToolRods.WOOD.getPart().getCraftingStack());
            for (int i = 0; i < config.getBowstringCount(); ++i)
                inputs.add(ModMaterials.bowstringString.getCraftingStack());

            list.add(recipes.makeShapeless(result, inputs.toArray()));
        }

        return list;
    }

    public static class EventHandler {

        public static final EventHandler INSTANCE = new EventHandler();

        private EventHandler() {
        }

        // TODO
    }
}
