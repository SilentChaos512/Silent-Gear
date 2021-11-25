package net.silentchaos512.gear.api.material;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * An instance of an {@link IMaterial} used in crafting, including the grade and item used. There
 * are two types, {@link net.silentchaos512.gear.gear.material.MaterialInstance} and {@link
 * net.silentchaos512.gear.gear.material.LazyMaterialInstance}. {@code MaterialInstance} should be
 * used in the majority of cases. {@code LazyMaterialInstance} is useful for cases where materials
 * may not yet be loaded (ie loot tables).
 */
public interface IMaterialInstance extends IGearComponentInstance<IMaterial> {
    @Deprecated
    default ResourceLocation getMaterialId() {return getId();}

    @Deprecated
    @Nullable
    default IMaterial getMaterial() {return get();}

    /**
     * Gets the grade on the material, or {@code MaterialGrade.NONE} if ungraded.
     *
     * @return The grade
     */
    @Deprecated
    MaterialGrade getGrade();

    default Collection<IMaterialModifier> getModifiers() {
        return Collections.emptyList();
    }

    @Override
    default MaterialList getMaterials() {
        return MaterialList.empty();
    }

    default boolean hasAnyCategory(Collection<IMaterialCategory> others) {
        for (IMaterialCategory cat1 : this.getCategories()) {
            for (IMaterialCategory cat2 : others) {
                if (cat1.matches(cat2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the tier of the material. Shortcut for {@link IMaterial#getTier(IMaterialInstance,
     * PartType)}.
     *
     * @param partType The part type
     * @return The tier
     */
    int getTier(PartType partType);

    default int getTier() {
        return getTier(PartType.MAIN);
    }

    default boolean isSimple() {
        IMaterial mat = get();
        return mat != null && mat.isSimple();
    }

    default Collection<IMaterialCategory> getCategories() {
        IMaterial material = get();
        if (material != null) {
            return material.getCategories(this);
        }
        return Collections.emptySet();
    }

    default Ingredient getIngredient() {
        return Ingredient.EMPTY;
    }

    default float getStat(PartType partType, IItemStat stat) {
        return getStat(partType, StatGearKey.of(stat, GearType.ALL), ItemStack.EMPTY);
    }

    @Override
    default float getStat(PartType partType, StatGearKey key, ItemStack gear) {
        IMaterial material = get();
        if (material != null) {
            return material.getStat(this, partType, key, gear);
        }
        return 0;
    }

    default Collection<StatGearKey> getStatKeys(PartType type) {
        IMaterial material = get();
        if (material != null) {
            return material.getStatKeys(this, type);
        }
        return Collections.emptyList();
    }

    default Set<PartType> getPartTypes() {
        IMaterial material = get();
        if (material != null) {
            return material.getPartTypes(this);
        }
        return Collections.emptySet();
    }

    @Override
    default Collection<StatInstance> getStatModifiers(PartType partType, StatGearKey key, ItemStack gear) {
        IMaterial material = get();
        if (material == null) {
            return Collections.emptyList();
        }
        return material.getStatModifiers(this, partType, key, gear);
    }

    @Override
    default Collection<TraitInstance> getTraits(PartType partType, GearType gearType, ItemStack gear) {
        IMaterial material = get();
        if (material == null) {
            return Collections.emptyList();
        }
        return material.getTraits(this, PartGearKey.of(gearType, partType), gear);
    }

    @Override
    default int getNameColor(PartType partType, GearType gearType) {
        return Color.VALUE_WHITE;
    }

    CompoundTag write(CompoundTag nbt);

    String getModelKey();

    IMaterialDisplay getDisplayProperties();

    default MutableComponent getDisplayNameWithModifiers(PartType partType, ItemStack gear) {
        MutableComponent name = getDisplayName(partType, gear).copy();
        for (IMaterialModifier modifier : getModifiers()) {
            name = modifier.modifyMaterialName(name);
        }
        return name;
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("material", getId().toString());
        MaterialGrade grade = getGrade();
        if (grade != MaterialGrade.NONE) {
            json.addProperty("grade", grade.name());
        }
        return json;
    }

    void write(FriendlyByteBuf buffer);

    default boolean allowedInPart(PartType partType) {
        IMaterial material = get();
        return material != null && material.allowedInPart(this, partType);
    }

    default boolean isCraftingAllowed(PartType partType, GearType gearType) {
        IMaterial material = get();
        return material != null && material.isCraftingAllowed(this, partType, gearType);
    }

    default IMaterialInstance onSalvage() {
        IMaterial material = get();
        return material != null ? material.onSalvage(this) : this;
    }
}
