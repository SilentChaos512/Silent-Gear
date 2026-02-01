package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;

public class EmptyPageElement implements PageElement {
    private final int height;

    public EmptyPageElement(int height) {
        this.height = height;
    }

    @Override
    public void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex) {
        // Nothing
    }

    @Override
    public int getHeight(Font font) {
        return this.height;
    }
}
