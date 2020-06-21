package net.silentchaos512.gear.api.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.utils.Color;
import net.silentchaos512.utils.EnumUtils;

import java.util.Locale;

public class MaterialDisplay implements IMaterialDisplay {
    public static final MaterialDisplay DEFAULT = new MaterialDisplay();

    private int color = Color.VALUE_WHITE;
    private int armorColor = Color.VALUE_WHITE;
    private PartTextureType texture = PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT;

    public MaterialDisplay() {
    }

    public MaterialDisplay(PartTextureType texture, int color, int armorColor) {
        this.texture = texture;
        this.color = color;
        this.armorColor = armorColor;
    }

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

    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("texture", this.texture.name().toLowerCase(Locale.ROOT));
        json.addProperty("color", Color.format(this.color & 0xFFFFFF));
        if ((this.armorColor & 0xFFFFFF) != Color.VALUE_WHITE) {
            json.addProperty("armor_color", Color.format(this.armorColor & 0xFFFFFF));
        }
        return json;
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

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.color);
        buffer.writeVarInt(this.armorColor);
        buffer.writeEnumValue(this.texture);
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
