package net.silentchaos512.gear.parts.type;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartPositions;

public final class RodPart extends AbstractGearPart {
    public RodPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return PartType.ROD;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.ROD;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.ROD.getSerializer();
    }
}
