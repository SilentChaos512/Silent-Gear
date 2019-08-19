package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Represents an instance of an {@link IGearPart}. In most cases, {@link
 * net.silentchaos512.gear.parts.PartData} should be used. {@link net.silentchaos512.gear.parts.LazyPartData}
 * can be useful in cases where a part might not exist yet, but you have something that needs a
 * part, such as a recipe or loot table.
 *
 * @since 1.3.9
 */
public interface IPartData {
    ResourceLocation getPartId();

    @Nullable
    IGearPart getPart();

    MaterialGrade getGrade();

    ItemStack getCraftingItem();

    CompoundNBT write(CompoundNBT nbt);

    default int getTier() {
        IGearPart part = getPart();
        return part != null ? part.getTier() : 0;
    }

    @Nullable
    default PartType getType() {
        IGearPart part = getPart();
        return part != null ? part.getType() : null;
    }

    @Nullable
    default IPartPosition getPartPosition() {
        IGearPart part = getPart();
        return part != null ? part.getPartPosition() : null;
    }

    default List<PartTraitInstance> getTraits() {
        return Collections.emptyList();
    }
}
