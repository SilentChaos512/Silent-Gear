package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.material.MaterialSerializer;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Supplier;

public final class MaterialSerializers {
    @SuppressWarnings("RedundantCast") // Fails to build without casting the codec
    public static final Codec<Material> DISPATCH_CODEC = SgRegistries.MATERIAL_SERIALIZER.byNameCodec()
            .dispatch(
                    Material::getSerializer,
                    materialSerializer -> (MapCodec<? extends Material>) materialSerializer.codec()
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, Material> DISPATCH_STREAM_CODEC =
            ByteBufCodecs.registry(SgRegistries.MATERIAL_SERIALIZER_KEY)
                    .dispatch(Material::getSerializer, MaterialSerializer::streamCodec);

    public static final DeferredRegister<MaterialSerializer<?>> REGISTRAR = DeferredRegister.create(SgRegistries.MATERIAL_SERIALIZER, SilentGear.MOD_ID);

    public static final Supplier<SimpleMaterial.Serializer> SIMPLE = register(
            "simple",
            SimpleMaterial.Serializer::new
    );
    public static final Supplier<CompoundMaterial.Serializer> COMPOUND = register(
            "compound",
            CompoundMaterial.Serializer::new
    );
    public static final Supplier<CustomCompoundMaterial.Serializer> CUSTOM_COMPOUND = register(
            "custom_compound",
            CustomCompoundMaterial.Serializer::new
    );
    public static final Supplier<ProcessedMaterial.Serializer> PROCESSED = register(
            "processed",
            ProcessedMaterial.Serializer::new
    );

    public static <T extends Material, S extends MaterialSerializer<T>> Supplier<S> register(String name, Supplier<S> serializer) {
        return REGISTRAR.register(name, serializer);
    }
}
