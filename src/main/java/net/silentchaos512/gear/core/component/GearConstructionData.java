package net.silentchaos512.gear.core.component;

import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.CompoundPart;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record GearConstructionData(
        PartDataList parts,
        boolean isExample,
        int brokenCount,
        int repairedCount
) {
    @Nullable
    public IPartData getPartOfType(Supplier<PartType> type) {
        return getPartOfType(type.get());
    }

    @Nullable
    public IPartData getPartOfType(PartType type) {
        for (IPartData part : parts) {
            if (part.getType() == type) {
                return part;
            }
        }
        return null;
    }

    @Nullable
    public IPartData getPrimaryPart() {
        return getPartOfType(PartTypes.MAIN);
    }

    @Nullable
    public IPartData getCoatingOrMainPart() {
        var coating = getPartOfType(PartTypes.COATING);
        return coating != null ? coating : getPrimaryPart();
    }

    @Nullable
    public IMaterialInstance getMainTextureMaterial() {
        var part = getCoatingOrMainPart();
        if (part != null && part.get() instanceof CompoundPart) {
            return CompoundPartItem.getPrimaryMaterial(part.getItem());
        }
        return null;
    }
}
