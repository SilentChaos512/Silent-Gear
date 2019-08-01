package net.silentchaos512.gear.api.event;

import lombok.Getter;
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
@Getter
public class GetStatModifierEvent extends Event {
    private final ItemStat stat;
    private final List<StatInstance> modifiers;
    private final PartData part;

    public GetStatModifierEvent(PartData part, ItemStat stat, List<StatInstance> modifiers) {
        this.stat = stat;
        this.modifiers = modifiers;
        this.part = part;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
