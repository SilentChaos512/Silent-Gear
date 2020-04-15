package net.silentchaos512.gear.api.parts;

import net.silentchaos512.gear.parts.PartTextureType;

public interface IPartDisplay {
    String getTextureDomain();

    @Deprecated
    String getTextureSuffix();

    String getArmorTexturePrefix();

    int getNormalColor();

    int getBrokenColor();

    int getFallbackColor();

    /**
     * Gets the color of the armor model worn by the player (not the item color)
     * @return The armor color
     */
    int getArmorColor();

    boolean hasHighlight();

    PartTextureType getLiteTexture();
}
