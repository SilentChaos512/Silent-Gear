package net.silentchaos512.gear.network;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Objects;
import java.util.function.Supplier;

public class RecalculateStatsPacket {
    private final int slot;
    private final IItemStat triggerStat;

    private RecalculateStatsPacket(int slot, IItemStat triggerStat) {
        this.slot = slot;
        this.triggerStat = triggerStat;
    }

    public RecalculateStatsPacket(Level level, ItemStack stack, IItemStat triggerStat) {
        this.slot = findSlotOnClientPlayer(level, stack);
        this.triggerStat = triggerStat;
    }

    private static int findSlotOnClientPlayer(Level level, ItemStack stack) {
        if (level instanceof ClientLevel) {
            Player player = SilentGear.PROXY.getClientPlayer();
            if (player != null) {
                return player.getInventory().findSlotMatchingItem(stack);
            }
        }
        return -1;
    }

    public ItemStack getItem(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();

        if (player != null && this.slot >= 0 && this.slot < player.getInventory().getContainerSize()) {
            return player.getInventory().getItem(this.slot);
        }

        return ItemStack.EMPTY;
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ItemStack stack = getItem(context);

        if (GearHelper.isGear(stack)) {
            GearData.recalculateStats(stack, context.get().getSender());

            // Also ensure the stat that triggered the recalculation is in the stat map
            GearData.putStatInNbtIfMissing(stack, this.triggerStat);
        }
    }

    public static RecalculateStatsPacket decode(FriendlyByteBuf buffer) {
        int slot = buffer.readVarInt();
        ItemStat stat = ItemStats.byName(buffer.readResourceLocation());
        return new RecalculateStatsPacket(slot, Objects.requireNonNull(stat));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.slot);
        buffer.writeResourceLocation(this.triggerStat.getStatId());
    }
}
