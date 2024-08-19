package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.HashMap;
import java.util.Map;

public class SimpleMaterial extends AbstractMaterial {
    public SimpleMaterial(
            DataResource<Material> parent,
            MaterialCraftingData crafting,
            MaterialDisplayData display,
            Map<PartType, GearPropertyMap> properties
    ) {
        super(parent, crafting, display, properties);
    }

    @Override
    public MaterialSerializer<?> getSerializer() {
        return MaterialSerializers.SIMPLE.get();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public boolean isAllowedInPart(MaterialInstance material, PartType partType) {
        return properties.containsKey(partType) || (getParent() != null && getParent().isAllowedInPart(material, partType));
    }

    @Override
    public String toString() {
        return "SimpleMaterial{" +
                "id=" + SgRegistries.MATERIAL.getKey(this) +
                '}';
    }

    public static class Serializer extends MaterialSerializer<SimpleMaterial> {
        public static final MapCodec<SimpleMaterial> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        DataResource.MATERIAL_CODEC.fieldOf("parent").forGetter(m -> m.parent),
                        MaterialCraftingData.CODEC.fieldOf("crafting").forGetter(m -> m.crafting),
                        MaterialDisplayData.CODEC.fieldOf("display").forGetter(m -> m.display),
                        Codec.unboundedMap(PartType.CODEC, GearPropertyMap.CODEC).fieldOf("properties").forGetter(m -> m.properties)
                ).apply(instance, SimpleMaterial::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleMaterial> STREAM_CODEC = StreamCodec.composite(
                DataResource.MATERIAL_STREAM_CODEC, m -> m.parent,
                MaterialCraftingData.STREAM_CODEC, m -> m.crafting,
                MaterialDisplayData.STREAM_CODEC, m -> m.display,
                ByteBufCodecs.map(
                        HashMap::new,
                        PartType.STREAM_CODEC,
                        GearPropertyMap.STREAM_CODEC
                ), m -> m.properties,
                SimpleMaterial::new
        );

        public Serializer() {
            super(CODEC, STREAM_CODEC);
        }
    }
}
