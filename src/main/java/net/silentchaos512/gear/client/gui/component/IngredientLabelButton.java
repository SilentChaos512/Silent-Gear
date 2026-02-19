package net.silentchaos512.gear.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.silentchaos512.lib.event.ClientTicks;

import java.util.Objects;

public class IngredientLabelButton extends LabelButton {
    private final Ingredient ingredient;

    public IngredientLabelButton(Ingredient ingredient, Component message, Font font, OnPress onPress) {
        super(message, font, onPress);
        this.ingredient = ingredient;
    }

    public IngredientLabelButton(int width, int height, Ingredient ingredient, Component message, Font font, OnPress onPress) {
        super(width, height, message, font, onPress);
        this.ingredient = ingredient;
    }

    public IngredientLabelButton(int x, int y, int width, int height, Ingredient ingredient, Component message, Font font, OnPress onPress) {
        super(x, y, width, height, message, font, onPress);
        this.ingredient = ingredient;
    }

    public static int getWidth(Component text, Font font) {
        var iconSpace = getIconSpace(font);
        var textWidth = font.width(text.getVisualOrderText());
        return iconSpace + textWidth;
    }

    private static int getIconSpace(Font font) {
        return font.lineHeight + 1;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = this.getFont();
        int textXOffset = getIconSpace(font);
        // Render label with offset to leave room for icon
        renderWithHorizontalOffset(guiGraphics, textXOffset);

        // Recompute x and y positions
        Component component = this.getMessage();
        int width = this.getWidth();
        int textWidth = font.width(component);
        int x = this.getX() + Math.round(this.alignX * (float)(width - textWidth));
        int y = this.getY() + (this.getHeight() - 9) / 2;

        // Render icon
        var contextMap = SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level));
        var items = this.ingredient.display().resolveForStacks(contextMap);
        ItemStack item;
        if (!items.isEmpty()) {
            // Cycle through items
            int index = (ClientTicks.ticksInGame() / 20) % items.size();
            item = items.get(index);
        } else {
            // Empty ingredient
            item = new ItemStack(Items.BARRIER);
        }
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(0.5f, 0.5f);
        guiGraphics.renderItem(item, x * 2, y * 2);
        guiGraphics.pose().popMatrix();
    }
}
