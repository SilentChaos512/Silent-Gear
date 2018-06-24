package net.silentchaos512.gear.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.util.EquipmentData;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface for all equipment items, including tools and armor.
 */
public interface ICoreItem extends IStatItem {

    default ItemStack construct(Item item, ItemStack... materials) {

        List<ItemPartData> parts = NonNullList.create();
        for (ItemStack mat : materials) {
            ItemPartData data = ItemPartData.fromStack(mat);
            if (data != null)
                parts.add(data);
        }
        return construct(item, parts);
    }

    default ItemStack construct(Item item, Collection<ItemPartData> parts) {

        ItemStack result = new ItemStack(item);
        EquipmentData.writeConstructionParts(result, parts);
        EquipmentData.recalculateStats(result);
        return result;
    }

    default Item getItem() {
        if (this instanceof Item)
            return (Item) this;
        return null;
    }

    default String getItemClassName() {

        ResourceLocation registryName = getItem().getRegistryName();
        return registryName == null ? "unknown" : registryName.getResourcePath();
    }

    default int getAnimationFrames() {

        return 1;
    }

    @Override
    default float getStat(@Nonnull ItemStack stack, @Nonnull ItemStat stat) {

        return EquipmentData.getStat(stack, stat);
    }

    @Override
    default int getStatInt(@Nonnull ItemStack stack, @Nonnull ItemStat stat) {

        return Math.round(EquipmentData.getStat(stack, stat));
    }

    Set<ItemStat> getRelevantStats(@Nonnull ItemStack stack);

    @Nonnull
    ConfigOptionEquipment getConfig();

    boolean matchesRecipe(@Nonnull Collection<ItemStack> parts);
}
