package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.SilentGear;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID)
public class KeyTracker {
    public static final KeyBinding DISPLAY_STATS = createKeyBinding("displayStats", GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyBinding CYCLE_MATERIAL_INFO = createKeyBinding("cycleMaterialInfo", GLFW.GLFW_KEY_C);

    private static int materialCycleCount = 0;

    public static void register(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(CYCLE_MATERIAL_INFO);
        ClientRegistry.registerKeyBinding(DISPLAY_STATS);
    }

    @Nonnull
    private static KeyBinding createKeyBinding(String description, int key) {
        return new KeyBinding(
                "key.silentgear." + description,
                KeyConflictContext.GUI,
                InputMappings.Type.KEYSYM,
                key,
                "key.categories.silentgear"
        );
    }

    public static int getMaterialCycleCount() {
        return materialCycleCount;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_RELEASE && (event.getKey() == DISPLAY_STATS.getKey().getKeyCode())) {
            materialCycleCount = 0;
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == CYCLE_MATERIAL_INFO.getKey().getKeyCode() && isDisplayStatsDown()) {
            ++materialCycleCount;
        }
    }

    public static boolean isDisplayStatsDown() {
        if (DISPLAY_STATS.getKey().getKeyCode() == GLFW.GLFW_KEY_LEFT_CONTROL) {
            // Maintain old behavior of checking both ctrl keys
            return isControlDown();
        }
        return DISPLAY_STATS.isKeyDown();
    }

    public static boolean isShiftDown() {
        long h = Minecraft.getInstance().getMainWindow().getHandle();
        return InputMappings.isKeyDown(h, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isControlDown() {
        long h = Minecraft.getInstance().getMainWindow().getHandle();
        return InputMappings.isKeyDown(h, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isAltDown() {
        long h = Minecraft.getInstance().getMainWindow().getHandle();
        return InputMappings.isKeyDown(h, GLFW.GLFW_KEY_LEFT_ALT) || InputMappings.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_ALT);
    }
}
