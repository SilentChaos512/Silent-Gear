package net.silentchaos512.gear.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused")
public final class GearApi {
    private GearApi() {throw new IllegalAccessError("Utility class");}

    //region Gear

    public static void recalculateStats(ItemStack gear, @Nullable PlayerEntity player) {
        GearData.recalculateStats(gear, player);
    }

    public static boolean isBroken(ItemStack gear) {
        return GearHelper.isBroken(gear);
    }

    public static boolean isGear(ItemStack stack) {
        return GearHelper.isGear(stack);
    }

    public static float getStat(ItemStack gear, IItemStat stat) {
        return GearData.getStat(gear, stat);
    }

    public static int getStatInt(ItemStack gear, IItemStat stat) {
        return GearData.getStatInt(gear, stat);
    }

    @Nullable
    public static IPartData getPartOfType(ItemStack gear, PartType type) {
        return GearData.getPartOfType(gear, type);
    }

    @Nullable
    public static IPartData getMainPart(ItemStack gear) {
        return GearData.getPrimaryPart(gear);
    }

    public static void attemptDamageGear(ItemStack gear, int amount, @Nullable PlayerEntity player, Hand hand) {
        GearHelper.attemptDamage(gear, amount, player, hand);
    }

    public static void attemptDamageGear(ItemStack gear, int amount, @Nullable PlayerEntity player, EquipmentSlotType slot) {
        GearHelper.attemptDamage(gear, amount, player, slot);
    }

    public static UUID getUuid(ItemStack gear) {
        return GearData.getUUID(gear);
    }

    //endregion

    //region Traits

    public static int getTraitLevel(ItemStack gear, DataResource<ITrait> trait) {
        return getTraitLevel(gear, trait.getId());
    }

    public static int getTraitLevel(ItemStack gear, ResourceLocation traitId) {
        return TraitHelper.getTraitLevel(gear, traitId);
    }

    public static void registerTraitSerializer(ITraitSerializer<?> serializer) {
        TraitSerializers.register(serializer);
    }

    //endregion

    //region Materials

    @Nullable
    public static IMaterial getMaterial(ResourceLocation id) {
        return MaterialManager.get(id);
    }

    @Nullable
    public static IMaterialInstance getMaterial(ItemStack stack) {
        return MaterialInstance.from(stack);
    }

    public static Collection<IMaterial> getMaterials() {
        return MaterialManager.getValues();
    }

    public static Collection<IMaterial> getMaterials(boolean includeChildren) {
        return MaterialManager.getValues(includeChildren);
    }

    //endregion

    //region Parts

    @Nullable
    public static IGearPart getPart(ResourceLocation id) {
        return PartManager.get(id);
    }

    @Nullable
    public static IPartData getPart(ItemStack stack) {
        return PartData.from(stack);
    }

    public static Collection<IGearPart> getValues() {
        return PartManager.getValues();
    }

    //endregion

    //region Random

    /**
     * Gets a {@link ResourceLocation} which will have the "silentgear" namespace if no namespace is
     * included in {@code key}, rather than the "minecraft" namespace.
     * <p>
     * Examples: "something" becomes "silentgear:something" and "my_mod:my_thing" remains unchanged
     *
     * Also see: {@link #shortenId(ResourceLocation)}
     *
     * @param key The string used to create a {@link ResourceLocation}
     * @return A new {@link ResourceLocation} which will default to the "silentgear" namespace.
     */
    public static ResourceLocation modId(String key) {
        return SilentGear.getIdWithDefaultNamespace(key);
    }

    /**
     * If the {@link ResourceLocation} has the "silentgear" namespace, only the path part is
     * returned. Otherwise, the full path including the namespace is returned.
     * <p>
     * Examples: "silentgear:something" becomes "something" and "my_mod:my_thing" remains unchanged
     *
     * Also see: {@link #modId(String)}
     *
     * @param id The {@link ResourceLocation} (object ID, etc.)
     * @return A string representation of {@code id} which may have been shortened to just a path.
     */
    public static String shortenId(ResourceLocation id) {
        return SilentGear.shortenId(id);
    }

    ///endregion
}
