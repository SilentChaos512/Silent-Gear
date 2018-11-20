package net.silentchaos512.gear.api.parts;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Used to register gear parts, and match parts to item stacks.
 *
 * @author SilentChaos512
 */
public final class PartRegistry {
    private static Map<String, ItemPart> map = new LinkedHashMap<>();
    private static ImmutableList<PartMain> mains = null;
    private static ImmutableList<PartRod> rods = null;
    private static ImmutableList<PartMain> visibleMains = null;
    private static ImmutableList<PartRod> visibleRods = null;
    private static Map<String, ItemPart> STACK_TO_PART = new HashMap<>();
    @Getter private static int highestMainPartTier = 0;

    private PartRegistry() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Gets the part with the given key, if it exists.
     *
     * @param key The part name/key
     * @return The {@link ItemPart} with the given key, or null if there is no match
     */
    @Nullable
    public static ItemPart get(String key) {
        return map.get(key);
    }

    /**
     * Gets the part with the given key, if it exists.
     *
     * @param key The part name/key
     * @return The {@link ItemPart} with the given key, or null if there is no match
     */
    @Nullable
    public static ItemPart get(ResourceLocation key) {
        return map.get(key.toString());
    }

    /**
     * Gets an {@link ItemPart} matching the stack, if one exists.
     *
     * @param stack {@link ItemStack} that may or may not be an {@link ItemPart}
     * @return The matching {@link ItemPart}, or null if there is none
     */
    @Nullable
    public static ItemPart get(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        String key = stack.getItem().getTranslationKey() + "@" + stack.getItemDamage();
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
     * Registers a gear part (material). A part with the same key must not be registered.
     *
     * @param part The {@link ItemPart}
     */
    public static <T extends ItemPart> T putPart(@Nonnull T part) {
        String key = part.registryName.toString();
        if (map.containsKey(key))
            throw new IllegalArgumentException("Already have a part with key " + part.registryName);
        map.put(key, part);

        if (part instanceof PartMain && part.getTier() > highestMainPartTier)
            highestMainPartTier = part.getTier();

        return part;
    }

    public static Set<String> getKeySet() {
        return map.keySet();
    }

    public static Collection<ItemPart> getValues() {
        return map.values();
    }

    /**
     * Gets a list of registered mains in the order they are registered (used for sub-item display,
     * among other things). List is immutable and cached.
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
     * Gets a list of registered rods in the order they are registered. List is immutable and
     * cached.
     */
    public static List<PartRod> getRods() {
        if (rods == null) {
            rods = map.values().stream()
                    .filter(p -> p instanceof PartRod)
                    .map(PartRod.class::cast).collect(ImmutableList.toImmutableList());
        }
        return rods;
    }

    /**
     * Gets a list of all mains that are not blacklisted or hidden. List is immutable and cached.
     */
    public static List<PartMain> getVisibleMains() {
        if (visibleMains == null) {
            visibleMains = map.values().stream()
                    .filter(p -> p instanceof PartMain && !p.isBlacklisted() && !p.isHidden())
                    .map(PartMain.class::cast).collect(ImmutableList.toImmutableList());
        }
        return visibleMains;
    }

    /**
     * Gets a list of all rods that are not blacklisted or hidden. List is immutable and cached.
     */
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

    public static void getDebugLines(List<String> list) {
        list.add("PartRegistry.map=" + map.size());
        list.add("PartRegistry.STACK_TO_PART=" + STACK_TO_PART.size());
    }
}
