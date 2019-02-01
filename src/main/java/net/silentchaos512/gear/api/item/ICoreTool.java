package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.parts.*;
import net.silentchaos512.gear.parts.type.PartBowstring;
import net.silentchaos512.gear.parts.type.PartGrip;
import net.silentchaos512.gear.parts.type.PartRod;
import net.silentchaos512.gear.parts.type.PartTip;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public interface ICoreTool extends ICoreItem {
    Set<ItemStat> RELEVANT_STATS = ImmutableSet.of(
            CommonItemStats.HARVEST_LEVEL,
            CommonItemStats.HARVEST_SPEED,
            CommonItemStats.MELEE_DAMAGE,
            CommonItemStats.ATTACK_SPEED,
            CommonItemStats.DURABILITY,
            CommonItemStats.ENCHANTABILITY,
            CommonItemStats.RARITY
    );

    @Override
    default Set<ItemStat> getRelevantStats(@Nonnull ItemStack stack) {
        return RELEVANT_STATS;
    }

    /**
     * The base damage done to the item when breaking a block, not considering enchantments
     */
    default int getDamageOnBlockBreak(ItemStack gear, World world, IBlockState state, BlockPos pos) {
        return state.getMaterial() != Material.LEAVES && state.getBlockHardness(world, pos) > 0 ? 1 : 0;
    }

    /**
     * The base damage done to the item when attacking an entity
     */
    default int getDamageOnHitEntity(ItemStack gear, EntityLivingBase target, EntityLivingBase attacker) {
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
            if (data.getPart() instanceof PartRod) return data;
        return null;
    }

    @Nullable
    default PartData getGripPart(ItemStack stack) {
        for (PartData part : GearData.getConstructionParts(stack))
            if (part.getPart() instanceof PartGrip) return part;
        return null;
    }

    @Nullable
    default PartData getTipPart(ItemStack stack) {
        for (PartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof PartTip) return data;
        return null;
    }

    @Nullable
    default PartData getBowstringPart(ItemStack stack) {
        for (PartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof PartBowstring) return data;
        return null;
    }

    @Deprecated
    @Override
    default boolean matchesRecipe(Collection<ItemStack> parts) {
        ConfigOptionEquipment config = getConfig();
        ItemStack head = ItemStack.EMPTY;
        Map<PartType, Integer> partCounts = new HashMap<>();
        Map<PartType, IGearPart> partsFound = new HashMap<>();

        for (ItemStack stack : parts) {
            IGearPart part = PartManager.from(stack);
            /*if (stack.getItem() instanceof ToolHead) {
                // Head
                if (!head.isEmpty())
                    return false;
                String headClass = ToolHead.getToolClass(stack);
                if (!headClass.equals(this.getGearClass()))
                    return false;
                head = stack;
                partCounts.put(PartType.MAIN, config.getHeadCount());
            } else*/ if (part != null) {
                // Count parts
                final PartType type = part.getType();
                if (partsFound.containsKey(type) && partsFound.get(type) != part)
                    return false;
                int current = partCounts.getOrDefault(type, 0);
                partCounts.put(type, current + 1);
                partsFound.putIfAbsent(type, part);
            } else {
                // Other non-part item
                return false;
            }
        }

        for (PartType type : PartType.getValues()) {
            final int required = config.getCraftingPartCount(type);
            final int found = partCounts.getOrDefault(type, 0);
            if (required == 0 && partsFound.get(type) instanceof IUpgradePart) {
                // Allow a single upgrade of this type
                if (found > 1) return false;
            } else if (required != found) {
                // Wrong number of parts
                return false;
            }
        }
        return !head.isEmpty();
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
        return "sword".equals(getGearClass());
    }
}
