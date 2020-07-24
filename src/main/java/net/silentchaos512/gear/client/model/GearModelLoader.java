package net.silentchaos512.gear.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.client.model.IModelLoader;
import net.silentchaos512.gear.api.item.GearType;

import java.util.ArrayList;
import java.util.Collection;

public class GearModelLoader implements IModelLoader<GearModel> {
    private static final Collection<GearModel> MODELS = new ArrayList<>();

    public static void clearCaches() {
        MODELS.forEach(GearModel::clearCache);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        MODELS.clear();
    }

    @Override
    public GearModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        ItemCameraTransforms cameraTransforms = deserializationContext.deserialize(modelContents.get("display"), ItemCameraTransforms.class);
        if (cameraTransforms == null) {
            cameraTransforms = ItemCameraTransforms.DEFAULT;
        }
        String gearTypeStr = JSONUtils.getString(modelContents, "gear_type");
        GearType gearType = GearType.get(gearTypeStr);
        if (gearType == null) {
            throw new NullPointerException("Unknown gear type: " + gearTypeStr);
        }

        GearModel model = new GearModel(cameraTransforms, gearType);
        MODELS.add(model);
        return model;
    }
}
