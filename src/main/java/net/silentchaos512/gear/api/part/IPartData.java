package net.silentchaos512.gear.api.part;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.LazyPartData;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.TierHelper;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents an instance of an {@link IGearPart}. In most cases, {@link PartData} should be used.
 * {@link LazyPartData} can be useful in cases where a part might not exist yet, but you have
 * something that needs a part, such as a recipe or loot table.
 *
 * @since 1.3.9
 */
public interface IPartData extends IGearComponentInstance<IGearPart> {
    @Deprecated
    default ResourceLocation getPartId() {return getId();}

    @Deprecated
    @Nullable
    default IGearPart getPart() {return get();}

    @Deprecated
    default ItemStack getCraftingItem() {return getItem();}

    CompoundTag write(CompoundTag nbt);

    default int getTier() {
        IGearPart part = get();
        return part != null ? part.getTier() : 0;
    }

    @Override
    default Tier getHarvestTier() {
        IGearPart part = get();
        return part != null ? part.getHarvestTier(this) : TierHelper.weakestTier();
    }

    default PartType getType() {
        IGearPart part = get();
        return part != null ? part.getType() : PartType.NONE;
    }

    default GearType getGearType() {
        IGearPart part = get();
        return part != null ? part.getGearType() : GearType.ALL;
    }

    @Override
    default MaterialList getMaterials() {
        IGearPart part = get();
        return part != null ? part.getMaterials(this) : MaterialList.empty();
    }

    @Override
    default float getStat(PartType partType, StatGearKey key, ItemStack gear) {
        ItemStat stat = ItemStats.get(key.getStat());
        if (stat == null) return key.getStat().getDefaultValue();

        return stat.compute(stat.getDefaultValue(), getStatModifiers(partType, key));
    }

    default Collection<StatInstance> getStatModifiers(StatGearKey key, ItemStack gear) {
        return getStatModifiers(this.getType(), key, gear);
    }

    @Override
    default Collection<StatInstance> getStatModifiers(PartType partType, StatGearKey key, ItemStack gear) {
        IGearPart part = get();
        if (part == null) {
            return Collections.emptyList();
        }
        return part.getStatModifiers(this, this.getType(), key, gear);
    }

    default Collection<TraitInstance> getTraits() {
        return getTraits(this.getType(), this.getGearType(), ItemStack.EMPTY);
    }

    default Collection<TraitInstance> getTraits(ItemStack gear) {
        return getTraits(this.getType(), this.getGearType(), gear);
    }

    @Override
    default Collection<TraitInstance> getTraits(PartType partType, GearType gearType, ItemStack gear) {
        IGearPart part = get();
        if (part == null) {
            return Collections.emptyList();
        }
        return part.getTraits(this, PartGearKey.of(gearType, partType), gear);
    }

    @Override
    default int getNameColor(PartType partType, GearType gearType) {
        return Color.VALUE_WHITE;
    }

    String getModelKey();

    default void onAddToGear(ItemStack gear) {
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("part", getId().toString());

        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
        }

        return json;
    }
}
