package net.silentchaos512.gear.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.lib.client.key.KeyTrackerSL;

public class KeyTracker extends KeyTrackerSL {

    public static KeyTracker INSTANCE = new KeyTracker();

    private KeyBinding keyTest;

    public KeyTracker() {
        super(SilentGear.MOD_NAME);
        keyTest = createBinding("Test", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_BACKSLASH);
    }

    @Override
    public void onKeyInput(KeyInputEvent event) {
        // TODO
    }
}
