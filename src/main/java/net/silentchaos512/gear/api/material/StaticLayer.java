package net.silentchaos512.gear.api.material;

import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.lib.util.Color;

public class StaticLayer extends MaterialLayer {
    public StaticLayer(ResourceLocation texture) {
        this(texture, Color.VALUE_WHITE);
    }

    public StaticLayer(ResourceLocation texture, int color) {
        super(texture, color);
    }

    public ResourceLocation getTexture() {
        String path = "item/" + this.texture.getPath();
        return new ResourceLocation(this.texture.getNamespace(), path);
    }

    @Override
    public ResourceLocation getTexture(GearType gearType, int animationFrame) {
        return getTexture();
    }

    @Override
    public ResourceLocation getTexture(String texturePath, int animationFrame) {
        return getTexture();
    }
}
