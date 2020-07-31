package net.silentchaos512.gear.api.parts;

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
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.gear.parts.RepairContext;

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

    IPartPosition getPartPosition();

    Ingredient getIngredient();

    IPartSerializer<?> getSerializer();

    default String getPackName() {
        return "PACK UNKNOWN";
    }

    /**
     * Used to copy data that is only needed on the server. This prevents certain things like trait
     * conditions from being lost when synchronizing parts in singleplayer.
     *
     * @param oldPart The old part instance
     */
    void retainData(@Nullable IGearPart oldPart);

    default Collection<StatInstance> getStatModifiers(ItemStat stat, PartData part) {
        return getStatModifiers(ItemStack.EMPTY, stat, part);
    }

    Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat, PartData part);

    default List<PartTraitInstance> getTraits(PartData part) {
        return getTraits(ItemStack.EMPTY, part);
    }

    List<PartTraitInstance> getTraits(ItemStack gear, PartData part);

    float getRepairAmount(RepairContext context);

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
     * @param gear           The gear item
     * @param part           The part
     * @param normalLossRate The default loss rate, which is based on config settings and how
     *                       damaged the item is
     * @return Chance of losing the part when salvaging (defaults to {@code normalLossRate})
     */
    default double getSalvageLossRate(ItemStack gear, PartData part, double normalLossRate) {
        return normalLossRate;
    }

    /**
     * Determine if the part can be used to craft an item of the given type.
     *
     * @param gearType The gear type (or null if not available)
     * @return True if crafting is allowed or {@code gearType} is {@code null}, false otherwise
     */
    default boolean isCraftingAllowed(PartData part, @Nullable GearType gearType) {
        if (gearType != null && this.getType() == PartType.MAIN) {
            if (gearType.matches("armor"))
                return computeUnclampedStatValue(ItemStats.ARMOR_DURABILITY, part) > 0;
            else
                return computeUnclampedStatValue(ItemStats.DURABILITY, part) > 0;
        }
        return true;
    }

    default boolean isCraftingAllowed(PartData part, @Nullable GearType gearType, @Nullable CraftingInventory inventory) {
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

    IPartDisplay getDisplayProperties(PartData part, ItemStack gear, int animationFrame);

    default PartTextureType getLiteTexture(PartData part, ItemStack gear) {
        return getDisplayProperties(part, gear, 0).getLiteTexture();
    }

    int getColor(PartData part, ItemStack gear, int layer, int animationFrame);

    default int getArmorColor(PartData part, ItemStack gear) {
        return part.getPart().getDisplayProperties(part, gear, 0).getArmorColor();
    }

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
