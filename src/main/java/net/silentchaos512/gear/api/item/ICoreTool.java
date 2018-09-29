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
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.gear.item.ToolRods;
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

    default ItemPartData getSecondaryPart(ItemStack stack) {
        ItemPartData data = GearData.getSecondaryPart(stack);
        if (data != null) return data;
        return ItemPartData.instance(ModMaterials.mainWood);
    }

    default ItemPartData getRodPart(ItemStack stack) {
        for (ItemPartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof PartRod) return data;
        return ItemPartData.instance(ToolRods.WOOD.getPart());
    }

    @Nullable
    default ItemPartData getGripPart(ItemStack stack) {
        for (ItemPartData part : GearData.getConstructionParts(stack))
            if (part.getPart() instanceof PartGrip) return part;
        return null;
    }

    @Nullable
    default ItemPartData getTipPart(ItemStack stack) {
        for (ItemPartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof PartTip) return data;
        return null;
    }

    @Nullable
    default ItemPartData getBowstringPart(ItemStack stack) {
        for (ItemPartData data : GearData.getConstructionParts(stack))
            if (data.getPart() instanceof PartBowstring) return data;
        return null;
    }

    @Override
    default boolean matchesRecipe(Collection<ItemStack> parts) {
        ConfigOptionEquipment config = getConfig();
        ItemStack head = ItemStack.EMPTY;
        Map<PartType, Integer> partCounts = new HashMap<>();
        Map<PartType, ItemPart> partsFound = new HashMap<>();

        for (ItemStack stack : parts) {
            ItemPart part = PartRegistry.get(stack);
            if (stack.getItem() instanceof ToolHead) {
                // Head
                if (!head.isEmpty())
                    return false;
                String headClass = ToolHead.getToolClass(stack);
                if (!headClass.equals(this.getGearClass()))
                    return false;
                head = stack;
                partCounts.put(PartType.MAIN, config.getHeadCount());
            } else if (part != null) {
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
    default ItemPartData[] getRenderParts(ItemStack stack) {
        PartDataList parts = GearData.getConstructionParts(stack);
        List<ItemPartData> list = new ArrayList<>();

        for (IPartPosition position : IPartPosition.RENDER_LAYERS) {
            if (position == PartPositions.HEAD) {
                list.add(getPrimaryPart(stack));
            } else if (position == PartPositions.GUARD && hasSwordGuard()) {
                list.add(getSecondaryPart(stack));
            } else if (position == PartPositions.ROD) {
                list.add(getRodPart(stack));
            } else {
                final ItemPartData part = parts.firstInPosition(position);
                if (part != null) list.add(part);
            }
        }

        return list.stream().filter(Objects::nonNull).toArray(ItemPartData[]::new);
    }

    default boolean hasSwordGuard() {
        return "sword".equals(getGearClass());
    }
}
