package net.silentchaos512.gear.api.material;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An object with display properties used by {@link IMaterial}
 */
public interface IMaterialLayerList extends Iterable<MaterialLayer> {
    List<MaterialLayer> getLayers();

    @Nullable
    default MaterialLayer getFirstLayer() {
        List<MaterialLayer> layers = getLayers();
        return layers.isEmpty() ? null : layers.get(0);
    }
}
