package net.silentchaos512.gear.client.gui.book.page;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.silentchaos512.gear.client.gui.book.page.element.EmptyPageElement;
import net.silentchaos512.gear.client.gui.book.page.element.PageElement;

import java.util.ArrayList;
import java.util.List;

public class SectionBuilder {
    private final Font font;
    private final List<Page> output = new ArrayList<>();
    private List<PageElement> currentPageElements = new ArrayList<>();
    private int currentPageHeight = 0;

    public SectionBuilder() {
        this(Minecraft.getInstance().font);
    }

    public SectionBuilder(Font font) {
        this.font = font;
    }

    public void add(PageElement element) {
        var elementHeight = element.getHeight(this.font);
        if (!this.currentPageElements.isEmpty() && this.currentPageHeight + elementHeight >= Page.PAGE_HEIGHT) {
            finishCurrentPage();
        }
        addNewPageElement(element, elementHeight);
    }

    public void addPageBreak() {
        if (this.currentPageElements.isEmpty()) {
            add(new EmptyPageElement(Page.PAGE_HEIGHT));
        }
        finishCurrentPage();
    }

    public List<Page> build() {
        finishCurrentPage();
        return new ArrayList<>(output);
    }

    private void finishCurrentPage() {
        if (!this.currentPageElements.isEmpty()) {
            var page = new Page(currentPageElements);
            this.output.add(page);
            this.currentPageElements.clear();
            this.currentPageHeight = 0;
        }
    }

    private void addNewPageElement(PageElement element, int elementHeight) {
        this.currentPageElements.add(element);
        this.currentPageHeight += elementHeight;
    }
}
