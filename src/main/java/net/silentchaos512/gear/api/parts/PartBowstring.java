package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class PartBowstring extends ItemPart {
    @Deprecated
    public PartBowstring(ResourceLocation name) {
        super(name, false);
    }

    @Deprecated
    public PartBowstring(ResourceLocation name, boolean userDefined) {
        super(name, userDefined);
    }

    public PartBowstring(ResourceLocation name, PartOrigins origin) {
        super(name, origin);
    }

    @Override
    public PartType getType() {
        return PartType.BOWSTRING;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.BOWSTRING;
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack stack, String toolClass, IPartPosition position, int animationFrame) {
        if (!"bow".equals(toolClass)) return BLANK_TEXTURE;
        return new ResourceLocation(this.textureDomain, "items/" + toolClass + "/bowstring_"
                + this.textureSuffix + "_" + animationFrame);
    }

    @Override
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, int animationFrame) {
        return getTexture(part, gear, gearClass, PartPositions.BOWSTRING, animationFrame);
    }

    @Nullable
    @Override
    public ResourceLocation getBrokenTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position) {
        return null;
    }

    @Override
    public String getModelIndex(ItemPartData part, int animationFrame) {
        return this.modelIndex + "_" + animationFrame;
    }

    @Override
    public void addInformation(ItemPartData part, ItemStack gear, World world, @Nonnull List<String> tooltip, boolean advanced) {
        // Nothing
    }

    @Override
    public String getTypeName() {
        return "bowstring";
    }
}
