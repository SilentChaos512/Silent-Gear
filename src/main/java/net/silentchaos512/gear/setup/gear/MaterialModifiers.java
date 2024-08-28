package net.silentchaos512.gear.setup.gear;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.gear.material.modifier.ChargedMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.GradeMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.StarchargedMaterialModifier;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgRegistries;

public class MaterialModifiers {
    public static final DeferredRegister<IMaterialModifierType<?>> REGISTRAR = DeferredRegister.create(SgRegistries.MATERIAL_MODIFIER_TYPE, SilentGear.MOD_ID);

    public static final DeferredHolder<IMaterialModifierType<?>, GradeMaterialModifier.Type> GRADE =
            REGISTRAR.register(
                    "grade",
                    GradeMaterialModifier.Type::new
            );
    public static final DeferredHolder<IMaterialModifierType<?>, ChargedMaterialModifier.Type<StarchargedMaterialModifier>> STARCHARGED =
            REGISTRAR.register(
                    "starcharged",
                    () -> new ChargedMaterialModifier.Type<>(
                            StarchargedMaterialModifier::new,
                            SgDataComponents.STARCHARGED_LEVEL
                    )
            );

    public static final Codec<IMaterialModifier> CODEC = SgRegistries.MATERIAL_MODIFIER_TYPE.byNameCodec()
            .dispatch("type", IMaterialModifier::getType, IMaterialModifierType::codec);

    public static final StreamCodec<RegistryFriendlyByteBuf, IMaterialModifier> STREAM_CODEC =
            ByteBufCodecs.registry(SgRegistries.MATERIAL_MODIFIER_TYPE_KEY)
                    .dispatch(IMaterialModifier::getType, IMaterialModifierType::streamCodec);
}
