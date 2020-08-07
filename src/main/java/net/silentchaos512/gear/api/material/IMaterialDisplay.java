package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.utils.Color;

import java.util.List;

public interface IMaterialDisplay {
    IMaterialLayerList getLayers(GearType gearType, PartType partType);

    default int getLayerColor(GearType gearType, PartType partType, int layer) {
        List<MaterialLayer> layers = getLayers(gearType, partType).getLayers();
        return layer < layers.size() ? layers.get(layer).getColor() : Color.VALUE_WHITE;
    }
}
