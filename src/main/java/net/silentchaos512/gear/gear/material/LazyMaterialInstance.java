package net.silentchaos512.gear.gear.material;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.utils.EnumUtils;

import javax.annotation.Nullable;

public class LazyMaterialInstance implements IMaterialInstance {
    private final ResourceLocation materialId;
    private final MaterialGrade grade;

    public LazyMaterialInstance(ResourceLocation materialId) {
        this(materialId, MaterialGrade.NONE);
    }

    public LazyMaterialInstance(ResourceLocation materialId, MaterialGrade grade) {
        this.materialId = materialId;
        this.grade = grade;
    }

    public static LazyMaterialInstance of(DataResource<IMaterial> material) {
        return new LazyMaterialInstance(material.getId());
    }

    public static LazyMaterialInstance of(ResourceLocation materialId) {
        return new LazyMaterialInstance(materialId);
    }

    public static LazyMaterialInstance of(DataResource<IMaterial> material, MaterialGrade grade) {
        return new LazyMaterialInstance(material.getId(), grade);
    }

    public static LazyMaterialInstance of(ResourceLocation materialId, MaterialGrade grade) {
        return new LazyMaterialInstance(materialId, grade);
    }

    @Override
    public ResourceLocation getId() {
        return materialId;
    }

    @Nullable
    @Override
    public IMaterial get() {
        return MaterialManager.get(materialId);
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public ItemStack getItem() {
        IMaterial material = get();
        return material != null ? MaterialInstance.of(material).getItem() : ItemStack.EMPTY;
    }

    @Override
    public int getTier(PartType partType) {
        IMaterial material = get();
        return material != null ? material.getTier(this, partType) : 0;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("ID", materialId.toString());
        if (grade != MaterialGrade.NONE) {
            nbt.putString("Grade", grade.name());
        }
        return nbt;
    }

    @Override
    public ITextComponent getDisplayName(PartType partType, ItemStack gear) {
        IMaterial material = get();
        return material != null ? material.getDisplayName(this, partType, gear) : new StringTextComponent("INVALID");
    }

    @Override
    public String getModelKey() {
        return "null";
    }

    public static LazyMaterialInstance deserialize(JsonObject json) {
        ResourceLocation id = new ResourceLocation(JSONUtils.getString(json, "material"));
        MaterialGrade grade = EnumUtils.byName(JSONUtils.getString(json, "grade", "NONE"), MaterialGrade.NONE);
        return new LazyMaterialInstance(id, grade);
    }

    public static LazyMaterialInstance read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        MaterialGrade grade = buffer.readEnumValue(MaterialGrade.class);
        return new LazyMaterialInstance(id, grade);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.materialId);
        buffer.writeEnumValue(this.grade);
    }
}
