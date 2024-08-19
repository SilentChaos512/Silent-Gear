package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.material.MaterialSerializer;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.util.Serializer;

import java.util.function.Supplier;

public final class MaterialSerializers {
    public static final Codec<Material> DISPATCH_CODEC = SgRegistries.MATERIAL_SERIALIZER.byNameCodec()
            .dispatch(
                    Material::getSerializer,
                    Serializer::codec
            );

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
