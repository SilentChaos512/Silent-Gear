package net.silentchaos512.gear.network;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.silentchaos512.gear.block.compounder.CompoundMakerContainer;
import net.silentchaos512.gear.item.ICycleItem;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookItem;
import net.silentchaos512.gear.network.client.*;
import net.silentchaos512.gear.network.payload.client.*;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class SgServerPayloadHandler {
    private static final SgServerPayloadHandler INSTANCE = new SgServerPayloadHandler();

    public static SgServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handleData(final PlayPayloadContext ctx, Runnable handler) {
        ctx.workHandler().submitAsync(handler)
                .exceptionally(e -> {
                    ctx.packetHandler().disconnect(Component.translatable("network.silentgear.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleAck(AckPayload data, ConfigurationPayloadContext ctx) {
        ctx.taskCompletedHandler().onTaskCompleted(data.type());
    }

    public void handleAlloyMakerUpdate(AlloyMakerUpdatePayload data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                if (player.containerMenu instanceof CompoundMakerContainer container) {
                    container.setWorkEnabled(data.workEnabled());
                }
            });
        });
    }

    public void handleSwingGear(SwingGearPayload data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                ItemStack stack = player.getMainHandItem();
                if (GearHelper.isGear(stack)) {
                    GearHelper.onItemSwing(stack, player);
                }
            });
        });
    }

    public void handleKeyPressOnItem(KeyPressOnItemPayload data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                if (data.slot() >= 0 && data.slot() < player.containerMenu.slots.size()) {
                    Slot inventorySlot = player.containerMenu.getSlot(data.slot());
                    ItemStack stack = inventorySlot.getItem();

                    if (!stack.isEmpty()) {
                        switch (data.type()) {
                            case CYCLE_BACK:
                            case CYCLE_NEXT:
                                if (stack.getItem() instanceof ICycleItem cycleItem) {
                                    cycleItem.onCycleKeyPress(stack, data.type().direction);
                                    player.containerMenu.slotsChanged(inventorySlot.container);
                                }
                                break;
                            case OPEN_ITEM:
                                // TODO
                                break;
                        }
                    }
                }
            });
        });
    }

    public void handleRecalculateStats(RecalculateStatsPayload data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                if (data.slot() >= 0 && data.slot() < player.getInventory().getContainerSize()) {
                    ItemStack stack = player.getInventory().getItem(data.slot());

                    if (GearHelper.isGear(stack)) {
                        GearData.recalculateStats(stack, player);
                        GearData.putStatInNbtIfMissing(stack, data.triggerStat());
                    }
                }
            });
        });
    }

    public void handleSelectBlueprintInBook(SelectBlueprintInBookPayload data, PlayPayloadContext ctx) {
        handleData(ctx, () -> {
            ctx.player().ifPresent(player -> {
                if (data.bookSlot() >= 0 && data.bookSlot() < player.containerMenu.slots.size()) {
                    ItemStack book = player.containerMenu.getSlot(data.bookSlot()).getItem();

                    if (!book.isEmpty() && book.getItem() instanceof BlueprintBookItem) {
                        BlueprintBookItem.setSelectedSlot(book, data.blueprintSlot());
                    }
                }
            });
        });
    }
}
