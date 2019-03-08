package net.silentchaos512.gear.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.type.PartMain;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.item.ICustomEnchantColor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for all equipment items, including tools and armor.
 */
public interface ICoreItem extends IItemProvider, IStatItem, ICustomEnchantColor {
    //region Item properties and construction

    default ItemStack construct(Item item, ItemStack... materials) {
        List<PartData> parts = PartDataList.of();
        for (ItemStack mat : materials) {
            PartData data = PartData.from(mat);
            if (data != null)
                parts.add(data);
        }
        return construct(parts);
    }

    default ItemStack construct(Collection<PartData> parts) {
        ItemStack result = new ItemStack(this);
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result);
        return result;
    }

    @Override
    default Item asItem() {
        return (Item) this;
    }

    GearType getGearType();

    default PartData getPrimaryPart(ItemStack stack) {
        PartData data = GearData.getPrimaryPart(stack);
        if (data != null) return data;

        return PartData.of(PartManager.tryGetFallback(PartType.MAIN));
    }

    default boolean requiresPartOfType(PartType type) {
        return type == PartType.MAIN;
    }

    //endregion

    //region Stats and config

    @Override
    default float getStat(ItemStack stack, ItemStat stat) {
        return GearData.getStat(stack, stat);
    }

    @Override
    default int getStatInt(ItemStack stack, ItemStat stat) {
        return Math.round(GearData.getStat(stack, stat));
    }

    Set<ItemStat> getRelevantStats(ItemStack stack);

    default Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        return Optional.empty();
    }

    default Optional<StatInstance> getStatModifier(ItemStat stat) {
        return Optional.empty();
    }

    //endregion

    //region Client-side stuff

    default int getAnimationFrames() {
        return 1;
    }

    default String getModelKey(ItemStack stack, int animationFrame, PartData... parts) {
        StringBuilder builder = new StringBuilder(getGearType().getName());
        if (GearHelper.isBroken(stack))
            builder.append("_b");

        boolean foundMain = false;
        for (PartData part : parts) {
            if (part.getPart() instanceof PartMain) {
                // Only first main matters
                if (!foundMain) {
                    foundMain = true;
                    builder.append("|").append(part.getModelIndex(animationFrame));
                }
            } else {
                // Non-main
                builder.append("|").append(part.getModelIndex(animationFrame));
            }
        }
        return builder.toString();
    }

    default String getModelKey(ItemStack stack, int animationFrame) {
        return getModelKey(stack, animationFrame, getRenderParts(stack));
    }

    PartData[] getRenderParts(ItemStack stack);

    //endregion
}
