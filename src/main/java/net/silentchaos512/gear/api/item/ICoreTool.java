package net.silentchaos512.gear.api.item;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.ToolHead;
import net.silentchaos512.gear.util.EquipmentData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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

    default ItemPartMain getPrimaryHeadPart(@Nonnull ItemStack stack) {

        ItemPartData data = EquipmentData.getPrimaryPart(stack);
        if (data != null)
            return (ItemPartMain) data.part;
        return ModMaterials.mainWood;
    }

    default ItemPartMain getSecondaryPart(@Nonnull ItemStack stack) {

        ItemPartData data = EquipmentData.getSecondaryPart(stack);
        if (data != null)
            return (ItemPartMain) data.part;
        return ModMaterials.mainWood;
    }

    default ToolPartRod getRodPart(@Nonnull ItemStack stack) {

        for (ItemPartData data : EquipmentData.getConstructionParts(stack))
            if (data.part instanceof ToolPartRod)
                return (ToolPartRod) data.part;
        return ModMaterials.rodWood;
    }

    @Nullable
    default ToolPartTip getTipPart(@Nonnull ItemStack stack) {

        for (ItemPartData data : EquipmentData.getConstructionParts(stack))
            if (data.part instanceof ToolPartTip)
                return (ToolPartTip) data.part;
        return null;
    }

    @Nullable
    default BowPartString getBowstringPart(@Nonnull ItemStack stack) {

        for (ItemPartData data : EquipmentData.getConstructionParts(stack))
            if (data.part instanceof BowPartString)
                return (BowPartString) data.part;
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
                if (!headClass.equals(this.getItemClassName()))
                    return false;
                head = stack;
            } else if (part instanceof ToolPartRod) {
                // Rod
                if (!rod.isEmpty() && part != PartRegistry.get(rod))
                    return false;
                ++rodCount;
                rod = stack;
            } else if (part instanceof BowPartString) {
                // Bowstring
                if (!bowstring.isEmpty() && part != PartRegistry.get(bowstring))
                    return false;
                ++bowstringCount;
                bowstring = stack;
            } else if (part instanceof ToolPartTip) {
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

//    if (config.item == ModItems.bow)
//      SilentGear.log.debug(config.getHeadCount(), config.getRodCount(), config.getBowstringCount(), head, rod, rodCount, bowstring, bowstringCount);

        boolean headCheck = !head.isEmpty();
        boolean rodCheck = config.getRodCount() == 0 || (!rod.isEmpty() && config.getRodCount() == rodCount);
        boolean bowstringCheck = config.getBowstringCount() == 0 || (!bowstring.isEmpty() && config.getBowstringCount() == bowstringCount);
        return headCheck && rodCheck && bowstringCheck && tipCount <= 1 && otherCount <= 1;
    }
}
