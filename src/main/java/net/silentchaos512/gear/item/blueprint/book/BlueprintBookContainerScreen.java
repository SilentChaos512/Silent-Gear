package net.silentchaos512.gear.item.blueprint.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.item.IContainerItem;
import net.silentchaos512.gear.network.payload.client.SelectBlueprintInBookPayload;
import net.silentchaos512.lib.util.Color;

public class BlueprintBookContainerScreen extends AbstractContainerScreen<BlueprintBookContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private final Inventory playerInventory;
    private final int inventoryRows;
    private int selected;

    public BlueprintBookContainerScreen(BlueprintBookContainerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        ItemStack stack = container.item;
        IContainerItem item = (IContainerItem) stack.getItem();
        this.inventoryRows = item.getInventoryRows(stack);
        this.imageHeight = 114 + this.inventoryRows * 18;
        this.selected = BlueprintBookItem.getSelectedSlot(container.item);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_mouseClicked_5_) {
        if (KeyTracker.isControlDown()) {
            Slot slot = getSlotUnderMouse();
            if (slot != null && !slot.getItem().isEmpty()) {
                this.selected = slot.index;
                PacketDistributor.sendToServer(new SelectBlueprintInBookPayload(this.menu.bookSlot, this.selected));

                if (this.minecraft != null) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int x, int y) {
        graphics.drawString(this.font, this.getTitle().getString(), 8, 6, 4210752, false);
        graphics.drawString(this.font, playerInventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        if (minecraft == null) return;
        RenderSystem.clearColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        graphics.blit(TEXTURE, i, j + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);

        int left = leftPos + 8 + 18 * (this.selected % 9);
        int top = topPos + 18 + 18 * (this.selected / 9);
        graphics.fill(left, top, left + 16, top + 16, Color.SEAGREEN.getColor());
    }
}
