package net.silentchaos512.gear.parts;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IPartDisplay;
import net.silentchaos512.utils.Color;
import net.silentchaos512.utils.EnumUtils;

public final class PartDisplay implements IPartDisplay {
    public static final PartDisplay DEFAULT = new PartDisplay();

    private String textureDomain = SilentGear.MOD_ID;
    private String textureSuffix = "";
    private String armorTexture = "";
    private int normalColor = Color.VALUE_WHITE;
    private int brokenColor = Color.VALUE_WHITE;
    private int fallbackColor = Color.VALUE_WHITE;
    private int armorColor = Color.VALUE_WHITE;
    private boolean highlight = false;
    private PartTextureType liteTexture = PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT;

    @Override
    public String getTextureDomain() {
        return textureDomain;
    }

    @Override
    public String getTextureSuffix() {
        return textureSuffix;
    }

    @Override
    public String getArmorTexturePrefix() {
        return armorTexture;
    }

    @Override
    public int getNormalColor() {
        return normalColor;
    }

    @Override
    public int getBrokenColor() {
        return brokenColor;
    }

    @Override
    public int getFallbackColor() {
        return fallbackColor;
    }

    @Override
    public int getArmorColor() {
        return armorColor;
    }

    @Override
    public boolean hasHighlight() {
        return highlight;
    }

    @Override
    public PartTextureType getLiteTexture() {
        return liteTexture;
    }

    public static PartDisplay from(JsonObject json, IPartDisplay defaultProps) {
        PartDisplay props = new PartDisplay();

        props.textureDomain = JSONUtils.getString(json, "texture_domain", defaultProps.getTextureDomain());
        props.textureSuffix = JSONUtils.getString(json, "texture_suffix", defaultProps.getTextureSuffix());
        props.armorTexture = JSONUtils.getString(json, "armor_texture", props.textureSuffix);

        props.normalColor = loadColor(json, defaultProps.getNormalColor(), defaultProps.getNormalColor(), "normal_color", "texture_color");
        props.brokenColor = loadColor(json, defaultProps.getBrokenColor(), props.normalColor, "broken_color");
        props.fallbackColor = loadColor(json, defaultProps.getFallbackColor(), props.brokenColor, "fallback_color");
        props.armorColor = loadColor(json, defaultProps.getArmorColor(), props.fallbackColor, "armor_color");

        props.highlight = JSONUtils.getBoolean(json, "highlight", props.highlight);
        props.liteTexture = EnumUtils.byName(JSONUtils.getString(json, "lite_texture", ""), props.liteTexture);

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

    public static PartDisplay read(PacketBuffer buffer) {
        PartDisplay display = new PartDisplay();
        display.textureDomain = buffer.readString(255);
        display.textureSuffix = buffer.readString(32676);
        display.armorTexture = buffer.readString(32676);
        display.normalColor = buffer.readVarInt();
        display.brokenColor = buffer.readVarInt();
        display.fallbackColor = buffer.readVarInt();
        display.armorColor = buffer.readVarInt();
        display.highlight = buffer.readBoolean();
        display.liteTexture = EnumUtils.byOrdinal(buffer.readByte(), PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT);
        return display;
    }

    public static void write(PacketBuffer buffer, PartDisplay display) {
        buffer.writeString(display.textureDomain);
        buffer.writeString(display.textureSuffix);
        buffer.writeString(display.armorTexture);
        buffer.writeVarInt(display.normalColor);
        buffer.writeVarInt(display.brokenColor);
        buffer.writeVarInt(display.fallbackColor);
        buffer.writeVarInt(display.armorColor);
        buffer.writeBoolean(display.highlight);
        buffer.writeByte(display.liteTexture.getIndex());
    }

    @Override
    public String toString() {
        return "PartDisplay{" +
                "textureDomain='" + textureDomain + '\'' +
                ", textureSuffix='" + textureSuffix + '\'' +
                ", armorTexture='" + armorTexture + '\'' +
                ", normalColor=" + Integer.toHexString(normalColor) +
                ", brokenColor=" + Integer.toHexString(brokenColor) +
                ", fallbackColor=" + Integer.toHexString(fallbackColor) +
                ", armorColor=" + Integer.toHexString(armorColor) +
                ", highlight=" + highlight +
                '}';
    }
}
