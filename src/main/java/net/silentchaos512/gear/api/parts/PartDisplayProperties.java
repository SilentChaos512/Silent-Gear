/*
 * Silent Gear -- PartDisplayProperties
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.api.parts;

import com.google.common.primitives.UnsignedInts;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.util.JsonUtils;
import net.silentchaos512.lib.util.Color;

import java.util.regex.Pattern;

/**
 * Collection of values used to determine the texture and color of a part.
 *
 * @since 0.4.1
 */
@Getter
public final class PartDisplayProperties {
    public static final PartDisplayProperties DEFAULT = new PartDisplayProperties();

    private static final Pattern REGEX_TEXTURE_SUFFIX_REPLACE = Pattern.compile("[a-z]+_");

    String textureDomain;
    String textureSuffix;
    int textureColor;
    int brokenColor;
    int fallbackColor;

    private PartDisplayProperties() {
        textureDomain = textureSuffix = "";
        textureColor = brokenColor = fallbackColor = Color.VALUE_WHITE;
    }

    public PartDisplayProperties(String textureDomain, String textureSuffix) {
        this(textureDomain, textureSuffix, Color.VALUE_WHITE, Color.VALUE_WHITE, Color.VALUE_WHITE);
    }

    public PartDisplayProperties(String textureDomain, String textureSuffix, int textureColor, int brokenColor, int fallbackColor) {
        this.textureDomain = textureDomain;
        this.textureSuffix = textureSuffix;
        this.textureColor = textureColor;
        this.brokenColor = brokenColor;
        this.fallbackColor = fallbackColor;
    }

    public static PartDisplayProperties from(JsonObject json, PartDisplayProperties defaultProps) {
        String textureDomain = JsonUtils.getString(json, "texture_domain", defaultProps.textureDomain);
        String textureSuffix = JsonUtils.getString(json, "texture_suffix", defaultProps.textureSuffix);

        int textureColor = readColorCode(JsonUtils.getString(json, "texture_color", Integer.toHexString(defaultProps.textureColor)));

        int brokenColor = textureColor;
        if (json.has("broken_color")) {
            brokenColor = readColorCode(JsonUtils.getString(json, "broken_color", Integer.toHexString(defaultProps.brokenColor)));
        }

        int fallbackColor = brokenColor;
        if (json.has("fallback_color")) {
            fallbackColor = readColorCode(JsonUtils.getString(json, "fallback_color", Integer.toHexString(defaultProps.fallbackColor)));
        }

        return new PartDisplayProperties(textureDomain, textureSuffix, textureColor, brokenColor, fallbackColor);
    }

    static int readColorCode(String str) {
        try {
            return UnsignedInts.parseUnsignedInt(str, 16);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return Color.VALUE_WHITE;
        }
    }

    @Override
    public String toString() {
        return "PartDisplayProperties{" +
                "textureDomain='" + textureDomain + '\'' +
                ", textureSuffix='" + textureSuffix + '\'' +
                ", textureColor=" + Integer.toHexString(textureColor) +
                ", brokenColor=" + Integer.toHexString(brokenColor) +
                ", fallbackColor=" + Integer.toHexString(fallbackColor) +
                '}';
    }
}
