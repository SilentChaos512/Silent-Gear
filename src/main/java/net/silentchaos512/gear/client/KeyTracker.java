package net.silentchaos512.gear.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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
    public static final KeyMapping DISPLAY_STATS = createKeyBinding("displayStats", GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyMapping DISPLAY_TRAITS = createKeyBinding("displayTraits", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final KeyMapping DISPLAY_CONSTRUCTION = createKeyBinding("displayConstruction", GLFW.GLFW_KEY_LEFT_ALT);
    public static final KeyMapping OPEN_ITEM = createKeyBinding("openItem", GLFW.GLFW_KEY_X);
    public static final KeyMapping CYCLE_BACK = createKeyBinding("cycle.back", GLFW.GLFW_KEY_Z);
    public static final KeyMapping CYCLE_NEXT = createKeyBinding("cycle.next", GLFW.GLFW_KEY_C);

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
    private static KeyMapping createKeyBinding(String description, int key) {
        return new KeyMapping(
                "key.silentgear." + description,
                KeyConflictContext.GUI,
                InputConstants.Type.KEYSYM,
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
        if (event.getAction() == GLFW.GLFW_RELEASE && (event.getKey() == DISPLAY_STATS.getKey().getValue())) {
            materialCycleCount = 0;
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == CYCLE_NEXT.getKey().getValue()) {
            if (isDisplayStatsDown()) {
                ++materialCycleCount;
            }
            ItemStack hovered = getHoveredItem();
            if (!hovered.isEmpty()) {
                Network.channel.sendToServer(new KeyPressOnItemPacket(KeyPressOnItemPacket.Type.CYCLE_NEXT, getHoveredSlot()));
            }
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == CYCLE_BACK.getKey().getValue()) {
            if (isDisplayStatsDown()) {
                --materialCycleCount;
            }
            ItemStack hovered = getHoveredItem();
            if (!hovered.isEmpty()) {
                Network.channel.sendToServer(new KeyPressOnItemPacket(KeyPressOnItemPacket.Type.CYCLE_BACK, getHoveredSlot()));
            }
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == OPEN_ITEM.getKey().getValue()) {
            ItemStack hovered = getHoveredItem();
            if (!hovered.isEmpty()) {
                Network.channel.sendToServer(new KeyPressOnItemPacket(KeyPressOnItemPacket.Type.OPEN_ITEM, getHoveredSlot()));
            }
        }
    }

    private static ItemStack getHoveredItem() {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof AbstractContainerScreen<?>) {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) currentScreen;
            Slot slot = containerScreen.getSlotUnderMouse();
            if (slot != null) {
                return slot.getItem();
            }
        }
        return ItemStack.EMPTY;
    }

    private static int getHoveredSlot() {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof AbstractContainerScreen<?>) {
            AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) currentScreen;
            Slot slot = containerScreen.getSlotUnderMouse();
            if (slot != null) {
                return slot.index;
            }
        }
        return -1;
    }

    public static boolean isDisplayStatsDown() {
        int code = DISPLAY_STATS.getKey().getValue();
        if (code == GLFW.GLFW_KEY_LEFT_CONTROL || code == GLFW.GLFW_KEY_RIGHT_CONTROL || DISPLAY_STATS.isUnbound()) {
            // Maintain old behavior of checking both ctrl keys
            return isControlDown();
        }
        return DISPLAY_STATS.isDown();
    }

    public static boolean isDisplayConstructionDown() {
        int code = DISPLAY_CONSTRUCTION.getKey().getValue();
        if (code == GLFW.GLFW_KEY_LEFT_ALT || code == GLFW.GLFW_KEY_RIGHT_ALT || DISPLAY_CONSTRUCTION.isUnbound()) {
            return isAltDown();
        }
        return DISPLAY_CONSTRUCTION.isDown();
    }

    public static boolean isDisplayTraitsDown() {
        int code = DISPLAY_TRAITS.getKey().getValue();
        if (code == GLFW.GLFW_KEY_LEFT_SHIFT || code == GLFW.GLFW_KEY_RIGHT_SHIFT || DISPLAY_TRAITS.isUnbound()) {
            return isShiftDown();
        }
        return DISPLAY_TRAITS.isDown();
    }

    public static boolean isShiftDown() {
        long h = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(h, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isControlDown() {
        long h = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(h, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isAltDown() {
        long h = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(h, GLFW.GLFW_KEY_LEFT_ALT) || InputConstants.isKeyDown(h, GLFW.GLFW_KEY_RIGHT_ALT);
    }
}
