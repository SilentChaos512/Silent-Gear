package net.silentchaos512.gear.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class LabelButton extends LabelWidget {
    private final LabelButton.OnPress onPress;

    public LabelButton(Component message, Font font, LabelButton.OnPress onPress) {
        this(0, 0, font.width(message.getVisualOrderText()), 9, message, font, onPress);
    }

    public LabelButton(int width, int height, Component message, Font font, LabelButton.OnPress onPress) {
        this(0, 0, width, height, message, font, onPress);
    }

    public LabelButton(int x, int y, int width, int height, Component message, Font font, LabelButton.OnPress onPress) {
        super(x, y, width, height, message, font);
        this.onPress = onPress;
        this.active = true;
    }

    private void onPress(InputWithModifiers input) {
        this.onPress.onPress(this);
    }

    @Override
    public void onClick(MouseButtonEvent input, boolean b) {
        this.onPress(input);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (!this.active || !this.visible) {
            return false;
        } else if (input.isSelection()) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress(input);
            return true;
        } else {
            return false;
        }
    }

    public interface OnPress {
        void onPress(LabelButton button);
    }
}
