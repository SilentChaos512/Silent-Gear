package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.parts.PartTextureType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An object with display properties used by {@link IMaterial}
 */
public interface IMaterialDisplay {
    List<MaterialLayer> getLayers();

    @Nullable
    default MaterialLayer getFirstLayer() {
        List<MaterialLayer> layers = getLayers();
        return layers.isEmpty() ? null : layers.get(0);
    }

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
