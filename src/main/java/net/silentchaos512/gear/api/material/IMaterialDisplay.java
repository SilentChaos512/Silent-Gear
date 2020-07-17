package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.parts.PartTextureType;

import java.util.Collection;

/**
 * An object with display properties used by {@link IMaterial}
 */
public interface IMaterialDisplay {
    Collection<MaterialLayer> getLayers();

    /**
     * Gets the texture type
     *
     * @return The texture type
     */
    @Deprecated
    PartTextureType getTexture();

    /**
     * Gets the item color
     *
     * @return The item color
     */
    @Deprecated
    int getColor();

    /**
     * Gets the color of the armor model worn by the player (not the item color)
     *
     * @return The armor color
     */
    int getArmorColor();
}
