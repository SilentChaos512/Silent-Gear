package net.silentchaos512.gear.client.gui.book.page;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Font;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.book.page.element.PageElement;

import java.util.List;

public class Page {
    public static final int PAGE_WIDTH = 124;
    public static final int PAGE_HEIGHT = 163;

    private final List<PageElement> elements;

    public Page(List<PageElement> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    public void init(AbstractMaterialBookScreen.ComponentAccess access, Font font, int pageX, int pageY, int pageIndex) {
        int y = pageY;
        for (PageElement element : this.elements) {
            element.init(access, font, pageX, y, pageIndex);
            y += element.getHeight(font);
        }
    }
}
