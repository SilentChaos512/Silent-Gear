package net.silentchaos512.gear.api.part;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.GearComponent;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.List;

public interface GearPart extends GearComponent<PartInstance> {
    PartType getType();

    default GearType getGearType() {
        return GearTypes.ALL.get();
    }

    PartSerializer<?> getSerializer();

    default String getPackName() {
        return "PACK UNKNOWN";
    }

    /**
     * Used to retain data on integrated server which is not sent on connect.
     *
     * @param oldPart The old part instance
     */
    default void retainData(@Nullable GearPart oldPart) {}

    List<MaterialInstance> getMaterials(PartInstance part);

    @Nullable
    default MaterialInstance getPrimaryMaterial(PartInstance part) {
        var materials = getMaterials(part);
        return !materials.isEmpty() ? materials.getFirst() : null;
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
    default double getSalvageLossRate(PartInstance part, ItemStack gear, double normalLossRate) {
        return normalLossRate;
    }

    /**
     * Determine if the part can be used to craft an item of the given type.
     *
     * @param part     The part
     * @param gearType The gear type
     * @return True if and only if crafting is allowed
     */
    default boolean isCraftingAllowed(PartInstance part, GearType gearType) {
        if (gearType.isGear() && this.getType() == PartTypes.MAIN.get()) {
            var key = PropertyKey.of(gearType.durabilityStat().get(), gearType);
            return getPropertyUnclamped(part, PartTypes.MAIN, key) > 0;
        }
        return true;
    }

    @Override
    default boolean isCraftingAllowed(PartInstance part, PartType partType, GearType gearType, @Nullable CraftingInput craftingInput) {
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
    default void onGearDamaged(PartInstance part, ItemStack gear, int amount) {
    }

    int getColor(PartInstance part, GearType gearType, int layer, int animationFrame);

    Component getDisplayName(@Nullable PartInstance part);

    default Component getMaterialName(@Nullable PartInstance part, ItemStack gear) {
        return getDisplayName(part);
    }

    default Component getDisplayNamePrefix(@Nullable PartInstance part, ItemStack gear) {
        return Component.empty();
    }

    default String getModelKey(PartInstance part) {
        return SilentGear.shortenId(SgRegistries.PART.getKey(this));
    }

    @OnlyIn(Dist.CLIENT)
    void addInformation(PartInstance part, ItemStack gear, List<Component> tooltip, TooltipFlag flag);

    /**
     * Whether the part should be displayed in tooltips and such.
     *
     * @return True if the part should be shown, false if it should be hidden.
     */
    default boolean isVisible() {
        return true;
    }

    /**
     * Creates a {@link PartInstance} instance with possibly randomized data. This can be overridden to
     * apply custom data to randomized parts (see {@link net.silentchaos512.gear.util.GearGenerator}).
     *
     * @param gearType The gear type
     * @param tier     The target tier for random materials. If there are no materials of that tier,
     *                 materials of another tier should be selected.
     * @return Part data instance
     */
    default PartInstance randomizeData(GearType gearType, int tier) {
        return PartInstance.of(this);
    }

    boolean canAddToGear(ItemStack gear, PartInstance part);

    default boolean replacesExistingInPosition(PartInstance part) {
        return true;
    }

    default void onAddToGear(ItemStack gear, PartInstance part) {
    }

    default void onRemoveFromGear(ItemStack gear, PartInstance part) {
    }
}
