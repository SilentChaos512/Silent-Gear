package net.silentchaos512.gear.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class LoginPacket implements IntSupplier {
    private int loginIndex;

    public int getLoginIndex() {
        return loginIndex;
    }

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    @Override
    public int getAsInt() {
        return loginIndex;
    }

    public static class Reply extends LoginPacket {
        public void handle(Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
        }
    }
}
