package net.silentchaos512.gear.client.model.fragment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import java.util.ArrayList;
import java.util.Collection;

public class FragmentModelLoader implements IModelLoader<FragmentModel> {
    private static final Collection<FragmentModel> MODELS = new ArrayList<>();

    public static void clearCaches() {
        MODELS.forEach(FragmentModel::clearCache);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        MODELS.clear();
    }

    @Override
    public FragmentModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        ItemCameraTransforms cameraTransforms = deserializationContext.deserialize(modelContents.get("display"), ItemCameraTransforms.class);
        if (cameraTransforms == null) {
            cameraTransforms = ItemCameraTransforms.DEFAULT;
        }

        FragmentModel model = new FragmentModel(cameraTransforms);
        MODELS.add(model);
        return model;
    }
}
