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
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        return new ResourceLocation(this.registryName.getNamespace(), "items/" + gearClass + "/rod_" + this.textureSuffix);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, int animationFrame) {
        return getTexture(part, gear, gearClass, PartPositions.ROD, animationFrame);
    }

    @Override
    public String getTypeName() {
        return "rod";
    }
}
