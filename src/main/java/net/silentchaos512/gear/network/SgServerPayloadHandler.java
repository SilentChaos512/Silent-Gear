package net.silentchaos512.gear.network;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silentchaos512.gear.block.compounder.AlloyMakerContainer;
import net.silentchaos512.gear.item.ICycleItem;
import net.silentchaos512.gear.item.blueprint.book.BlueprintBookItem;
import net.silentchaos512.gear.network.payload.client.*;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class SgServerPayloadHandler {
    private static final SgServerPayloadHandler INSTANCE = new SgServerPayloadHandler();

    public static SgServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    private static void handleData(final IPayloadContext ctx, Runnable handler) {
        ctx.enqueueWork(handler)
                .exceptionally(e -> {
                    ctx.disconnect(Component.translatable("network.silentgear.failure", e.getMessage()));
                    return null;
                });
    }

    public void handleAck(AckPayload data, IPayloadContext ctx) {
    }

    public void handleAlloyMakerUpdate(AlloyMakerUpdatePayload data, IPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player();
            if (player.containerMenu instanceof AlloyMakerContainer container) {
                container.setWorkEnabled(data.workEnabled());
            }
        });
    }

    public void handleSwingGear(SwingGearPayload data, IPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player();
            ItemStack stack = player.getMainHandItem();
            if (GearHelper.isGear(stack)) {
                GearHelper.onItemSwing(stack, player);
            }
        });
    }

    public void handleKeyPressOnItem(KeyPressOnItemPayload data, IPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player();
            if (data.slot() >= 0 && data.slot() < player.containerMenu.slots.size()) {
                Slot inventorySlot = player.containerMenu.getSlot(data.slot());
                ItemStack stack = inventorySlot.getItem();

                if (!stack.isEmpty()) {
                    switch (data.keyPressType()) {
                        case CYCLE_BACK:
                        case CYCLE_NEXT:
                            if (stack.getItem() instanceof ICycleItem cycleItem) {
                                cycleItem.onCycleKeyPress(stack, data.keyPressType().direction);
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
    }

    public void handleRecalculateStats(RecalculateStatsPayload data, IPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player();
            if (data.slot() >= 0 && data.slot() < player.getInventory().getContainerSize()) {
                ItemStack stack = player.getInventory().getItem(data.slot());

                if (GearHelper.isGear(stack)) {
                    GearData.recalculateGearData(stack, player);
                }
            }
        });
    }

    public void handleSelectBlueprintInBook(SelectBlueprintInBookPayload data, IPayloadContext ctx) {
        handleData(ctx, () -> {
            Player player = ctx.player();
            if (data.bookSlot() >= 0 && data.bookSlot() < player.containerMenu.slots.size()) {
                ItemStack book = player.containerMenu.getSlot(data.bookSlot()).getItem();

                if (!book.isEmpty() && book.getItem() instanceof BlueprintBookItem) {
                    BlueprintBookItem.setSelectedSlot(book, data.blueprintSlot());
                }
            }
        });
    }
}
