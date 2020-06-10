package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.parts.PartTextureType;

/**
 * An object with display properties used by {@link IMaterial}
 */
public interface IMaterialDisplay {
    /**
     * Gets the texture type
     *
     * @return The texture type
     */
    PartTextureType getTexture();

    /**
     * Gets the item color
     *
     * @return The item color
     */
    int getColor();

    /**
     * Gets the color of the armor model worn by the player (not the item color)
     *
     * @return The armor color
     */
    int getArmorColor();
}
