package net.silentchaos512.gear.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.client.gui.GuiItemParts;

import java.util.function.Supplier;

public class ShowPartsScreenPacket {
    public void handle(Supplier<NetworkEvent.Context> context) {
        openScreen();
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen() {
        Minecraft.getInstance().displayGuiScreen(new GuiItemParts(new TranslationTextComponent("gui.silentgear.parts")));
    }
}
