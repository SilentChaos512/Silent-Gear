package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.material.MaterialCraftingData;
import net.silentchaos512.gear.api.material.MaterialDisplayData;
import net.silentchaos512.gear.api.material.MaterialSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.util.DataResource;

import java.util.HashMap;
import java.util.Map;

public class CustomCompoundMaterial extends AbstractMaterial {
    public CustomCompoundMaterial(DataResource<Material> parent, MaterialCraftingData crafting, MaterialDisplayData display, Map<PartType, GearPropertyMap> properties) {
        super(parent, crafting, display, properties);
    }

    @Override
    public MaterialSerializer<?> getSerializer() {
        return MaterialSerializers.CUSTOM_COMPOUND.get();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public static class Serializer extends MaterialSerializer<CustomCompoundMaterial> {
        public static final MapCodec<CustomCompoundMaterial> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        DataResource.MATERIAL_CODEC.fieldOf("parent").forGetter(m -> m.parent),
                        MaterialCraftingData.CODEC.fieldOf("crafting").forGetter(m -> m.crafting),
                        MaterialDisplayData.CODEC.fieldOf("display").forGetter(m -> m.display),
                        Codec.unboundedMap(PartType.CODEC, GearPropertyMap.CODEC).fieldOf("properties").forGetter(m -> m.properties)
                ).apply(instance, CustomCompoundMaterial::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CustomCompoundMaterial> STREAM_CODEC = StreamCodec.composite(
                DataResource.MATERIAL_STREAM_CODEC, m -> m.parent,
                MaterialCraftingData.STREAM_CODEC, m -> m.crafting,
                MaterialDisplayData.STREAM_CODEC, m -> m.display,
                ByteBufCodecs.map(
                        HashMap::new,
                        PartType.STREAM_CODEC,
                        GearPropertyMap.STREAM_CODEC
                ), m -> m.properties,
                CustomCompoundMaterial::new
        );

        public Serializer() {
            super(CODEC, STREAM_CODEC);
        }
    }
}
