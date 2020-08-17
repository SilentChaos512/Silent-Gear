package net.silentchaos512.gear.client.model.part;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import java.util.ArrayList;
import java.util.Collection;

public class CompoundPartModelLoader implements IModelLoader<CompoundPartModel> {
    private static final Collection<CompoundPartModel> MODELS = new ArrayList<>();

    public static void clearCaches() {
        MODELS.forEach(CompoundPartModel::clearCache);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        MODELS.clear();
    }

    @Override
    public CompoundPartModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        ItemCameraTransforms cameraTransforms = deserializationContext.deserialize(modelContents.get("display"), ItemCameraTransforms.class);
        if (cameraTransforms == null) {
            cameraTransforms = ItemCameraTransforms.DEFAULT;
        }
        GearType gearType = GearType.fromJson(modelContents, "gear_type");
        PartType partType = PartType.fromJson(modelContents, "part_type");

        CompoundPartModel model = new CompoundPartModel(cameraTransforms, gearType, partType);
        MODELS.add(model);
        return model;
    }
}
