package net.silentchaos512.gear.api.event;

import net.minecraftforge.eventbus.api.Event;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.ArrayList;
import java.util.Collection;
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
    private final PartType partType;
    private final List<StatInstance> modifiers;
    private final MaterialInstance material;

    public GetMaterialStatsEvent(MaterialInstance material, ItemStat stat, PartType partType, Collection<StatInstance> modifiers) {
        this.stat = stat;
        this.partType = partType;
        this.modifiers = new ArrayList<>(modifiers);
        this.material = material;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public ItemStat getStat() {
        return stat;
    }

    public PartType getPartType() {
        return partType;
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<StatInstance> getModifiers() {
        return modifiers;
    }

    public MaterialInstance getMaterial() {
        return material;
    }
}
