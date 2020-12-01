package net.silentchaos512.gear.api.material;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;

public class StaticLayer extends MaterialLayer {
    public StaticLayer(ResourceLocation texture, int color) {
        super(texture, color);
    }

    @Override
    public ResourceLocation getTexture(GearType gearType, int animationFrame) {
        String path = "item/" + this.texture.getPath();
        return new ResourceLocation(this.texture.getNamespace(), path);
    }
}
