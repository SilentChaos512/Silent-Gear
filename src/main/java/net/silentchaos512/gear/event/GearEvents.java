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
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
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
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.gear.CoreArmor;
import net.silentchaos512.gear.util.*;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.util.EntityHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        if (attacked == null || attacked.world.isRemote || entityAttackedThisTick.contains(attacked.getUniqueID()))
            return;

        DamageSource source = event.getSource();
        if (source == null || !"player".equals(source.damageType)) return;

        Entity attacker = source.getTrueSource();
        if (!(attacker instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) attacker;
        ItemStack weapon = player.getHeldItemMainhand();
        if (!(weapon.getItem() instanceof ICoreTool)) return;

        final float baseDamage = event.getAmount();
        final float newDamage = TraitHelper.activateTraits(weapon, baseDamage, (trait, level, value) ->
                trait.onAttackEntity(new TraitActionContext(player, level, weapon), attacked, value));

        if (Math.abs(newDamage - baseDamage) > 0.0001f) {
            event.setCanceled(true);
            entityAttackedThisTick.add(attacked.getUniqueID());
            attacked.attackEntityFrom(source, newDamage);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof PlayerEntity && isFireDamage(event.getSource())) {
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                ItemStack stack = event.getEntityLiving().getItemStackFromSlot(slot);
                if (GearHelper.isGear(stack) && TraitHelper.hasTrait(stack, Const.Traits.FLAMMABLE)) {
                    GearHelper.attemptDamage(stack, 2, event.getEntityLiving(), slot);
                    if (GearHelper.isBroken(stack)) {
                        event.getEntityLiving().sendMessage(TextUtil.translate("trait", "flammable.itemDestroyed", stack.getDisplayName()), Util.DUMMY_UUID);
                        event.getEntityLiving().sendBreakAnimation(slot);
                        stack.shrink(1);
                    }
                }
            }
        }
    }

    private static boolean isFireDamage(DamageSource source) {
        return source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE || source == DamageSource.LAVA;
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
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().isMagicDamage()) {
            float magicArmor = getTotalMagicArmor(event.getEntityLiving());
            float scale = 1f - getReducedMagicDamageScale(magicArmor);
            //SilentGear.LOGGER.debug("magic damage: {} x {} -> {}", event.getAmount(), scale, event.getAmount() * scale);

            if (event.getEntityLiving() instanceof PlayerEntity) {
                // Damage player's armor
                ((PlayerEntity) event.getEntityLiving()).inventory.func_234563_a_(event.getSource(), event.getAmount());
            }

            event.setAmount(event.getAmount() * scale);
        }
    }

    private static float getTotalMagicArmor(LivingEntity entity) {
        float total = 0f;
        for (ItemStack stack : entity.getArmorInventoryList()) {
            if (stack.getItem() instanceof CoreArmor) {
                total += ((CoreArmor) stack.getItem()).getArmorMagicProtection(stack);
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
        final PlayerEntity player = event.getPlayer();
        ItemStack tool = player.getHeldItemMainhand();

        if (tool.getItem() instanceof ICoreItem) {
            final BlockState state = event.getState();
            ToolType toolClass = state.getBlock().getHarvestTool(state);
//            if (toolClass == null) toolClass = "";
            final int blockLevel = state.getBlock().getHarvestLevel(state);
            final int toolLevel = tool.getItem().getHarvestLevel(tool, toolClass, player, state);
            final boolean canHarvest = toolLevel >= blockLevel;

            if (canHarvest) {
                int level = TraitHelper.getTraitLevel(tool, Const.Traits.LUSTROUS);
                int light = getLightForLustrousTrait(player.world, player.getPosition());
                //use getNewSpeed() instead of getOriginalSpeed() to support other mods that are changing the break speed with this event.
                event.setNewSpeed(event.getNewSpeed() + getLustrousSpeedBonus(level, light));
            }
        }
    }

    public static int getLightForLustrousTrait(IBlockDisplayReader world, BlockPos pos) {
        int blockLight = world.getLightFor(LightType.BLOCK, pos);
        int skyLight = world.getLightFor(LightType.SKY, pos);
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

        ItemStack tool = event.getAttackingPlayer().getHeldItemMainhand();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, Const.Traits.ANCIENT);
        if (ancientLevel == 0) return;

        int bonusXp = (int) (event.getOriginalExperience() * Const.Traits.ANCIENT_XP_BOOST * ancientLevel);
        event.setDroppedExperience(event.getDroppedExperience() + bonusXp);
    }

    private static final List<Function<World, Entity>> JABBERWOCKY_MOBS = ImmutableList.of(
            world -> new WolfEntity(EntityType.WOLF, world),
            world -> new CatEntity(EntityType.CAT, world),
            world -> new RabbitEntity(EntityType.RABBIT, world),
            world -> new ChickenEntity(EntityType.CHICKEN, world),
            world -> new CodEntity(EntityType.COD, world),
            world -> new SalmonEntity(EntityType.SALMON, world),
            world -> new PufferfishEntity(EntityType.PUFFERFISH, world)
    );

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null) return;

        ItemStack tool = event.getPlayer().getHeldItemMainhand();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, Const.Traits.ANCIENT);
        if (ancientLevel > 0) {
            int bonusXp = (int) (event.getExpToDrop() * Const.Traits.ANCIENT_XP_BOOST * ancientLevel);
            event.setExpToDrop(event.getExpToDrop() + bonusXp);
        }

        if (TraitHelper.hasTrait(tool, Const.Traits.JABBERWOCKY) && event.getState().isIn(Tags.Blocks.ORES_DIAMOND) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            Entity entity = JABBERWOCKY_MOBS.get(SilentGear.RANDOM.nextInt(JABBERWOCKY_MOBS.size())).apply(event.getPlayer().getEntityWorld());
            entity.setPositionAndUpdate(event.getPos().getX() + 0.5, event.getPos().getY(), event.getPos().getZ() + 0.5);
            EntityHelper.safeSpawn(entity);
        }
    }

    @SubscribeEvent
    public static void onGearCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();

        if (GearHelper.isGear(result) && event.getPlayer() instanceof ServerPlayerEntity) {
            // Try to trigger some advancments
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

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
                List<MaterialInstance> materials = CompoundPartItem.getMaterials(part.getItem());
                return SynergyUtils.getUniques(materials).size();
            }
        }

        return 1;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getTrueSource();
        if (killer instanceof PlayerEntity && !killer.world.isRemote) {
            PlayerEntity player = (PlayerEntity) killer;
            if (TraitHelper.hasTraitEitherHand(player, Const.Traits.CONFETTI)) {
                for (int i = 0; i < 3; ++i) {
                    FireworkRocketEntity rocket = new FireworkRocketEntity(player.world, event.getEntity().getPosX(), event.getEntity().getPosYEye(), event.getEntity().getPosZ(), createRandomFirework());
                    EntityHelper.safeSpawn(rocket);
                }
            }
        }
    }

    private static ItemStack createRandomFirework() {
        ItemStack ret = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundNBT nbt = ret.getOrCreateChildTag("Fireworks");
        nbt.putByte("Flight", (byte) (SilentGear.RANDOM.nextInt(3) + 1));
        CompoundNBT explosion = new CompoundNBT();
        explosion.putByte("Type", (byte) SilentGear.RANDOM.nextInt(FireworkRocketItem.Shape.values().length));
        ListNBT colors = new ListNBT();
        for (int i = 0; i < SilentGear.RANDOM.nextInt(4) + 1; ++i) {
            DyeColor dye = DyeColor.values()[SilentGear.RANDOM.nextInt(DyeColor.values().length)];
            SilentGear.LOGGER.debug(dye);
            colors.add(IntNBT.valueOf(dye.getFireworkColor()));
        }
        explosion.put("Colors", colors);
        ListNBT explosions = new ListNBT();
        explosions.add(explosion);
        nbt.put("Explosions", explosions);
        return ret;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote) {
            int magnetic = Math.max(TraitHelper.getHighestLevelEitherHand(event.player, Const.Traits.MAGNETIC),
                    TraitHelper.getHighestLevelCurio(event.player, Const.Traits.MAGNETIC));

            if (magnetic > 0) {
                tickMagnetic(event.player, magnetic);
            }

            // Turtle trait
            // TODO: May want to add player conditions to wielder effect traits, for more control and possibilities for pack devs.
            if (!event.player.areEyesInFluid(FluidTags.WATER) && TraitHelper.hasTrait(event.player.getItemStackFromSlot(EquipmentSlotType.HEAD), Const.Traits.TURTLE)) {
                // Vanilla duration is 200, but that causes flickering numbers/icon
                event.player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 210, 0, false, false, true));
            }
        }
    }

    private static void tickMagnetic(PlayerEntity player, int magneticLevel) {
        if (player.isCrouching()) return;

        final int range = magneticLevel * 3 + 1;
        Vector3d target = new Vector3d(player.getPosX(), player.getPosYHeight(0.5), player.getPosZ());

        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - range, player.getPosY() - range, player.getPosZ() - range, player.getPosX() + range + 1, player.getPosY() + range + 1, player.getPosZ() + range + 1);
        for (ItemEntity entity : player.world.getEntitiesWithinAABB(ItemEntity.class, aabb, e -> e.getDistanceSq(player) < range * range)) {
            if (canMagneticPullItem(entity)) {
                // Accelerate to target point
                Vector3d vec = entity.func_230268_c_(player).subtractReverse(target);
                vec = vec.normalize().scale(0.03);
                if (entity.getPosY() < target.y) {
                    double xzDistanceSq = (entity.getPosX() - target.x) * (entity.getPosX() - target.x) + (entity.getPosZ() - target.z) * (entity.getPosZ() - target.z);
                    vec = vec.add(0, 0.005 + xzDistanceSq / 1000, 0);
                }
                entity.addVelocity(vec.x, vec.y, vec.z);
            }
        }
    }

    private static boolean canMagneticPullItem(ItemEntity entity) {
        return !entity.cannotPickup() && !entity.getPersistentData().getBoolean("PreventRemoteMovement");
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET);

        if (!stack.isEmpty()) {
            // TODO: Add a dedicated fall protection trait
            //  Slime armor linings?
            int moonwalker = TraitHelper.getTraitLevel(stack, Const.Traits.MOONWALKER);
            if (moonwalker > 0) {
                float gravity = 1 + moonwalker * Const.Traits.MOONWALKER_GRAVITY_MOD;
                event.setDistance(event.getDistance() * gravity);

                if (event.getEntityLiving() instanceof ServerPlayerEntity) {
                    LibTriggers.GENERIC_INT.trigger((ServerPlayerEntity) event.getEntityLiving(), FALL_WITH_MOONWALKER, 1);
                }
            }
        }
    }
}
