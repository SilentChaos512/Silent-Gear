package net.silentchaos512.gear.client.gui.book.page.element;

import net.minecraft.client.gui.Font;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;

public interface PageElement {
    /**
     * Preferred vertical padding in between elements
     */
    int VERTICAL_PADDING = 2;

    void init(AbstractMaterialBookScreen.ComponentAccess componentAccess, Font font, int pageX, int pageY, int pageIndex);

    int getHeight(Font font);
}
