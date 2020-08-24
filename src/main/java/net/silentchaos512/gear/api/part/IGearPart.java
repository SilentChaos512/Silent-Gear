package net.silentchaos512.gear.api.part;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.RepairContext;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface IGearPart {
    ResourceLocation getId();

    int getTier(PartData part);

    default int getTier() {
        return getTier(PartData.of(this));
    }

    PartType getType();

    default GearType getGearType() {
        return GearType.ALL;
    }

    IPartPosition getPartPosition();

    Ingredient getIngredient();

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

    default Collection<StatInstance> getStatModifiers(ItemStat stat, PartData part) {
        return getStatModifiers(stat, part, ItemStack.EMPTY);
    }

    Collection<StatInstance> getStatModifiers(ItemStat stat, PartData part, ItemStack gear);

    default List<TraitInstance> getTraits(PartData part) {
        return getTraits(part, ItemStack.EMPTY);
    }

    List<TraitInstance> getTraits(PartData part, ItemStack gear);

    @Deprecated
    default float getRepairAmount(RepairContext context){
        return 0f;
    }

    default float computeStatValue(ItemStat stat) {
        return computeStatValue(stat, PartData.of(this));
    }

    default float computeStatValue(ItemStat stat, PartData part) {
        return stat.compute(0, getStatModifiers(stat, part));
    }

    default float computeUnclampedStatValue(ItemStat stat, PartData part) {
        return stat.compute(0, false, getStatModifiers(stat, part));
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
     * @param gearType The gear type (or null if not available)
     * @return True if crafting is allowed or {@code gearType} is {@code null}, false otherwise
     */
    default boolean isCraftingAllowed(PartData part, GearType gearType) {
        if (gearType.isGear() && this.getType() == PartType.MAIN) {
            if (gearType.isArmor())
                return computeUnclampedStatValue(ItemStats.ARMOR_DURABILITY, part) > 0;
            else
                return computeUnclampedStatValue(ItemStats.DURABILITY, part) > 0;
        }
        return true;
    }

    default boolean isCraftingAllowed(PartData part, GearType gearType, @Nullable CraftingInventory inventory) {
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

    ITextComponent getDisplayName(@Nullable PartData part, ItemStack gear);

    default ITextComponent getMaterialName(@Nullable PartData part, ItemStack gear) {
        return getDisplayName(part, gear);
    }

    @Nullable
    default ITextComponent getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        return null;
    }

    default String getModelKey(PartData part) {
        return SilentGear.shortenId(this.getId());
    }

    @OnlyIn(Dist.CLIENT)
    void addInformation(PartData part, ItemStack gear, List<ITextComponent> tooltip, ITooltipFlag flag);

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
     * @param tier The target tier for random materials. If there are no materials of that tier,
     *             materials of another tier should be selected.
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
