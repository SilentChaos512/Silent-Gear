package net.silentchaos512.gear.util;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GearNamePrefixesEvent;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearTool;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgCriteriaTriggers;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.SgSounds;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains various methods used by gear items. Many are delegates for item overrides, to cut down
 * on code duplication. But there are some useful helper methods, like {@link #isGear}.
 * <p>
 * Also see {@link GearData}, which focuses on getting/updating item data and NBT.
 */
public final class GearHelper {
    public static Tiers DEFAULT_DUMMY_TIER = Tiers.WOOD;

    private static final ResourceLocation REACH_MODIFIER_ID = SilentGear.getId("reach_modifier");
    private static final float BROKEN_ATTACK_SPEED_CHANGE = 0.7f;
    private static final float BROKEN_DESTROY_SPEED = 0.25f;

    private GearHelper() {}

    public static Optional<GearItem> getItem(ItemStack gear) {
        if (gear.getItem() instanceof GearItem) {
            return Optional.of((GearItem) gear.getItem());
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
        return stack.getItem() instanceof GearItem;
    }

    /**
     * Check if the item is a Silent Gear item and has all the parts it requires to function.
     *
     * @param stack The item
     * @return True if {@code stack} is a gear item with no missing required parts
     */
    public static boolean isValidGear(ItemStack stack) {
        if (!isGear(stack)) {
            return false;
        }

        GearItem item = (GearItem) stack.getItem();
        for (PartType type : item.getRequiredParts()) {
            if (!GearData.hasPartOfType(stack, type)) {
                return false;
            }
        }

        return true;
    }

    //region Attribute modifiers

    public static float getAttackDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 1f;

        float val = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_DAMAGE);
        return val < 0 ? 0 : val;
    }

    public static float getMagicDamageModifier(ItemStack stack) {
        if (isBroken(stack))
            return 0f;

        float val = GearData.getProperties(stack).getNumber(GearProperties.MAGIC_DAMAGE);
        return val < 0 ? 0 : val;
    }

    public static float getAttackSpeedModifier(ItemStack stack) {
        if (!(stack.getItem() instanceof GearTool))
            return 0.0f;

        float speed = GearData.getProperties(stack).getNumber(GearProperties.ATTACK_SPEED) - 4.0f;
        if (isBroken(stack))
            speed += BROKEN_ATTACK_SPEED_CHANGE;
        return speed;
    }

    public static void addAttributeModifiers(ItemStack stack, ItemAttributeModifiers.Builder builder) {
        addAttributeModifiers(stack, builder, true);
    }

    public static void addAttributeModifiers(ItemStack stack, ItemAttributeModifiers.Builder builder, boolean addStandardMainHandMods) {
        if (addStandardMainHandMods) {
            builder
                    .add(
                            Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(
                                    Item.BASE_ATTACK_DAMAGE_ID,
                                    getAttackDamageModifier(stack),
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                    )
                    .add(
                            Attributes.ATTACK_SPEED,
                            new AttributeModifier(
                                    Item.BASE_ATTACK_SPEED_ID,
                                    getAttackSpeedModifier(stack),
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                    )
                    .add(
                            Attributes.BLOCK_INTERACTION_RANGE,
                            new AttributeModifier(
                                    REACH_MODIFIER_ID,
                                    GearData.getProperties(stack).getNumber(GearProperties.BLOCK_REACH),
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                    );
        }

        TraitHelper.getTraits(stack).forEach(inst -> {
            var context = new TraitActionContext(null, inst, stack);
            inst.getTrait().onGetAttributeModifiers(context, builder);
        });
    }

    @Deprecated
    public static boolean isValidSlot(ItemStack gear, String slot) {
        if (gear.getItem() instanceof GearItem) {
            return ((GearItem) gear.getItem()).isValidSlot(slot);
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
        var data = gear.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null
                && data.getPrimaryPart() != null
                && material.getRepairValue(gear) > 0
                && material.canRepair(gear);
    }

    public static NumberProperty getDurabilityProperty(ItemStack gear) {
        return getItem(gear).map(GearItem::getDurabilityStat).map(Supplier::get).orElse(GearProperties.DURABILITY.get());
    }

    public static float getRepairModifier(ItemStack gear) {
        return getItem(gear).map(item -> item.getRepairModifier(gear)).orElse(1f);
    }

    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand) {
        attemptDamage(stack, amount, entity, hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
    }

    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot) {
        if (isUnbreakable(stack) || (entity instanceof Player && ((Player) entity).getAbilities().instabuild))
            return;

        ServerPlayer player = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
        final int preTraitAmount = amount;
        amount = TraitHelper.activateTraits(stack, preTraitAmount, (trait, val) ->
                (int) trait.getTrait().onDurabilityDamage(new TraitActionContext(player, trait, stack), val));

        final int maxDamage = stack.getMaxDamage();
        final int previousDamageFactor = getDamageFactor(stack, maxDamage);
        if (!canBreakPermanently(stack))
            amount = Math.min(maxDamage - stack.getDamageValue(), amount);
        stack.hurtAndBreak(amount, entity, slot);

        // Recalculate stats occasionally
        var currentDamageFactory = getDamageFactor(stack, maxDamage);
        if (currentDamageFactory != previousDamageFactor) {
            GearData.recalculateGearData(stack, player);
            if (player != null) {
                onDamageFactorChange(player, previousDamageFactor, currentDamageFactory);
            }
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
                player.onEquippedItemBroken(stack.getItem(), slot);
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
        GearData.recalculateGearData(stack, player);
        if (player != null) {
            player.onEquippedItemBroken(stack.getItem(), slot);
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
            player.level().playSound(null, player.blockPosition(), SgSounds.GEAR_DAMAGED.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
            SgCriteriaTriggers.DAMAGE_FACTOR_CHANGE.get().trigger(player);
        }
    }

    private static void notifyPlayerOfBrokenGear(ItemStack stack, Player player) {
        if (Config.Common.sendGearBrokenMessage.get()) {
            // Notify player. Mostly for armor, but might help new players as well.
            player.sendSystemMessage(Component.translatable("misc.silentgear.notifyOnBreak", stack.getHoverName()));
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
        return (Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get()) || TraitHelper.hasTrait(stack, Const.Traits.RED_CARD);
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
        boolean alreadyBroken = GearHelper.isBroken(stack);
        int newDamage = GearHelper.calcDamageClamped(stack, damage);
        int diff = newDamage - stack.getDamageValue();
        if (diff > 0 && !GearHelper.isBroken(stack)) {
            GearHelper.damageParts(stack, diff);
        }
        superFunction.accept(stack, newDamage);
        if (!alreadyBroken && GearHelper.isBroken(stack)) {
            GearData.recalculateGearData(stack, null);
        }
    }

    public static <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        final int preTraitValue;
        if (GearHelper.isUnbreakable(stack)) {
            preTraitValue = 0;
        } else if (!(Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get())) {
            preTraitValue = Mth.clamp(amount, 0, stack.getMaxDamage() - stack.getDamageValue() - 1);
            if (!isBroken(stack) && stack.getDamageValue() + preTraitValue >= stack.getMaxDamage() - 1) {
                onBroken.accept(stack.getItem());
            }
        } else {
            preTraitValue = amount;
        }

        final int value = TraitHelper.activateTraits(stack, preTraitValue, (trait, val) ->
                (int) trait.getTrait().onDurabilityDamage(new TraitActionContext(null, trait, stack), val));
        GearHelper.damageParts(stack, value);
        return value;
    }

    private static void damageParts(ItemStack stack, int amount) {
        var construction = GearData.getConstruction(stack);
        construction.parts().forEach(part -> {
            if (part.isValid()) {
                part.get().onGearDamaged(part, stack, amount);
            }
        });
    }

    //endregion

    public static Item.Properties getBaseItemProperties() {
        return new Item.Properties()
                .stacksTo(1)
                .durability(100)
                .component(SgDataComponents.GEAR_CONSTRUCTION, new GearConstructionData(PartList.empty(), false, 0, 0))
                .component(SgDataComponents.GEAR_PROPERTIES, new GearPropertiesData(Map.of()));
    }

    public static GearType getType(ItemStack gear) {
        return getType(gear, GearTypes.NONE.get());
    }

    public static GearType getType(ItemStack gear, GearType defaultType) {
        if (gear.isEmpty() || !(gear.getItem() instanceof GearItem)) {
            return defaultType;
        }
        return ((GearItem) gear.getItem()).getGearType();
    }

    /**
     * Check if both gear items are made of the same parts.
     *
     * @param gear1 First item
     * @param gear2 Second item
     * @return True only if all parts are identical
     */
    @Deprecated // May not be needed if arrows get redesigned
    public static boolean isEquivalent(ItemStack gear1, ItemStack gear2) {
        if (!GearHelper.isGear(gear1) || !GearHelper.isGear(gear2) || gear1.getItem() != gear2.getItem()) {
            return false;
        }

        var constructionData1 = GearData.getConstruction(gear1);
        var constructionData2 = GearData.getConstruction(gear2);


        return constructionData1.equals(constructionData2);
    }

    public static boolean isCorrectToolForDrops(ItemStack stack, BlockState state, @Nullable TagKey<Block> blocksForTool) {
        if (GearHelper.isBroken(stack)) return false;

        Tool tool = stack.get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(state);
    }

    public static float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isBroken(stack))
            return BROKEN_DESTROY_SPEED;

        float speed = GearData.getProperties(stack).getNumber(GearProperties.HARVEST_SPEED);

        // Tool effective on block?
        if (stack.getItem().isCorrectToolForDrops(stack, state)) {
            return getTraitModifiedMiningSpeed(stack, state, speed);
        }

        // Tool ineffective.
        return 1f;
    }

    private static float getTraitModifiedMiningSpeed(ItemStack stack, BlockState state, float baseSpeed) {
        var totalModifier = 0f;
        for (var traitInstance : TraitHelper.getTraits(stack)) {
            totalModifier += traitInstance.getTrait().getMiningSpeedModifier(traitInstance.getLevel(), state, baseSpeed);
        }
        return baseSpeed * (1f + totalModifier);
    }

    public static boolean onBlockDestroyed(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!isBroken(stack) && stack.getItem() instanceof GearTool) {
            int damage = ((GearTool) stack.getItem()).getDamageOnBlockBreak(stack, world, state, pos);
            attemptDamage(stack, damage, entityLiving, EquipmentSlot.MAINHAND);
        }
//        GearStatistics.incrementStat(stack, GearStatistics.BLOCKS_MINED);

        // TODO: Implement multi-break skill

        return true;
    }

    public static boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return !isBroken(stack);
    }

    public static void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!isBroken(stack) && stack.getItem() instanceof GearTool gearToolItem) {
            var damageOnHitEntity = gearToolItem.getDamageOnHitEntity(stack, target, attacker);
            attemptDamage(stack, damageOnHitEntity, attacker, EquipmentSlot.MAINHAND);
        }
    }

    // Formerly onUpdate
    public static void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isEquipped) {
        if (!world.isClientSide) {
            @Nullable Player player = entity instanceof Player ? (Player) entity : null;
            TraitHelper.tickTraits(world, player, stack, isEquipped);
        }
    }

    public static InteractionResult onItemUse(UseOnContext context) {
        InteractionResult ret = InteractionResult.PASS;
        for (var traitInstance : TraitHelper.getTraits(context.getItemInHand())) {
            InteractionResult result = traitInstance.getTrait().onItemUse(context, traitInstance.getLevel());
            if (result != InteractionResult.PASS) {
                ret = result;
            }
        }
        return ret;
    }

    public static void onItemSwing(ItemStack stack, LivingEntity wielder) {
        if (wielder instanceof Player
                && getType(stack).matches(GearTypes.MELEE_WEAPON.get())
                && tryAttackWithExtraReach((Player) wielder, false) != null) {
            // Player attacked something, ignore traits
            return;
        }

        for (var traitInstance : TraitHelper.getTraits(stack)) {
            traitInstance.getTrait().onItemSwing(stack, wielder, traitInstance.getLevel());
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
        if (getType(player.getMainHandItem()).matches(GearTypes.MELEE_WEAPON.get())) {
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
        double base = getType(stack).matches(GearTypes.TOOL.get())
                ? GearData.getProperties(stack).getNumber(GearProperties.ATTACK_REACH)
                : GearProperties.ATTACK_REACH.get().getBaseValue();

        // Also check Forge reach distance, to allow curios to add more reach
        AttributeInstance attribute = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
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
        Level world = shooter.level();
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

    public static int getEnchantmentValue(ItemStack stack) {
        if (Config.Common.allowEnchanting.get()) {
            return (int) GearData.getProperties(stack).getNumber(GearProperties.ENCHANTMENT_VALUE);
        }
        return 0;
    }

    public static Rarity getRarity(ItemStack stack) {
        int rarity = (int) GearData.getProperties(stack).getNumber(GearProperties.RARITY);
        if (stack.isEnchanted())
            if (Config.Client.vanillaStyleTooltips.get()) {
                rarity += 80;
            } else {
                rarity += 20;
            }
        if (rarity < 40)
            return Rarity.COMMON;
        if (rarity < 80)
            return Rarity.UNCOMMON;
        if (rarity < 120)
            return Rarity.RARE;
        return Rarity.EPIC;
    }

    public static void fillItemGroup(GearItem item, CreativeModeTab group, Collection<ItemStack> items) {
        boolean inTab = false;
        // FIXME?
        /*for (CreativeModeTab tabInList : item.asItem().getCreativeTabs()) {
            if (tabInList == group) {
                inTab = true;
                break;
            }
        }*/
        if (!inTab) return;

        /*Collection<ItemStack> list = new ArrayList<>();
        // Create a few samples of each tool type, because rendering performance is a problem on many machines.
        for (int i = 3; i <= Math.max(4, PartManager.getHighestMainPartTier()); ++i) {
            ItemStack stack = createSampleItem(item, i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        items.addAll(list);*/

        // Add some standard materials instead of randoms
        items.add(createSampleItem(item, Const.Materials.IRON));
        items.add(createSampleItem(item, Const.Materials.DIAMOND));
        items.add(createSampleItem(item, Const.Materials.CRIMSON_STEEL));
        items.add(createSampleItem(item, Const.Materials.AZURE_ELECTRUM));
        items.add(createSampleItem(item, Const.Materials.TYRIAN_STEEL));
    }

    private static ItemStack createSampleItem(GearItem item, int tier) {
        ItemStack result = GearGenerator.create(item);
        if (result.isEmpty()) {
            Collection<PartInstance> parts = new ArrayList<>();
            for (PartType partType : item.getRequiredParts()) {
                partType.makeCompoundPart(item.getGearType(), Const.Materials.EXAMPLE).ifPresent(parts::add);
            }
            result = item.construct(parts);
        }
        GearData.setExampleTag(result, true);
        return result;
    }

    private static ItemStack createSampleItem(GearItem item, DataResource<Material> mainMaterial) {
        Collection<PartInstance> parts = Lists.newArrayList();
        for (PartType partType : item.getRequiredParts()) {
            // FIXME: Cords are missing from bows and fishing rods
            partType.makeCompoundPart(item.getGearType(), selectMaterialForSample(partType, item.getGearType(), mainMaterial))
                    .ifPresent(parts::add);
        }
        ItemStack result = new ItemStack(item);
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateGearData(result, null);
        return result;
    }

    private static DataResource<Material> selectMaterialForSample(PartType partType, GearType gearType, DataResource<Material> main) {
        if (partType == PartTypes.ROD.get()) {
            return Const.Materials.WOOD;
        } else if (partType == PartTypes.CORD.get()) {
            return Const.Materials.STRING;
        } else if (partType == PartTypes.FLETCHING.get()) {
            return Const.Materials.FEATHER;
        } else if (partType == PartTypes.BINDING.get()) {
            return Const.Materials.STRING;
        } else if (partType == PartTypes.SETTING.get()) {
            return getRandomMaterial(partType, gearType);
        }
        return main;
    }

    private static DataResource<Material> getRandomMaterial(PartType partType, GearType gearType) {
        // Excludes children, will select a random child material (if appropriate) below
        List<Material> matsOfTier = new ArrayList<>();
        for (Material material : SgRegistries.MATERIAL.getValues(true)) {
            MaterialInstance inst = MaterialInstance.of(material);
            if (inst.allowedInPart(partType) && inst.isCraftingAllowed(partType, gearType)) {
                matsOfTier.add(inst.get());
            }
        }

        if (!matsOfTier.isEmpty()) {
            Material material = matsOfTier.get(SilentGear.RANDOM.nextInt(matsOfTier.size()));
            return DataResource.material(SgRegistries.MATERIAL.getKey(material));
        }

        // Something went wrong...
        return Const.Materials.EXAMPLE;
    }

    public static Component getDisplayName(ItemStack gear) {
        var data = GearData.getConstruction(gear);
        var part = data.getPrimaryPart();
        if (part == null) return Component.translatable(gear.getDescriptionId());

        Component partName = part.getMaterialName(gear);
        if (TimedEvents.isAprilFools()) {
            partName = partName.copy().append(Component.literal(" & Knuckles"));
        }
        Component gearName = Component.translatable(gear.getDescriptionId() + ".nameProper", partName);
        Component result = gearName;

        if (gear.getItem() instanceof GearTool) {
            GearItem item = (GearItem) gear.getItem();
            if (item.requiresPartOfType(PartTypes.ROD.get()) && GearData.getPartOfType(gear, PartTypes.ROD.get()) == null) {
                result = Component.translatable(gear.getDescriptionId() + ".noRod", gearName);
            } else if (item.requiresPartOfType(PartTypes.CORD.get()) && GearData.getPartOfType(gear, PartTypes.CORD.get()) == null) {
                result = Component.translatable(gear.getDescriptionId() + ".unstrung", gearName);
            }
        }

        // Prefixes
        for (Component t : getNamePrefixes(gear, data.parts())) {
            result = t.copy().append(result);
        }

        return result;
    }

    private static Collection<Component> getNamePrefixes(ItemStack gear, PartList parts) {
        GearNamePrefixesEvent event = new GearNamePrefixesEvent(gear, parts);
        NeoForge.EVENT_BUS.post(event);
        return event.getPrefixes();
    }

    public static Collection<PartInstance> getExamplePartsFromRecipe(GearType gearType, Iterable<Ingredient> ingredients) {
        Map<PartType, PartInstance> map = new LinkedHashMap<>();

        var mainType = PartTypes.MAIN.get();
        mainType.makeCompoundPart(gearType, Const.Materials.EXAMPLE).ifPresent(p -> map.put(mainType, p));

        for (Ingredient ingredient : ingredients) {
            if (ingredient.getCustomIngredient() instanceof IGearIngredient customGearIngredient) {
                PartType type = customGearIngredient.getPartType();
                type.makeCompoundPart(gearType, Const.Materials.EXAMPLE).ifPresent(p -> map.put(type, p));
            }
        }

        return map.values();
    }

    public static Set<ItemAbility> makeItemAbilitySet(ItemAbility... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }

    public static int getBarWidth(ItemStack stack) {
        return Math.round(13f - 13f * stack.getDamageValue() / stack.getMaxDamage());
    }

    public static int getBarColor(ItemStack stack) {
        float f = Math.max(0f, (float) (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
        return Mth.hsvToRgb(f / 3f, 1f, 1f);
    }
}
