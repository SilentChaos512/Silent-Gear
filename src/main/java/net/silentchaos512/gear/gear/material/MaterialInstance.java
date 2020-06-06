package net.silentchaos512.gear.gear.material;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.api.parts.MaterialGrade;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MaterialInstance implements IMaterialInstance {
    private static final Map<ResourceLocation, MaterialInstance> QUICK_CACHE = new HashMap<>();

    private final IPartMaterial material;
    private final MaterialGrade grade;
    private final ItemStack item;

    private MaterialInstance(IPartMaterial material) {
        this(material, MaterialGrade.NONE, material.getDisplayItem(PartType.MAIN, 0));
    }

    private MaterialInstance(IPartMaterial material, MaterialGrade grade) {
        this(material, MaterialGrade.NONE, material.getDisplayItem(PartType.MAIN, 0));
    }

    private MaterialInstance(IPartMaterial material, ItemStack craftingItem) {
        this(material, MaterialGrade.NONE, craftingItem);
    }

    private MaterialInstance(IPartMaterial material, MaterialGrade grade, ItemStack craftingItem) {
        this.material = material;
        this.grade = grade;
        this.item = craftingItem;
    }

    public static MaterialInstance of(IPartMaterial material) {
        return QUICK_CACHE.computeIfAbsent(material.getId(), id -> new MaterialInstance(material));
    }

    public static MaterialInstance of(IPartMaterial material, MaterialGrade grade) {
        return new MaterialInstance(material, grade);
    }

    public static MaterialInstance of(IPartMaterial material, ItemStack craftingItem) {
        return new MaterialInstance(material, craftingItem);
    }

    public static MaterialInstance of(IPartMaterial material, MaterialGrade grade, ItemStack craftingItem) {
        return new MaterialInstance(material, grade, craftingItem);
    }

    @Override
    public ResourceLocation getMaterialId() {
        return material.getId();
    }

    @Nonnull
    @Override
    public IPartMaterial getMaterial() {
        return material;
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType, ItemStack gear) {
        Collection<StatInstance> mods = material.getStatModifiers(gear, stat, partType);
        if (stat.isAffectedByGrades() && grade != MaterialGrade.NONE) {
            float bonus = grade.bonusPercent / 100f;
            mods.add(new StatInstance(bonus, StatInstance.Operation.MUL1));
        }
        return mods;
    }

    @Nullable
    public static MaterialInstance read(CompoundNBT nbt) {
        ResourceLocation id = ResourceLocation.tryCreate(nbt.getString("ID"));
        IPartMaterial material = MaterialManager.get(id);
        if (material == null) return null;

        MaterialGrade grade = MaterialGrade.fromString(nbt.getString("Grade"));
        ItemStack stack = ItemStack.read(nbt.getCompound("Item"));
        return of(material, grade, stack);
    }

    @Nullable
    public static MaterialInstance readFast(CompoundNBT nbt) {
        ResourceLocation id = ResourceLocation.tryCreate(nbt.getString("ID"));
        IPartMaterial material = MaterialManager.get(id);
        if (material == null) return null;

        return of(material);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("ID", material.getId().toString());
        nbt.putString("Grade", grade.name());
        nbt.put("Item", item.write(new CompoundNBT()));
        return nbt;
    }
}
