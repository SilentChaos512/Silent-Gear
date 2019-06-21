package net.silentchaos512.gear.parts.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.api.parts.IPartDisplay;
import net.silentchaos512.gear.parts.PartData;

public final class MainPart extends AbstractGearPart {
    public MainPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return PartType.MAIN;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.HEAD;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartType.MAIN.getSerializer();
    }

    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        String frameStr = "bow".equals(gearClass) && animationFrame == 3 ? "_3" : "";
        String partPosition = position.getTexturePrefix();
        String subtypePrefix = partPosition + (partPosition.isEmpty() ? "" : "_");
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        String path = "items/" + gearClass + "/" + subtypePrefix + props.getTextureSuffix() + frameStr;
        return new ResourceLocation(props.getTextureDomain(), path);
    }

    @Override
    public ResourceLocation getBrokenTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position) {
        return new ResourceLocation(SilentGear.MOD_ID, "items/" + gearClass + "/_broken");
    }

    @Override
    public StatInstance.Operation getDefaultStatOperation(ItemStat stat) {
        return stat == ItemStats.HARVEST_LEVEL ? StatInstance.Operation.MAX : StatInstance.Operation.AVG;
    }
}
