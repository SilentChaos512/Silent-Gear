package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.parts.type.BowstringPart;
import net.silentchaos512.gear.parts.type.GripPart;
import net.silentchaos512.gear.parts.type.RodPart;
import net.silentchaos512.gear.parts.type.TipPart;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public interface ICoreTool extends ICoreItem {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            ItemStats.HARVEST_LEVEL,
            ItemStats.HARVEST_SPEED,
            ItemStats.MELEE_DAMAGE,
            ItemStats.ATTACK_SPEED,
            ItemStats.DURABILITY,
            ItemStats.ENCHANTABILITY,
            ItemStats.RARITY
    );

    @Override
    default Set<ItemStat> getRelevantStats(@Nonnull ItemStack stack) {
        return RELEVANT_STATS;
    }

    @Override
    default boolean requiresPartOfType(PartType type) {
        return type == PartType.MAIN || type == PartType.ROD;
    }

    @Override
    default boolean supportsPartOfType(PartType type) {
        return type != PartType.BOWSTRING;
    }

    /**
     * The base damage done to the item when breaking a block, not considering enchantments
     * @param gear The item
     * @param world The world
     * @param state The block being broken
     * @param pos The position of the block
     * @return The amount of damage done (durability lost) to the item
     */
    default int getDamageOnBlockBreak(ItemStack gear, World world, BlockState state, BlockPos pos) {
        return state.getMaterial() != Material.LEAVES && state.getBlockHardness(world, pos) > 0 ? 1 : 0;
    }

    /**
     * The base damage done to the item when attacking an entity
     *
     * @param gear     The item
     * @param target   The entity being attacked
     * @param attacker The entity attacking the target
     * @return The amount of damage done (durability lost) to the item
     */
    default int getDamageOnHitEntity(ItemStack gear, LivingEntity target, LivingEntity attacker) {
        return 2;
    }

    default PartData getSecondaryPart(ItemStack stack) {
        PartData data = GearData.getSecondaryPart(stack);
        if (data != null) return data;
        return PartData.of(PartManager.tryGetFallback(PartType.MAIN));
    }

    @Nullable
    default PartData getRodPart(ItemStack stack) {
        for (PartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof RodPart) return data;
        return null;
    }

    @Nullable
    default PartData getGripPart(ItemStack stack) {
        for (PartData part : GearData.getConstructionParts(stack))
            if (part.getPart() instanceof GripPart) return part;
        return null;
    }

    @Nullable
    default PartData getTipPart(ItemStack stack) {
        for (PartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof TipPart) return data;
        return null;
    }

    @Nullable
    default PartData getBowstringPart(ItemStack stack) {
        for (PartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof BowstringPart) return data;
        return null;
    }

    @Override
    default PartData[] getRenderParts(ItemStack stack) {
        PartDataList parts = GearData.getConstructionParts(stack);
        Collection<PartData> list = new ArrayList<>();

        for (IPartPosition position : IPartPosition.RENDER_LAYERS) {
            if (position == PartPositions.HEAD) {
                list.add(getPrimaryPart(stack));
            } else if (position == PartPositions.GUARD && hasSwordGuard()) {
                list.add(getSecondaryPart(stack));
            } else if (position == PartPositions.ROD) {
                list.add(getRodPart(stack));
            } else {
                PartData part = parts.firstInPosition(position);
                if (part != null) list.add(part);
            }
        }

        return list.stream().filter(Objects::nonNull).toArray(PartData[]::new);
    }

    default boolean hasSwordGuard() {
        return getGearType() == GearType.SWORD;
    }
}
