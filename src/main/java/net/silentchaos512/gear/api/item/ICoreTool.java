package net.silentchaos512.gear.api.item;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public interface ICoreTool extends ICoreItem {

    Set<ItemStat> RELEVANT_STATS = new LinkedHashSet<>(Arrays.asList(
            CommonItemStats.HARVEST_LEVEL,
            CommonItemStats.HARVEST_SPEED,
            CommonItemStats.MELEE_DAMAGE,
            CommonItemStats.ATTACK_SPEED,
            CommonItemStats.DURABILITY,
            CommonItemStats.ENCHANTABILITY,
            CommonItemStats.RARITY
    ));

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

    default PartMain getPrimaryHeadPart(@Nonnull ItemStack stack) {

        ItemPartData data = GearData.getPrimaryPart(stack);
        if (data != null)
            return (PartMain) data.part;
        return ModMaterials.mainWood;
    }

    default PartMain getSecondaryPart(@Nonnull ItemStack stack) {

        ItemPartData data = GearData.getSecondaryPart(stack);
        if (data != null)
            return (PartMain) data.part;
        return ModMaterials.mainWood;
    }

    default PartRod getRodPart(@Nonnull ItemStack stack) {

        for (ItemPartData data : GearData.getConstructionParts(stack))
            if (data.part instanceof PartRod)
                return (PartRod) data.part;
        return ModMaterials.rodWood;
    }

    @Nullable
    default PartTip getTipPart(@Nonnull ItemStack stack) {

        for (ItemPartData data : GearData.getConstructionParts(stack))
            if (data.part instanceof PartTip)
                return (PartTip) data.part;
        return null;
    }

    @Nullable
    default PartBowstring getBowstringPart(@Nonnull ItemStack stack) {

        for (ItemPartData data : GearData.getConstructionParts(stack))
            if (data.part instanceof PartBowstring)
                return (PartBowstring) data.part;
        return null;
    }

    @Override
    default boolean matchesRecipe(@Nonnull Collection<ItemStack> parts) {

        ItemStack head = ItemStack.EMPTY;
        ItemStack rod = ItemStack.EMPTY;
        ItemStack bowstring = ItemStack.EMPTY;
        int rodCount = 0;
        int bowstringCount = 0;
        int tipCount = 0;
        int otherCount = 0;

        for (ItemStack stack : parts) {
            ItemPart part = PartRegistry.get(stack);
            //SilentGear.log.debug(stack, part);
            if (stack.getItem() instanceof ToolHead) {
                // Head
                if (!head.isEmpty())
                    return false;
                String headClass = ((ToolHead) stack.getItem()).getToolClass(stack);
                if (!headClass.equals(this.getGearClass()))
                    return false;
                head = stack;
            } else if (part instanceof PartRod) {
                // Rod
                if (!rod.isEmpty() && part != PartRegistry.get(rod))
                    return false;
                ++rodCount;
                rod = stack;
            } else if (part instanceof PartBowstring) {
                // Bowstring
                if (!bowstring.isEmpty() && part != PartRegistry.get(bowstring))
                    return false;
                ++bowstringCount;
                bowstring = stack;
            } else if (part instanceof PartTip) {
                // Tipped upgrade
                ++tipCount;
            } else if (part != null) {
                // Some other part type
                ++otherCount;
            } else {
                // Other non-part item
                return false;
            }
        }

        ConfigOptionEquipment config = getConfig();

        boolean headCheck = !head.isEmpty();
        boolean rodCheck = config.getRodCount() == 0 || (!rod.isEmpty() && config.getRodCount() == rodCount);
        boolean bowstringCheck = config.getBowstringCount() == 0 || (!bowstring.isEmpty() && config.getBowstringCount() == bowstringCount);
        return headCheck && rodCheck && bowstringCheck && tipCount <= 1 && otherCount <= 1;
    }

    @Override
    default ItemPart[] getRenderParts(ItemStack stack) {
        ItemPart partHead = getPrimaryHeadPart(stack);
        ItemPart partGuard = hasSwordGuard() ? getSecondaryPart(stack) : null;
        ItemPart partRod = getRodPart(stack);
        ItemPart partTip = getTipPart(stack);
        ItemPart partBowstring = getBowstringPart(stack);
        List<ItemPart> list = Lists.newArrayList(partHead, partGuard, partRod, partTip, partBowstring);
        return list.stream().filter(Objects::nonNull).toArray(ItemPart[]::new);
    }

    default boolean hasSwordGuard() {
        return "sword".equals(getGearClass());
    }
}
