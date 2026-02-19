package net.silentchaos512.gear.api.material;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.GearComponent;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A material used for crafting gear parts. Provides stats and traits for different part types.
 */
public interface Material extends GearComponent<MaterialInstance> {
    /**
     * Gets the material serializer. Most materials should not require custom serializers.
     *
     * @return The serializer
     */
    MaterialSerializer<?> getSerializer();

    /**
     * Gets the parent of the material. Child materials will inherit some properties of their
     * parent, such as stats and traits. This is useful for creating materials that vary only in
     * color (e.g. wood). Most materials should not have a parent. Methods will need to check this if
     * they need to inherit properties.
     *
     * @return The parent, or null if there is no parent
     */
    @Nullable
    Material getParent();

    /**
     * Gets the parent material, wrapped in an optional. See {@link #getParent()} for more
     * information on parent materials.
     *
     * @return The parent in an optional, or an empty optional if there is no parent
     */
    default Optional<Material> getParentOptional() {
        return Optional.ofNullable(getParent());
    }

    @Deprecated
    default Collection<IMaterialCategory> getCategories() {
        return getCategories(MaterialInstance.of(this));
    }

    /**
     * Gets the categories this material belongs to.
     *
     * @param material The material
     * @return Collection of categories
     */
    Collection<IMaterialCategory> getCategories(MaterialInstance material);

    boolean isInCategory(IMaterialCategory category);

    Optional<Ingredient> getPartSubstitute(PartType partType);

    boolean hasPartSubstitutes();

    boolean canSalvage();

    MaterialInstance onSalvage(MaterialInstance material);

    /**
     * Check if the material is simple or compound.
     *
     * @return True if simple, false if compound
     */
    boolean isSimple();

    /**
     * Check if the material is valid. Materials that are not considered valid will still be loaded and retained in
     * memory, but will not be displayed in most contexts.
     *
     * @return True if the material is valid and obtainable, false otherwise
     */
    boolean isValid();

    /**
     * Gets the part types this material supports. In general, a material will support a part type
     * if the type is present in the stats JSON object (even if the value is empty).
     *
     * @param material The material
     * @return Supported part types
     */
    Set<PartType> getPartTypes(MaterialInstance material);

    /**
     * Determine if the material can be used to craft parts of the given type. This should include a
     * parent check.
     *
     * @param material The material instance
     * @param partType The part type
     * @return True if and only if crafting should be allowed
     */
    boolean isAllowedInPart(MaterialInstance material, PartType partType);

    default boolean canRepair(MaterialInstance partMaterial) {
        // TODO: Repair categories?
        return this.equals(partMaterial.get());
    }

    /**
     * Used to retain data on integrated server which is not sent on connect.
     *
     * @param oldMaterial The material object being overwritten
     */
    default void retainData(@Nullable Material oldMaterial) {
    }

    Collection<PropertyKey<?, ?>> getPropertyKeys(MaterialInstance material, PartType type);

    default Component getBaseMaterialName(@Nullable MaterialInstance material, PartType partType) {
        return getDisplayName(material, partType);
    }

    /**
     * Gets a simple name for the material, which may differ from the name that ends up being display in the final gear
     * item name. The returned value should make sense when displayed by itself, not necessarily when appended to other
     * text. For example, "gold" instead of "golden" or "wood" instead of "wooden".
     *
     * @return A simple name for the material
     */
    default Component getSimpleName() {
        return getDisplayName(MaterialInstance.of(this), PartTypes.MAIN.get());
    }

    Component getDisplayNamePrefix(PartType partType);

    TextureType getMainTextureType(MaterialInstance material);

    int getColor(MaterialInstance material, PartType partType, GearType gearType);

    int getNameColor(MaterialInstance material, PartType partType, GearType gearType);

    @Deprecated(forRemoval = true)
    default String getModelKey(MaterialInstance material) {
        return SilentGear.shortenId(SgRegistries.MATERIAL.getKey(this));
    }

    MaterialEquippableInfo getEquippableInfo();
}
