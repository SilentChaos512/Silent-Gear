package net.silentchaos512.gear.api.parts;

import net.silentchaos512.gear.parts.PartTextureType;

public interface IPartDisplay {
    String getTextureDomain();

    String getTextureSuffix();

    int getNormalColor();

    int getBrokenColor();

    int getFallbackColor();

    boolean hasHighlight();

    PartTextureType getLiteTexture();
}
