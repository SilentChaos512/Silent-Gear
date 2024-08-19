package net.silentchaos512.gear.api.event;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.gear.trait.Trait;

import java.util.Map;

/**
 * Fired when a gear item's traits have been calculated, allowing for mods to adjust the result.
 *
 * @author SilentChaos512
 * @since 1.3.1
 */
@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
public class GetTraitsEvent extends GearItemEvent {
    private final Map<Trait, Integer> traits;

    public GetTraitsEvent(ItemStack gear, PartList parts, Map<Trait, Integer> traits) {
        super(gear, parts);
        this.traits = traits;
    }

    public Map<Trait, Integer> getTraits() {
        return traits;
    }
}
