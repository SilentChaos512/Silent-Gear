package net.silentchaos512.gear.item.blueprint.book;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.network.payload.client.SelectBlueprintInBookPayload;
import net.silentchaos512.lib.util.Color;

public class BlueprintBookContainerScreen extends AbstractContainerScreen<BlueprintBookContainerMenu> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");

    private final Inventory playerInventory;
    private final int inventoryRows;
    private int selected;

    public BlueprintBookContainerScreen(BlueprintBookContainerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 176, 114 + container.getContainerRows() * 18);
        this.playerInventory = playerInventory;
        this.inventoryRows = container.getContainerRows();
        this.selected = BlueprintBookItem.getSelectedSlot(container.item);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean p_432883_) {
        if (KeyTracker.isControlDown()) {
            Slot slot = getSlotUnderMouse();
            if (slot != null && !slot.getItem().isEmpty()) {
                this.selected = slot.index;
                ClientPacketDistributor.sendToServer(new SelectBlueprintInBookPayload(this.menu.bookSlot, this.selected));

                if (this.minecraft != null) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                return true;
            }
        }

        return super.mouseClicked(event, p_432883_);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96, 256, 256);

        int left = leftPos + 8 + 18 * (this.selected % 9);
        int top = topPos + 18 + 18 * (this.selected / 9);
        graphics.fill(left, top, left + 16, top + 16, Color.SEAGREEN.getColor());
    }
}
