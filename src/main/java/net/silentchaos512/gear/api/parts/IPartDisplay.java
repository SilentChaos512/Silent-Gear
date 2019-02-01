package net.silentchaos512.gear.api.parts;

public interface IPartDisplay {
    String getTextureDomain();

    String getTextureSuffix();

    int getNormalColor();

    int getBrokenColor();

    int getFallbackColor();

    boolean hasHighlight();
}
