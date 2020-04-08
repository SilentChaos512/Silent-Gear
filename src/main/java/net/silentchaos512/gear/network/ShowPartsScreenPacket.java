package net.silentchaos512.gear.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.client.gui.GuiItemParts;

import java.util.function.Supplier;

public class ShowPartsScreenPacket {
    public void handle(Supplier<NetworkEvent.Context> context) {
        Minecraft.getInstance().displayGuiScreen(new GuiItemParts(new TranslationTextComponent("gui.silentgear.parts")));
        context.get().setPacketHandled(true);
    }
}
