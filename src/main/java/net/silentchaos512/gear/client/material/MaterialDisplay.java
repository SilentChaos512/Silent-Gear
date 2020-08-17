package net.silentchaos512.gear.client.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialLayerList;
import net.silentchaos512.gear.api.material.MaterialLayerList;
import net.silentchaos512.gear.api.part.PartType;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MaterialDisplay implements IMaterialDisplay {
    private final Map<PartGearKey, MaterialLayerList> map = new LinkedHashMap<>();
    private final ResourceLocation modelId;

    private MaterialDisplay(ResourceLocation modelId) {
        this.modelId = modelId;
    }

    public static MaterialDisplay of(Map<PartGearKey, MaterialLayerList> display) {
        MaterialDisplay model = new MaterialDisplay(new ResourceLocation("null"));
        model.map.putAll(display);
        return model;
    }

    @Override
    public IMaterialLayerList getLayers(GearType gearType, PartType partType) {
        return map.getOrDefault(getMostSpecificKey(gearType, partType), MaterialLayerList.DEFAULT);
    }

    private PartGearKey getMostSpecificKey(GearType gearType, PartType partType) {
        PartGearKey key = PartGearKey.of(gearType, partType);
        if (map.containsKey(key)) {
            return key;
        }

        PartGearKey parent = key.getParent();
        while (parent != null) {
            if (map.containsKey(parent)) {
                return parent;
            }
            parent = parent.getParent();
        }

        // No match
        return key;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        this.map.forEach((key, layerList) -> json.add(key.toString(), layerList.serialize()));
        return json;
    }

    public static MaterialDisplay deserialize(ResourceLocation modelId, JsonObject json) {
        MaterialDisplay ret = new MaterialDisplay(modelId);
        json.entrySet().forEach(entry -> {
            PartGearKey key = PartGearKey.read(entry.getKey());
            JsonElement value = entry.getValue();
            ret.map.put(key, MaterialLayerList.deserialize(value, MaterialLayerList.DEFAULT));
        });
        return ret;
    }

    @Override
    public String toString() {
        return "MaterialDisplay{" +
                "modelId=" + modelId +
                '}';
    }
}
