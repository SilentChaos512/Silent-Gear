package net.silentchaos512.gear.api.parts;

import com.google.common.collect.ImmutableList;
import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to register gear parts, and match parts to item stacks.
 *
 * @author SilentChaos512
 */
public final class PartRegistry {

    private static Map<String, ItemPart> map = new THashMap<>();
    private static List<PartMain> mains = null;
    private static List<PartRod> rods = null;
    private static List<PartMain> visibleMains = null;
    private static List<PartRod> visibleRods = null;
    private static Map<String, ItemPart> STACK_TO_PART = new THashMap<>();

    private PartRegistry() {
    }

    /**
     * @param key The key the part was registered with.
     * @return The part for the given key.
     */
    @Nullable
    public static ItemPart get(String key) {
        return map.get(key);
    }

    /**
     * Gets the gear part that matches the ItemStack. Also checks the ore dictionary for parts that have an ore dictionary
     * key.
     *
     * @param stack
     * @return
     */
    @Nullable
    public static ItemPart get(ItemStack stack) {
        if (StackHelper.isEmpty(stack))
            return null;

        String key = stack.getItem().getUnlocalizedName() + "@" + stack.getItemDamage();
        if (STACK_TO_PART.containsKey(key))
            return STACK_TO_PART.get(key);

        for (ItemPart part : map.values()) {
            if (part.matchesForCrafting(stack, true)) {
                STACK_TO_PART.put(key, part);
                return part;
            }
        }
        return null;
    }

    /**
     * Registers a gear part.
     *
     * @param part
     */
    public static <T extends ItemPart> T putPart(@Nonnull T part) {
        String key = part.key.toString();
        if (map.containsKey(key))
            throw new IllegalArgumentException("Already have a part with key " + part.key);
        map.put(key, part);

        return part;
    }

    public static @Nullable
    ItemPart fromDecoStack(ItemStack stack) {
        if (StackHelper.isEmpty(stack))
            return null;

        for (ItemPart part : map.values()) {
            if (part.matchesForDecorating(stack, true)) {
                return part;
            }
        }
        return null;
    }

    public static Set<String> getKeySet() {
        return map.keySet();
    }

    public static Collection<ItemPart> getValues() {
        return map.values();
    }

    /**
     * Gets a list of registered ToolPartMains in the order they are registered (used for sub-item display).
     */
    public static List<PartMain> getMains() {
        if (mains == null) {
            mains = map.values().stream()
                    .filter(p -> p instanceof PartMain)
                    .map(PartMain.class::cast).collect(ImmutableList.toImmutableList());
        }
        return mains;
    }

    /**
     * Gets a list of registered ToolPartRods in the order they are registered.
     */
    public static List<PartRod> getRods() {
        if (rods == null) {
            rods = map.values().stream()
                    .filter(p -> p instanceof PartRod)
                    .map(PartRod.class::cast).collect(ImmutableList.toImmutableList());
        }
        return rods;
    }

    public static List<PartMain> getVisibleMains() {
        if (visibleMains == null) {
            visibleMains = map.values().stream()
                    .filter(p -> p instanceof PartMain && !p.isBlacklisted() && !p.isHidden())
                    .map(PartMain.class::cast).collect(ImmutableList.toImmutableList());
        }
        return visibleMains;
    }

    public static List<PartRod> getVisibleRods() {
        if (visibleRods == null) {
            visibleRods = map.values().stream()
                    .filter(p -> p instanceof PartRod && !p.isBlacklisted() && !p.isHidden())
                    .map(PartRod.class::cast).collect(ImmutableList.toImmutableList());
        }
        return visibleRods;
    }

    public static void resetVisiblePartCaches() {
        visibleMains = null;
        visibleRods = null;
    }
}
