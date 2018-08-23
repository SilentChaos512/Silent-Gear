package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class PartGrip extends ItemPart implements IUpgradePart {
    public PartGrip(ResourceLocation name) {
        super(name, false);
    }

    public PartGrip(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        return new ResourceLocation(this.registryName.getNamespace(), "items/" + gearClass + "/grip_" + this.textureSuffix);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, int animationFrame) {
        return getTexture(part, gear, gearClass, PartPositions.GRIP, animationFrame);
    }

    @Override
    public String getTypeName() {
        return "grip";
    }
}
