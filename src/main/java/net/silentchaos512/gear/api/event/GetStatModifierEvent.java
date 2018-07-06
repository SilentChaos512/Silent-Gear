package net.silentchaos512.gear.api.event;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import java.util.List;

/**
 * TODO
 *
 * @author SilentChaos512
 * @since Experimental
 */
@Getter
public class GetStatModifierEvent extends Event {

    private final ItemStat stat;
    private final List<StatInstance> modifiers;
    private final ItemStack partRep;

    public GetStatModifierEvent(ItemStat stat, List<StatInstance> modifiers, ItemStack partRep) {
        this.stat = stat;
        this.modifiers = modifiers;
        this.partRep = partRep;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
