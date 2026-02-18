package net.silentchaos512.gear.client.gui.component;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.lib.event.ClientTicks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientLabelButton extends LabelButton {
    private static final ItemStack NO_ITEMS_ICON = new ItemStack(Items.BARRIER);

    private final List<ItemStack> displayItems;

    public IngredientLabelButton(int x, int y, int width, int height, List<ItemStack> displayItems, Component message, Font font, OnPress onPress) {
        super(x, y, width, height, message, font, onPress);
        this.displayItems = ImmutableList.copyOf(displayItems);
    }

    public IngredientLabelButton(int x, int y, int width, int height, Ingredient ingredient, Component message, Font font, OnPress onPress) {
        this(x, y, width, height, Arrays.asList(ingredient.getItems()), message, font, onPress);
    }

    public IngredientLabelButton(int x, int y, int width, int height, Material material, Component message, Font font, OnPress onPress) {
        this(x, y, width, height, getItemsFromMaterial(material), message, font, onPress);
    }

    private static List<ItemStack> getItemsFromMaterial(Material material) {
        List<ItemStack> list = new ArrayList<>();
        if (!material.getIngredient().hasNoItems()) {
            list.addAll(Arrays.asList(material.getIngredient().getItems()));
        }
        // Add part substitutes so pure rod materials will display an item
        for (PartType partType : material.getPartTypes(MaterialInstance.of(material))) {
            material.getPartSubstitute(partType).ifPresent(ingredient -> {
                if (!ingredient.hasNoItems()) {
                    list.addAll(Arrays.asList(ingredient.getItems()));
                }
            });
        }
        return list;
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
        ItemStack item = getRenderItem(ClientTicks.ticksInGame());
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
        guiGraphics.renderItem(item, x * 2, y * 2);
        guiGraphics.pose().popPose();
    }

    private ItemStack getRenderItem(int ticksInGame) {
        if (this.displayItems.isEmpty()) {
            return NO_ITEMS_ICON;
        }
        int index = (ticksInGame / 20) % this.displayItems.size();
        return this.displayItems.get(index);
    }
}
