package net.silentchaos512.gear.api.part;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialLayerList;
import net.silentchaos512.gear.api.material.MaterialLayerList;
import net.silentchaos512.gear.api.util.PartGearKey;

import java.util.LinkedHashMap;
import java.util.Map;

public class PartDisplay implements IPartDisplay {
    protected final Map<PartGearKey, MaterialLayerList> map = new LinkedHashMap<>();
    private PartType partType = PartType.NONE;

    public static PartDisplay of(Map<PartGearKey, MaterialLayerList> display) {
        PartDisplay model = new PartDisplay();
        if (!display.isEmpty()) {
            model.partType = display.keySet().iterator().next().getPartType();
        }
        display.forEach(model.map::put);
        return model;
    }

    @Override
    public IMaterialLayerList getLayers(GearType gearType, IPartData part) {
        return map.getOrDefault(getMostSpecificKey(gearType), MaterialLayerList.DEFAULT);
    }

    private PartGearKey getMostSpecificKey(GearType gearType) {
        PartGearKey key = PartGearKey.of(gearType, this.partType);
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

    public static PartDisplay deserialize(ResourceLocation modelId, JsonObject json) {
        PartDisplay ret = new PartDisplay();
        //noinspection OverlyLongLambda
        json.entrySet().forEach(entry -> {
            PartGearKey key = PartGearKey.read(entry.getKey());
            JsonElement value = entry.getValue();
            ret.partType = key.getPartType();
            ret.map.put(key, MaterialLayerList.deserialize(key, value, MaterialLayerList.DEFAULT));
        });
        return ret;
    }
}
