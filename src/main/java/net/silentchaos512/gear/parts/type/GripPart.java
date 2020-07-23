package net.silentchaos512.gear.parts.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.util.GearHelper;

public final class GripPart extends AbstractGearPart implements IUpgradePart {
    public GripPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return PartType.GRIP;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.GRIP;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.GRIP.getSerializer();
    }

    @Override
    public boolean canAddToGear(ItemStack gear, PartData part) {
        GearType type = GearHelper.getType(gear);
        return type != null && type.matches(GearType.TOOL);
    }

    @Override
    public boolean isValidFor(ICoreItem gearItem) {
        return gearItem instanceof ICoreTool;
    }

    @Override
    public boolean replacesExisting() {
        return true;
    }
}
