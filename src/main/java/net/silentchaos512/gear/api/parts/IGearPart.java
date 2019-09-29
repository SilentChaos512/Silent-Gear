package net.silentchaos512.gear.api.parts;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.RepairContext;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface IGearPart {
    ResourceLocation getId();

    int getTier();

    PartType getType();

    IPartPosition getPartPosition();

    IPartMaterial getMaterials();

    IPartSerializer<?> getSerializer();

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

    StatInstance.Operation getDefaultStatOperation(ItemStat stat);

    float getRepairAmount(RepairContext context);

    default float computeStatValue(ItemStat stat) {
        return computeStatValue(stat, PartData.of(this));
    }

    default float computeStatValue(ItemStat stat, PartData part) {
        return stat.compute(0, getStatModifiers(stat, part));
    }

    default float computeUnclampedStatValue(ItemStat stat) {
        return stat.compute(0, false, getStatModifiers(stat, PartData.of(this)));
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
    default boolean isCraftingAllowed(@Nullable GearType gearType) {
        if (gearType != null && this.getType() == PartType.MAIN) {
            if (gearType.matches("armor"))
                return computeUnclampedStatValue(ItemStats.ARMOR_DURABILITY) > 0;
            else
                return computeUnclampedStatValue(ItemStats.DURABILITY) > 0;
        }
        return true;
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

    @Nullable
    ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame);

    @Nullable
    ResourceLocation getBrokenTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position);

    int getColor(PartData part, ItemStack gear, int animationFrame);

    ITextComponent getDisplayName(@Nullable PartData part, ItemStack gear);

    @Nullable
    default ITextComponent getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        return null;
    }

    // May be removed or changed?
    @Deprecated
    String getModelIndex(PartData part, int animationFrame);

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
}
