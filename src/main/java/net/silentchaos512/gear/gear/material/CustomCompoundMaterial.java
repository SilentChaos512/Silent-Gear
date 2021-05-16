package net.silentchaos512.gear.gear.material;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterialSerializer;

public class CustomCompoundMaterial extends AbstractMaterial {
    public CustomCompoundMaterial(ResourceLocation id, String packName) {
        super(id, packName);
        this.simple = false;
    }

    @Override
    public IMaterialSerializer<?> getSerializer() {
        return MaterialSerializers.CUSTOM_COMPOUND;
    }
}
