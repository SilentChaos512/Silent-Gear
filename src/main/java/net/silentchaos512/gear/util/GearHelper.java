package net.silentchaos512.gear.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.item.MiscUpgrades;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.lib.advancements.LibTriggers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class GearHelper {
    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("5e889b20-a8bd-43df-9ece-88a9f9be7530");
    private static final float BROKEN_ATTACK_SPEED_CHANGE = 0.7f;
    private static final float BROKEN_DESTROY_SPEED = 0.25f;
    private static final int DAMAGE_FACTOR_LEVELS = 10;

    private GearHelper() {}

    public static boolean isGear(ItemStack stack) {
        return stack.getItem() instanceof ICoreItem;
    }

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

    public static Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        // Need to use this version to prevent stack overflow
        @SuppressWarnings("deprecation") Multimap<String, AttributeModifier> map = stack.getItem().getAttributeModifiers(slot);

        if (slot == EquipmentSlotType.MAINHAND) {
            // Melee Damage
            String key = SharedMonsterAttributes.ATTACK_DAMAGE.getName();
            float value = getMeleeDamageModifier(stack);
            replaceAttributeModifierInMap(map, key, value);

            // Melee Speed
            key = SharedMonsterAttributes.ATTACK_SPEED.getName();
            value = getAttackSpeedModifier(stack);
            replaceAttributeModifierInMap(map, key, value);

            // Reach distance
            float reachStat = GearData.getStat(stack, CommonItemStats.REACH_DISTANCE);
            AttributeModifier reachModifier = new AttributeModifier(REACH_MODIFIER_UUID, "Gear reach", reachStat, AttributeModifier.Operation.ADDITION);
            map.put(PlayerEntity.REACH_DISTANCE.getName(), reachModifier);
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
        PartData data = GearData.getPrimaryPart(stack);
        PartData dataMaterial = PartData.from(material);
        return data != null && dataMaterial != null && data.getTier() <= dataMaterial.getTier();
    }

    @Deprecated
    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity) {
        // TODO: Remove this version when Gems updates
        attemptDamage(stack, amount, entity);
    }

    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity, Hand hand) {
        attemptDamage(stack, amount, entity, hand == Hand.OFF_HAND ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND);
    }

    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity, EquipmentSlotType slot) {
        if (isUnbreakable(stack) || (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.isCreativeMode))
            return;

        ServerPlayerEntity player = entity instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity : null;
        final int preTraitAmount = amount;
        amount = (int) TraitHelper.activateTraits(stack, preTraitAmount, (trait, level, val) ->
                trait.onDurabilityDamage(new TraitActionContext(player, level, stack), (int) val));

        final int maxDamage = stack.getMaxDamage();
        final int preDamageFactor = getDamageFactor(stack, maxDamage);
        if (!canBreakPermanently(stack))
            amount = Math.min(maxDamage - stack.getDamage(), amount);
        stack.attemptDamageItem(amount, SilentGear.random, player);

        // Recalculate stats occasionally
        if (getDamageFactor(stack, maxDamage) != preDamageFactor) {
            GearData.recalculateStats(stack, player);
            if (player != null)
                onDamageFactorChange(player, preDamageFactor, getDamageFactor(stack, maxDamage));
        }

        handleBrokenItem(stack, player, slot);
    }

    private static void handleBrokenItem(ItemStack stack, @Nullable ServerPlayerEntity player, EquipmentSlotType slot) {
        if (isBroken(stack)) {
            // The item "broke" (can still be repaired)
            GearData.incrementBrokenCount(stack);
            GearData.recalculateStats(stack, player);
            if (player != null) {
                player.sendBreakAnimation(slot); // entity.renderBrokenItemStack(stack);
                notifyPlayerOfBrokenGear(stack, player);
            }
        } else if (canBreakPermanently(stack) && stack.getDamage() > stack.getMaxDamage()) {
            // Item is gone forever, rest in pieces
            if (player != null) {
                player.sendBreakAnimation(slot); // entity.renderBrokenItemStack(stack);
            }
            stack.shrink(1);
        }
    }

    private static void onDamageFactorChange(ServerPlayerEntity player, int preDamageFactor, int newDamageFactor) {
        if (newDamageFactor > preDamageFactor) {
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.5f, 2.0f);
            LibTriggers.GENERIC_INT.trigger(player, new ResourceLocation(SilentGear.MOD_ID, "damage_factor_change"), 1);
        }
    }

    private static void notifyPlayerOfBrokenGear(ItemStack stack, ServerPlayerEntity player) {
        // Notify player. Mostly for armor, but might help new players as well.
        // FIXME: Does not work with armor currently, need to find a way to get player
        player.sendMessage(new TranslationTextComponent("misc.silentgear.notifyOnBreak", stack.getDisplayName()));
    }

    private static int getDamageFactor(ItemStack stack, int maxDamage) {
        if (maxDamage == 0) return 1;
        int step = Math.max(1, maxDamage / DAMAGE_FACTOR_LEVELS);
        return stack.getDamage() / step;
    }

    // Used by setDamage in gear items to prevent other mods from breaking them
    public static int calcDamageClamped(ItemStack stack, int damage) {
        if (isUnbreakable(stack)) return 0;

        if (!canBreakPermanently(stack)) {
            if (damage > stack.getDamage()) damage = Math.min(stack.getMaxDamage(), damage);
            else damage = Math.max(0, damage);
        }
        return damage;
    }

    private static boolean canBreakPermanently(ItemStack stack) {
        return Config.GENERAL.gearBreaksPermanently.get() || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPartId());
    }

    public static boolean isBroken(ItemStack stack) {
        // if (gear.getItem() instanceof ItemGemArrow) {
        // // Quick hack for arrow coloring.
        // return true;
        // }

        if (stack.isEmpty() || canBreakPermanently(stack))
            return false;

        int maxDamage = stack.getMaxDamage();
        return maxDamage > 0 && stack.getDamage() >= maxDamage - 1;
    }

    public static boolean isUnbreakable(ItemStack stack) {
        // TODO: Is this the best solution?
        return stack.getMaxDamage() >= CommonItemStats.DURABILITY.getMaximumValue();
    }

    //endregion

    public static Item.Properties getBuilder(@Nullable ToolType toolType) {
        Item.Properties b = new Item.Properties().maxStackSize(1).group(SilentGear.ITEM_GROUP);
        if (toolType != null) b.addToolType(toolType, 3);
        return b;
    }

    public static void addModelTypeProperty(ICoreItem item) {
        // TODO: Add grips and bindings. Something to change head/rod textures and highlight?
        item.asItem().addPropertyOverride(SilentGear.getId("model_type"), (stack, world, entity) ->
                (GearData.hasPartOfType(stack, PartType.ROD) ? 1 : 0)
                        + (GearData.hasPartOfType(stack, PartType.MAIN) ? 2 : 0)
                        + (GearData.hasPartOfType(stack, PartType.TIP) ? 4 : 0)
                        + (item.requiresPartOfType(PartType.BOWSTRING) && GearData.hasPartOfType(stack, PartType.BOWSTRING) ? 8 : 0)
        );
    }

    @Nullable
    public static GearType getType(ItemStack gear) {
        if (!(gear.getItem() instanceof ICoreItem)) {
            return null;
        }
        return ((ICoreItem) gear.getItem()).getGearType();
    }

    public static int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable BlockState state, @Nullable Set<Material> effectiveMaterials) {
        if (isBroken(stack) || !stack.getItem().getToolTypes(stack).contains(toolClass))
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

    public static float getDestroySpeed(ItemStack stack, BlockState state, @Nullable Set<Material> extraMaterials) {
        if (isBroken(stack))
            return BROKEN_DESTROY_SPEED;

        float speed = GearData.getStat(stack, CommonItemStats.HARVEST_SPEED);

        // Tool effective on block?
        if (stack.getItem().canHarvestBlock(stack, state)) {
            return speed;
        }

        // Check tool classes
        for (ToolType type : stack.getItem().getToolTypes(stack)) {
            if (state.getBlock().isToolEffective(state, type)) {
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

    public static boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!isBroken(stack) && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnBlockBreak(stack, world, state, pos);
            attemptDamage(stack, damage, entityLiving, EquipmentSlotType.MAINHAND);
        }
//        GearStatistics.incrementStat(stack, GearStatistics.BLOCKS_MINED);

        // TODO: Implement multi-break skill

        selfHarmWithToolHead(stack, entityLiving);

        return true;
    }

    public static boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean isBroken = isBroken(stack);
        if (!isBroken && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnHitEntity(stack, target, attacker);
            attemptDamage(stack, damage, attacker, EquipmentSlotType.MAINHAND);
        }

        selfHarmWithToolHead(stack, attacker);

        return !isBroken;
    }

    private static void selfHarmWithToolHead(ItemStack stack, LivingEntity user) {
        // If missing rod, hurt the user
        if (stack.getItem() instanceof ICoreItem) {
            ICoreItem item = (ICoreItem) stack.getItem();
            if (GearData.isMissingRequiredPart(stack, PartType.ROD)) {
                float damageAmount = item.getStat(stack, CommonItemStats.MELEE_DAMAGE) / 2;
                DamageSource source = new DamageSource("silentgear.broken_tool") {
                    @Nonnull
                    @Override
                    public ITextComponent getDeathMessage(LivingEntity entity) {
                        return new TranslationTextComponent("death.silentgear.broken_tool",
                                entity.getDisplayName(),
                                stack.getDisplayName());
                    }
                }.setDamageBypassesArmor();

                user.attackEntityFrom(source, damageAmount);

                if (user instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) user;
                    player.sendStatusMessage(new TranslationTextComponent("misc.silentgear.missingRod.attack"), true);
                }
            }
        }
    }

    // Formerly onUpdate
    public static void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        @Nullable PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

        if (world.getGameTime() % 20 == 0) {
            // Any ungraded parts get a random grade
            if (!GearData.isRandomGradingDone(stack)) {
                MaterialGrade median = Config.GENERAL.randomGradeMean.get();
                MaterialGrade maxGrade = Config.GENERAL.randomGradeMax.get();
                double stdDev = Config.GENERAL.randomGradeStd.get();

                PartDataList parts = PartDataList.of();
                for (PartData data : GearData.getConstructionParts(stack)) {
                    if (data.getGrade() == MaterialGrade.NONE) {
                        MaterialGrade grade = MaterialGrade.selectRandom(SilentGear.random, median, stdDev, maxGrade);
                        parts.add(PartData.of(data.getPart(), grade, data.getCraftingItem()));
                    } else {
                        parts.add(data);
                    }
                }
                GearData.writeConstructionParts(stack, parts);
                GearData.setRandomGradingDone(stack, true);
                GearData.recalculateStats(stack, player);
            }
        }

        if (!world.isRemote) {
            TraitHelper.tickTraits(world, player, stack, isSelected);
        }
    }

    public static boolean shouldUseFallbackColor(ItemStack stack, PartData part) {
        // TODO
        return false;
    }

    public static Rarity getRarity(ItemStack stack) {
        int rarity = GearData.getStatInt(stack, CommonItemStats.RARITY);
        if (stack.isEnchanted())
            rarity += 20;

        if (rarity < 40)
            return Rarity.COMMON;
        if (rarity < 80)
            return Rarity.UNCOMMON;
        if (rarity < 120)
            return Rarity.RARE;
        return Rarity.EPIC;
    }

    private static final Map<GearType, List<ItemStack>> subItemCache = new HashMap<>();

    // Formerly getSubItems
    public static void fillItemGroup(ICoreItem item, ItemGroup group, Collection<ItemStack> items) {
        boolean inTab = false;
        for (ItemGroup tabInList : item.asItem().getCreativeTabs()) {
            if (tabInList == group) {
                inTab = true;
                break;
            }
        }
        if (!inTab) return;

        if (!subItemCache.containsKey(item.getGearType())) {
            List<ItemStack> list = new ArrayList<>();
            // Create a few samples of each tool type, because rendering performance is a problem on many machines.
            for (int i = 1; i <= 3 /*PartRegistry.getHighestMainPartTier()*/; ++i) {
                ItemStack stack = createSampleItem(item, i);
                if (!stack.isEmpty())
                    list.add(stack);
            }
            subItemCache.put(item.getGearType(), list);
        }
        items.addAll(subItemCache.get(item.getGearType()));
    }

    private static ItemStack createSampleItem(ICoreItem item, int tier) {
        ItemStack result = GearGenerator.create(item, tier);
        GearData.setExampleTag(result, true);
        return result;
    }

    public static void resetSubItemsCache() {
        subItemCache.clear();
    }

    public static ITextComponent getDisplayName(ItemStack gear) {
        PartData part = GearData.getPrimaryPart(gear);
        if (part == null) return new TranslationTextComponent(gear.getTranslationKey());

        ITextComponent partName = part.getDisplayName(gear);
        ITextComponent gearName = new TranslationTextComponent(gear.getTranslationKey() + ".nameProper", partName);

        if (gear.getItem() instanceof ICoreTool) {
            ICoreItem item = (ICoreItem) gear.getItem();
            if (item.requiresPartOfType(PartType.ROD) && GearData.getPartOfType(gear, PartType.ROD) == null) {
                return new TranslationTextComponent(gear.getTranslationKey() + ".noRod", gearName);
            }
            if (item.requiresPartOfType(PartType.BOWSTRING) && GearData.getPartOfType(gear, PartType.BOWSTRING) == null) {
                return new TranslationTextComponent(gear.getTranslationKey() + ".unstrung", gearName);
            }
        }

        return gearName;
    }
}
