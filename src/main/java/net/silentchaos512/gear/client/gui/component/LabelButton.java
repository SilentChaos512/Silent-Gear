package net.silentchaos512.gear.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.CommonInputs;
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

    private void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (CommonInputs.selected(keyCode)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        } else {
            return false;
        }
    }

    public interface OnPress {
        void onPress(LabelButton button);
    }
}
