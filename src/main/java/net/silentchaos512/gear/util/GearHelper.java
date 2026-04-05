package net.silentchaos512.gear.util;

import com.google.common.collect.Sets;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.crafting.ingredient.IGearIngredient;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgCriteriaTriggers;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgSounds;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.lib.util.NameUtils;

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
    private static final Identifier REACH_MODIFIER_ID = SilentGear.getId("reach_modifier");
    private static final float BROKEN_ATTACK_SPEED_CHANGE = 0.7f;
    private static final float BROKEN_DESTROY_SPEED = 0.25f;

    private GearHelper() {
    }

    public static Optional<GearItem> getItem(ItemInstance instance) {
        var item = instance.typeHolder().value();
        return item instanceof GearItem gearItem ? Optional.of(gearItem) : Optional.empty();
    }

    /**
     * Check if the item is a Silent Gear tool, weapon, or armor item.
     *
     * @param instance The item
     * @return True if {@code instance} is a gear item
     */
    public static boolean isGear(ItemInstance instance) {
        return instance.typeHolder().value() instanceof GearItem;
    }

    /**
     * Check if the item is a Silent Gear item and has all the parts it requires to function.
     *
     * @param instance The item
     * @return True if {@code instance} is a gear item with no missing required parts
     */
    public static boolean isValidGear(ItemInstance instance) {
        var item = instance.typeHolder().value();
        if (!(item instanceof GearItem gearItem)) return false;

        for (PartType type : gearItem.getRequiredParts()) {
            if (!GearData.hasPartOfType(instance, type)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAttackingItem(ItemInstance instance) {
        var type = getType(instance);
        return type.matches(GearTypes.MELEE_WEAPON.get(), false) || type.matches(GearTypes.HARVEST_TOOL.get());
    }

    //region Attribute modifiers

    public static void onAddAttackDamageModifier(ItemInstance instance, float value, ItemAttributeModifiers.Builder builder) {
        if (!isAttackingItem(instance)) {
            return;
        }

        float adjustedValue = isBroken(instance) ? 1f : Math.max(value, 0f);
        builder.add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_ID,
                        adjustedValue,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
        );
    }

    public static void onAddAttackSpeedModifier(ItemInstance instance, float value, ItemAttributeModifiers.Builder builder) {
        if (!isAttackingItem(instance)) {
            return;
        }

        float speed = value - 4.0f;
        if (isBroken(instance)) {
            speed += BROKEN_ATTACK_SPEED_CHANGE;
        }
        builder.add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        Item.BASE_ATTACK_SPEED_ID,
                        speed,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
        );
    }

    public static void onAddBlockReachModifier(ItemInstance instance, float value, ItemAttributeModifiers.Builder builder) {
        builder.add(
                Attributes.BLOCK_INTERACTION_RANGE,
                new AttributeModifier(
                        REACH_MODIFIER_ID,
                        value,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.MAINHAND
        );
    }

    //endregion

    //region Damage and repair

    public static boolean getIsRepairable(ItemStack stack, ItemStack materialItem) {
        MaterialInstance material = MaterialInstance.from(materialItem);
        return material != null && getIsRepairable(stack, material);
    }

    public static boolean getIsRepairable(ItemInstance instance, MaterialInstance material) {
        var data = instance.get(SgDataComponents.GEAR_CONSTRUCTION);
        return data != null
                && data.getPrimaryPart() != null
                && material.getRepairValue(instance) > 0
                && material.canRepair(instance);
    }

    public static NumberProperty getDurabilityProperty(ItemInstance instance) {
        return getItem(instance).map(GearItem::getDurabilityStat).map(Supplier::get).orElse(GearProperties.DURABILITY.get());
    }

    public static float getRepairModifier(ItemInstance gear) {
        return getItem(gear).map(item -> item.getRepairModifier(gear)).orElse(1f);
    }

    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity, InteractionHand hand) {
        attemptDamage(stack, amount, entity, hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
    }

    public static void attemptDamage(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot) {
        if (isUnbreakable(stack) || (entity instanceof Player && ((Player) entity).getAbilities().instabuild))
            return;

        ServerPlayer player = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
        final int maxDamage = stack.getMaxDamage();
        final int previousDamageFactor = getDamageFactor(stack, maxDamage);
        if (!canBreakPermanently(stack)) {
            // Always leave items with at least 1 durability (which is the broken state)
            amount = Math.min(maxDamage - stack.getDamageValue() - 1, amount);
        }

        if (amount < 0) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() + amount));
        } else {
            stack.hurtAndBreak(amount, entity, slot);
        }

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

    private static boolean canBreakPermanently(ItemInstance stack) {
        return (Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get()) || TraitHelper.hasTrait(stack, Const.Traits.RED_CARD);
    }

    public static boolean isBroken(ItemInstance stack) {
        if (canBreakPermanently(stack) || isUnbreakable(stack))
            return false;

        int maxDamage = stack.getOrDefault(DataComponents.MAX_DAMAGE, 0);
        int damage = stack.getOrDefault(DataComponents.DAMAGE, 0);
        return maxDamage > 0 && damage >= maxDamage - 1;
    }

    public static boolean isUnbreakable(ItemInstance stack) {
        return TraitHelper.getTraitLevel(stack, Const.Traits.INDESTRUCTIBLE) > 0 || stack.has(DataComponents.UNBREAKABLE);
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
        if (!isGear(stack)) {
            return amount;
        }

        var gearBreaksPermanently = Config.Common.isLoaded() && Config.Common.gearBreaksPermanently.get();
        final int preTraitValue;
        final int clampedValue;
        if (GearHelper.isUnbreakable(stack)) {
            // Gear is indestructible
            clampedValue = 0;
        } else {
            preTraitValue = amount;
            final int postTraitValue = TraitHelper.activateTraits(stack, preTraitValue, (trait, val) ->
                    trait.getTrait().onDurabilityDamage(new TraitActionContext(null, trait, stack), val));

            if (gearBreaksPermanently) {
                // Gear can break permanently, no clamping necessary
                clampedValue = postTraitValue;
            } else {
                // Gear does not break permanently, so adjust damage amount. This prevents damage value from dropping
                // below 1 (the broken state)
                clampedValue = Math.min(stack.getMaxDamage() - stack.getDamageValue() - 1, postTraitValue);
                if (!isBroken(stack) && stack.getDamageValue() + preTraitValue >= stack.getMaxDamage() - 1) {
                    onBroken.accept(stack.getItem());
                }
            }
        }

        // Apply damage to gear item
        GearHelper.damageParts(stack, clampedValue);
        return clampedValue;
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
                .setNoCombineRepair();
    }

    public static GearType getType(ItemInstance gear) {
        return getType(gear, GearTypes.NONE.get());
    }

    public static GearType getType(ItemInstance gear, GearType defaultType) {
        var item = gear.typeHolder().value();
        return item instanceof GearItem gearItem ? gearItem.getGearType() : defaultType;
    }

    public static GearType getType(ItemLike item) {
        return getType(new ItemStackTemplate(item.asItem()));
    }

    public static boolean isCorrectToolForDrops(ItemInstance stack, BlockState state, @Nullable TagKey<Block> blocksForTool) {
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

    private static float getTraitModifiedMiningSpeed(ItemInstance stack, BlockState state, float baseSpeed) {
        var totalModifier = 0f;
        for (var traitInstance : TraitHelper.getTraits(stack)) {
            if (traitInstance.isValid()) {
                totalModifier += traitInstance.getTrait().getMiningSpeedModifier(traitInstance.getLevel(), state, baseSpeed);
            }
        }
        return baseSpeed * (1f + totalModifier);
    }

    // Formerly onUpdate
    public static void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        var isEquipped = slot != null;
        inventoryTick(stack, level, entity, isEquipped);
    }

    public static void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, boolean isEquipped) {
        if (stack.has(SgDataComponents.RECALCULATE_FLAG)) {
            GearData.recalculateGearData(stack, entity instanceof Player ? (Player) entity : null);
            stack.remove(SgDataComponents.RECALCULATE_FLAG);
        }
        TraitHelper.tickTraits(level, entity, stack, isEquipped);
    }

    public static InteractionResult useOn(UseOnContext context) {
        InteractionResult ret = InteractionResult.PASS;
        for (var traitInstance : TraitHelper.getTraits(context.getItemInHand())) {
            if (traitInstance.isValid()) {
                InteractionResult result = traitInstance.getTrait().onItemUse(context, traitInstance.getLevel());
                if (result != InteractionResult.PASS) {
                    ret = result;
                }
            }
        }
        return ret;
    }

    public static void onItemSwing(ItemStack stack, LivingEntity wielder) {
        for (var traitInstance : TraitHelper.getTraits(stack)) {
            if (traitInstance.isValid()) {
                traitInstance.getTrait().onItemSwing(stack, wielder, traitInstance.getLevel());
            }
        }
    }

    public static Rarity getRarity(ItemStack stack) {
        int rarity = (int) GearData.getProperties(stack).getNumber(GearProperties.RARITY);
        if (stack.isEnchanted())
            if (Config.Client.isLoaded() && Config.Client.vanillaStyleTooltips.get()) {
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

    @Nullable
    public static Component getItemName(ItemStack gear, GearConstructionData constructionData) {
        var part = constructionData.getPrimaryPart();
        if (part == null) return null;

        Component partName = part.getMaterialName(gear);
        if (TimedEvents.isAprilFools()) {
            partName = partName.copy().append(Component.literal(" & Knuckles"));
        }
        String prefix = Util.makeDescriptionId("item", NameUtils.fromItem(gear));
        Component gearName = Component.translatable(prefix + ".nameProper", partName);
        Component result = gearName;

        if (gear.getItem() instanceof GearTool item) {
            if (item.requiresPartOfType(PartTypes.ROD.get()) && GearData.getPartOfType(gear, PartTypes.ROD.get()) == null) {
                result = Component.translatable(prefix + ".noRod", gearName);
            } else if (item.requiresPartOfType(PartTypes.CORD.get()) && GearData.getPartOfType(gear, PartTypes.CORD.get()) == null) {
                result = Component.translatable(prefix + ".unstrung", gearName);
            }
        }

        // Prefixes
        for (Component t : getNamePrefixes(gear, constructionData.parts())) {
            if (t.getContents() != PlainTextContents.EMPTY) {
                result = t.copy().append(TextUtil.misc("space")).append(result);
            }
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

    public static class Spear {
        public static KineticWeapon createKineticWeapon(ItemStack gear, GearPropertiesData properties) {
            var harvestTier = properties.get(GearProperties.HARVEST_TIER);
            var rarity = properties.getNumber(GearProperties.RARITY);
            float estimatedTier;
            if (harvestTier != null && harvestTier.value().levelHint().isPresent()) {
                try {
                    estimatedTier = Float.parseFloat(harvestTier.value().levelHint().get());
                } catch (NumberFormatException ex) {
                    estimatedTier = estimateTierByRarity(rarity);
                }
            } else {
                estimatedTier = estimateTierByRarity(rarity);
            }

            return new KineticWeapon(
                    10,
                    delayTicks(estimatedTier),
                    KineticWeapon.Condition.ofAttackerSpeed((int) (maxDismountDuration(rarity) * 20f), dismountMinSpeed(estimatedTier)),
                    KineticWeapon.Condition.ofAttackerSpeed((int) (maxKnockbackDuration(rarity) * 20f), 5.1f),
                    KineticWeapon.Condition.ofRelativeSpeed((int) (maxDamageDuration(estimatedTier) * 20f), 4.6f),
                    0.38f,
                    damageMultiplier(properties.getNumber(GearProperties.ATTACK_DAMAGE)),
                    Optional.of(estimatedTier < 1f ? SoundEvents.SPEAR_WOOD_USE : SoundEvents.SPEAR_USE),
                    Optional.of(estimatedTier < 1f ? SoundEvents.SPEAR_WOOD_HIT : SoundEvents.SPEAR_HIT)
            );
        }

        public static SwingAnimation createSwingAnimation(ItemStack gear, GearPropertiesData properties) {
            int duration = swingDuration(properties.getNumber(GearProperties.RARITY, 0f));
            return new SwingAnimation(SwingAnimationType.STAB, duration);
        }

        private static float estimateTierByRarity(float rarity) {
            double result = 0.434248 * Math.pow(rarity, 0.469146);
            return Math.round(2 * result) / 2.0f;
        }

        private static int swingDuration(float rarity) {
            double result = 0.631132 + 0.104594 * Math.log(rarity);
            double rounded = Math.round(20 * result) / 20f;
            return (int) (20 * rounded);
        }

        public static int delayTicks(float tier) {
            double result = 0.777915 * Math.pow(0.861644, tier);
            double rounded = Math.round(20 * result) / 20f;
            return (int) (20 * rounded);
        }

        public static float dismountMinSpeed(float tier) {
            double result = tier > 4
                    ? 14.0 * Math.pow(0.84, tier)
                    : -0.166667 * tier * tier * tier + 1.60714 * tier * tier - 5.5119 * tier + 14.01429;
            return Math.round(2 * result) / 2f;
        }

        public static float damageMultiplier(float attackDamage) {
            float multi = 0.125f * attackDamage + 0.7f;
            return (int) (multi * 100) / 100f;
        }

        public static float maxDismountDuration(float rarity) {
            if (rarity < 1) {
                return 5;
            }
            // a very loose approximation of vanilla material rarity to max dismount duration
            double result = 4.97761 - 0.543271 * Math.log(rarity);
            return Math.round(4 * result) / 4.0f;
        }

        public static float maxKnockbackDuration(float rarity) {
            if (rarity < 1) {
                return 10;
            }
            // a very loose approximation of vanilla material rarity to max knockback duration
            double result = 10.24271 - 0.949249 * Math.log(rarity);
            return Math.round(4 * result) / 4.0f;
        }

        public static float maxDamageDuration(float tier) {
            // roughly, wood = 0, stone = 1, copper = 1.5, iron = 2, diamond = 3, netherite = 4
            double result = 15.24935 * Math.pow(0.87033, tier);
            return Math.round(4 * result) / 4.0f;
        }
    }
}
