package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class PartGrip extends ItemPart implements IUpgradePart {

    public PartGrip(ResourceLocation resource) {
        super(resource);
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame) {
        return new ResourceLocation(this.key.getResourceDomain(), "items/" + toolClass + "/grip_" + this.textureSuffix);
    }

    @Override
    public String getTypeName() {
        return "grip";
    }
}
