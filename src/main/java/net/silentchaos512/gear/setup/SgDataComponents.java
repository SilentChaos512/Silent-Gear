package net.silentchaos512.gear.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.core.component.TraitAddedEnchantments;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SgDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(SilentGear.MOD_ID);

    public static final Supplier<DataComponentType<GearConstructionData>> GEAR_CONSTRUCTION = REGISTRAR.registerComponentType(
            "construction",
            builder -> builder
                    .persistent(GearConstructionData.CODEC)
                    .networkSynchronized(GearConstructionData.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<GearPropertiesData>> GEAR_PROPERTIES = REGISTRAR.registerComponentType(
            "properties",
            builder -> builder
                    .networkSynchronized(GearPropertiesData.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<String>> GEAR_MODEL_KEY = REGISTRAR.registerComponentType(
            "model_key",
            builder -> builder
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );
    public static final Supplier<DataComponentType<MaterialGrade>> MATERIAL_GRADE = REGISTRAR.registerComponentType(
            "grade",
            builder -> builder
                    .persistent(MaterialGrade.CODEC)
                    .networkSynchronized(MaterialGrade.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<MaterialInstance>> MATERIAL_SINGLE = REGISTRAR.registerComponentType(
            "material",
            builder -> builder
                    .persistent(MaterialInstance.CODEC)
                    .networkSynchronized(MaterialInstance.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<List<MaterialInstance>>> MATERIAL_LIST = REGISTRAR.registerComponentType(
            "material_list",
            builder -> builder
                    .persistent(Codec.list(MaterialInstance.CODEC))
                    .networkSynchronized(MaterialInstance.STREAM_CODEC.apply(ByteBufCodecs.list()))
    );
    public static final Supplier<DataComponentType<PartType>> PART_TYPE = REGISTRAR.registerComponentType(
            "part_type",
            builder -> builder
                    .persistent(PartType.CODEC)
                    .networkSynchronized(PartType.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<Integer>> STARCHARGED_LEVEL = REGISTRAR.registerComponentType(
            "starcharged_level",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );
    public static final Supplier<DataComponentType<TraitAddedEnchantments>> TRAIT_ADDED_ENCHANTMENTS = REGISTRAR.registerComponentType(
            "trait_added_enchantments",
            builder -> builder
                    .persistent(TraitAddedEnchantments.CODEC)
                    .networkSynchronized(TraitAddedEnchantments.STREAM_CODEC)
    );
    /**
     * A map of MaterialInstance to Float, encoded as a map of DataResource<Material> to Float.
     * Used by repair kits.
     */
    public static final Supplier<DataComponentType<Map<MaterialInstance, Float>>> MATERIAL_STORAGE = REGISTRAR.registerComponentType(
            "material_storage",
            builder -> builder
                    .persistent(
                            Codec.unboundedMap(DataResource.MATERIAL_CODEC, Codec.FLOAT)
                                    .xmap(
                                            map -> {
                                                var result = new HashMap<MaterialInstance, Float>();
                                                map.forEach((material, value) -> result.put(MaterialInstance.of(material), value));
                                                return result;
                                            },
                                            map -> {
                                                var result = new HashMap<DataResource<Material>, Float>();
                                                map.forEach((material, value) -> result.put(DataResource.material(material), value));
                                                return result;
                                            }
                                    )
                    )
                    .networkSynchronized(
                            StreamCodec.of(
                                    (buf, map) -> {
                                        buf.writeVarInt(map.size());
                                        map.forEach((material, value) -> {
                                            DataResource.MATERIAL_STREAM_CODEC.encode(buf, DataResource.material(material));
                                            buf.writeFloat(value);
                                        });
                                    },
                                    buf -> {
                                        var result = new HashMap<MaterialInstance, Float>();
                                        int size = buf.readVarInt();
                                        for (int i = 0; i < size; ++i) {
                                            var material = MaterialInstance.of(DataResource.MATERIAL_STREAM_CODEC.decode(buf));
                                            var value = buf.readFloat();
                                            result.put(material, value);
                                        }
                                        return result;
                                    }
                            )
                    )
    );
}
