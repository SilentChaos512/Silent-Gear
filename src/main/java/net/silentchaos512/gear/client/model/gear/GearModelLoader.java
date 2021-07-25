package net.silentchaos512.gear.client.model.gear;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import java.util.ArrayList;
import java.util.Collection;

public class GearModelLoader implements IModelLoader<GearModel> {
    private static final Collection<GearModel> MODELS = new ArrayList<>();

    public static void clearCaches() {
        MODELS.forEach(GearModel::clearCache);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        MODELS.clear();
    }

    @Override
    public GearModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        ItemTransforms cameraTransforms = deserializationContext.deserialize(modelContents.get("display"), ItemTransforms.class);
        if (cameraTransforms == null) {
            cameraTransforms = ItemTransforms.NO_TRANSFORMS;
        }
        String gearTypeStr = GsonHelper.getAsString(modelContents, "gear_type");
        GearType gearType = GearType.get(gearTypeStr);
        if (gearType.isInvalid()) {
            throw new NullPointerException("Unknown gear type: " + gearTypeStr);
        }
        String texturePath = GsonHelper.getAsString(modelContents, "texture_path", gearType.getName());
        String brokenTexturePath = GsonHelper.getAsString(modelContents, "broken_texture_path", gearType.getName());

        Collection<PartType> brokenTextureTypes = new ArrayList<>();
        JsonArray brokenTypesJson = GsonHelper.getAsJsonArray(modelContents, "broken_texture_types", null);
        if (brokenTypesJson != null) {
            for (JsonElement element : brokenTypesJson) {
                ResourceLocation id = SilentGear.getIdWithDefaultNamespace(element.getAsString());
                if (id != null) {
                    PartType type = PartType.get(id);
                    if (type != null) {
                        brokenTextureTypes.add(type);
                    } else {
                        SilentGear.LOGGER.error("Unknown part type '{}' in model {}", id, this.getName());
                    }
                }
            }
        }

        GearModel model = new GearModel(cameraTransforms, gearType, texturePath, brokenTexturePath, brokenTextureTypes);
        MODELS.add(model);
        return model;
    }
}
