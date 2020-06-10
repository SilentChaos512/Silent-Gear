package net.silentchaos512.gear.api.event;

import net.minecraftforge.eventbus.api.Event;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import java.util.List;

/**
 * Fired when collecting the stat modifiers for a part material. This allows modifiers to be added
 * or removed.
 *
 * @author SilentChaos512
 * @since 2.0.0
 */
public class GetMaterialStatsEvent extends Event {
    private final ItemStat stat;
    private final List<StatInstance> modifiers;
    private final IMaterial material;

    public GetMaterialStatsEvent(IMaterial material, ItemStat stat, List<StatInstance> modifiers) {
        this.stat = stat;
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        this.modifiers = modifiers;
        this.material = material;
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

    ;
}
