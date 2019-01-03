package net.silentchaos512.gear.api.parts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class PartBowstring extends ItemPart {
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
    public ResourceLocation getTexture(ItemPartData part, ItemStack gear, String gearClass, IPartPosition position, int animationFrame) {
        if (!"bow".equals(gearClass)) return BLANK_TEXTURE;
        PartDisplayProperties props = getDisplayProperties(part, gear, animationFrame);
        // items/bow/bowstring_material_frame.png
        String path = String.format("items/%s/%s_%s_%d", gearClass, position.getTexturePrefix(),
                props.textureSuffix, animationFrame);
        return new ResourceLocation(props.textureDomain, path);
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
