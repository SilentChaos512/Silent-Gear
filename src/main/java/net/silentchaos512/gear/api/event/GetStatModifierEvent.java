package net.silentchaos512.gear.api.event;

import net.minecraftforge.eventbus.api.Event;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.PartData;

import java.util.List;

/**
 * Fired when collecting the stat modifiers for a gear part. This allows modifiers to be added or
 * removed.
 *
 * @author SilentChaos512
 * @since Experimental
 */
public class GetStatModifierEvent extends Event {
    private final ItemStat stat;
    private final List<StatInstance> modifiers;
    private final PartData part;

    public GetStatModifierEvent(PartData part, ItemStat stat, List<StatInstance> modifiers) {
        this.stat = stat;
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        this.modifiers = modifiers;
        this.part = part;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public ItemStat getStat() {
        return stat;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<StatInstance> getModifiers() {
        return modifiers;
    }

    public PartData getPart() {
        return part;
    }
}
