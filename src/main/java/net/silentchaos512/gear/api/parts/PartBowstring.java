package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class PartBowstring extends ItemPart {
    public PartBowstring(ResourceLocation name) {
        super(name, false);
    }

    public PartBowstring(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.BOWSTRING;
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack stack, String toolClass, IPartPosition position, int animationFrame) {
        if (!"bow".equals(toolClass)) return BLANK_TEXTURE;
        return new ResourceLocation(this.registryName.getNamespace(), "items/" + toolClass + "/bowstring_"
                + this.textureSuffix + "_" + animationFrame);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, int animationFrame) {
        return getTexture(part, gear, gearClass, PartPositions.BOWSTRING, animationFrame);
    }

    @Override
    public String getModelIndex(ItemPartData part, int animationFrame) {
        return this.modelIndex + "_" + animationFrame;
    }

    @Override
    public String getTypeName() {
        return "bowstring";
    }
}
