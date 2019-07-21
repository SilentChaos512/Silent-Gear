package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
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
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.registry.RecipeMaker;

import javax.annotation.Nullable;
import java.util.*;

public final class GearHelper {
    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("5e889b20-a8bd-43df-9ece-88a9f9be7530");
    private static final float BROKEN_ATTACK_SPEED_CHANGE = 0.7f;
    private static final float BROKEN_DESTROY_SPEED = 0.25f;
    private static final int DAMAGE_FACTOR_LEVELS = 10;

    private GearHelper() {}

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
            speed += BROKEN_ATTACK_SPEED_CHANGE;
        return speed;
    }

    public static Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
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
        final boolean canBreakPermanently = Config.gearBreaksPermanently || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPart());

        EntityPlayerMP player = entityLiving instanceof EntityPlayerMP ? (EntityPlayerMP) entityLiving : null;
        final int preTraitAmount = amount;
        amount = (int) TraitHelper.activateTraits(stack, preTraitAmount,
                (trait, level, val) -> trait.onDurabilityDamage(player, level, stack, (int) val));

        final int maxDamage = stack.getMaxDamage();
        final int preDamageFactor = getDamageFactor(stack, maxDamage);
        if (!canBreakPermanently)
            amount = Math.min(maxDamage - stack.getItemDamage(), amount);
        boolean wouldBreak = stack.attemptDamageItem(amount, SilentGear.RANDOM, player);

        // Recalculate stats occasionally
        if (getDamageFactor(stack, maxDamage) != preDamageFactor) {
            GearData.recalculateStats(stack);
            if (player != null)
                onDamageFactorChange(player, preDamageFactor, getDamageFactor(stack, maxDamage));
        }

        if (isBroken(stack)) {
            // The item "broke" (can still be repaired)
            entityLiving.renderBrokenItemStack(stack);
            GearData.incrementBrokenCount(stack);
            GearData.recalculateStats(stack);
            if (player != null)
                notifyPlayerOfBrokenGear(stack, player);
        } else if (canBreakPermanently && wouldBreak) {
            // Item is gone forever, rest in pieces
            entityLiving.renderBrokenItemStack(stack);
            stack.shrink(1);
        }
    }

    private static void onDamageFactorChange(EntityPlayerMP player, int preDamageFactor, int newDamageFactor) {
        if (newDamageFactor > preDamageFactor) {
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK,
                    SoundCategory.PLAYERS, 0.5f, 2.0f);

            LibTriggers.GENERIC_INT.trigger(player, new ResourceLocation(SilentGear.MOD_ID, "damage_factor_change"), 1);
        }
    }

    private static void notifyPlayerOfBrokenGear(ItemStack stack, EntityPlayerMP player) {
        // Notify player. Mostly for armor, but might help new players as well.
        // FIXME: Does not work with armor currently, need to find a way to get player
        player.sendMessage(new TextComponentTranslation("misc.silentgear.notifyOnBreak", stack.getDisplayName()));
    }

    private static int getDamageFactor(ItemStack stack, int maxDamage) {
        if (maxDamage == 0) return 1;
        int step = Math.max(1, maxDamage / DAMAGE_FACTOR_LEVELS);
        return stack.getItemDamage() / step;
    }

    // Used by setDamage in gear items to prevent other mods from breaking them
    public static int calcDamageClamped(ItemStack stack, int damage) {
        if (isUnbreakable(stack)) return 0;
        final boolean canBreakPermanently = Config.gearBreaksPermanently || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPart());

        if (!canBreakPermanently) {
            if (damage > stack.getItemDamage()) damage = Math.min(stack.getMaxDamage(), damage);
            else damage = Math.max(0, damage);
        }
        return damage;
    }

    public static boolean isBroken(ItemStack stack) {
        // if (gear.getItem() instanceof ItemGemArrow) {
        // // Quick hack for arrow coloring.
        // return true;
        // }

        if (Config.gearBreaksPermanently || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPart()))
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

    public static int getHarvestLevel(ItemStack stack, String toolClass, @Nullable IBlockState state, @Nullable Set<Material> effectiveMaterials) {
        if (isBroken(stack) || !stack.getItem().getToolClasses(stack).contains(toolClass))
            return -1;

        final int level = GearData.getStatInt(stack, CommonItemStats.HARVEST_LEVEL);
        if (state == null) return level;

        final boolean effectiveOnMaterial = effectiveMaterials == null || effectiveMaterials.contains(state.getMaterial());
        if (effectiveOnMaterial && state.getBlock().getHarvestLevel(state) <= level)
            return level;
        else return -1;
    }

    public static void setHarvestLevel(ICoreItem item, String toolClass, int level, Set<String> mutableSet) {
        // Add tool class to list if level is non-negative. Because this is on the item level, the
        // actual number is meaningless. Harvest levels can be customized in the material JSONs.
        final boolean add = level >= 0;
        SilentGear.LOGGER.info("{}: {} tool class \"{}\"", item.getClass().getSimpleName(), (add ? "set" : "remove"), toolClass);
        if (add) mutableSet.add(toolClass);
        else mutableSet.remove(toolClass);
    }

    public static float getDestroySpeed(ItemStack stack, IBlockState state, @Nullable Set<Material> extraMaterials) {
        if (isBroken(stack))
            return BROKEN_DESTROY_SPEED;

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

        // TODO: Implement multi-break skill

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
                MaterialGrade median = SilentGear.RANDOM.nextInt(100) < 20 ? MaterialGrade.D : SilentGear.RANDOM.nextInt(100) < 40 ? MaterialGrade.B : MaterialGrade.C;
                PartDataList parts = PartDataList.of();
                for (ItemPartData data : GearData.getConstructionParts(stack)) {
                    if (data.getGrade() == MaterialGrade.NONE) {
                        MaterialGrade grade = MaterialGrade.selectRandom(SilentGear.RANDOM, median, 1.5, MaterialGrade.S);
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

        if (!world.isRemote) {
            EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
            TraitHelper.tickTraits(world, player, stack, isSelected);
        }
    }

    public static boolean shouldUseFallbackColor(ItemStack stack, ItemPartData part) {
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

    private static final Map<String, List<ItemStack>> subItemCache = new HashMap<>();

    public static void getSubItems(ICoreItem item, CreativeTabs tab, NonNullList<ItemStack> subitems) {
        boolean inTab = false;
        for (CreativeTabs tabInList : item.getItem().getCreativeTabs()) {
            if (tabInList == tab) {
                inTab = true;
                break;
            }
        }
        if (!inTab) return;

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

    @Nullable
    public static GearType getType(ItemStack gear) {
        if (gear.getItem() instanceof ICoreItem) {
            return ((ICoreItem) gear.getItem()).getGearType();
        }
        return null;
    }
}
