package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.network.KeyPressOnItemPacket;
import net.silentchaos512.gear.network.Network;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = SilentGear.MOD_ID, value = Dist.CLIENT)
public class KeyTracker {
    public static final KeyBinding DISPLAY_STATS = createKeyBinding("displayStats", GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyBinding DISPLAY_TRAITS = createKeyBinding("displayTraits", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final KeyBinding DISPLAY_CONSTRUCTION = createKeyBinding("displayConstruction", GLFW.GLFW_KEY_LEFT_ALT);
    public static final KeyBinding OPEN_ITEM = createKeyBinding("openItem", GLFW.GLFW_KEY_X);
    public static final KeyBinding CYCLE_BACK = createKeyBinding("cycle.back", GLFW.GLFW_KEY_Z);
    public static final KeyBinding CYCLE_NEXT = createKeyBinding("cycle.next", GLFW.GLFW_KEY_C);

    private static int materialCycleCount = 0;

    public static void register(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(CYCLE_BACK);
        ClientRegistry.registerKeyBinding(CYCLE_NEXT);
        ClientRegistry.registerKeyBinding(DISPLAY_STATS);
        ClientRegistry.registerKeyBinding(DISPLAY_TRAITS);
        ClientRegistry.registerKeyBinding(DISPLAY_CONSTRUCTION);
        ClientRegistry.registerKeyBinding(OPEN_ITEM);
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

    public static int getMaterialCycleIndex(int total) {
        int i = materialCycleCount % total;
        return i < 0 ? i + total : i;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_RELEASE && (event.getKey() == DISPLAY_STATS.getKey().getKeyCode())) {
            materialCycleCount = 0;
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == CYCLE_NEXT.getKey().getKeyCode()) {
            if (isDisplayStatsDown()) {
                ++materialCycleCount;
            }
            ItemStack hovered = getHoveredItem();
            if (!hovered.isEmpty()) {
                Network.channel.sendToServer(new KeyPressOnItemPacket(KeyPressOnItemPacket.Type.CYCLE_NEXT, getHoveredSlot()));
            }
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == CYCLE_BACK.getKey().getKeyCode()) {
            if (isDisplayStatsDown()) {
                --materialCycleCount;
            }
            ItemStack hovered = getHoveredItem();
            if (!hovered.isEmpty()) {
                Network.channel.sendToServer(new KeyPressOnItemPacket(KeyPressOnItemPacket.Type.CYCLE_BACK, getHoveredSlot()));
            }
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == OPEN_ITEM.getKey().getKeyCode()) {
            ItemStack hovered = getHoveredItem();
            if (!hovered.isEmpty()) {
                Network.channel.sendToServer(new KeyPressOnItemPacket(KeyPressOnItemPacket.Type.OPEN_ITEM, getHoveredSlot()));
            }
        }
    }

    private static ItemStack getHoveredItem() {
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        if (currentScreen instanceof ContainerScreen<?>) {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>) currentScreen;
            Slot slot = containerScreen.getSlotUnderMouse();
            if (slot != null) {
                return slot.getStack();
            }
        }
        return ItemStack.EMPTY;
    }

    private static int getHoveredSlot() {
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        if (currentScreen instanceof ContainerScreen<?>) {
            ContainerScreen<?> containerScreen = (ContainerScreen<?>) currentScreen;
            Slot slot = containerScreen.getSlotUnderMouse();
            if (slot != null) {
                return slot.slotNumber;
            }
        }
        return -1;
    }

    public static boolean isDisplayStatsDown() {
        int code = DISPLAY_STATS.getKey().getKeyCode();
        if (code == GLFW.GLFW_KEY_LEFT_CONTROL || code == GLFW.GLFW_KEY_RIGHT_CONTROL || DISPLAY_STATS.isInvalid()) {
            // Maintain old behavior of checking both ctrl keys
            return isControlDown();
        }
        return DISPLAY_STATS.isKeyDown();
    }

    public static boolean isDisplayConstructionDown() {
        int code = DISPLAY_CONSTRUCTION.getKey().getKeyCode();
        if (code == GLFW.GLFW_KEY_LEFT_ALT || code == GLFW.GLFW_KEY_RIGHT_ALT || DISPLAY_CONSTRUCTION.isInvalid()) {
            return isAltDown();
        }
        return DISPLAY_CONSTRUCTION.isKeyDown();
    }

    public static boolean isDisplayTraitsDown() {
        int code = DISPLAY_TRAITS.getKey().getKeyCode();
        if (code == GLFW.GLFW_KEY_LEFT_SHIFT || code == GLFW.GLFW_KEY_RIGHT_SHIFT || DISPLAY_TRAITS.isInvalid()) {
            return isShiftDown();
        }
        return DISPLAY_TRAITS.isKeyDown();
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
