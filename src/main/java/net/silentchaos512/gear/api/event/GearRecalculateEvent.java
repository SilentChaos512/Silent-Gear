package net.silentchaos512.gear.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.part.PartInstance;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Fired when gear properties are recalculated using {@link net.silentchaos512.gear.util.GearData#recalculateGearData(ItemStack, Player)}
 */
public abstract class GearRecalculateEvent extends GearItemEvent {
    private final Player player;
    private final GearType gearType;

    public GearRecalculateEvent(ItemStack gear, Collection<PartInstance> parts, @Nullable Player player, GearType type) {
        super(gear, parts);
        this.player = player;
        this.gearType = type;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public GearType getGearType() {
        return gearType;
    }

    /**
     * Fired before gear properties are recalculated using {@link net.silentchaos512.gear.util.GearData#recalculateGearData(ItemStack, Player)}
     */
    public static class Pre extends GearRecalculateEvent {
        public Pre(ItemStack gear, Collection<PartInstance> parts, @Nullable Player player, GearType type) {
            super(gear, parts, player, type);
        }
    }

    /**
     * Fired after gear properties are recalculated using {@link net.silentchaos512.gear.util.GearData#recalculateGearData(ItemStack, Player)}
     */
    public static class Post extends GearRecalculateEvent {
        public Post(ItemStack gear, Collection<PartInstance> parts, @Nullable Player player, GearType type) {
            super(gear, parts, player, type);
        }
    }
}
