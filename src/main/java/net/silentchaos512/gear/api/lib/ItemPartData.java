package net.silentchaos512.gear.api.lib;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

@Getter(value = AccessLevel.PUBLIC)
public class ItemPartData {
    public final ItemPart part;
    public final MaterialGrade grade;
    public final ItemStack craftingItem;

    public ItemPartData(ItemPart part, MaterialGrade grade, ItemStack craftingItem) {
        this.part = part;
        this.grade = grade;
        this.craftingItem = craftingItem.copy();
        this.craftingItem.setCount(1);
    }

    public Collection<StatInstance> getStatModifiers(ItemStat stat) {
        return part.getStatModifiers(stat, craftingItem);
    }

    public float computeStat(ItemStat stat) {
        return stat.compute(0, getStatModifiers(stat));
    }

    @Nullable
    public static ItemPartData fromStack(ItemStack craftingItem) {
        ItemPart part = PartRegistry.get(craftingItem);
        if (part == null) return null;

        MaterialGrade grade = MaterialGrade.fromStack(craftingItem);
        return new ItemPartData(part, grade, craftingItem);
    }

    @Nullable
    public static ItemPartData readFromNBT(NBTTagCompound tags) {
        ItemPart part = ItemPart.fromNBT(tags);
        if (part == null)
            return null;
        MaterialGrade grade = MaterialGrade.fromString(tags.getString("Grade"));
        ItemStack craftingItem = new ItemStack(tags.getCompoundTag("Item"));
        return new ItemPartData(part, grade, craftingItem);
    }

    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tags) {
        this.part.writeToNBT(tags);
        if (this.grade != MaterialGrade.NONE)
            tags.setString("Grade", this.grade.name());
        NBTTagCompound itemTag = new NBTTagCompound();
        this.craftingItem.writeToNBT(itemTag);
        tags.setTag("Item", itemTag);
        return tags;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemPartData) {
            ItemPartData other = (ItemPartData) obj;
            return this.part == other.part && this.grade == other.grade && this.craftingItem.isItemEqual(other.craftingItem);
        }
        return super.equals(obj);
    }
}
