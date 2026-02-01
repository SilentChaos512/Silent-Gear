package net.silentchaos512.gear.client.gui.book.page;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.book.AbstractMaterialBookScreen;
import net.silentchaos512.gear.client.gui.component.LabelButton;

public class Page {
    public void init(AbstractMaterialBookScreen.ComponentAccess access, Font font, int pageX, int pageY, int pageIndex) {
        access.addRenderableWidget(Button.builder(Component.literal("Hi!"), b -> {
        }).build());
        access.addRenderableOnly(new LabelButton(pageX, pageY, 80, font.lineHeight, Component.literal("Test" + (pageIndex + 1)), font, b -> {
        }));
    }
}
