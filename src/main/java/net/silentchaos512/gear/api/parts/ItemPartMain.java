package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public final class ItemPartMain extends ItemPart {

    public ItemPartMain(ResourceLocation resource) {
        super(resource);
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame) {
        return getTexture(stack, toolClass, animationFrame, "head");
    }

    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame, String subtype) {
        String frameStr = "bow".equals(toolClass) && animationFrame == 3 ? "_3" : "";
        String subtypePrefix = subtype + (subtype.isEmpty() ? "" : "_");
        String path = "items/" + toolClass + "/" + subtypePrefix + this.textureSuffix + frameStr;
        return new ResourceLocation(this.key.getResourceDomain(), path);
    }

    @Override
    public ResourceLocation getBrokenTexture(ItemStack stack, String toolClass) {
        return new ResourceLocation(this.key.getResourceDomain(), "items/" + toolClass + "/_broken");
    }

    @Override
    public String getTypeName() {
        return "main";
    }
}
