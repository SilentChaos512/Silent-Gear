package net.silentchaos512.gear.api.part;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialLayerList;
import net.silentchaos512.gear.api.material.MaterialLayerList;

import java.util.LinkedHashMap;
import java.util.Map;

public class PartDisplay implements IPartDisplay {
    private final Map<GearType, MaterialLayerList> map = new LinkedHashMap<>();
    private final ResourceLocation modelId;

    public PartDisplay(ResourceLocation modelId) {
        this.modelId = modelId;
    }

    public static PartDisplay of(Map<GearType, MaterialLayerList> display) {
        PartDisplay model = new PartDisplay(new ResourceLocation("null"));
        model.map.putAll(display);
        return model;
    }

    @Override
    public IMaterialLayerList getLayers(GearType gearType) {
        return map.getOrDefault(getMostSpecificKey(gearType), MaterialLayerList.DEFAULT);
    }

    private GearType getMostSpecificKey(GearType gearType) {
        if (map.containsKey(gearType)) {
            return gearType;
        }

        GearType parent = gearType.getParent();
        while (parent != null) {
            if (map.containsKey(parent)) {
                return parent;
            }
            parent = parent.getParent();
        }

        // No match
        return gearType;
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        this.map.forEach((key, layerList) -> json.add(key.getName(), layerList.serialize()));
        return json;
    }

    public static PartDisplay deserialize(ResourceLocation modelId, JsonObject json) {
        PartDisplay ret = new PartDisplay(modelId);
        json.entrySet().forEach(entry -> {
            GearType key = GearType.get(entry.getKey());
            JsonElement value = entry.getValue();
            ret.map.put(key, MaterialLayerList.deserialize(value, MaterialLayerList.DEFAULT));
        });
        return ret;
    }

    @Override
    public String toString() {
        return "PartDisplay{" +
                "modelId=" + modelId +
                '}';
    }
}
