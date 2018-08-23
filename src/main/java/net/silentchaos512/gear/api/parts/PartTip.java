package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public final class PartTip extends ItemPart implements IUpgradePart {

    public PartTip(ResourceLocation name) {
        super(name, false);
    }

    public PartTip(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        String frameStr = "bow".equals(gearClass) && animationFrame == 3 ? "_3" : "";
        return new ResourceLocation(this.registryName.getNamespace(), "items/" + gearClass + "/tip_" + this.textureSuffix + frameStr);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, int animationFrame) {
        return getTexture(part, gear, gearClass, PartPositions.TIP, animationFrame);
    }

    @Override
    public ResourceLocation getBrokenTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position) {
        return ItemPart.BLANK_TEXTURE;
    }

    @Override
    public void addInformation(ItemPartData data, ItemStack gear, World world, List<String> tooltip, boolean advanced) {
        tooltip.add(1, getNameColor() + getTranslatedName(data, gear));
    }

    @Override
    public String getTypeName() {
        return "tip";
    }

    @Override
    public boolean replacesExisting() {
        return true;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.TIP;
    }
}
