package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.lib.ItemPartData;

import java.util.List;

public final class ToolPartTip extends ItemPart implements IUpgradePart {

    public ToolPartTip(ResourceLocation resource) {
        super(resource);
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, String toolClass, int animationFrame) {
        String frameStr = "bow".equals(toolClass) && animationFrame == 3 ? "_3" : "";
        return new ResourceLocation(this.key.getResourceDomain(), "items/" + toolClass + "/tip_" + this.textureSuffix + frameStr);
    }

    @Override
    public ResourceLocation getBrokenTexture(ItemStack stack, String toolClass) {
        return ItemPart.BLANK_TEXTURE;
    }

    @Override
    public void addInformation(ItemPartData data, ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        tooltip.add(1, getLocalizedName(data, stack));
    }

    @Override
    public String getTypeName() {
        return "tip";
    }
}
