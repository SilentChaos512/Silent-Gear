package net.silentchaos512.gear.api.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.network.PacketBuffer;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.utils.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialDisplay implements IMaterialDisplay {
    public static final MaterialDisplay DEFAULT = new MaterialDisplay();

    private final List<MaterialLayer> layers;
    private PartTextureType oldTextureType = PartTextureType.ABSENT;

    public MaterialDisplay() {
        this(PartType.MAIN, PartTextureType.ABSENT, Color.VALUE_WHITE);
    }

    public MaterialDisplay(PartType partType, PartTextureType texture, int color) {
        this(texture.getLayers(partType).stream()
                .map(tex -> {
                    int c = tex.equals(SilentGear.getId("_highlight")) ? Color.VALUE_WHITE : color;
                    return new MaterialLayer(tex, c);
                })
                .collect(Collectors.toList()));
        this.oldTextureType = texture;
    }

    public MaterialDisplay(MaterialLayer... layers) {
        this(Arrays.asList(layers));
    }

    public MaterialDisplay(List<MaterialLayer> layers) {
        this.layers = new ArrayList<>(layers);
    }

    @Override
    public List<MaterialLayer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    @Deprecated
    @Override
    public PartTextureType getTexture() {
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

    public static MaterialDisplay deserialize(JsonElement json, IMaterialDisplay defaultProps) {
        MaterialDisplay props = new MaterialDisplay();

        JsonArray jsonLayers = json.getAsJsonArray();
        if (jsonLayers != null) {
            for (JsonElement je : jsonLayers) {
                props.layers.add(MaterialLayer.deserialize(je));
            }
        }

        return props;
    }

    public static MaterialDisplay read(PacketBuffer buffer) {
        MaterialDisplay props = new MaterialDisplay();

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
        return "MaterialDisplay{" +
                "layers=" + layers +
                '}';
    }
}
