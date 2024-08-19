package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartSerializer;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.Serializer;

public final class PartSerializers {
    public static final Codec<GearPart> DISPATCH_CODEC = SgRegistries.PART_SERIALIZER.byNameCodec()
            .dispatch(
                    GearPart::getSerializer,
                    Serializer::codec
            );

    public static final DeferredRegister<PartSerializer<?>> REGISTRAR = DeferredRegister.create(SgRegistries.PART_SERIALIZER_KEY, SilentGear.MOD_ID);

    public static final DeferredHolder<PartSerializer<?>, CoreGearPart.Serializer> CORE = REGISTRAR.register("core", CoreGearPart.Serializer::new);
    public static final DeferredHolder<PartSerializer<?>, UpgradeGearPart.Serializer> UPGRADE = REGISTRAR.register("upgrade", UpgradeGearPart.Serializer::new);
}
