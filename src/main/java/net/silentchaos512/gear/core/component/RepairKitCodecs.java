package net.silentchaos512.gear.core.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.HashMap;
import java.util.Map;

public class RepairKitCodecs {
    /**
     * Old codec used by repair kit material storage. It reduces materials to just their ID, which causes a lot of
     * important data to be lost. This codec is retained just to allow old repair kits to load.
     */
    private static final Codec<Map<MaterialInstance, Float>> OLD_MATERIAL_STORAGE_CODEC =
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
                    );

    /**
     * Stores the materials inside a repair kit using a list of  {@link PartialMaterialStorage}. However, the codec
     * still returns a map in order to be compatible with existing code.
     */
    private static final Codec<Map<MaterialInstance, Float>> NEW_MATERIAL_STORAGE_CODEC =
            Codec.list(PartialMaterialStorage.CODEC)
                    .xmap(
                            list -> {
                                var builder = ImmutableMap.<MaterialInstance, Float>builder();
                                for (PartialMaterialStorage partialMaterialStorage : list) {
                                    builder.put(partialMaterialStorage.material(), partialMaterialStorage.amount());
                                }
                                return builder.build();
                            },
                            map -> {
                                var builder = ImmutableList.<PartialMaterialStorage>builder();
                                for (Map.Entry<MaterialInstance, Float> entry : map.entrySet()) {
                                    builder.add(new PartialMaterialStorage(entry.getKey(), entry.getValue()));
                                }
                                return builder.build();
                            }
                    );

    public static final Codec<Map<MaterialInstance, Float>> MATERIAL_STORAGE_CODEC =
            Codec.withAlternative(NEW_MATERIAL_STORAGE_CODEC, OLD_MATERIAL_STORAGE_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Map<MaterialInstance, Float>> MATERIAL_STORAGE_STREAM_CODEC =
            StreamCodec.of(
                    (buf, map) -> {
                        buf.writeVarInt(map.size());
                        map.forEach((material, value) -> {
                            MaterialInstance.STREAM_CODEC.encode(buf, material);
                            buf.writeFloat(value);
                        });
                    },
                    buf -> {
                        var result = new HashMap<MaterialInstance, Float>();
                        int size = buf.readVarInt();
                        for (int i = 0; i < size; ++i) {
                            var material = MaterialInstance.STREAM_CODEC.decode(buf);
                            var value = buf.readFloat();
                            result.put(material, value);
                        }
                        return result;
                    }
            );
}
