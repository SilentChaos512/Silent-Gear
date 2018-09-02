/*
 * Silent Gear -- ItemPartData
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms instance the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * instance the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty instance
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy instance the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.api.parts;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
public class ItemPartData {
    private static final Map<ResourceLocation, ItemPartData> CACHE_UNGRADED_PARTS = new HashMap<>();

    final ItemPart part;
    final MaterialGrade grade;
    final ItemStack craftingItem;

    private ItemPartData(ItemPart part, MaterialGrade grade, ItemStack craftingItem) {
        this.part = part;
        this.grade = grade;
        this.craftingItem = craftingItem.copy();
        this.craftingItem.setCount(1);
    }

    public static ItemPartData instance(ItemPart part) {
        ResourceLocation registryName = part.getRegistryName();
        if (CACHE_UNGRADED_PARTS.containsKey(registryName))
            return CACHE_UNGRADED_PARTS.get(registryName);

        ItemPartData data = new ItemPartData(part, MaterialGrade.NONE, part.getCraftingStack());
        CACHE_UNGRADED_PARTS.put(registryName, data);
        return data;
    }

    public static ItemPartData instance(ItemPart part, MaterialGrade grade) {
        // TODO: Should we cache these?
        return new ItemPartData(part, grade, part.getCraftingStack());
    }

    public static ItemPartData instance(ItemPart part, MaterialGrade grade, ItemStack craftingItem) {
        // TODO: Should we cache these?
        return new ItemPartData(part, grade, craftingItem);
    }

    @Nullable
    public static ItemPartData fromStack(ItemStack craftingItem) {
        ItemPart part = PartRegistry.get(craftingItem);
        if (part == null) return null;

        MaterialGrade grade = MaterialGrade.fromStack(craftingItem);
        return instance(part, grade, craftingItem);
    }

    @Nullable
    public static ItemPartData readFromNBT(NBTTagCompound tags) {
        ItemPart part = ItemPart.fromNBT(tags);
        if (part == null) return null;
        MaterialGrade grade = MaterialGrade.fromString(tags.getString("Grade"));
        ItemStack craftingItem = new ItemStack(tags.getCompoundTag("Item"));
        return instance(part, grade, craftingItem);
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

    public Collection<StatInstance> getStatModifiers(ItemStat stat) {
        return part.getStatModifiers(stat, this);
    }

    public float computeStat(ItemStat stat) {
        return stat.compute(0, getStatModifiers(stat));
    }

    public float getRepairAmount(ItemStack gear, ItemPart.RepairContext context) {
        return this.part.getRepairAmount(gear, this, context);
    }

    @Nullable
    public ResourceLocation getTexture(ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        return part.getTexture(this, gear, gearClass, position, animationFrame);
    }

    @Nullable
    public ResourceLocation getBrokenTexture(ItemStack gear, String gearClass, IPartPosition position) {
        return part.getBrokenTexture(this, gear, gearClass, position);
    }

    public int getColor(ItemStack gear, int animationFrame) {
        return part.getColor(this, gear, animationFrame);
    }

    public TextFormatting getNameColor() {
        return part.getNameColor();
    }

    public String getModelIndex(int animationFrame) {
        return part.getModelIndex(this, animationFrame);
    }

    public String getTranslatedName(ItemStack gear) {
        return part.getTranslatedName(this, gear);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemPartData) {
            ItemPartData other = (ItemPartData) obj;
            return this.part == other.part && this.grade == other.grade && this.craftingItem.isItemEqual(other.craftingItem);
        }
        return super.equals(obj);
    }

    public boolean isMain() {
        return this.part instanceof PartMain;
    }

    public boolean isRod() {
        return this.part instanceof PartRod;
    }

    public boolean isTip() {
        return this.part instanceof PartTip;
    }

    public boolean isGrip() {
        return this.part instanceof PartGrip;
    }

    public boolean isBowstring() {
        return this.part instanceof PartBowstring;
    }
}
