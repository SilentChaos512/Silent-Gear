package net.silentchaos512.gear.api.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.part.PartTextureSet;
import net.silentchaos512.utils.Color;

import java.util.*;
import java.util.stream.Collectors;

public class MaterialLayerList implements IMaterialLayerList {
    public static final MaterialLayerList DEFAULT = new MaterialLayerList();

    private final List<MaterialLayer> layers;
    private PartTextureSet oldTextureType = PartTextureSet.ABSENT;

    public MaterialLayerList() {
        this.layers = Collections.emptyList();
    }

    public MaterialLayerList(PartType partType, PartTextureSet texture, int color) {
        this(texture.getLayers(partType).stream()
                .map(PartTextures::getTexture)
                .map(tex -> {
                    int c = tex.equals(SilentGear.getId("_highlight")) ? Color.VALUE_WHITE : color;
                    return new MaterialLayer(tex, c);
                })
                .collect(Collectors.toList()));
        this.oldTextureType = texture;
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

    @Deprecated
    @Override
    public PartTextureSet getTexture() {
        return oldTextureType;
    }

    @Override
    public int getPrimaryColor() {
        if (!layers.isEmpty()) {
            return layers.get(0).getColor();
        }
        return Color.VALUE_WHITE;
    }

    public JsonElement serialize() {
        JsonArray jsonLayers = new JsonArray();
        this.layers.forEach(layer -> jsonLayers.add(layer.serialize()));
        return jsonLayers;
    }

    public static MaterialLayerList deserialize(JsonObject json) {
        return deserialize(json.getAsJsonArray("types"), DEFAULT);
    }

    public static MaterialLayerList deserialize(JsonElement json, IMaterialLayerList defaultProps) {
        MaterialLayerList props = new MaterialLayerList();

        JsonArray jsonLayers = json.getAsJsonArray();
        if (jsonLayers != null) {
            for (JsonElement je : jsonLayers) {
                props.layers.add(MaterialLayer.deserialize(je));
            }
        }

        return props;
    }

    public static MaterialLayerList read(PacketBuffer buffer) {
        MaterialLayerList props = new MaterialLayerList();

        int layerCount = buffer.readByte();
        for (int i = 0; i < layerCount; ++i) {
            props.layers.add(MaterialLayer.read(buffer));
        }

        return props;
    }

    public void write(PacketBuffer buffer) {
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
