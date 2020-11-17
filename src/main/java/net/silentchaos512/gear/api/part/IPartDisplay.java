package net.silentchaos512.gear.api.part;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialLayerList;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.utils.Color;

import java.util.List;

public interface IPartDisplay {
    IMaterialLayerList getLayers(GearType gearType, IPartData part);

    default int getLayerColor(GearType gearType, IPartData part, int layer) {
        List<MaterialLayer> layers = getLayers(gearType, part).getLayers();
        return layer < layers.size() ? layers.get(layer).getColor() : Color.VALUE_WHITE;
    }
}
