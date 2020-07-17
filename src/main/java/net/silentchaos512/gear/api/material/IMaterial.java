package net.silentchaos512.gear.api.material;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.lib.event.ClientTicks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A material used for crafting gear parts. Provides stats and traits for different part types.
 */
public interface IMaterial {
    String getPackName();

    ResourceLocation getId();

    IMaterialSerializer<?> getSerializer();

    @Nullable
    IMaterial getParent();

    default Optional<IMaterial> getParentOptional() {
        return Optional.ofNullable(getParent());
    }

    int getTier(PartType partType);

    // FIXME: PartType param should not be used?
    Ingredient getIngredient(PartType partType);

    Set<PartType> getPartTypes();

    boolean allowedInPart(PartType partType);

    void retainData(@Nullable IMaterial oldMaterial);

    Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType, ItemStack gear);

    default Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType) {
        return getStatModifiers(stat, partType, ItemStack.EMPTY);
    }

    Collection<PartTraitInstance> getTraits(PartType partType, ItemStack gear);

    default Collection<PartTraitInstance> getTraits(PartType partType) {
        return getTraits(partType, ItemStack.EMPTY);
    }

    default float getStat(ItemStat stat, PartType partType) {
        return stat.compute(0, getStatModifiers(stat, partType));
    }

    default float getStatUnclamped(ItemStat stat, PartType partType) {
        return stat.compute(0, false, getStatModifiers(stat, partType));
    }

    boolean isCraftingAllowed(PartType partType, GearType gearType);

    @Deprecated
    int getColor(ItemStack gear, PartType partType);

    @Deprecated
    PartTextureType getTexture(PartType partType, ItemStack gear);

    IMaterialDisplay getMaterialDisplay(ItemStack gear, PartType partType);

    ITextComponent getDisplayName(PartType partType, ItemStack gear);

    default ITextComponent getDisplayName(PartType partType) {
        return getDisplayName(partType, ItemStack.EMPTY);
    }

    @Nullable
    default ITextComponent getDisplayNamePrefix(ItemStack gear, PartType partType) {
        return null;
    }

    default boolean isVisible(PartType partType) {
        return true;
    }

    /**
     * Get an {@code ItemStack} which matches the normal ingredient. The {@code ticks} parameter can
     * be used to cycle between possible matches.
     *
     * @param type The part type
     * @param ticks Used to index into matching stacks. If on the client, {@link
     *              ClientTicks#totalTicks()} can be used. Zero will consistently return the first
     *              item in the matching stacks array.
     * @return An item matching the normal ingredient, or {@link ItemStack#EMPTY} if there are none
     */
    default ItemStack getDisplayItem(PartType type, int ticks) {
        ItemStack[] stacks = getIngredient(type).getMatchingStacks();
        if (stacks.length == 0) return ItemStack.EMPTY;
        return stacks[(ticks / 20) % stacks.length];
    }

    void updateIngredient(SyncMaterialCraftingItemsPacket msg);
}
