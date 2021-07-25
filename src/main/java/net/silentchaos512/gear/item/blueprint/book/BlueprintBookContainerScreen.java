package net.silentchaos512.gear.item.blueprint.book;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.item.IContainerItem;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.SelectBlueprintFromBookPacket;
import net.silentchaos512.utils.Color;

public class BlueprintBookContainerScreen extends AbstractContainerScreen<BlueprintBookContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final Inventory playerInventory;
    private final int inventoryRows;
    private int selected;

    public BlueprintBookContainerScreen(BlueprintBookContainer container, Inventory playerInventory, Component title) {
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
                Network.channel.sendToServer(new SelectBlueprintFromBookPacket(this.menu.bookSlot, this.selected));

                if (this.minecraft != null) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        this.font.draw(matrixStack, this.getTitle().getString(), 8, 6, 4210752);
        this.font.draw(matrixStack, playerInventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bind(TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        blit(matrixStack, i, j, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);

        int left = leftPos + 8 + 18 * (this.selected % 9);
        int top = topPos + 18 + 18 * (this.selected / 9);
        fill(matrixStack, left, top, left + 16, top + 16, Color.SEAGREEN.getColor());
    }
}
