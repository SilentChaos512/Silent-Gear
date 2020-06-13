package net.silentchaos512.gear.api.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.parts.IPartData;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.utils.Color;

import java.util.*;

/**
 * Interface for all equipment items, including tools and armor.
 */
public interface ICoreItem extends IItemProvider, IStatItem {
    //region Item properties and construction

    default ItemStack construct(Collection<? extends IPartData> parts) {
        ItemStack result = new ItemStack(this);
        GearData.writeConstructionParts(result, parts);
        parts.forEach(p -> p.onAddToGear(result));
        GearData.recalculateStats(result, null);
        // Allow traits to make any needed changes (must be done after a recalculate)
        TraitHelper.activateTraits(result, 0, (trait, level, nothing) -> {
            trait.onGearCrafted(new TraitActionContext(null, level, result));
            return 0;
        });
        return result;
    }

    @Override
    default Item asItem() {
        return (Item) this;
    }

    GearType getGearType();

    @Deprecated
    default PartData getPrimaryPart(ItemStack stack) {
        PartData data = GearData.getPrimaryPart(stack);
        if (data != null) return data;

        return Objects.requireNonNull(PartData.ofNullable(PartType.MAIN.getFallbackPart()));
    }

    default boolean requiresPartOfType(PartType type) {
        return type == PartType.MAIN;
    }

    default boolean supportsPartOfType(PartType type) {
        return requiresPartOfType(type) || type == PartType.BINDING || type == PartType.GRIP || type == PartType.MISC_UPGRADE || type == PartType.TIP;
    }

    //endregion

    //region Stats and config

    @Override
    default float getStat(ItemStack stack, ItemStat stat) {
        return GearData.getStat(stack, stat);
    }

    /**
     * Gets a set of stats to display in the item's tooltip. Relevant stats allow only the most
     * important stats to be shown to the player. Stats not in this set will still be calculated and
     * stored.
     * <p>
     * Also see: {@link #getExcludedStats(ItemStack)}
     *
     * @param stack The item
     * @return A set of stats to display in the item's tooltip
     */
    Set<ItemStat> getRelevantStats(ItemStack stack);

    /**
     * Gets all stats that will not be calculated or stored for this item. <em>Be very careful with
     * this!</em> If you are not sure if a stat should be excluded, then do not exclude it. The
     * default implementation should be suitable for most cases.
     * <p>
     * Examples of logical exclusions are armor stats for harvest tools, or weapon stats for armor.
     *
     * @param stack The item
     * @return A set of stats to not include.
     */
    default Set<ItemStat> getExcludedStats(ItemStack stack) {
        return Collections.emptySet();
    }

    default Optional<StatInstance> getBaseStatModifier(ItemStat stat) {
        return Optional.empty();
    }

    default Optional<StatInstance> getStatModifier(ItemStat stat) {
        return Optional.empty();
    }

    default ItemStat getDurabilityStat() {
        return ItemStats.DURABILITY;
    }

    //endregion

    //region Client-side stuff

    default int getAnimationFrames() {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    default IItemColor getItemColors() {
        return (stack, tintIndex) -> Color.VALUE_WHITE;
    }

    @Deprecated
    default String getModelKey(ItemStack stack, int animationFrame, PartData... parts) {
        StringBuilder builder = new StringBuilder(getGearType().getName());
        if (GearHelper.isBroken(stack))
            builder.append("_b");

        boolean foundMain = false;
        for (PartData part : parts) {
            if (part.getType() == PartType.MAIN) {
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

    @Deprecated
    default String getModelKey(ItemStack stack, int animationFrame) {
        return getModelKey(stack, animationFrame, getRenderParts(stack));
    }

    @Deprecated
    PartData[] getRenderParts(ItemStack stack);

    //endregion
}
