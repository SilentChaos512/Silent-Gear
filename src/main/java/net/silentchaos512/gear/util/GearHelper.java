package net.silentchaos512.gear.util;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GearNamePrefixesEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.IPartData;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.crafting.ingredient.IPartIngredient;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.item.MiscUpgrades;
import net.silentchaos512.gear.parts.LazyPartData;
import net.silentchaos512.gear.parts.PartConst;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.traits.TraitConst;
import net.silentchaos512.lib.advancements.LibTriggers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Contains various methods used by gear items. Many are delegates for item overrides, to cut down
 * on code duplication. But there are some useful helper methods, like {@link #isGear}.
 * <p>
 * Also see {@link GearData}, which focuses on getting/updating item data and NBT.
 */
public final class GearHelper {
    public static final ResourceLocation DAMAGE_FACTOR_CHANGE = SilentGear.getId("damage_factor_change");

    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("5e889b20-a8bd-43df-9ece-88a9f9be7530");
    private static final float BROKEN_ATTACK_SPEED_CHANGE = 0.7f;
    private static final float BROKEN_DESTROY_SPEED = 0.25f;
    private static final int DAMAGE_FACTOR_LEVELS = 10;

    private GearHelper() {}

    /**
     * Check if the item is a Silent Gear tool, weapon, or armor item.
     *
     * @param stack The item
     * @return True if {@code stack} is a gear item
     */
    public static boolean isGear(ItemStack stack) {
        return stack.getItem() instanceof ICoreItem;
    }

    /**
     * Check if the item is a Silent Gear tool, weapon, or armor item. Also checks that the stack is
     * not null and not empty.
     *
     * @param stack The item
     * @return True if {@code stack} is a gear item, false if null, empty, or not a gear item
     */
    public static boolean isGearNullable(@Nullable ItemStack stack) {
        return stack != null && !stack.isEmpty() && isGear(stack);
    }

    //region Attribute modifiers

    public static float getMeleeDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 1f;

        float val = GearData.getStat(stack, ItemStats.MELEE_DAMAGE);
        return val < 0 ? 0 : val;
    }

    public static float getMagicDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 0f;

        float val = GearData.getStat(stack, ItemStats.MAGIC_DAMAGE);
        return val < 0 ? 0 : val;
    }

    public static float getAttackSpeedModifier(ItemStack stack) {
        if (!(stack.getItem() instanceof ICoreTool))
            return 0.0f;

        float speed = GearData.getStat(stack, ItemStats.ATTACK_SPEED);
        if (isBroken(stack))
            speed += BROKEN_ATTACK_SPEED_CHANGE;
        return speed;
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return getAttributeModifiers(slot, stack, true);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack, boolean addStandardMainHandMods) {
        // Need to use this version to prevent stack overflow
        @SuppressWarnings("deprecation") Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create(stack.getItem().getAttributeModifiers(slot));

        return getAttributeModifiers(slot, stack, map, addStandardMainHandMods);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map) {
        return getAttributeModifiers(slot, stack, map, true);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map, boolean addStandardMainHandMods) {
        if (addStandardMainHandMods && slot == EquipmentSlotType.MAINHAND) {
            // Melee Damage
            replaceAttributeModifierInMap(map, Attributes.ATTACK_DAMAGE, getMeleeDamageModifier(stack));

            // Melee Speed
            replaceAttributeModifierInMap(map, Attributes.ATTACK_SPEED, getAttackSpeedModifier(stack));

            // Reach distance
            ForgeMod.REACH_DISTANCE.ifPresent(attr -> {
                float reachStat = GearData.getStat(stack, ItemStats.REACH_DISTANCE);
                AttributeModifier mod = new AttributeModifier(REACH_MODIFIER_UUID, "Gear reach", reachStat, AttributeModifier.Operation.ADDITION);
                map.put(attr, mod);
            });
        }

        TraitHelper.getCachedTraits(stack).forEach((trait, level) -> trait.onGetAttributeModifiers(new TraitActionContext(null, level, stack), map, slot));

        return map;
    }

    private static void replaceAttributeModifierInMap(Multimap<Attribute, AttributeModifier> map, Attribute key, float value) {
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

    public static ItemStat getDurabilityStat(ItemStack gear) {
        return gear.getItem() instanceof ICoreItem ? ((ICoreItem) gear.getItem()).getDurabilityStat() : ItemStats.DURABILITY;
    }

    public static void attemptDamage(ItemStack stack, int amount, @Nullable LivingEntity entity, Hand hand) {
        attemptDamage(stack, amount, entity, hand == Hand.OFF_HAND ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND);
    }

    public static void attemptDamage(ItemStack stack, int amount, @Nullable LivingEntity entity, EquipmentSlotType slot) {
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

    public static ActionResultType useAndCheckBroken(ItemUseContext context, Function<ItemUseContext, ActionResultType> useFunction) {
        ActionResultType result = useFunction.apply(context);
        if (context.getPlayer() instanceof ServerPlayerEntity)
            handleBrokenItem(context.getItem(), (ServerPlayerEntity) context.getPlayer(), context.getHand() == Hand.OFF_HAND ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND);
        return result;
    }

    private static void onDamageFactorChange(ServerPlayerEntity player, int preDamageFactor, int newDamageFactor) {
        if (newDamageFactor > preDamageFactor) {
            player.world.playSound(null, player.func_233580_cy_(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.5f, 2.0f);
            LibTriggers.GENERIC_INT.trigger(player, DAMAGE_FACTOR_CHANGE, 1);
        }
    }

    private static void notifyPlayerOfBrokenGear(ItemStack stack, ServerPlayerEntity player) {
        // Notify player. Mostly for armor, but might help new players as well.
        // FIXME: Does not work with armor currently, need to find a way to get player
        player.sendMessage(new TranslationTextComponent("misc.silentgear.notifyOnBreak", stack.getDisplayName()), Util.DUMMY_UUID);
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
        return Config.Server.gearBreaksPermanently.get() || GearData.hasPart(stack, MiscUpgrades.RED_CARD.getPartId());
    }

    public static boolean isBroken(ItemStack stack) {
        if (stack.isEmpty() || canBreakPermanently(stack) || isUnbreakable(stack))
            return false;

        int maxDamage = stack.getMaxDamage();
        return maxDamage > 0 && stack.getDamage() >= maxDamage - 1;
    }

    public static boolean isUnbreakable(ItemStack stack) {
        return TraitHelper.getTraitLevel(stack, TraitConst.INDESTRUCTIBLE) > 0;
    }

    public static void setDamage(ItemStack stack, int damage, BiConsumer<ItemStack, Integer> superFunction) {
        int newDamage = GearHelper.calcDamageClamped(stack, damage);
        int diff = newDamage - stack.getDamage();
        if (diff > 0 && !GearHelper.isBroken(stack)) {
            GearHelper.damageParts(stack, diff);
        }
        superFunction.accept(stack, newDamage);
        if (GearHelper.isBroken(stack)) {
            GearData.recalculateStats(stack, null);
        }
    }

    public static void damageParts(ItemStack stack, int amount) {
        GearData.getConstructionParts(stack).forEach(p -> p.getPart().onGearDamaged(p, stack, amount));
    }

    //endregion

    public static Item.Properties getBuilder(@Nullable ToolType toolType) {
        Item.Properties b = new Item.Properties().maxStackSize(1).group(SilentGear.ITEM_GROUP);
        if (toolType != null) b.addToolType(toolType, 3);
        return b;
    }

    @Deprecated
    public static void addModelTypeProperty(@SuppressWarnings("TypeMayBeWeakened") ICoreItem item) {
/*        PartPositions.LITE_MODEL_LAYERS.forEach((position, partType) -> {
            item.asItem().addPropertyOverride(SilentGear.getId("lite_" + position.getTexturePrefix()), (stack, world, entity) -> {
                PartData part = GearData.getPartOfType(stack, partType);
                return part != null ? part.getPart().getLiteTexture(part, stack).getIndex() : -1;
            });
        });*/
    }

    @Nullable
    public static GearType getType(ItemStack gear) {
        if (gear.isEmpty() || !(gear.getItem() instanceof ICoreItem)) {
            return null;
        }
        return ((ICoreItem) gear.getItem()).getGearType();
    }

    public static int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable BlockState state, @Nullable Set<Material> effectiveMaterials) {
        if (isBroken(stack) || !stack.getItem().getToolTypes(stack).contains(toolClass))
            return -1;

        final int level = GearData.getStatInt(stack, ItemStats.HARVEST_LEVEL);
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

        float speed = GearData.getStat(stack, ItemStats.HARVEST_SPEED);

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

        return true;
    }

    public static boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean isBroken = isBroken(stack);
        if (!isBroken && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnHitEntity(stack, target, attacker);
            attemptDamage(stack, damage, attacker, EquipmentSlotType.MAINHAND);
        }

        return !isBroken;
    }

    // Formerly onUpdate
    public static void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        @Nullable PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

        if (!world.isRemote) {
            TraitHelper.tickTraits(world, player, stack, isSelected);
        }
    }

    public static ActionResultType onItemUse(ItemUseContext context) {
        ActionResultType ret = ActionResultType.PASS;
        Map<ITrait, Integer> traits = TraitHelper.getCachedTraits(context.getItem());
        for (Map.Entry<ITrait, Integer> entry : traits.entrySet()) {
            ActionResultType result = entry.getKey().onItemUse(context, entry.getValue());
            if (result != ActionResultType.PASS) {
                ret = result;
            }
        }
        return ret;
    }

    public static boolean shouldUseFallbackColor(ItemStack stack, PartData part) {
        // TODO
        return true;
    }

    public static Rarity getRarity(ItemStack stack) {
        int rarity = GearData.getStatInt(stack, ItemStats.RARITY);
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

        Collection<ItemStack> list = new ArrayList<>();
        // Create a few samples of each tool type, because rendering performance is a problem on many machines.
        for (int i = 1; i <= Math.max(3, PartManager.getHighestMainPartTier()); ++i) {
            ItemStack stack = createSampleItem(item, i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        items.addAll(list);
    }

    private static ItemStack createSampleItem(ICoreItem item, int tier) {
        ItemStack result = GearGenerator.create(item, tier);
        if (result.isEmpty()) {
            Collection<IPartData> parts = new ArrayList<>();
            parts.add(new LazyPartData(PartConst.MAIN_EXAMPLE));
            if (item.requiresPartOfType(PartType.ROD))
                parts.add(new LazyPartData(PartConst.ROD_EXAMPLE));
            if (item.requiresPartOfType(PartType.BOWSTRING))
                parts.add(new LazyPartData(PartConst.BOWSTRING_EXAMPLE));
            result = item.construct(parts);
        }
        GearData.setExampleTag(result, true);
        return result;
    }

    public static ITextComponent getDisplayName(ItemStack gear) {
        PartData part = GearData.getPrimaryPart(gear);
        if (part == null) return new TranslationTextComponent(gear.getTranslationKey());

        ITextComponent partName = part.getMaterialName(gear);
        ITextComponent gearName = new TranslationTextComponent(gear.getTranslationKey() + ".nameProper", partName);
        ITextComponent result = gearName;

        if (gear.getItem() instanceof ICoreTool) {
            ICoreItem item = (ICoreItem) gear.getItem();
            if (item.requiresPartOfType(PartType.ROD) && GearData.getPartOfType(gear, PartType.ROD) == null) {
                result = new TranslationTextComponent(gear.getTranslationKey() + ".noRod", gearName);
            } else if (item.requiresPartOfType(PartType.BOWSTRING) && GearData.getPartOfType(gear, PartType.BOWSTRING) == null) {
                result = new TranslationTextComponent(gear.getTranslationKey() + ".unstrung", gearName);
            }
        }

        // Prefixes
        // TODO: Probably should cache this somehow...
        for (ITextComponent t : getNamePrefixes(gear, GearData.getConstructionParts(gear))) {
            // TODO: Spaces are probably inappropriate for some languages?
            result = t.deepCopy().func_230529_a_(new StringTextComponent(" ")).func_230529_a_(result);
        }

        return result;
    }

    private static Collection<ITextComponent> getNamePrefixes(ItemStack gear, PartDataList parts) {
        GearNamePrefixesEvent event = new GearNamePrefixesEvent(gear, parts);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getPrefixes();
    }

    public static Collection<IPartData> getExamplePartsFromRecipe(GearType gearType, Iterable<Ingredient> ingredients) {
        Collection<IPartData> list = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof IPartIngredient) {
                PartType type = ((IPartIngredient) ingredient).getPartType();
                type.getCompoundPartItem(gearType).ifPresent(item -> {
                    ItemStack stack = item.create(Collections.singletonList(LazyMaterialInstance.of(Const.EXAMPLE)));
                    list.add(LazyPartData.of(type.getCompoundPartId(gearType), stack));
                });
            } else {
                // This isn't perfect, since parts may not be loaded at this time...
                ItemStack[] matchingStacks = ingredient.getMatchingStacks();
                if (matchingStacks.length > 0) {
                    PartData part = PartData.fromStackFast(matchingStacks[0]);
                    if (part != null)
                        list.add(part);
                }
            }
        }

        return list;
    }
}
