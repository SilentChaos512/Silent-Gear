package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.FakePartData;
import net.silentchaos512.utils.Color;

import java.util.List;

public interface IMaterialDisplay {
    IMaterialLayerList getLayers(GearType gearType, IPartData part);

    default IMaterialLayerList getLayers(GearType gearType, PartType partType) {
        return getLayers(gearType, FakePartData.of(partType));
    }

    default int getLayerColor(GearType gearType, IPartData part, int layer) {
        List<MaterialLayer> layers = getLayers(gearType, part).getLayers();
        return layer < layers.size() ? layers.get(layer).getColor() : Color.VALUE_WHITE;
    }

    default int getLayerColor(GearType gearType, PartType partType, int layer) {
        return getLayerColor(gearType, FakePartData.of(partType), layer);
    }
}
