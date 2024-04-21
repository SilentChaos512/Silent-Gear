package net.silentchaos512.gear.api.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.part.PartTextureSet;
import net.silentchaos512.lib.util.Color;

import java.util.*;
import java.util.stream.Collectors;

public class MaterialLayerList implements IMaterialLayerList {
    public static final MaterialLayerList DEFAULT = new MaterialLayerList();

    private final List<MaterialLayer> layers;

    public MaterialLayerList() {
        this.layers = new ArrayList<>();
    }

    public MaterialLayerList(PartType partType, PartTextureSet texture, int color) {
        this(texture.getLayers(partType).stream()
                .map(PartTextures::getTexture)
                .map(tex -> {
                    int c = tex.equals(SilentGear.getId("_highlight")) ? Color.VALUE_WHITE : color;
                    return new MaterialLayer(tex, partType, c, false);
                })
                .collect(Collectors.toList()));
    }

    public MaterialLayerList(MaterialLayer... layers) {
        this(Arrays.asList(layers));
    }

    public MaterialLayerList(List<MaterialLayer> layers) {
        this.layers = new ArrayList<>(layers);
    }

    @Override
    public List<MaterialLayer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    public JsonElement serialize() {
        JsonArray jsonLayers = new JsonArray();
        this.layers.forEach(layer -> jsonLayers.add(layer.serialize()));
        return jsonLayers;
    }

    public static MaterialLayerList deserialize(PartGearKey key, JsonElement json, IMaterialLayerList defaultProps) {
        MaterialLayerList props = new MaterialLayerList();

        JsonArray jsonLayers = json.getAsJsonArray();
        if (jsonLayers != null) {
            for (JsonElement je : jsonLayers) {
                props.layers.add(MaterialLayer.deserialize(key, je));
            }
        }

        return props;
    }

    public static MaterialLayerList read(FriendlyByteBuf buffer) {
        MaterialLayerList props = new MaterialLayerList();

        int layerCount = buffer.readByte();
        for (int i = 0; i < layerCount; ++i) {
            props.layers.add(MaterialLayer.read(buffer));
        }

        return props;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(this.layers.size());
        this.layers.forEach(layer -> layer.write(buffer));
    }

    @Override
    public String toString() {
        return "MaterialLayerList{" +
                "layers=" + layers +
                '}';
    }

    @Override
    public Iterator<MaterialLayer> iterator() {
        return layers.iterator();
    }
}
