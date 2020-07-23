package net.silentchaos512.gear.parts.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.util.GearHelper;

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

    @Override
    public boolean canAddToGear(ItemStack gear, PartData part) {
        GearType type = GearHelper.getType(gear);
        return type != null && type.matches(GearType.TOOL);
    }
}
