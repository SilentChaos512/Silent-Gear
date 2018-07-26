package net.silentchaos512.gear.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Interface for all equipment items, including tools and armor.
 */
public interface ICoreItem extends IStatItem {

    //region Item properties and construction

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
        GearData.writeConstructionParts(result, parts);
        GearData.recalculateStats(result);
        return result;
    }

    default Item getItem() {
        return (Item) this;
    }

    String getGearClass();

    default ItemPartData getPrimaryPart(ItemStack stack) {
        ItemPartData data = GearData.getPrimaryPart(stack);
        return data != null ? data : ItemPartData.instance(ModMaterials.mainWood);
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

    ConfigOptionEquipment getConfig();

    boolean matchesRecipe(Collection<ItemStack> parts);

    //endregion

    //region Client-side stuff

    default int getAnimationFrames() {

        return 1;
    }

    default String getModelKey(ItemStack stack, int animationFrame, ItemPartData... parts) {
        StringBuilder builder = new StringBuilder(getGearClass());
        if (GearHelper.isBroken(stack))
            builder.append("_b");

        for (ItemPartData part : parts)
            builder.append("|").append(part == null ? "n" : part.getModelIndex(animationFrame));
        return builder.toString();
    }

    default String getModelKey(ItemStack stack, int animationFrame) {
        return getModelKey(stack, animationFrame, getRenderParts(stack));
    }

    ItemPartData[] getRenderParts(ItemStack stack);

    //endregion
}
