package net.silentchaos512.gear.gear.material;

import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.IMaterialSerializer;
import net.silentchaos512.gear.api.part.PartType;

public class SimpleMaterial extends AbstractMaterial {
    public SimpleMaterial(ResourceLocation id, String packName) {
        super(id, packName);
    }

    @Override
    public IMaterialSerializer<?> getSerializer() {
        return MaterialSerializers.STANDARD;
    }

    @Override
    public boolean allowedInPart(IMaterialInstance material, PartType partType) {
        return stats.containsKey(partType) || (getParent() != null && getParent().allowedInPart(material, partType));
    }

    @Override
    public String toString() {
        return "SimpleMaterial{" +
                "id=" + materialId +
                ", tier=" + tier +
                '}';
    }
}
