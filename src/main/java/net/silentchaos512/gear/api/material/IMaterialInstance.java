package net.silentchaos512.gear.api.material;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;

/**
 * An instance of an {@link IMaterial} used in crafting, including the grade and item used. There
 * are two types, {@link net.silentchaos512.gear.gear.material.MaterialInstance} and {@link
 * net.silentchaos512.gear.gear.material.LazyMaterialInstance}. {@code MaterialInstance} should be
 * used in the majority of cases. {@code LazyMaterialInstance} is useful for cases where materials
 * may not yet be loaded (ie loot tables).
 */
public interface IMaterialInstance {
    /**
     * Gets the material ID. This is particular useful for lazy instances, since the ID can exist
     * before the material.
     *
     * @return The material ID
     */
    ResourceLocation getMaterialId();

    /**
     * Gets the material, if possible. For lazy instances, this will return null if materials are
     * not yet loaded. Use {@link #getMaterialId()} for such cases. {@link
     * net.silentchaos512.gear.gear.material.MaterialInstance} will never return null here.
     *
     * @return The material, or null if not yet available.
     */
    @Nullable
    IMaterial getMaterial();

    /**
     * Gets the grade on the material, or {@code MaterialGrade.NONE} if ungraded.
     *
     * @return The grade
     */
    MaterialGrade getGrade();

    /**
     * Gets the item stack used in crafting, including all NBT the item had.
     *
     * @return The crafting item
     */
    ItemStack getItem();

    /**
     * Gets the tier of the material. Shortcut for {@link IMaterial#getTier(PartType)}.
     *
     * @param partType The part type
     * @return The tier
     */
    int getTier(PartType partType);

    default float getStat(ItemStat stat, PartType partType) {
        return getStat(stat, partType, ItemStack.EMPTY);
    }

    float getStat(ItemStat stat, PartType partType, ItemStack gear);

    CompoundNBT write(CompoundNBT nbt);

    @Deprecated
    int getColor(PartType partType, ItemStack gear);

    @Deprecated
    default int getColor(PartType partType) {
        return getColor(partType, ItemStack.EMPTY);
    }

    IFormattableTextComponent getDisplayName(PartType partType, ItemStack gear);

    default IFormattableTextComponent getDisplayName(PartType partType) {
        return getDisplayName(partType, ItemStack.EMPTY);
    }

    default IFormattableTextComponent getDisplayNameWithGrade(PartType partType) {
        IFormattableTextComponent displayName = getDisplayName(partType, ItemStack.EMPTY);
        MaterialGrade grade = getGrade();
        if (grade != MaterialGrade.NONE) {
            displayName.append(TextUtil.translate("misc", "spaceBrackets", grade.getDisplayName()));
        }
        return displayName;
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("material", getMaterialId().toString());
        MaterialGrade grade = getGrade();
        if (grade != MaterialGrade.NONE) {
            json.addProperty("grade", grade.name());
        }
        return json;
    }

    void write(PacketBuffer buffer);
}
