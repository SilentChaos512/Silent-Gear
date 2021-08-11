/*
 * Silent Gear -- GearEvents
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.gear.GearArmorItem;
import net.silentchaos512.gear.util.*;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.util.EntityHelper;

import java.util.*;
import java.util.function.Function;

@Mod.EventBusSubscriber
public final class GearEvents {
    public static final ResourceLocation APPLY_TIP_UPGRADE = SilentGear.getId("apply_tip_upgrade");
    public static final ResourceLocation CRAFTED_WITH_ROUGH_ROD = SilentGear.getId("crafted_with_rough_rod");
    public static final ResourceLocation MAX_DURABILITY = SilentGear.getId("max_durability");
    public static final ResourceLocation REPAIR_FROM_BROKEN = SilentGear.getId("repair_from_broken");
    public static final ResourceLocation UNIQUE_MAIN_PARTS = SilentGear.getId("unique_main_parts");
    public static final ResourceLocation FALL_WITH_MOONWALKER = SilentGear.getId("fall_with_moonwalker");

    private GearEvents() {}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent events) {
        entityAttackedThisTick.clear();
    }

    //region Damaging traits

    private static final Set<UUID> entityAttackedThisTick = new HashSet<>();

    @SubscribeEvent
    public static void onAttackEntity(LivingAttackEvent event) {
        // Check if already handled
        LivingEntity attacked = event.getEntityLiving();
        if (attacked == null || attacked.level.isClientSide || entityAttackedThisTick.contains(attacked.getUUID()))
            return;

        DamageSource source = event.getSource();
        if (source == null || !"player".equals(source.msgId)) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof Player)) return;

        Player player = (Player) attacker;
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof ICoreTool)) return;

        final float baseDamage = event.getAmount();
        final float newDamage = TraitHelper.activateTraits(weapon, baseDamage, (trait, level, value) ->
                trait.onAttackEntity(new TraitActionContext(player, level, weapon), attacked, value));

        if (Math.abs(newDamage - baseDamage) > 0.0001f) {
            event.setCanceled(true);
            entityAttackedThisTick.add(attacked.getUUID());
            attacked.hurt(source, newDamage);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntityLiving();

        DamageSource source = event.getSource();
        if (source == null || !"player".equals(source.msgId)) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof Player)) return;

        Player player = (Player) attacker;
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof ICoreTool)) return;

        // Traits that increase damage to specific mob types.
        // TODO: Maybe add a new trait type/effect to give pack makers control?

        int adamant = TraitHelper.getTraitLevel(weapon, Const.Traits.ADAMANT);
        if (adamant > 0 && attacked.getMaxHealth() > 21f) {
            event.setAmount(event.getAmount() + 2 * adamant);
        }

        int aquatic = TraitHelper.getTraitLevel(weapon, Const.Traits.AQUATIC);
        if (aquatic > 0 && attacked.canBreatheUnderwater()) {
            event.setAmount(event.getAmount() + 2 * aquatic);
        }

        int chilled = TraitHelper.getTraitLevel(weapon, Const.Traits.CHILLED);
        if (chilled > 0 && attacked.fireImmune()) {
            event.setAmount(event.getAmount() + 2 * chilled);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if (isFireDamage(event.getSource())) {
                damageFlammableItems(event);
            }
    }

    private static boolean isFireDamage(DamageSource source) {
        return source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA;
    }

    private static void damageFlammableItems(LivingDamageEvent event) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = event.getEntityLiving().getItemBySlot(slot);
            if (GearHelper.isGear(stack) && TraitHelper.hasTrait(stack, Const.Traits.FLAMMABLE)) {
                GearHelper.attemptDamage(stack, 2, event.getEntityLiving(), slot);
                if (GearHelper.isBroken(stack)) {
                    event.getEntityLiving().sendMessage(TextUtil.translate("trait", "flammable.itemDestroyed", stack.getHoverName()), Util.NIL_UUID);
                    event.getEntityLiving().broadcastBreakEvent(slot);
                    stack.shrink(1);
                }
            }
        }
    }

    private static final float BURN_TICKS_PER_DURABILITY = 200f / 50f;

    @SubscribeEvent
    public static void onFurnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        if (GearHelper.isGear(stack) && TraitHelper.hasTrait(stack, Const.Traits.FLAMMABLE)) {
            float durability = GearData.getStat(stack, GearHelper.getDurabilityStat(stack));
            event.setBurnTime((int) (durability * BURN_TICKS_PER_DURABILITY));
        }
    }

    //endregion

    //region Magic armor

    @SubscribeEvent
    public static void onLivingHurtMagicArmor(LivingHurtEvent event) {
        if (event.getSource().isMagic()) {
            float magicArmor = getTotalMagicArmor(event.getEntityLiving());
            float scale = 1f - getReducedMagicDamageScale(magicArmor);
            //SilentGear.LOGGER.debug("magic damage: {} x {} -> {}", event.getAmount(), scale, event.getAmount() * scale);

            if (event.getEntityLiving() instanceof Player) {
                // Damage player's armor
                ((Player) event.getEntityLiving()).getInventory().hurtArmor(event.getSource(), event.getAmount(), Inventory.ALL_ARMOR_SLOTS);
            }

            event.setAmount(event.getAmount() * scale);
        }
    }

    private static float getTotalMagicArmor(LivingEntity entity) {
        float total = 0f;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (stack.getItem() instanceof GearArmorItem) {
                total += ((GearArmorItem) stack.getItem()).getArmorMagicProtection(stack);
            }
        }
        return total;
    }

    private static float getReducedMagicDamageScale(float magicArmor) {
        // Scale linearly up to 60% for magic armor < 20. Above 20, scale half as fast (40 = 90%)
        if (magicArmor > 20)
            return 0.6f + 0.015f * (magicArmor - 20);
        return 0.03f * magicArmor;
    }

    //endregion

    //region Lustrous trait

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        final Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();

        if (tool.getItem() instanceof ICoreItem) {
            final BlockState state = event.getState();

            if (tool.isCorrectToolForDrops(state)) {
                int level = TraitHelper.getTraitLevel(tool, Const.Traits.LUSTROUS);
                int light = getLightForLustrousTrait(player.level, player.blockPosition());
                //use getNewSpeed() instead of getOriginalSpeed() to support other mods that are changing the break speed with this event.
                event.setNewSpeed(event.getNewSpeed() + getLustrousSpeedBonus(level, light));
            }
        }
    }

    public static int getLightForLustrousTrait(BlockAndTintGetter world, BlockPos pos) {
        int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = world.getBrightness(LightLayer.SKY, pos);
        // Block light is less effective
        return Math.max(skyLight, blockLight * 3 / 4);
    }

    public static int getLustrousSpeedBonus(int level, int light) {
        return 4 * level * light / 15;
    }

    // endregion

    @SubscribeEvent
    public static void onXpDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() == null) return;

        ItemStack tool = event.getAttackingPlayer().getMainHandItem();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, Const.Traits.ANCIENT);
        if (ancientLevel == 0) return;

        int bonusXp = (int) (event.getOriginalExperience() * Const.Traits.ANCIENT_XP_BOOST * ancientLevel);
        event.setDroppedExperience(event.getDroppedExperience() + bonusXp);
    }

    private static final List<Function<Level, Entity>> JABBERWOCKY_MOBS = ImmutableList.of(
            world -> new Wolf(EntityType.WOLF, world),
            world -> new Cat(EntityType.CAT, world),
            world -> new Rabbit(EntityType.RABBIT, world),
            world -> new Chicken(EntityType.CHICKEN, world),
            world -> new Cod(EntityType.COD, world),
            world -> new Salmon(EntityType.SALMON, world),
            world -> new Pufferfish(EntityType.PUFFERFISH, world)
    );

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null) return;

        ItemStack tool = event.getPlayer().getMainHandItem();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, Const.Traits.ANCIENT);
        if (ancientLevel > 0) {
            int bonusXp = (int) (event.getExpToDrop() * Const.Traits.ANCIENT_XP_BOOST * ancientLevel);
            event.setExpToDrop(event.getExpToDrop() + bonusXp);
        }

        if (TraitHelper.hasTrait(tool, Const.Traits.JABBERWOCKY) && event.getState().is(Tags.Blocks.ORES_DIAMOND) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            Entity entity = JABBERWOCKY_MOBS.get(SilentGear.RANDOM.nextInt(JABBERWOCKY_MOBS.size())).apply(event.getPlayer().getCommandSenderWorld());
            entity.teleportTo(event.getPos().getX() + 0.5, event.getPos().getY(), event.getPos().getZ() + 0.5);
            EntityHelper.safeSpawn(entity);
        }
    }

    @SubscribeEvent
    public static void onGearCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();

        if (GearHelper.isGear(result) && event.getPlayer() instanceof ServerPlayer) {
            // Try to trigger some advancments
            ServerPlayer player = (ServerPlayer) event.getPlayer();

            // Crude tool
            if (GearData.hasPart(result, PartType.ROD, p -> p.containsMaterial(Const.Materials.WOOD_ROUGH))) {
                LibTriggers.GENERIC_INT.trigger(player, CRAFTED_WITH_ROUGH_ROD, 1);
            }

            // Repair from broken
            int brokenCount = GearData.getBrokenCount(result);
            int repairCount = GearData.getRepairCount(result);
            if (brokenCount > 0 && repairCount > 0) {
                LibTriggers.GENERIC_INT.trigger(player, REPAIR_FROM_BROKEN, brokenCount);
            }

            // High durability
            LibTriggers.GENERIC_INT.trigger(player, MAX_DURABILITY, result.getMaxDamage());

            PartDataList parts = GearData.getConstructionParts(result);

            // Add tip upgrade?
            if (!parts.getTips().isEmpty()) {
                LibTriggers.GENERIC_INT.trigger(player, APPLY_TIP_UPGRADE, 1);
            }

            // Mixed materials?
            LibTriggers.GENERIC_INT.trigger(player, UNIQUE_MAIN_PARTS, getUniqueMainMaterialCount(parts));
        }
    }

    private static int getUniqueMainMaterialCount(PartDataList parts) {
        for (PartData part : parts) {
            if (part.get() instanceof CompoundPart && part.getType() == PartType.MAIN) {
                MaterialList materials = CompoundPartItem.getMaterials(part.getItem());
                return SynergyUtils.getUniques(materials).size();
            }
        }

        return 1;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer instanceof Player && !killer.level.isClientSide) {
            Player player = (Player) killer;
            if (TraitHelper.hasTraitEitherHand(player, Const.Traits.CONFETTI)) {
                for (int i = 0; i < 3; ++i) {
                    FireworkRocketEntity rocket = new FireworkRocketEntity(player.level, event.getEntity().getX(), event.getEntity().getEyeY(), event.getEntity().getZ(), createRandomFirework());
                    EntityHelper.safeSpawn(rocket);
                }
            }
        }
    }

    private static ItemStack createRandomFirework() {
        ItemStack ret = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag nbt = ret.getOrCreateTagElement("Fireworks");
        nbt.putByte("Flight", (byte) (SilentGear.RANDOM.nextInt(3) + 1));
        CompoundTag explosion = new CompoundTag();
        explosion.putByte("Type", (byte) SilentGear.RANDOM.nextInt(FireworkRocketItem.Shape.values().length));
        ListTag colors = new ListTag();
        for (int i = 0; i < SilentGear.RANDOM.nextInt(4) + 1; ++i) {
            DyeColor dye = DyeColor.values()[SilentGear.RANDOM.nextInt(DyeColor.values().length)];
            SilentGear.LOGGER.debug(dye);
            colors.add(IntTag.valueOf(dye.getFireworkColor()));
        }
        explosion.put("Colors", colors);
        ListTag explosions = new ListTag();
        explosions.add(explosion);
        nbt.put("Explosions", explosions);
        return ret;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level.isClientSide && event.phase == TickEvent.Phase.START) {
            int magnetic = Math.max(TraitHelper.getHighestLevelEitherHand(event.player, Const.Traits.MAGNETIC),
                    TraitHelper.getHighestLevelCurio(event.player, Const.Traits.MAGNETIC));

            if (magnetic > 0) {
                tickMagnetic(event.player, magnetic);
            }

            // Turtle trait
            // TODO: May want to add player conditions to wielder effect traits, for more control and possibilities for pack devs.
            if (!event.player.isEyeInFluid(FluidTags.WATER) && TraitHelper.hasTrait(event.player.getItemBySlot(EquipmentSlot.HEAD), Const.Traits.TURTLE)) {
                // Vanilla duration is 200, but that causes flickering numbers/icon
                event.player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 210, 0, false, false, true));
            }

            // Void Ward trait
            if (event.player.getY() < -64 && TraitHelper.hasTraitArmor(event.player, Const.Traits.VOID_WARD)) {
                // A small boost to get the player out of the void, then levitation and slow falling
                // to allow them to navigate back to safety
                event.player.push(0, 10, 0);
                event.player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 3, true, false));
                event.player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 9, true, false));
            }
        }
    }

    private static void tickMagnetic(Player player, int magneticLevel) {
        if (player.isCrouching()) return;

        final int range = magneticLevel * 3 + 1;
        Vec3 target = new Vec3(player.getX(), player.getY(0.5), player.getZ());

        AABB aabb = new AABB(player.getX() - range, player.getY() - range, player.getZ() - range, player.getX() + range + 1, player.getY() + range + 1, player.getZ() + range + 1);
        for (ItemEntity entity : player.level.getEntitiesOfClass(ItemEntity.class, aabb, e -> e.distanceToSqr(player) < range * range)) {
            if (canMagneticPullItem(entity)) {
                // Accelerate to target point
                Vec3 vec = entity.getDismountLocationForPassenger(player).vectorTo(target);
                vec = vec.normalize().scale(0.06);
                if (entity.getY() < target.y) {
                    double xzDistanceSq = (entity.getX() - target.x) * (entity.getX() - target.x) + (entity.getZ() - target.z) * (entity.getZ() - target.z);
                    vec = vec.add(0, 0.005 + xzDistanceSq / 1000, 0);
                }
                entity.push(vec.x, vec.y, vec.z);
            }
        }
    }

    private static boolean canMagneticPullItem(ItemEntity entity) {
        return !entity.hasPickUpDelay() && !entity.getPersistentData().getBoolean("PreventRemoteMovement");
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlot.FEET);

        if (event.getDistance() > 3 && GearHelper.isGear(stack) && !GearHelper.isBroken(stack)) {
            // Moonwalker fall damage canceling/reduction
            int moonwalker = TraitHelper.getTraitLevel(stack, Const.Traits.MOONWALKER);
            if (moonwalker > 0) {
                float gravity = 1 + moonwalker * Const.Traits.MOONWALKER_GRAVITY_MOD;
                event.setDistance(event.getDistance() * gravity);

                if (event.getEntityLiving() instanceof ServerPlayer) {
                    LibTriggers.GENERIC_INT.trigger((ServerPlayer) event.getEntityLiving(), FALL_WITH_MOONWALKER, 1);
                }
            }

            // Bounce fall damage nullification and bouncing
            // FIXME: bounce is very unpredictable. Usually it does not work at all. The few times
            //  it does, the bounce height is inconsistent
            int bounce = TraitHelper.getTraitLevel(stack, Const.Traits.BOUNCE);
            if (bounce > 0 && event.getDistance() > 3) {
                if (!event.getEntity().isSuppressingBounce()) {
//                    bounceEntity(event.getEntity());
                    int damage = (int) (event.getDistance() / 3) - 1;
                    if (damage > 0) {
                        GearHelper.attemptDamage(stack, damage, event.getEntityLiving(), EquipmentSlot.FEET);
                    }
                    event.getEntity().level.playSound(null, event.getEntity().blockPosition(), SoundEvents.SLIME_BLOCK_FALL, SoundSource.PLAYERS, 1f, 1f);
                    event.setCanceled(true);
                }
            }
        }
    }

    private static final Map<Entity, Vec3> BOUNCE_TICKS = new HashMap<>();

//    @SubscribeEvent
    public static void onPlayerTickBouncing(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.isFallFlying() && BOUNCE_TICKS.containsKey(event.player)) {
//            event.player.moveForced(event.player.getPosX(), event.player.getPosY() + 0.1, event.player.getPosZ());
            Vec3 motion = BOUNCE_TICKS.get(event.player);
            event.player.setDeltaMovement(motion.x, motion.y * 20, motion.z); // why * 20?
            event.player.setOnGround(false);
            BOUNCE_TICKS.remove(event.player);
            SilentGear.LOGGER.debug("bounce {}", motion);

            if (event.player.getDeltaMovement().y < 0) {
                bounceEntity(event.player);
            }
        }
    }

    private static void bounceEntity(Entity entity) {
        Vec3 vector3d = entity.getDeltaMovement();
        if (vector3d.y < 0) {
//            entity.moveForced(entity.getPosX(), entity.getPosY() + 0.1, entity.getPosZ());
//            entity.setOnGround(false);
            Vec3 vec = new Vec3(vector3d.x, -vector3d.y * 0.75, vector3d.z);
//            entity.setMotion(vec);
            SilentGear.LOGGER.debug("{} -> {}", vector3d, vec);
            BOUNCE_TICKS.put(entity, vec);
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof Player) {
            Entity source = event.getSource().getDirectEntity();
            if (source instanceof LivingEntity) {
                Player player = (Player) event.getEntityLiving();
                int bounce = TraitHelper.getHighestLevelArmor(player, Const.Traits.BOUNCE);
                if (bounce > 0) {
                    SilentGear.LOGGER.debug("knockback");
                    ((LivingEntity) source).knockback(2 * bounce,
                            -Mth.sin(source.getYRot() * ((float)Math.PI / 180F)),
                            Mth.cos(source.getYRot() * ((float)Math.PI / 180F)));
                }
            }
        }
    }
}
