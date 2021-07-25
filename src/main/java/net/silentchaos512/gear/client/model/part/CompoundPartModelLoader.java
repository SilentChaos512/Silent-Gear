package net.silentchaos512.gear.client.model.part;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompoundPartModelLoader implements IModelLoader<CompoundPartModel> {
    private static final Collection<CompoundPartModel> MODELS = new ArrayList<>();

    public static void clearCaches() {
        MODELS.forEach(CompoundPartModel::clearCache);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        MODELS.clear();
    }

    @Override
    public CompoundPartModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        ItemTransforms cameraTransforms = deserializationContext.deserialize(modelContents.get("display"), ItemTransforms.class);
        if (cameraTransforms == null) {
            cameraTransforms = ItemTransforms.NO_TRANSFORMS;
        }
        GearType gearType = GearType.fromJson(modelContents, "gear_type");
        PartType partType = PartType.fromJson(modelContents, "part_type");
        String subPath = GsonHelper.getAsString(modelContents, "texture_path", gearType.getName());

        List<ResourceLocation> extras = new ArrayList<>();
        if (modelContents.has("extra_layers") && modelContents.get("extra_layers").isJsonArray()) {
            JsonArray array = modelContents.getAsJsonArray("extra_layers");
            array.forEach(e -> extras.add(new ResourceLocation(e.getAsString())));
        }

        CompoundPartModel model = new CompoundPartModel(cameraTransforms, gearType, partType, subPath, extras);
        MODELS.add(model);
        return model;
    }
}
