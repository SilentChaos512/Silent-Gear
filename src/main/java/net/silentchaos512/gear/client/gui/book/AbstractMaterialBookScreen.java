package net.silentchaos512.gear.client.gui.book;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.book.page.SectionBuilder;
import net.silentchaos512.gear.client.gui.component.TexturedButton;

import javax.annotation.Nullable;
import java.util.List;

public class AbstractMaterialBookScreen extends Screen {
    public static final ResourceLocation BOOK_TEXTURE = SilentGear.getId("textures/gui/guide_book.png");
    public static final ResourceLocation PAGE_BACKWARD = ResourceLocation.withDefaultNamespace("widget/page_backward");
    public static final ResourceLocation PAGE_BACKWARD_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/page_backward_highlighted");
    public static final ResourceLocation PAGE_FORWARD = ResourceLocation.withDefaultNamespace("widget/page_forward");
    public static final ResourceLocation PAGE_FORWARD_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/page_forward_highlighted");
    public static final ResourceLocation PAGE_RETURN = SilentGear.getId("textures/gui/sprites/widget/page_return.png");
    public static final ResourceLocation PAGE_RETURN_HIGHLIGHTED = SilentGear.getId("textures/gui/sprites/widget/page_return_highlighted.png");

    @Nullable private final Screen previousScreen;
    private final List<Page> pages;
    private int leftPageIndex = 0;

    public AbstractMaterialBookScreen(@Nullable Screen previousScreen, List<Page> pages) {
        super(GameNarrator.NO_TITLE);
        this.previousScreen = previousScreen;
        this.pages = ImmutableList.copyOf(pages);
    }

    public AbstractMaterialBookScreen(@Nullable Screen previousScreen, SectionBuilder pagesBuilder) {
        this(previousScreen, pagesBuilder.build());
    }

    @Nullable
    protected Page getLeftPage() {
        return getPage(this.leftPageIndex);
    }

    @Nullable
    protected Page getRightPage() {
        return getPage(this.leftPageIndex + 1);
    }

    @Nullable
    protected Page getPage(int index) {
        if (index >= 0 && index < this.pages.size()) {
            return this.pages.get(index);
        }
        return null;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.initCommonControls();
        this.initPageComponents();
    }

    private void initPageComponents() {
        ComponentAccess componentAccess = new ComponentAccess(this);

        var leftPage = this.getLeftPage();
        if (leftPage != null) {
            leftPage.init(componentAccess, font, this.width / 2 - 130, 30, this.leftPageIndex);
        }

        var rightPage = this.getRightPage();
        if (rightPage != null) {
            rightPage.init(componentAccess, font, this.width / 2 + 10, 30, this.leftPageIndex + 1);
        }
    }

    private void initCommonControls() {
        // Page Back
        var pageBackwardSprites = new WidgetSprites(PAGE_BACKWARD, PAGE_BACKWARD, PAGE_BACKWARD_HIGHLIGHTED);
        this.addRenderableWidget(new TexturedButton(true, this.width / 2 - 140, 199, 23, 13, pageBackwardSprites, button -> this.onPageBackward()));
        // Page Forward
        var pageForwardSprites = new WidgetSprites(PAGE_FORWARD, PAGE_FORWARD, PAGE_FORWARD_HIGHLIGHTED);
        this.addRenderableWidget(new TexturedButton(true, this.width / 2 + 117, 199, 23, 13, pageForwardSprites, button -> this.onPageForward()));
        // Page Return
        var pageReturnSprites = new WidgetSprites(PAGE_RETURN, PAGE_RETURN, PAGE_RETURN_HIGHLIGHTED);
        this.addRenderableWidget(new TexturedButton(false, this.width / 2 - 140, 7, 23, 13, pageReturnSprites, button -> this.onPageReturn()));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.pose().pushPose();
        guiGraphics.blit(BOOK_TEXTURE, this.width / 2 - 140, 20, 280, 179, 0, 0, 280, 179, 512, 512);
        guiGraphics.pose().popPose();
    }

    protected  void onPageBackward() {
        SilentGear.LOGGER.debug("onPageBackward");
        if (this.leftPageIndex > 1) {
            this.leftPageIndex -= 2;
            this.rebuildWidgets();
        }
    }

    protected void onPageForward() {
        SilentGear.LOGGER.debug("onPageForward");
        if (this.leftPageIndex < this.pages.size() - 2) {
            this.leftPageIndex += 2;
            this.rebuildWidgets();
        }
    }

    protected void onPageReturn() {
        SilentGear.LOGGER.debug("onPageReturn");
        if (this.previousScreen != null) {
            Minecraft.getInstance().setScreen(this.previousScreen);
        } else {
            this.onClose();
        }
    }

    public record ComponentAccess(AbstractMaterialBookScreen screen) {
        public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
            return this.screen.addRenderableWidget(widget);
        }

        public <T extends Renderable> T addRenderableOnly(T renderable) {
            return this.screen.addRenderableOnly(renderable);
        }

        public <T extends GuiEventListener & NarratableEntry> T addWidget(T listener) {
            return this.screen.addWidget(listener);
        }
    }
}
