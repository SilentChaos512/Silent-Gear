package net.silentchaos512.gear.gear.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.item.ProcessedMaterialItem;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A material which has been modified in some way, such as pressing metals into sheets
 */
public class ProcessedMaterial extends AbstractMaterial {
    public ProcessedMaterial(DataResource<Material> parent, MaterialCraftingData crafting, MaterialDisplayData display, Map<PartType, GearPropertyMap> properties) {
        super(parent, crafting, display, properties);
    }

    public static MaterialInstance getBaseMaterial(MaterialInstance material) {
        var baseMaterial = ProcessedMaterialItem.getMaterial(material.getItem());
        if (baseMaterial == null) {
            return MaterialInstance.of(Const.Materials.EMPTY);
        }
        return baseMaterial;
    }

    @Override
    public MaterialSerializer<?> getSerializer() {
        return MaterialSerializers.PROCESSED.get();
    }

    @Override
    public Collection<IMaterialCategory> getCategories(MaterialInstance material) {
        Collection<IMaterialCategory> set = super.getCategories(material);
        MaterialInstance base = getBaseMaterial(material);
        set.addAll(base.getCategories());
        return set;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Set<PartType> getPartTypes(MaterialInstance material) {
        return Collections.singleton(PartTypes.MAIN.get());
    }

    @Override
    public int getColor(MaterialInstance material, PartType partType, GearType gearType) {
        return getBaseMaterial(material).getColor(gearType, partType);
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(MaterialInstance material, PartType partType, PropertyKey<T, V> key) {
        var ret = super.getPropertyModifiers(material, partType, key);
        MaterialInstance baseMaterial = getBaseMaterial(material);
        ret.addAll(baseMaterial.getPropertyModifiers(partType, key));
        return ret;
    }

    @Override
    public Collection<PropertyKey<?, ?>> getPropertyKeys(MaterialInstance material, PartType type) {
        var ret = new LinkedHashSet<>(super.getPropertyKeys(material, type));
        MaterialInstance baseMaterial = getBaseMaterial(material);
        ret.addAll(baseMaterial.get().getPropertyKeys(baseMaterial, type));
        return ret;
    }

    @Override
    public Collection<TraitInstance> getTraits(MaterialInstance material, PartGearKey partKey) {
        Collection<TraitInstance> ret = super.getTraits(material, partKey);
        MaterialInstance base = getBaseMaterial(material);
        ret.addAll(base.getTraits(partKey));
        return ret;
    }

    @Override
    public Component getBaseMaterialName(@Nullable MaterialInstance material, PartType partType) {
        if (material != null) {
            return getBaseMaterial(material).getDisplayName(partType).plainCopy();
        }
        return super.getBaseMaterialName(null, partType);
    }

    @Override
    public Component getDisplayName(@Nullable MaterialInstance material, PartType type) {
        if (material != null) {
            return material.getItem().getHoverName().plainCopy();
        }
        return super.getDisplayName(null, type);
    }

    @Override
    public int getNameColor(MaterialInstance material, PartType partType, GearType gearType) {
        MaterialInstance base = getBaseMaterial(material);
        return base.getNameColor(partType, gearType);
    }

    @Override
    public String getModelKey(MaterialInstance material) {
        MaterialInstance base = getBaseMaterial(material);
        return super.getModelKey(material) + "[" + base.getModelKey() + "]";
    }

    public static class Serializer extends MaterialSerializer<ProcessedMaterial> {
        public static final MapCodec<ProcessedMaterial> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        DataResource.MATERIAL_CODEC.fieldOf("parent").forGetter(m -> m.parent),
                        MaterialCraftingData.CODEC.fieldOf("crafting").forGetter(m -> m.crafting),
                        MaterialDisplayData.CODEC.fieldOf("display").forGetter(m -> m.display),
                        Codec.unboundedMap(PartType.CODEC, GearPropertyMap.CODEC).fieldOf("properties").forGetter(m -> m.properties)
                ).apply(instance, ProcessedMaterial::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ProcessedMaterial> STREAM_CODEC = StreamCodec.composite(
                DataResource.MATERIAL_STREAM_CODEC, m -> m.parent,
                MaterialCraftingData.STREAM_CODEC, m -> m.crafting,
                MaterialDisplayData.STREAM_CODEC, m -> m.display,
                ByteBufCodecs.map(
                        HashMap::new,
                        PartType.STREAM_CODEC,
                        GearPropertyMap.STREAM_CODEC
                ), m -> m.properties,
                ProcessedMaterial::new
        );

        public Serializer() {
            super(CODEC, STREAM_CODEC);
        }
    }
}
