package net.silentchaos512.gear.event;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.BreakEventHandler;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.NumberPropertyValue;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.item.gear.GearArmorItem;
import net.silentchaos512.gear.setup.SgCriteriaTriggers;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@EventBusSubscriber
public final class GearEvents {
    public static final ResourceLocation APPLY_TIP_UPGRADE = SilentGear.getId("apply_tip_upgrade");
    public static final ResourceLocation CRAFTED_WITH_ROUGH_ROD = SilentGear.getId("crafted_with_rough_rod");
    public static final ResourceLocation MAX_DURABILITY = SilentGear.getId("max_durability");
    public static final ResourceLocation REPAIR_FROM_BROKEN = SilentGear.getId("repair_from_broken");
    public static final ResourceLocation UNIQUE_MAIN_PARTS = SilentGear.getId("unique_main_parts");
    public static final ResourceLocation FALL_WITH_MOONWALKER = SilentGear.getId("fall_with_moonwalker");

    private GearEvents() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent events) {
        entityAttackedThisTick.clear();
    }

    //region Damaging traits

    private static final Set<UUID> entityAttackedThisTick = new HashSet<>();

    @SubscribeEvent
    public static void onAttackEntity(LivingIncomingDamageEvent event) {
        // Check if already handled
        LivingEntity attacked = event.getEntity();
        if (attacked == null || attacked.level().isClientSide || entityAttackedThisTick.contains(attacked.getUUID()))
            return;

        DamageSource source = event.getSource();
        if (source == null || !"player".equals(source.getMsgId())) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof Player)) return;

        Player player = (Player) attacker;
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof ICoreTool)) return;

        final float baseDamage = event.getAmount();
        final float newDamage = TraitHelper.activateTraits(weapon, baseDamage, (trait, value) ->
                trait.getTrait().onAttackEntity(new TraitActionContext(player, trait, weapon), attacked, value));

        if (Math.abs(newDamage - baseDamage) > 0.0001f) {
            event.setCanceled(true);
            entityAttackedThisTick.add(attacked.getUUID());
            attacked.hurt(source, newDamage);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof Player)
            if (isFireDamage(event.getSource())) {
                damageFlammableItems(event);
            }
    }

    private static boolean isFireDamage(@Nullable DamageSource source) {
        return source != null && source.is(DamageTypeTags.IS_FIRE);
    }

    private static void damageFlammableItems(LivingDamageEvent.Post event) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = event.getEntity().getItemBySlot(slot);
            if (GearHelper.isGear(stack) && TraitHelper.hasTrait(stack, Const.Traits.FLAMMABLE)) {
                GearHelper.attemptDamage(stack, 2, event.getEntity(), slot);
                if (GearHelper.isBroken(stack)) {
                    event.getEntity().sendSystemMessage(TextUtil.translate("trait", "flammable.itemDestroyed", stack.getHoverName()));
                    event.getEntity().onEquippedItemBroken(stack.getItem(), slot);
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
            float durability = stack.getMaxDamage();
            event.setBurnTime((int) (durability * BURN_TICKS_PER_DURABILITY));
        }
    }

    //endregion

    //region Magic armor

    @SubscribeEvent
    public static void onLivingHurtMagicArmor(LivingDamageEvent.Pre event) {
        if (event.getSource().is(DamageTypes.MAGIC)) {
            float magicArmor = getTotalMagicArmor(event.getEntity());
            float scale = 1f - getReducedMagicDamageScale(magicArmor);
            event.setNewDamage(event.getNewDamage() * scale);
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
        final Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();

        if (tool.getItem() instanceof ICoreItem) {
            final BlockState state = event.getState();

            if (tool.isCorrectToolForDrops(state)) {
                int level = TraitHelper.getTraitLevel(tool, Const.Traits.LUSTROUS);
                int light = getLightForLustrousTrait(player.level(), player.blockPosition());
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
    public static void onBlockDrops(BlockDropsEvent event) {
        if (event.getBreaker() == null) return;

        ItemStack tool = event.getTool();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, Const.Traits.ANCIENT);
        if (ancientLevel > 0) {
            int bonusXp = (int) (event.getDroppedExperience() * Const.Traits.ANCIENT_XP_BOOST * ancientLevel); // TODO: Make it a trait effect
            event.setDroppedExperience(event.getDroppedExperience() + bonusXp);
        }

        if (TraitHelper.hasTrait(tool, Const.Traits.JABBERWOCKY) && event.getState().is(Tags.Blocks.ORES_DIAMOND) && !hasSilkTouch(event.getLevel(), tool)) {
            Entity entity = JABBERWOCKY_MOBS.get(SilentGear.RANDOM.nextInt(JABBERWOCKY_MOBS.size())).apply(event.getBreaker().getCommandSenderWorld());
            entity.teleportTo(event.getPos().getX() + 0.5, event.getPos().getY(), event.getPos().getZ() + 0.5);
            event.getLevel().addFreshEntity(entity);
        }
    }

    private static boolean hasSilkTouch(Level level, ItemStack tool) {
        Holder.Reference<Enchantment> silkTouch = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.SILK_TOUCH);
        return EnchantmentHelper.getTagEnchantmentLevel(silkTouch, tool) > 0;
    }

    @SubscribeEvent
    public static void onGearCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();

        if (GearHelper.isGear(result) && event.getEntity() instanceof ServerPlayer player) {
            // Try to trigger some advancements

            // Crude tool
            if (GearData.hasPart(result, PartTypes.ROD.get(), p -> {
                var primaryMaterial = p.getPrimaryMaterial();
                return primaryMaterial != null && primaryMaterial.is(Const.Materials.WOOD_ROUGH);
            })) {
                SgCriteriaTriggers.CRAFTED_WITH_ROUGH_ROD.get().trigger(player);
            }

            // Repairs
            int brokenCount = GearData.getBrokenCount(result);
            int repairedCount = GearData.getRepairedCount(result);
            SgCriteriaTriggers.GEAR_REPAIRED.get().trigger(player, brokenCount, repairedCount);

            // Number properties
            for (var propertyType : SgRegistries.GEAR_PROPERTY) {
                GearPropertyValue<?> property = GearData.getProperties(result, player).get(propertyType);
                if (property instanceof NumberPropertyValue numberProperty) {
                    SgCriteriaTriggers.GEAR_PROPERTY.get().trigger(player, propertyType, numberProperty.value());
                }
            }

            // Parts
            SgCriteriaTriggers.HAS_PART.get().trigger(player, result);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer instanceof Player player && !killer.level().isClientSide) {
            if (TraitHelper.hasTraitEitherHand(player, Const.Traits.CONFETTI)) {
                for (int i = 0; i < 3; ++i) {
                    FireworkRocketEntity rocket = new FireworkRocketEntity(player.level(), event.getEntity().getX(), event.getEntity().getEyeY(), event.getEntity().getZ(), createRandomFirework());
                    killer.level().addFreshEntity(rocket);
                }
            }
        }
    }

    private static ItemStack createRandomFirework() {
        ItemStack ret = new ItemStack(Items.FIREWORK_ROCKET);
        int flightDuration = SilentGear.RANDOM.nextInt(3) + 1;
        var shape = FireworkExplosion.Shape.values()[SilentGear.RANDOM.nextInt(FireworkExplosion.Shape.values().length)];
        var fireworkColor = DyeColor.values()[SilentGear.RANDOM.nextInt(DyeColor.values().length)].getFireworkColor();
        var fireworkExplosion = new FireworkExplosion(
                shape,
                IntList.of(fireworkColor),
                IntList.of(),
                false,
                false
        );
        ret.set(DataComponents.FIREWORKS, new Fireworks(flightDuration, Collections.singletonList(fireworkExplosion)));
        return ret;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (!player.level().isClientSide) {
            // Turtle trait
            // TODO: May want to add player conditions to wielder effect traits, for more control and possibilities for pack devs.
            if (!player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) && TraitHelper.hasTrait(player.getItemBySlot(EquipmentSlot.HEAD), Const.Traits.TURTLE)) {
                // Vanilla duration is 200, but that causes flickering numbers/icon
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 210, 0, false, false, true));
            }

            // Void Ward trait
            if (player.getY() < -64 && TraitHelper.hasTraitArmor(player, Const.Traits.VOID_WARD)) {
                // A small boost to get the player out of the void, then levitation and slow falling
                // to allow them to navigate back to safety
                player.push(0, 10, 0);
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 3, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 9, true, false));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        ItemStack stack = event.getEntity().getItemBySlot(EquipmentSlot.FEET);

        if (event.getDistance() > 3 && event.getEntity() instanceof Player player) {
            // Moonwalker fall damage canceling/reduction
            int moonwalker = TraitHelper.getHighestLevelArmorOrCurio(player, Const.Traits.MOONWALKER);
            if (moonwalker > 0) {
                float gravity = 1 + moonwalker * Const.Traits.MOONWALKER_GRAVITY_MOD;
                event.setDistance(event.getDistance() * gravity);

                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    SgCriteriaTriggers.FALL_WITH_MOONWALKER.get().trigger(serverPlayer);
                }
            }

            // Bounce fall damage nullification and bouncing
            // FIXME: bounce is very unpredictable. Usually it does not work at all. The few times
            //  it does, the bounce height is inconsistent
            int bounce = TraitHelper.getHighestLevelArmorOrCurio(player, Const.Traits.BOUNCE);
            if (bounce > 0 && event.getDistance() > 3) {
                if (!event.getEntity().isSuppressingBounce()) {
//                    bounceEntity(event.getEntity());
                    int damage = (int) (event.getDistance() / 3) - 1;
                    if (damage > 0) {
                        GearHelper.attemptDamage(stack, damage, event.getEntity(), EquipmentSlot.FEET);
                    }
                    event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SoundEvents.SLIME_BLOCK_FALL, SoundSource.PLAYERS, 1f, 1f);
                    event.setCanceled(true);
                }
            }
        }
    }

    private static final Map<Entity, Vec3> BOUNCE_TICKS = new HashMap<>();

//    @SubscribeEvent
    public static void onPlayerTickBouncing(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (!player.isFallFlying() && BOUNCE_TICKS.containsKey(player)) {
//            player.moveForced(player.getPosX(), player.getPosY() + 0.1, player.getPosZ());
            Vec3 motion = BOUNCE_TICKS.get(player);
            player.setDeltaMovement(motion.x, motion.y * 20, motion.z); // why * 20?
            player.setOnGround(false);
            BOUNCE_TICKS.remove(player);
            SilentGear.LOGGER.debug("bounce {}", motion);

            if (player.getDeltaMovement().y < 0) {
                bounceEntity(player);
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
    public static void onPlayerHurt(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof Player) {
            Entity source = event.getSource().getDirectEntity();
            if (source instanceof LivingEntity) {
                Player player = (Player) event.getEntity();
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

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        var player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        if (tool.getItem() instanceof BreakEventHandler breakEventHandler) {
            breakEventHandler.onBlockBreakEvent(tool, player, player.level(), event.getPos(), event.getState());
        }
    }
}
