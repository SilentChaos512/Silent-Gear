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

    private String textureDomain;
    private String textureSuffix;
    private int normalColor;
    private int brokenColor;
    private int fallbackColor;
    private boolean highlight;
    private PartTextureType liteTexture = PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT;

    PartDisplay() {
        textureDomain = SilentGear.MOD_ID;
        textureSuffix = "";
        normalColor = brokenColor = fallbackColor = Color.VALUE_WHITE;
    }

    public PartDisplay(String textureDomain, String textureSuffix) {
        this(textureDomain, textureSuffix, Color.VALUE_WHITE, Color.VALUE_WHITE, Color.VALUE_WHITE);
    }

    public PartDisplay(String textureDomain, String textureSuffix, int normalColor, int brokenColor, int fallbackColor) {
        this.textureDomain = textureDomain;
        this.textureSuffix = textureSuffix;
        this.normalColor = normalColor;
        this.brokenColor = brokenColor;
        this.fallbackColor = fallbackColor;
    }

    @Override
    public String getTextureDomain() {
        return textureDomain;
    }

    @Override
    public String getTextureSuffix() {
        return textureSuffix;
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
    public boolean hasHighlight() {
        return highlight;
    }

    @Override
    public PartTextureType getLiteTexture() {
        return liteTexture;
    }

    public static PartDisplay from(JsonObject json, IPartDisplay defaultProps) {
        String textureDomain = JSONUtils.getString(json, "texture_domain", defaultProps.getTextureDomain());
        String textureSuffix = JSONUtils.getString(json, "texture_suffix", defaultProps.getTextureSuffix());

        int normalColor = loadColor(json, defaultProps.getNormalColor(), defaultProps.getNormalColor(), "normal_color", "texture_color");
        int brokenColor = loadColor(json, defaultProps.getBrokenColor(), normalColor, "broken_color");
        int fallbackColor = loadColor(json, defaultProps.getFallbackColor(), brokenColor, "fallback_color");

        PartDisplay props = new PartDisplay(textureDomain, textureSuffix, normalColor, brokenColor, fallbackColor);

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
        display.normalColor = buffer.readVarInt();
        display.brokenColor = buffer.readVarInt();
        display.fallbackColor = buffer.readVarInt();
        display.highlight = buffer.readBoolean();
        display.liteTexture = EnumUtils.byOrdinal(buffer.readByte(), PartTextureType.HIGH_CONTRAST_WITH_HIGHLIGHT);
        return display;
    }

    public static void write(PacketBuffer buffer, PartDisplay display) {
        buffer.writeString(display.textureDomain);
        buffer.writeString(display.textureSuffix);
        buffer.writeVarInt(display.normalColor);
        buffer.writeVarInt(display.brokenColor);
        buffer.writeVarInt(display.fallbackColor);
        buffer.writeBoolean(display.highlight);
        buffer.writeByte(display.liteTexture.getIndex());
    }

    @Override
    public String toString() {
        return "PartDisplay{" +
                "textureDomain='" + textureDomain + '\'' +
                ", textureSuffix='" + textureSuffix + '\'' +
                ", normalColor=" + Integer.toHexString(normalColor) +
                ", brokenColor=" + Integer.toHexString(brokenColor) +
                ", fallbackColor=" + Integer.toHexString(fallbackColor) +
                ", highlight=" + highlight +
                '}';
    }
}
