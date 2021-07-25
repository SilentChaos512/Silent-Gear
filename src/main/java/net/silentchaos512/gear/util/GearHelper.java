package net.silentchaos512.gear.util;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GearNamePrefixesEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.lib.advancements.LibTriggers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;

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

    private GearHelper() {}

    public static Optional<ICoreItem> getItem(ItemStack gear) {
        if (gear.getItem() instanceof ICoreItem) {
            return Optional.of((ICoreItem) gear.getItem());
        }
        return Optional.empty();
    }

    /**
     * Check if the item is a Silent Gear tool, weapon, or armor item.
     *
     * @param stack The item
     * @return True if {@code stack} is a gear item
     */
    public static boolean isGear(ItemStack stack) {
        return stack.getItem() instanceof ICoreItem;
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

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return getAttributeModifiers(slot, stack, true);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack, boolean addStandardMainHandMods) {
        // Need to use this version to prevent stack overflow
        @SuppressWarnings("deprecation") Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create(stack.getItem().getDefaultAttributeModifiers(slot));

        return getAttributeModifiers(slot, stack, map, addStandardMainHandMods);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map) {
        return getAttributeModifiers(slot, stack, map, true);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map, boolean addStandardMainHandMods) {
        return getAttributeModifiers(slot.getName(), stack, map, addStandardMainHandMods);
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(String slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map, boolean addStandardMainHandMods) {
        if (addStandardMainHandMods && isValidSlot(stack, slot) && slot.equals(EquipmentSlot.MAINHAND.getName())) {
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
                map.put(key, new AttributeModifier(mod.getId(), mod.getName(), value, mod.getOperation()));
            }
        }
    }

    public static boolean isValidSlot(ItemStack gear, String slot) {
        if (gear.getItem() instanceof ICoreItem) {
            return ((ICoreItem) gear.getItem()).isValidSlot(slot);
        }
        return false;
    }

    //endregion

    //region Damage and repair

    public static boolean getIsRepairable(ItemStack stack, ItemStack materialItem) {
        MaterialInstance material = MaterialInstance.from(materialItem);
        return material != null && getIsRepairable(stack, material);
    }

    public static boolean getIsRepairable(ItemStack gear, MaterialInstance material) {
        PartData part = GearData.getPrimaryPart(gear);
        return part != null && material.getTier(PartType.MAIN) >= part.getTier() && material.getRepairValue(gear) > 0;
    }

    public static ItemStat getDurabilityStat(ItemStack gear) {
        return getItem(gear).map(ICoreItem::getDurabilityStat).orElse(ItemStats.DURABILITY);
    }

    public static float getRepairModifier(ItemStack gear) {
        return getItem(gear).map(item -> item.getRepairModifier(gear)).orElse(1f);
    }

    public static void attemptDamage(ItemStack stack, int amount, @Nullable LivingEntity entity, InteractionHand hand) {
        attemptDamage(stack, amount, entity, hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
    }

    public static void attemptDamage(ItemStack stack, int amount, @Nullable LivingEntity entity, EquipmentSlot slot) {
        if (isUnbreakable(stack) || (entity instanceof Player && ((Player) entity).abilities.instabuild))
            return;

        ServerPlayer player = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
        final int preTraitAmount = amount;
        amount = (int) TraitHelper.activateTraits(stack, preTraitAmount, (trait, level, val) ->
                trait.onDurabilityDamage(new TraitActionContext(player, level, stack), (int) val));

        final int maxDamage = stack.getMaxDamage();
        final int preDamageFactor = getDamageFactor(stack, maxDamage);
        if (!canBreakPermanently(stack))
            amount = Math.min(maxDamage - stack.getDamageValue(), amount);
        stack.hurt(amount, SilentGear.RANDOM, player);

        // Recalculate stats occasionally
        if (getDamageFactor(stack, maxDamage) != preDamageFactor) {
            GearData.recalculateStats(stack, player);
            if (player != null)
                onDamageFactorChange(player, preDamageFactor, getDamageFactor(stack, maxDamage));
        }

        handleBrokenItem(stack, player, slot);
    }

    private static void handleBrokenItem(ItemStack stack, @Nullable Player player, EquipmentSlot slot) {
        if (isBroken(stack)) {
            // The item "broke" (can still be repaired)
            onBroken(stack, player, slot);
        } else if (canBreakPermanently(stack) && stack.getDamageValue() > stack.getMaxDamage()) {
            // Item is gone forever, rest in pieces
            if (player != null) {
                player.broadcastBreakEvent(slot); // entity.renderBrokenItemStack(stack);
            }
            stack.shrink(1);
        }
    }

    /**
     * Called when an item is newly broken after being damaged. This should be called in most cases.
     * Do not call unless the break animation is not working!
     *
     * @param stack  The gear item
     * @param player The player
     * @param slot   The item/armor slot
     */
    public static void onBroken(ItemStack stack, @Nullable Player player, EquipmentSlot slot) {
        GearData.incrementBrokenCount(stack);
        GearData.recalculateStats(stack, player);
        if (player != null) {
            player.broadcastBreakEvent(slot);
            notifyPlayerOfBrokenGear(stack, player);
        }
    }

    public static InteractionResult useAndCheckBroken(UseOnContext context, Function<UseOnContext, InteractionResult> useFunction) {
        InteractionResult result = useFunction.apply(context);
        if (context.getPlayer() instanceof ServerPlayer)
            handleBrokenItem(context.getItemInHand(), context.getPlayer(), context.getHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
        return result;
    }

    private static void onDamageFactorChange(ServerPlayer player, int preDamageFactor, int newDamageFactor) {
        if (newDamageFactor > preDamageFactor) {
            if (Config.Client.playKachinkSound.get()) {
                player.level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.5f, 2.0f);
            }
            LibTriggers.GENERIC_INT.trigger(player, DAMAGE_FACTOR_CHANGE, 1);
        }
    }

    private static void notifyPlayerOfBrokenGear(ItemStack stack, Player player) {
        if (Config.Common.sendGearBrokenMessage.get()) {
            // Notify player. Mostly for armor, but might help new players as well.
            player.sendMessage(new TranslatableComponent("misc.silentgear.notifyOnBreak", stack.getHoverName()), Util.NIL_UUID);
        }
    }

    private static int getDamageFactor(ItemStack stack, int maxDamage) {
        if (maxDamage == 0) return 1;
        int levels = Config.Common.damageFactorLevels.get();
        int step = Math.max(1, maxDamage / (levels < 1 ? 10 : levels));
        return stack.getDamageValue() / step;
    }

    // Used by setDamage in gear items to prevent other mods from breaking them
    public static int calcDamageClamped(ItemStack stack, int damage) {
        if (isUnbreakable(stack)) return 0;

        if (!canBreakPermanently(stack)) {
            if (damage > stack.getDamageValue()) damage = Math.min(stack.getMaxDamage(), damage);
            else damage = Math.max(0, damage);
        }
        return damage;
    }

    private static boolean canBreakPermanently(ItemStack stack) {
        return Config.Common.gearBreaksPermanently.get() || TraitHelper.hasTrait(stack, Const.Traits.RED_CARD);
    }

    public static boolean isBroken(ItemStack stack) {
        if (stack.isEmpty() || canBreakPermanently(stack) || isUnbreakable(stack))
            return false;

        int maxDamage = stack.getMaxDamage();
        return maxDamage > 0 && stack.getDamageValue() >= maxDamage - 1;
    }

    public static boolean isUnbreakable(ItemStack stack) {
        return TraitHelper.getTraitLevel(stack, Const.Traits.INDESTRUCTIBLE) > 0;
    }

    public static void setDamage(ItemStack stack, int damage, BiConsumer<ItemStack, Integer> superFunction) {
        int newDamage = GearHelper.calcDamageClamped(stack, damage);
        int diff = newDamage - stack.getDamageValue();
        if (diff > 0 && !GearHelper.isBroken(stack)) {
            GearHelper.damageParts(stack, diff);
        }
        superFunction.accept(stack, newDamage);
        if (GearHelper.isBroken(stack)) {
            GearData.recalculateStats(stack, null);
        }
    }

    public static <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        final int preTraitValue;
        if (GearHelper.isUnbreakable(stack)) {
            preTraitValue = 0;
        } else if (!Config.Common.gearBreaksPermanently.get()) {
            preTraitValue = Mth.clamp(amount, 0, stack.getMaxDamage() - stack.getDamageValue() - 1);
            if (!isBroken(stack) && stack.getDamageValue() + preTraitValue >= stack.getMaxDamage() - 1) {
                onBroken.accept(entity);
            }
        } else {
            preTraitValue = amount;
        }

        final int value = (int) TraitHelper.activateTraits(stack, preTraitValue, (trait, level, val) ->
                trait.onDurabilityDamage(new TraitActionContext(null, level, stack), (int) val));
        GearHelper.damageParts(stack, value);
        return value;
    }

    private static void damageParts(ItemStack stack, int amount) {
        GearData.getConstructionParts(stack).forEach(p -> p.get().onGearDamaged(p, stack, amount));
    }

    //endregion

    public static Item.Properties getBuilder(@Nullable ToolType toolType) {
        Item.Properties b = new Item.Properties().stacksTo(1).tab(SilentGear.ITEM_GROUP);
        if (toolType != null) b.addToolType(toolType, 3);
        return b;
    }

    public static GearType getType(ItemStack gear) {
        return getType(gear, GearType.NONE);
    }

    public static GearType getType(ItemStack gear, GearType defaultType) {
        if (gear.isEmpty() || !(gear.getItem() instanceof ICoreItem)) {
            return defaultType;
        }
        return ((ICoreItem) gear.getItem()).getGearType();
    }

    /**
     * Check if both gear items are made of the same parts.
     *
     * @param gear1 First item
     * @param gear2 Second item
     * @return True only if all parts are identical
     */
    public static boolean isEquivalent(ItemStack gear1, ItemStack gear2) {
        if (!GearHelper.isGear(gear1) || !GearHelper.isGear(gear2) || gear1.getItem() != gear2.getItem()) {
            return false;
        }

        List<PartData> parts1 = GearData.getConstructionParts(gear1);
        List<PartData> parts2 = GearData.getConstructionParts(gear2);
        if (parts1.size() != parts2.size()) {
            return false;
        }
        if (parts1.isEmpty()) {
            return true;
        }

        for (PartData part1 : parts1) {
            for (PartData part2 : parts2) {
                if (part1.equals(part2)) {
                    parts2.remove(part2);
                    break;
                }
            }
        }

        return parts2.isEmpty();
    }

    public static int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable BlockState state, @Nullable Set<Material> effectiveMaterials) {
        if (isBroken(stack) || !stack.getItem().getToolTypes(stack).contains(toolClass))
            return -1;

        return GearData.getStatInt(stack, ItemStats.HARVEST_LEVEL);
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

    public static boolean onBlockDestroyed(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!isBroken(stack) && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnBlockBreak(stack, world, state, pos);
            attemptDamage(stack, damage, entityLiving, EquipmentSlot.MAINHAND);
        }
//        GearStatistics.incrementStat(stack, GearStatistics.BLOCKS_MINED);

        // TODO: Implement multi-break skill

        return true;
    }

    public static boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean isBroken = isBroken(stack);
        if (!isBroken && stack.getItem() instanceof ICoreTool) {
            int damage = ((ICoreTool) stack.getItem()).getDamageOnHitEntity(stack, target, attacker);
            attemptDamage(stack, damage, attacker, EquipmentSlot.MAINHAND);
        }

        return !isBroken;
    }

    // Formerly onUpdate
    public static void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isClientSide) {
            @Nullable Player player = entity instanceof Player ? (Player) entity : null;
            TraitHelper.tickTraits(world, player, stack, isSelected);
        }
    }

    public static InteractionResult onItemUse(UseOnContext context) {
        InteractionResult ret = InteractionResult.PASS;
        Map<ITrait, Integer> traits = TraitHelper.getCachedTraits(context.getItemInHand());
        for (Map.Entry<ITrait, Integer> entry : traits.entrySet()) {
            InteractionResult result = entry.getKey().onItemUse(context, entry.getValue());
            if (result != InteractionResult.PASS) {
                ret = result;
            }
        }
        return ret;
    }

    public static void onItemSwing(ItemStack stack, LivingEntity wielder) {
        if (wielder instanceof Player
                && getType(stack).matches(GearType.MELEE_WEAPON)
                && tryAttackWithExtraReach((Player) wielder, false) != null) {
            // Player attacked something, ignore traits
            return;
        }

        Map<ITrait, Integer> traits = TraitHelper.getCachedTraits(stack);
        for (Map.Entry<ITrait, Integer> entry : traits.entrySet()) {
            entry.getKey().onItemSwing(stack, wielder, entry.getValue());
        }
    }

    /**
     * Checks if the player would be able to attack an entity which may be outside the vanilla
     * range, based on reach distance attribute value.
     *
     * @param player The attacking player
     * @return The targeted entity if a vulnerable entity is within range, null otherwise
     */
    @Nullable
    public static Entity getAttackTargetWithExtraReach(Player player) {
        if (getType(player.getMainHandItem()).matches(GearType.MELEE_WEAPON)) {
            return tryAttackWithExtraReach(player, true);
        }
        return null;
    }

    /**
     * Attempts to attack an entity which may be outside the vanilla range, based on reach distance
     * attribute value.
     *
     * @param player The attacking player
     * @return The attacked entity if the attack was successful, null otherwise
     */
    @Nullable
    public static Entity tryAttackWithExtraReach(Player player) {
        return tryAttackWithExtraReach(player, false);
    }

    @Nullable
    private static Entity tryAttackWithExtraReach(Player player, boolean simulate) {
        // Attempt to attack something if wielding a weapon with increased melee range
        double range = getAttackRange(player);
        Vec3 vector3d = player.getEyePosition(0f);
        double rangeSquared = range * range;

        Vec3 vector3d1 = player.getViewVector(1.0F);
        Vec3 vector3d2 = vector3d.add(vector3d1.x * range, vector3d1.y * range, vector3d1.z * range);
        AABB axisalignedbb = player.getBoundingBox().expandTowards(vector3d1.scale(range)).inflate(1.0D, 1.0D, 1.0D);

        EntityHitResult rayTrace = rayTraceEntities(player, vector3d, vector3d2, axisalignedbb, (entity) -> {
            return !entity.isSpectator() && entity.isPickable();
        }, rangeSquared);

        if (rayTrace != null) {
            Entity entity = rayTrace.getEntity();
            if (!simulate) {
                player.attack(entity);
            }
            return entity;
        }

        return null;
    }

    private static double getAttackRange(LivingEntity entity) {
        ItemStack stack = entity.getMainHandItem();
        double base = getType(stack).matches(GearType.TOOL)
                ? GearData.getStat(stack, ItemStats.ATTACK_REACH)
                : ItemStats.ATTACK_REACH.getBaseValue();

        // Also check Forge reach distance, to allow curios to add more reach
        AttributeInstance attribute = entity.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (attribute != null) {
            double reachBonus = attribute.getValue() - attribute.getBaseValue();
            return base + reachBonus;
        }

        return base;
    }

    @SuppressWarnings({"MethodWithTooManyParameters", "OverlyComplexMethod"})
    @Nullable
    private static EntityHitResult rayTraceEntities(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double distance) {
        // Copied from ProjectileHelper (getEntityHitResult)
        Level world = shooter.level;
        double d0 = distance;
        Entity entity = null;
        Vec3 vector3d = null;

        for (Entity entity1 : world.getEntities(shooter, boundingBox, filter)) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> optional = axisalignedbb.clip(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    vector3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3 vector3d1 = optional.get();
                double d1 = startVec.distanceToSqr(vector3d1);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getRootVehicle() == shooter.getRootVehicle() && !entity1.canRiderInteract()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            vector3d = vector3d1;
                        }
                    } else {
                        entity = entity1;
                        vector3d = vector3d1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity, vector3d);
    }

    public static int getEnchantability(ItemStack stack) {
        if (Config.Common.allowEnchanting.get()) {
            return GearData.getStatInt(stack, ItemStats.ENCHANTABILITY);
        }
        return 0;
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

    public static void fillItemGroup(ICoreItem item, CreativeModeTab group, Collection<ItemStack> items) {
        boolean inTab = false;
        for (CreativeModeTab tabInList : item.asItem().getCreativeTabs()) {
            if (tabInList == group) {
                inTab = true;
                break;
            }
        }
        if (!inTab) return;

        Collection<ItemStack> list = new ArrayList<>();
        // Create a few samples of each tool type, because rendering performance is a problem on many machines.
        for (int i = 3; i <= Math.max(4, PartManager.getHighestMainPartTier()); ++i) {
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
            for (PartType partType : item.getRequiredParts()) {
                partType.makeCompoundPart(item.getGearType(), Const.Materials.EXAMPLE).ifPresent(parts::add);
            }
            result = item.construct(parts);
        }
        GearData.setExampleTag(result, true);
        return result;
    }

    public static Component getDisplayName(ItemStack gear) {
        PartData part = GearData.getPrimaryPart(gear);
        if (part == null) return new TranslatableComponent(gear.getDescriptionId());

        Component partName = part.getMaterialName(gear);
        if (TimedEvents.isAprilFools()) {
            partName = partName.copy().append(new TextComponent(" & Knuckles"));
        }
        Component gearName = new TranslatableComponent(gear.getDescriptionId() + ".nameProper", partName);
        Component result = gearName;

        if (gear.getItem() instanceof ICoreTool) {
            ICoreItem item = (ICoreItem) gear.getItem();
            if (item.requiresPartOfType(PartType.ROD) && GearData.getPartOfType(gear, PartType.ROD) == null) {
                result = new TranslatableComponent(gear.getDescriptionId() + ".noRod", gearName);
            } else if (item.requiresPartOfType(PartType.BOWSTRING) && GearData.getPartOfType(gear, PartType.BOWSTRING) == null) {
                result = new TranslatableComponent(gear.getDescriptionId() + ".unstrung", gearName);
            }
        }

        // Prefixes
        // TODO: Probably should cache this somehow...
        for (Component t : getNamePrefixes(gear, GearData.getConstructionParts(gear))) {
            // TODO: Spaces are probably inappropriate for some languages?
            result = t.copy().append(new TextComponent(" ")).append(result);
        }

        return result;
    }

    private static Collection<Component> getNamePrefixes(ItemStack gear, PartDataList parts) {
        GearNamePrefixesEvent event = new GearNamePrefixesEvent(gear, parts);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getPrefixes();
    }

    public static Collection<IPartData> getExamplePartsFromRecipe(GearType gearType, Iterable<Ingredient> ingredients) {
        Map<PartType, IPartData> map = new LinkedHashMap<>();

        PartType.MAIN.makeCompoundPart(gearType, Const.Materials.EXAMPLE).ifPresent(p -> map.put(PartType.MAIN, p));

        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof IGearIngredient) {
                PartType type = ((IGearIngredient) ingredient).getPartType();
                type.makeCompoundPart(gearType, Const.Materials.EXAMPLE).ifPresent(p -> map.put(type, p));
            }
        }

        return map.values();
    }
}
