package net.silentchaos512.gear.block;

import net.minecraft.world.MenuProvider;
import net.minecraft.network.FriendlyByteBuf;

public interface INamedContainerExtraData extends MenuProvider {
    void encodeExtraData(FriendlyByteBuf buffer);
}
