package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class PartRod extends ItemPart {

    public PartRod(ResourceLocation name) {
        super(name, false);
    }

    public PartRod(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame) {
        return new ResourceLocation(this.key.getNamespace(), "items/" + toolClass + "/rod_" + this.textureSuffix);
    }

    @Override
    public String getTypeName() {
        return "rod";
    }
}
