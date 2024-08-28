package net.silentchaos512.gear.api.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.Color;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Interface for all equipment items, including tools and armor.
 */
public interface ICoreItem extends ItemLike {
    //region Item properties and construction

    default ItemStack construct(Collection<PartInstance> parts) {
        ItemStack result = new ItemStack(this);
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateGearData(result, null);
        parts.forEach(p -> p.onAddToGear(result));
        // Allow traits to make any needed changes (must be done after a recalculate)
        TraitHelper.activateTraits(result, 0, (trait, value) -> {
            trait.getTrait().onGearCrafted(new TraitActionContext(null, trait, result));
            return 0;
        });
        return result;
    }

    @Override
    default Item asItem() {
        return (Item) this;
    }

    GearType getGearType();

    default boolean isValidSlot(String slot) {
        return false;
    }

    default boolean requiresPartOfType(PartType type) {
        return getRequiredParts().contains(type);
    }

    default boolean supportsPart(ItemStack gear, PartInstance part) {
        boolean canAdd = part.get().canAddToGear(gear, part);
        return (requiresPartOfType(part.getType()) && canAdd) || canAdd;
    }

    default Collection<PartType> getRequiredParts() {
        return ImmutableList.of(PartTypes.MAIN.get());
    }

    //endregion

    //region Stats and config

    default Supplier<NumberProperty> getDurabilityStat() {
        return getGearType().durabilityStat();
    }

    default float getRepairModifier(ItemStack stack) {
        return 1f;
    }

    //endregion

    //region Client-side stuff

    @OnlyIn(Dist.CLIENT)
    default ItemColor getItemColors() {
        //noinspection OverlyLongLambda
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> ColorUtils.getBlendedColor(stack, PartTypes.ROD.get());
                case 1 -> {
                    if (GearData.hasPartOfType(stack, PartTypes.COATING.get())) {
                        yield ColorUtils.getBlendedColor(stack, PartTypes.COATING.get());
                    } else {
                        yield ColorUtils.getBlendedColor(stack, PartTypes.MAIN.get());
                    }
                }
                // 2: highlight layer, no color needed
                case 3 -> ColorUtils.getBlendedColor(stack, PartTypes.TIP.get());
                case 4 -> ColorUtils.getBlendedColor(stack, PartTypes.GRIP.get());
                default -> Color.VALUE_WHITE;
            };
        };
    }

    //endregion
}
