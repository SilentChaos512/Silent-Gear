package net.silentchaos512.gear.api.material;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.IGearComponent;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.lib.event.ClientTicks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A material used for crafting gear parts. Provides stats and traits for different part types.
 */
public interface IMaterial extends IGearComponent<IMaterialInstance> {
    /**
     * The name of the data pack this material is from. For mods, this is the name of the JAR file.
     * In userdev, it's "main".
     *
     * @return The data pack name
     */
    String getPackName();

    /**
     * Gets the material ID (eg "silentgear:iron")
     *
     * @return The material ID
     */
    ResourceLocation getId();

    /**
     * Gets the material serializer. Most materials should not require custom serializers.
     *
     * @return The serializer
     */
    IMaterialSerializer<?> getSerializer();

    /**
     * Gets the parent of the material. Child materials will inherit some properties of their
     * parent, such as stats and traits. This is useful for creating materials that vary only in
     * color (eg wood). Most materials should not have a parent. Methods will need to check this if
     * they need to inherit properties.
     *
     * @return The parent, or null if there is no parent
     */
    @Nullable
    IMaterial getParent();

    /**
     * Gets the parent material, wrapped in an optional. See {@link #getParent()} for more
     * information on parent materials.
     *
     * @return The parent in an optional, or an empty optional if there is no parent
     */
    default Optional<IMaterial> getParentOptional() {
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
    Collection<IMaterialCategory> getCategories(IMaterialInstance material);

    @Deprecated
    default int getTier(PartType partType) {
        return getTier(MaterialInstance.of(this), partType);
    }

    /**
     * Gets the tier of the material. Currently, the tier never depends on the part type.
     *
     * @param material The material instance
     * @param partType The part type
     * @return The tier
     */
    int getTier(IMaterialInstance material, PartType partType);

    Optional<Ingredient> getPartSubstitute(PartType partType);

    boolean hasPartSubstitutes();

    boolean canSalvage();

    IMaterialInstance onSalvage(IMaterialInstance material);

    /**
     * Check if the material is simple or compound.
     *
     * @return True if simple, false if compound
     */
    boolean isSimple();

    /**
     * Gets the part types this material supports. In general, a material will support a part type
     * if the type is present in the stats JSON object (even if the value is empty).
     *
     * @param material The material
     * @return Supported part types
     */
    Set<PartType> getPartTypes(IMaterialInstance material);

    /**
     * Determine if the material can be used to craft parts of the given type. This should include a
     * parent check.
     *
     * @param material The material instance
     * @param partType The part type
     * @return True if and only if crafting should be allowed
     */
    boolean allowedInPart(IMaterialInstance material, PartType partType);

    /**
     * Used to retain data on integrated server which is not sent on connect.
     *
     * @param oldMaterial The material object being overwritten
     */
    default void retainData(@Nullable IMaterial oldMaterial) {}

    Collection<StatGearKey> getStatKeys(IMaterialInstance material, PartType type);

    @Nullable
    default IMaterialDisplay getDisplayOverride(IMaterialInstance material) {
        return null;
    }

    @Nullable
    Component getDisplayNamePrefix(ItemStack gear, PartType partType);

    int getNameColor(IMaterialInstance material, PartType partType, GearType gearType);

    default String getModelKey(IMaterialInstance material) {
        return SilentGear.shortenId(getId());
    }

    default boolean isVisible(PartType partType) {
        return true;
    }

    /**
     * Get an {@code ItemStack} which matches the normal ingredient. The {@code ticks} parameter can
     * be used to cycle between possible matches.
     *
     * @param type  The part type
     * @param ticks Used to index into matching stacks. If on the client, {@link
     *              ClientTicks#totalTicks()} can be used. Zero will consistently return the first
     *              item in the matching stacks array.
     * @return An item matching the normal ingredient, or {@link ItemStack#EMPTY} if there are none
     */
    default ItemStack getDisplayItem(PartType type, int ticks) {
        ItemStack[] stacks = getIngredient().getItems();
        if (stacks.length == 0) return ItemStack.EMPTY;
        return stacks[(ticks / 20) % stacks.length];
    }

    void updateIngredient(SyncMaterialCraftingItemsPacket msg);

    @Override
    default MaterialList getMaterials(IMaterialInstance instance) {
        return MaterialList.empty();
    }
}
