package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.parts.PartTextureType;

import java.util.List;

/**
 * An object with display properties used by {@link IMaterial}
 */
public interface IMaterialDisplay {
    List<MaterialLayer> getLayers();

    /**
     * Gets the texture type
     *
     * @return The texture type
     */
    @Deprecated
    PartTextureType getTexture();

    /**
     * Gets the color of the first layer
     *
     * @return The item color
     */
    int getPrimaryColor();
}
