package net.silentchaos512.gear.data.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompoundModelBuilder extends ItemModelBuilder {
    private ResourceLocation loader = Const.GEAR_MODEL_LOADER;
    @Nullable private GearType gearType = null;
    @Nullable private PartType partType = null;
    private String texturePath = "";
    private List<ResourceLocation> extraLayers = new ArrayList<>();

    public CompoundModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public CompoundModelBuilder setLoader(ResourceLocation loader) {
        this.loader = loader;
        return this;
    }

    public CompoundModelBuilder setGearType(GearType gearType) {
        this.gearType = gearType;
        if (this.texturePath.isEmpty()) {
            this.texturePath = gearType.getName();
        }
        return this;
    }

    public CompoundModelBuilder setPartType(PartType partType) {
        this.partType = partType;
        return this;
    }

    public CompoundModelBuilder setTexturePath(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public CompoundModelBuilder addExtraLayer(ResourceLocation texture) {
        this.extraLayers.add(texture);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("loader", this.loader.toString());
        if (this.gearType != null) {
            json.addProperty("gear_type", this.gearType.getName());
        }
        if (this.partType != null) {
            json.addProperty("part_type", this.partType.getName().toString());
        }
        if (!this.texturePath.isEmpty()) {
            json.addProperty("texture_path", this.texturePath);
        }
        if (!this.extraLayers.isEmpty()) {
            JsonArray array = new JsonArray();
            this.extraLayers.forEach(tex -> array.add(tex.toString()));
            json.add("extra_layers", array);
        }
        return json;
    }
}
