package net.silentchaos512.gear.network;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.gear.util.TextUtil;

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
            mc.player.sendSystemMessage(TextUtil.translate("item", "guide_book.unimplemented1"));
            mc.player.sendSystemMessage(TextUtil.translate("item", "guide_book.check_wiki")
                    .append(Component.literal(WIKI_URL)
                            .withStyle(ChatFormatting.UNDERLINE)
                            .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                    WIKI_URL)))));
        }
    }
}
