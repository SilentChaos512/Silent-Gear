package net.silentchaos512.gear.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.gear.SilentGear;

// Temporary solution for Optifine crashes. Will likely spam the log, but no helping that.
public final class ModelErrorLogging {
    private ModelErrorLogging() {}

    public static void notifyOfException(Exception exception, String modelType) {
        if (ModList.get().isLoaded("optifine")) {
            // Optifine installed, assume anything and everything could be broken
            SilentGear.LOGGER.error("Rendering error while Optifine is installed. Test without Optifine before reporting.");

            //noinspection ConstantConditions
            if (Minecraft.getInstance() != null && Minecraft.getInstance().player != null) {
                LocalPlayer player = Minecraft.getInstance().player;
                player.sendSystemMessage(Component.literal(String.format("Error while rendering %s, please test without Optifine before reporting", modelType)));
            }
        }

        SilentGear.LOGGER.catching(exception);
    }
}
