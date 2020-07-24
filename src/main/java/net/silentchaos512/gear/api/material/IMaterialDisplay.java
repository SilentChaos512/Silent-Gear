package net.silentchaos512.gear.api.material;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;

public interface IMaterialDisplay {
    IMaterialLayerList getLayers(GearType gearType, PartType partType);
}
