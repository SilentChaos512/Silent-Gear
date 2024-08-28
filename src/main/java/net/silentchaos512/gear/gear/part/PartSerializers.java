package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartSerializer;
import net.silentchaos512.gear.setup.SgRegistries;

public final class PartSerializers {
    @SuppressWarnings("RedundantCast") // Fails to build without casting the codec
    public static final Codec<GearPart> DISPATCH_CODEC = SgRegistries.PART_SERIALIZER.byNameCodec()
            .dispatch(
                    GearPart::getSerializer,
                    partSerializer -> (MapCodec<? extends GearPart>) partSerializer.codec()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, GearPart> DISPATCH_STREAM_CODEC =
            ByteBufCodecs.registry(SgRegistries.PART_SERIALIZER_KEY)
                    .dispatch(GearPart::getSerializer, PartSerializer::streamCodec);

    public static final DeferredRegister<PartSerializer<?>> REGISTRAR = DeferredRegister.create(SgRegistries.PART_SERIALIZER_KEY, SilentGear.MOD_ID);

    public static final DeferredHolder<PartSerializer<?>, CoreGearPart.Serializer> CORE = REGISTRAR.register("core", CoreGearPart.Serializer::new);
    public static final DeferredHolder<PartSerializer<?>, UpgradeGearPart.Serializer> UPGRADE = REGISTRAR.register("upgrade", UpgradeGearPart.Serializer::new);
}
