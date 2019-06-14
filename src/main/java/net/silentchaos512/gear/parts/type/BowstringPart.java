package net.silentchaos512.gear.parts.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.api.parts.IPartDisplay;
import net.silentchaos512.gear.parts.PartData;

import javax.annotation.Nullable;

public final class BowstringPart extends AbstractGearPart {
    public BowstringPart(ResourceLocation name) {
        super(name);
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
    public IPartSerializer<?> getSerializer() {
        return PartType.BOWSTRING.getSerializer();
    }

    @Nullable
    @Override
    public ResourceLocation getTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position, int animationFrame) {
        if (!GearType.BOW.equals(gearClass)) return null;
        IPartDisplay props = getDisplayProperties(part, gear, animationFrame);
        // items/bow/bowstring_material_frame.png
        String path = String.format("items/%s/%s_%s_%d", gearClass, position.getTexturePrefix(),
                props.getTextureSuffix(), animationFrame);
        return new ResourceLocation(props.getTextureDomain(), path);
    }

    @Nullable
    @Override
    public ResourceLocation getBrokenTexture(PartData part, ItemStack gear, GearType gearClass, IPartPosition position) {
        return null;
    }

    @Override
    public String getModelIndex(PartData part, int animationFrame) {
        return this.getModelIndex() + "_" + animationFrame;
    }
}
