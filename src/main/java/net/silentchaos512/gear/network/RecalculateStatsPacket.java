package net.silentchaos512.gear.network;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
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

    public RecalculateStatsPacket(World level, ItemStack stack, IItemStat triggerStat) {
        this.slot = findSlotOnClientPlayer(level, stack);
        this.triggerStat = triggerStat;
    }

    private static int findSlotOnClientPlayer(World level, ItemStack stack) {
        if (level instanceof ClientWorld) {
            PlayerEntity player = SilentGear.PROXY.getClientPlayer();
            if (player != null) {
                return player.inventory.findSlotMatchingItem(stack);
            }
        }
        return -1;
    }

    public ItemStack getItem(Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = context.get().getSender();

        if (player != null && player.inventory != null && this.slot >= 0 && this.slot < player.inventory.getContainerSize()) {
            return player.inventory.getItem(this.slot);
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

    public static RecalculateStatsPacket decode(PacketBuffer buffer) {
        int slot = buffer.readVarInt();
        ItemStat stat = ItemStats.byName(buffer.readResourceLocation());
        return new RecalculateStatsPacket(slot, Objects.requireNonNull(stat));
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.slot);
        buffer.writeResourceLocation(this.triggerStat.getStatId());
    }
}
