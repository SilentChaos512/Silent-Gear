package net.silentchaos512.gear.api.part;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.gear.part.PartData;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Represents an instance of an {@link IGearPart}. In most cases, {@link
 * PartData} should be used. {@link LazyPartData}
 * can be useful in cases where a part might not exist yet, but you have something that needs a
 * part, such as a recipe or loot table.
 *
 * @since 1.3.9
 */
public interface IPartData {
    ResourceLocation getPartId();

    @Nullable
    IGearPart getPart();

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

    default GearType getGearType() {
        IGearPart part = getPart();
        return part != null ? part.getGearType() : GearType.ALL;
    }

    @Deprecated
    @Nullable
    default IPartPosition getPartPosition() {
        IGearPart part = getPart();
        return part != null ? part.getPartPosition() : null;
    }

    default List<TraitInstance> getTraits() {
        return Collections.emptyList();
    }

    String getModelKey();

    default void onAddToGear(ItemStack gear) {
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("part", getPartId().toString());

        ItemStack stack = getCraftingItem();
        if (!stack.isEmpty()) {
        }

        return json;
    }
}
