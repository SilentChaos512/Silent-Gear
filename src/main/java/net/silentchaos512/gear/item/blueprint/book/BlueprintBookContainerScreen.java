package net.silentchaos512.gear.item.blueprint.book;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.item.IContainerItem;
import net.silentchaos512.gear.network.Network;
import net.silentchaos512.gear.network.SelectBlueprintFromBookPacket;

public class BlueprintBookContainerScreen extends ContainerScreen<BlueprintBookContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final PlayerInventory playerInventory;
    private final int inventoryRows;
    private int selected = 0;

    public BlueprintBookContainerScreen(BlueprintBookContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        ItemStack stack = container.item;
        IContainerItem item = (IContainerItem) stack.getItem();
        this.inventoryRows = item.getInventoryRows(stack);
        this.ySize = 114 + this.inventoryRows * 18;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_mouseClicked_5_) {
        if (KeyTracker.isControlDown()) {
            Slot slot = getSlotUnderMouse();
            if (slot != null && !slot.getStack().isEmpty()) {
                this.selected = slot.slotNumber;
                Network.channel.sendToServer(new SelectBlueprintFromBookPacket(this.container.bookSlot, this.selected));

                if (this.minecraft != null) {
                    this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }



    @Override
    protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.drawString(matrixStack, this.getTitle().getString(), 8, 6, 4210752);
        this.font.drawString(matrixStack, playerInventory.getDisplayName().getString(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        if (minecraft == null) return;
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(matrixStack, i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);

        // FIXME: I hate rendering...
        int left = i + 16 * (this.selected % 9);
        int top = j + 16 * (this.selected / 9);
        int color = 0xFFFFAA;
//        GuiUtils.drawGradientRect(matrixStack, -10, left, top, left + 16, top + 16, color, color);
    }
}
