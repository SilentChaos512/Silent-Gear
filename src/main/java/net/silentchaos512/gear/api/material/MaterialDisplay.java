package net.silentchaos512.gear.api.material;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.utils.Color;
import net.silentchaos512.utils.EnumUtils;

public class MaterialDisplay implements IMaterialDisplay {
    public static final MaterialDisplay DEFAULT = new MaterialDisplay();

    private int color = Color.VALUE_WHITE;
    private int armorColor = Color.VALUE_WHITE;
    private PartTextureType texture = PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT;

    @Override
    public PartTextureType getTexture() {
        return texture;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public int getArmorColor() {
        return armorColor;
    }

    public static MaterialDisplay deserialize(JsonObject json, IMaterialDisplay defaultProps) {
        MaterialDisplay props = new MaterialDisplay();

        props.color = loadColor(json, defaultProps.getColor(), defaultProps.getColor(), "color", "normal_color");
        props.armorColor = loadColor(json, defaultProps.getArmorColor(), props.color, "armor_color");

        props.texture = EnumUtils.byName(JSONUtils.getString(json, "texture", ""), props.texture);

        return props;
    }

    private static int loadColor(JsonObject json, int defaultValue, int fallback, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                return Color.from(json, key, defaultValue).getColor();
            }
        }
        return fallback;
    }

    public static MaterialDisplay read(PacketBuffer buffer) {
        MaterialDisplay props = new MaterialDisplay();
        props.color = buffer.readVarInt();
        props.armorColor = buffer.readVarInt();
        props.texture = buffer.readEnumValue(PartTextureType.class);
        return props;
    }

    public static void write(PacketBuffer buffer, MaterialDisplay props) {
        buffer.writeVarInt(props.color);
        buffer.writeVarInt(props.armorColor);
        buffer.writeEnumValue(props.texture);
    }

    @Override
    public String toString() {
        return "MaterialDisplay{" +
                "color=" + Color.format(color) +
                ", armorColor=" + Color.format(armorColor) +
                ", texture=" + texture +
                '}';
    }
}
