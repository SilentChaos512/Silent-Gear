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
    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame) {
        return new ResourceLocation(this.key.getNamespace(), "items/" + toolClass + "/grip_" + this.textureSuffix);
    }

    @Override
    public String getTypeName() {
        return "grip";
    }
}
