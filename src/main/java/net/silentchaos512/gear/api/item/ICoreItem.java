package net.silentchaos512.gear.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.silentchaos512.gear.api.parts.IPartData;
import net.silentchaos512.gear.api.parts.IUpgradePart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.item.ICustomEnchantColor;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for all equipment items, including tools and armor.
 */
public interface ICoreItem extends IItemProvider, IStatItem, ICustomEnchantColor {
    //region Item properties and construction

    default ItemStack construct(Collection<? extends IPartData> parts) {
        ItemStack result = new ItemStack(this);
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result, null);
        // Allow upgrade parts to add additional data
        parts.forEach(p -> {
            if (p.getPart() instanceof IUpgradePart) {
                ((IUpgradePart) p.getPart()).onAddToGear(result, p.getCraftingItem());
            }
        });
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

    default PartData getPrimaryPart(ItemStack stack) {
        PartData data = GearData.getPrimaryPart(stack);
        if (data != null) return data;

        return PartData.ofNullable(PartManager.tryGetFallback(PartType.MAIN));
    }

    default boolean requiresPartOfType(PartType type) {
        return type == PartType.MAIN;
    }

    default boolean supportsPartOfType(PartType type) {
        return requiresPartOfType(type);
    }

    //endregion

    //region Stats and config

    @Override
    default float getStat(ItemStack stack, ItemStat stat) {
        return GearData.getStat(stack, stat);
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

    @SuppressWarnings("deprecation")
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

    default String getModelKey(ItemStack stack, int animationFrame) {
        return getModelKey(stack, animationFrame, getRenderParts(stack));
    }

    PartData[] getRenderParts(ItemStack stack);

    //endregion
}
