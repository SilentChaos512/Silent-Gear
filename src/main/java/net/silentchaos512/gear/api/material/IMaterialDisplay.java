package net.silentchaos512.gear.api.material;

import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.FakePartData;
import net.silentchaos512.utils.Color;

import java.util.List;

public interface IMaterialDisplay {
    ResourceLocation getMaterialId();

    IMaterialLayerList getLayerList(GearType gearType, IPartData part, IMaterialInstance materialIn);

    default IMaterialLayerList getLayerList(GearType gearType, PartType partType, IMaterialInstance materialIn) {
        return getLayerList(gearType, FakePartData.of(partType), materialIn);
    }

    default int getLayerColor(GearType gearType, IPartData part, IMaterialInstance materialIn, int layer) {
        List<MaterialLayer> layers = getLayerList(gearType, part, materialIn).getLayers();
        return layer < layers.size() ? layers.get(layer).getColor() : Color.VALUE_WHITE;
    }

    default int getLayerColor(GearType gearType, PartType partType, IMaterialInstance materialIn, int layer) {
        return getLayerColor(gearType, FakePartData.of(partType), materialIn, layer);
    }
}
