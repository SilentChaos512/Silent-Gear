package net.silentchaos512.gear.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PartData implements IPartData {
    private static final Map<ResourceLocation, PartData> CACHE_UNGRADED_PARTS = new HashMap<>();
    public static final String NBT_ID = "ID";

    private final IGearPart part;
    private final MaterialGrade grade;
    private final ItemStack craftingItem;

    private PartData(IGearPart part, MaterialGrade grade) {
        this(part, grade, part.getMaterials().getDisplayItem(0));
    }

    private PartData(IGearPart part, MaterialGrade grade, ItemStack craftingItem) {
        this.part = part;
        this.grade = grade;
        this.craftingItem = craftingItem.copy();
        this.craftingItem.setCount(1);
    }

    public static PartData of(IGearPart part) {
        ResourceLocation name = part.getId();
        if (CACHE_UNGRADED_PARTS.containsKey(name)) {
            return CACHE_UNGRADED_PARTS.get(name);
        }

        PartData inst = new PartData(part, MaterialGrade.NONE);
        CACHE_UNGRADED_PARTS.put(name, inst);
        return inst;
    }

    public static PartData of(IGearPart part, MaterialGrade grade) {
        return new PartData(part, grade);
    }

    public static PartData of(IGearPart part, MaterialGrade grade, ItemStack craftingItem) {
        return new PartData(part, grade, craftingItem);
    }

    @Nullable
    public static PartData ofNullable(@Nullable IGearPart part) {
        if (part == null) return null;
        return of(part);
    }

    @Nullable
    public static PartData from(ItemStack craftingItem) {
        IGearPart part = PartManager.from(craftingItem);
        if (part == null) return null;

        MaterialGrade grade = MaterialGrade.fromStack(craftingItem);
        return of(part, grade, craftingItem);
    }

    @Nullable
    public static PartData fromStackFast(ItemStack craftingItem) {
        return ofNullable(PartManager.from(craftingItem));
    }

    @Nullable
    public static PartData fromId(ResourceLocation partId) {
        return ofNullable(PartManager.get(partId));
    }

    @Nullable
    public static PartData read(CompoundNBT tags) {
        String key = tags.getString(NBT_ID);
        IGearPart part = PartManager.get(new ResourceLocation(key));
        if (part == null) return null;

        MaterialGrade grade = MaterialGrade.fromString(tags.getString("Grade"));
        ItemStack craftingItem = ItemStack.read(tags.getCompound("Item"));
        return of(part, grade, craftingItem);
    }

    @Nullable
    public static PartData readFast(CompoundNBT tags) {
        String key = tags.getString(NBT_ID);
        if (key.isEmpty()) return null;
        IGearPart part = PartManager.get(new ResourceLocation(key));
        if (part == null) return null;
        return of(part);
    }

    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tags) {
        tags.putString("ID", part.getId().toString());
        if (this.grade != MaterialGrade.NONE) {
            tags.putString("Grade", this.grade.name());
        }

        CompoundNBT itemTag = new CompoundNBT();
        this.craftingItem.write(itemTag);
        tags.put("Item", itemTag);
        return tags;
    }

    @Override
    public ResourceLocation getPartId() {
        return part.getId();
    }

    @Nonnull
    @Override
    public IGearPart getPart() {
        return part;
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public ItemStack getCraftingItem() {
        return craftingItem;
    }

    @Override
    public int getTier() {
        return part.getTier();
    }

    @Override
    public PartType getType() {
        return part.getType();
    }

    public float computeStat(ItemStat stat) {
        return part.computeStatValue(stat, this);
    }

    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat) {
        return part.getStatModifiers(gear, stat, this);
    }

    @Override
    public List<PartTraitInstance> getTraits() {
        return getTraits(ItemStack.EMPTY);
    }

    public List<PartTraitInstance> getTraits(ItemStack gear) {
        return part.getTraits(gear, this);
    }

    public ITextComponent getDisplayName(ItemStack gear) {
        return part.getDisplayName(this, gear);
    }

    public float getRepairAmount(ItemStack gear, RepairContext.Type type) {
        return part.getRepairAmount(new RepairContext(type, gear, this));
    }

    @Nullable
    public ResourceLocation getTexture(ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        return part.getTexture(this, gear, gearClass, position, animationFrame);
    }

    @Nullable
    public ResourceLocation getBrokenTexture(ItemStack gear, GearType gearClass, IPartPosition position) {
        return part.getBrokenTexture(this, gear, gearClass, position);
    }

    @Deprecated // May be changed or removed?
    public String getModelIndex(int animationFrame) {
        return part.getModelIndex(this, animationFrame);
    }

    public int getColor(ItemStack gear, int animationFrame) {
        return part.getColor(this, gear, animationFrame);
    }

    public int getFallbackColor(ItemStack gear, int animationFrame) {
        return part.getDisplayProperties(this, gear, animationFrame).getFallbackColor();
    }

    @Override
    public String toString() {
        return "PartData{" +
                this.part +
                ", Grade: " + this.grade +
                "}";
    }
}
