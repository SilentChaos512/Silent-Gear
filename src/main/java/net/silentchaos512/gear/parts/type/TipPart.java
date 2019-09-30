package net.silentchaos512.gear.parts.type;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.api.parts.IPartDisplay;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;

import java.util.List;

public final class TipPart extends AbstractGearPart implements IUpgradePart {
    public TipPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return PartType.TIP;
    }

    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        String frameStr = "bow".equals(gearClass) && animationFrame == 3 ? "_3" : "";
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        String path = "items/" + gearClass + "/tip_" + props.getTextureSuffix() + frameStr;
        return new ResourceLocation(props.getTextureDomain(), path);
    }

    @Override
    public ResourceLocation getBrokenTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position) {
        return null;
    }

    @Override
    public void addInformation(PartData part, ItemStack gear, List<ITextComponent> tooltip, ITooltipFlag flag) {
        // Add just below item name. Check list size in case it's been tampered with.
        tooltip.add(Math.min(1, tooltip.size()), part.getDisplayName(gear));
    }

    @Override
    public boolean replacesExisting() {
        return true;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.TIP;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.TIP.getSerializer();
    }

    @Override
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        return stat == ItemStats.HARVEST_LEVEL ? StatInstance.Operation.MAX : StatInstance.Operation.ADD;
    }
}
