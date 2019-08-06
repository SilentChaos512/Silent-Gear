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

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.traits.TraitConst;
import net.silentchaos512.gear.traits.TraitManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.advancements.LibTriggers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public final class GearEvents {
    private static final ResourceLocation APPLY_TIP_UPGRADE = SilentGear.getId("apply_tip_upgrade");
    private static final ResourceLocation MAX_DURABILITY = SilentGear.getId("max_durability");
    private static final ResourceLocation REPAIR_FROM_BROKEN = SilentGear.getId("repair_from_broken");
    private static final ResourceLocation UNIQUE_MAIN_PARTS = SilentGear.getId("unique_main_parts");

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
        if (attacked == null || entityAttackedThisTick.contains(attacked.getUniqueID())) return;

        DamageSource source = event.getSource();
        if (source == null || !"player".equals(source.damageType)) return;

        Entity attacker = source.getTrueSource();
        if (!(attacker instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) attacker;
        ItemStack weapon = player.getHeldItemMainhand();
        if (!(weapon.getItem() instanceof ICoreTool)) return;

        final float baseDamage = event.getAmount();
        final float newDamage = TraitHelper.activateTraits(weapon, baseDamage, (trait, level, value) ->
                trait.onAttackEntity(new TraitActionContext(player, level, weapon), attacked, baseDamage));

        if (Math.abs(newDamage - baseDamage) > 0.0001f) {
            event.setCanceled(true);
            entityAttackedThisTick.add(attacked.getUniqueID());
            attacked.attackEntityFrom(source, newDamage);
        }
    }

    //endregion

    //region Lustrous trait

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        final PlayerEntity player = event.getEntityPlayer();
        ItemStack tool = player.getHeldItemMainhand();

        if (tool.getItem() instanceof ICoreItem) {
            final BlockState state = event.getState();
            ToolType toolClass = state.getBlock().getHarvestTool(state);
//            if (toolClass == null) toolClass = "";
            final int blockLevel = state.getBlock().getHarvestLevel(state);
            final int toolLevel = tool.getItem().getHarvestLevel(tool, toolClass, player, state);
            final boolean canHarvest = toolLevel >= blockLevel;

            if (canHarvest) {
                ITrait lustrous = TraitManager.get(TraitConst.LUSTROUS);
                int level = TraitHelper.getTraitLevel(tool, lustrous);
                int light = getLightForLustrousTrait(player.world, player.getPosition());
                event.setNewSpeed(event.getOriginalSpeed() + getLustrousSpeedBonus(level, light));
            }
        }
    }

    public static int getLightForLustrousTrait(IEnviromentBlockReader world, BlockPos pos) {
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
    public static void onBlockDrops(BlockEvent.HarvestDropsEvent event) {
        PlayerEntity harvester = event.getHarvester();
        if (harvester == null || event.isSilkTouching()) return;

        if (!(harvester instanceof ServerPlayerEntity)) return;

        ItemStack tool = harvester.getHeldItemMainhand();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int magmaticLevel = TraitHelper.getTraitLevel(tool, TraitConst.MAGMATIC);
        if (magmaticLevel == 0) return;

        for (int i = 0; i < event.getDrops().size(); ++i) {
            ItemStack stack = event.getDrops().get(i);
            ServerWorld world = ((ServerPlayerEntity) harvester).getServerWorld();

            // Magmatic smelting
            Optional<FurnaceRecipe> recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), world);
            if (recipe.isPresent()) {
                ItemStack smelted = recipe.get().getRecipeOutput();
                if (!smelted.isEmpty()) {
                    ItemStack copy = smelted.copy();
                    copy.setCount(stack.getCount());
                    event.getDrops().remove(i);
                    event.getDrops().add(i, copy);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onXpDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() == null) return;

        ItemStack tool = event.getAttackingPlayer().getHeldItemMainhand();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, TraitConst.ANCIENT);
        if (ancientLevel == 0) return;

        int bonusXp = (int) (event.getOriginalExperience() * TraitConst.ANCIENT_XP_BOOST * ancientLevel);
        event.setDroppedExperience(event.getDroppedExperience() + bonusXp);
    }

    @SubscribeEvent
    public static void onBlockXpDrop(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null) return;

        ItemStack tool = event.getPlayer().getHeldItemMainhand();
        if (tool.isEmpty() || !(tool.getItem() instanceof ICoreTool)) return;

        int ancientLevel = TraitHelper.getTraitLevel(tool, TraitConst.ANCIENT);
        if (ancientLevel == 0) return;

        int bonusXp = (int) (event.getExpToDrop() * TraitConst.ANCIENT_XP_BOOST * ancientLevel);
        event.setExpToDrop(event.getExpToDrop() + bonusXp);
    }

    @SubscribeEvent
    public static void onGearCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (!(result.getItem() instanceof ICoreItem)) return;

        if (event.getPlayer() instanceof ServerPlayerEntity) {
            // Try to trigger some advancments
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

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
            int mainCount = parts.getUniqueParts(true).size();
            SilentGear.LOGGER.debug("mainCount = {}", mainCount);
            LibTriggers.GENERIC_INT.trigger(player, UNIQUE_MAIN_PARTS, mainCount);
        }
    }
}
