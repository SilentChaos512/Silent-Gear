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
    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame) {
        if (!"bow".equals(toolClass))
            return BLANK_TEXTURE;
        return new ResourceLocation(this.key.getNamespace(), "items/" + toolClass + "/bowstring_" + this.textureSuffix + "_" + animationFrame);
    }

    @Override
    public String getModelIndex(int animationFrame) {
        return this.modelIndex + "_" + animationFrame;
    }

    @Override
    public String getTypeName() {
        return "bowstring";
    }
}
