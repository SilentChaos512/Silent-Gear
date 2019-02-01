package net.silentchaos512.gear.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

public class KeyTracker {
    public static KeyTracker INSTANCE = new KeyTracker();

    private KeyBinding keyTest;

    public KeyTracker() {
//        keyTest = createBinding("Test", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_BACKSLASH);
    }

//    @Override
//    public void onKeyInput(KeyInputEvent event) {
//        // TODO
//    }

    public static boolean isShiftDown() {
        return InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isControlDown() {
        return InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isAltDown() {
        return InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)
                || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }
}
