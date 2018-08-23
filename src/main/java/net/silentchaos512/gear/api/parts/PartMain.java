package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public final class PartMain extends ItemPart {
    public PartMain(ResourceLocation name) {
        super(name, false);
    }

    public PartMain(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.HEAD;
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String toolClass, IPartPosition position, int animationFrame) {
        String frameStr = "bow".equals(toolClass) && animationFrame == 3 ? "_3" : "";
        String partPosition = position.getTexturePrefix();
        String subtypePrefix = partPosition + (partPosition.isEmpty() ? "" : "_");
        String path = "items/" + toolClass + "/" + subtypePrefix + this.textureSuffix + frameStr;
        return new ResourceLocation(this.registryName.getNamespace(), path);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack stack, String toolClass, int animationFrame) {
        return getTexture(part, stack, toolClass, PartPositions.HEAD, animationFrame);
    }

    @Override
    public ResourceLocation getBrokenTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position) {
        return new ResourceLocation(SilentGear.MOD_ID, "items/" + gearClass + "/_broken");
    }

    @Override
    public String getTypeName() {
        return "main";
    }
}
