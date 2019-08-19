package net.silentchaos512.gear.parts;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartData;
import net.silentchaos512.gear.api.parts.MaterialGrade;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A "lazy" version of {@link PartData}. Since {@link IGearPart}s may not exist when certain things
 * like loot tables are loaded, {@code LazyPartData} can be used to represent a future part.
 */
public class LazyPartData implements IPartData {
    private final ResourceLocation partId;
    private final MaterialGrade grade;

    public LazyPartData(ResourceLocation partId) {
        this(partId, MaterialGrade.NONE);
    }

    public LazyPartData(ResourceLocation partId, MaterialGrade grade) {
        this.partId = partId;
        this.grade = grade;
    }

    @Override
    public ResourceLocation getPartId() {
        return partId;
    }

    @Nullable
    @Override
    public IGearPart getPart() {
        return PartManager.get(partId);
    }

    @Override
    public MaterialGrade getGrade() {
        return grade;
    }

    @Override
    public ItemStack getCraftingItem() {
        IGearPart part = getPart();
        if (part != null)
            return PartData.of(part).getCraftingItem();
        return ItemStack.EMPTY;
    }

    @Override
    public CompoundNBT write(CompoundNBT tags) {
        tags.putString("ID", partId.toString());
        if (this.grade != MaterialGrade.NONE) {
            tags.putString("Grade", this.grade.name());
        }
        return tags;
    }

    public boolean isValid() {
        return getPart() != null;
    }

    public static LazyPartData readJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            String key = json.getAsString();
            return new LazyPartData(new ResourceLocation(key));
        }

        JsonObject jsonObject = json.getAsJsonObject();
        String key = JSONUtils.getString(jsonObject, "part");
        String gradeStr = JSONUtils.getString(jsonObject, "grade", MaterialGrade.NONE.name());
        MaterialGrade grade = MaterialGrade.fromString(gradeStr);
        return new LazyPartData(new ResourceLocation(key), grade);
    }

    @SuppressWarnings("ConstantConditions") // map says getPart might be null
    public static List<PartData> createPartList(Collection<LazyPartData> parts) {
        return parts.stream()
                .filter(LazyPartData::isValid)
                .map(p -> PartData.of(p.getPart(), p.grade))
                .collect(Collectors.toList());
    }
}
