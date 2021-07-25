package net.silentchaos512.gear.api.part;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.util.IGearComponent;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.gear.part.PartData;

import javax.annotation.Nullable;
import java.util.List;

public interface IGearPart extends IGearComponent<IPartData> {
    ResourceLocation getId();

    int getTier(PartData part);

    default int getTier() {
        return getTier(PartData.of(this));
    }

    PartType getType();

    default GearType getGearType() {
        return GearType.ALL;
    }

    IPartSerializer<?> getSerializer();

    default String getPackName() {
        return "PACK UNKNOWN";
    }

    /**
     * Used to retain data on integrated server which is not sent on connect.
     *
     * @param oldPart The old part instance
     */
    default void retainData(@Nullable IGearPart oldPart) {}

    @Override
    default MaterialList getMaterials(IPartData part) {
        return MaterialList.empty();
    }

    /**
     * Get the chance the part will be lost when salvaging. Returning zero will ensure the part is
     * returned, regardless of how damaged the gear is.
     *
     * @param part           The part
     * @param gear           The gear item
     * @param normalLossRate The default loss rate, which is based on config settings and how
     *                       damaged the item is
     * @return Chance of losing the part when salvaging (defaults to {@code normalLossRate})
     */
    default double getSalvageLossRate(PartData part, ItemStack gear, double normalLossRate) {
        return normalLossRate;
    }

    /**
     * Determine if the part can be used to craft an item of the given type.
     *
     * @param part     The part
     * @param gearType The gear type (or null if not available)
     * @return True if crafting is allowed or {@code gearType} is {@code null}, false otherwise
     */
    default boolean isCraftingAllowed(IPartData part, GearType gearType) {
        if (gearType.isGear() && this.getType() == PartType.MAIN) {
            StatGearKey key = StatGearKey.of(gearType.getDurabilityStat(), gearType);
            return getStatUnclamped(part, this.getType(), key, ItemStack.EMPTY) > 0;
        }
        return true;
    }

    @Override
    default boolean isCraftingAllowed(IPartData part, PartType partType, GearType gearType, @Nullable Container inventory) {
/*        if (!GameStagesCompatProxy.canCraft(gearType, inventory) || !GameStagesCompatProxy.canCraft(this, inventory)) {
            return false;
        }*/
        return isCraftingAllowed(part, gearType);
    }

    /**
     * Called when a gear item containing this part is damaged.
     *
     * @param part   The part
     * @param gear   The gear item
     * @param amount The amount of damage done to the item
     */
    default void onGearDamaged(PartData part, ItemStack gear, int amount) {
    }

    int getColor(PartData part, ItemStack gear, int layer, int animationFrame);

    default Component getDisplayName(@Nullable PartData part) {
        return getDisplayName(part, ItemStack.EMPTY);
    }

    Component getDisplayName(@Nullable PartData part, ItemStack gear);

    default Component getMaterialName(@Nullable PartData part, ItemStack gear) {
        return getDisplayName(part, gear);
    }

    @Nullable
    default Component getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        return null;
    }

    default String getModelKey(PartData part) {
        return SilentGear.shortenId(this.getId());
    }

    @OnlyIn(Dist.CLIENT)
    void addInformation(PartData part, ItemStack gear, List<Component> tooltip, TooltipFlag flag);

    /**
     * Whether or not the part should be displayed in tooltips and such.
     *
     * @return True if the part should be shown, false if it should be hidden.
     */
    default boolean isVisible() {
        return true;
    }

    /**
     * Creates a {@link PartData} instance with possibly randomized data. This can be overridden to
     * apply custom data to randomized parts (see {@link net.silentchaos512.gear.util.GearGenerator}).
     *
     * @param gearType The gear type
     * @param tier     The target tier for random materials. If there are no materials of that tier,
     *                 materials of another tier should be selected.
     * @return Part data instance
     */
    default PartData randomizeData(GearType gearType, int tier) {
        return PartData.of(this);
    }

    boolean canAddToGear(ItemStack gear, PartData part);

    default boolean replacesExistingInPosition(PartData part) {
        return true;
    }

    default void onAddToGear(ItemStack gear, PartData part) {
    }

    default void onRemoveFromGear(ItemStack gear, PartData part) {
    }
}
