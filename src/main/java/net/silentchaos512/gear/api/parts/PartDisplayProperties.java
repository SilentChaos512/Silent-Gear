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

import lombok.Getter;
import net.silentchaos512.lib.util.Color;

/**
 * May be used in the future, if it becomes necessary to have separate display properties for armor.
 * Currently unused.
 * @since 0.4.1
 */
@Getter
public final class PartDisplayProperties {
    public static final PartDisplayProperties DEFAULT = new PartDisplayProperties("", "", Color.VALUE_WHITE, Color.VALUE_WHITE);

    String textureDomain;
    String textureSuffix;
    int textureColor;
    int brokenColor;

    protected PartDisplayProperties() {
        textureDomain = textureSuffix = "";
        textureColor = brokenColor = 0xFFFFFF;
    }

    public PartDisplayProperties(String textureDomain, String textureSuffix, int textureColor, int brokenColor) {
        this.textureDomain = textureDomain;
        this.textureSuffix = textureSuffix;
        this.textureColor = textureColor;
        this.brokenColor = brokenColor;
    }

    @Override
    public String toString() {
        return "PartDisplayProperties{" +
                "textureDomain='" + textureDomain + '\'' +
                ", textureSuffix='" + textureSuffix + '\'' +
                ", textureColor=" + Integer.toHexString(textureColor) +
                ", brokenColor=" + Integer.toHexString(brokenColor) +
                '}';
    }
}
