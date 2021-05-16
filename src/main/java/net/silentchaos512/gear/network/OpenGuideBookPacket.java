package net.silentchaos512.gear.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGuideBookPacket {
    public static final String WIKI_URL = "https://github.com/SilentChaos512/Silent-Gear/wiki";

    public void handle(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isClient()) {
            context.get().enqueueWork(this::openBookScreen);
        }
        context.get().setPacketHandled(true);
    }

    private void openBookScreen() {
        Minecraft mc = Minecraft.getInstance();
//        mc.displayGuiScreen(new GuideBookScreen(new StringTextComponent("Guide Book Temp")));
        if (mc.player != null) {
            mc.player.sendMessage(new StringTextComponent("Guide book is work-in-progress (probably gonna take a long time...)"), Util.DUMMY_UUID);
            mc.player.sendMessage(new StringTextComponent("Check the wiki instead: ")
                    .append(new StringTextComponent(WIKI_URL)
                            .mergeStyle(TextFormatting.UNDERLINE)
                            .modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                    WIKI_URL)))), Util.DUMMY_UUID);
        }
    }
}
