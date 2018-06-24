package net.silentchaos512.gear.api.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * TODO
 *
 * @author SilentChaos512
 * @since Experimental
 */
public class GetStatModifierEvent extends Event {

    @Getter(value = AccessLevel.PUBLIC, onMethod = @__({@Nonnull}))
    private final ItemStat stat;
    @Getter(value = AccessLevel.PUBLIC, onMethod = @__({@Nonnull}))
    @Setter(value = AccessLevel.PUBLIC, onParam = @__({@Nonnull}))
    private Collection<StatInstance> modifiers;
    @Getter(value = AccessLevel.PUBLIC, onMethod = @__({@Nonnull}))
    private ItemStack partRep;

    public GetStatModifierEvent(ItemStat stat, Collection<StatInstance> modifiers, ItemStack partRep) {

        this.stat = stat;
        this.modifiers = modifiers;
        this.partRep = partRep;
    }

    @Override
    public boolean isCancelable() {

        return false;
    }
}
