package net.silentchaos512.gear.data.client;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;

public class CompoundModelBuilder extends ItemModelBuilder {
    private ResourceLocation loader = Const.GEAR_MODEL_LOADER;
    @Nullable private GearType gearType = null;
    @Nullable private PartType partType = null;

    public CompoundModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public CompoundModelBuilder setLoader(ResourceLocation loader) {
        this.loader = loader;
        return this;
    }

    public CompoundModelBuilder setGearType(GearType gearType) {
        this.gearType = gearType;
        return this;
    }

    public CompoundModelBuilder setPartType(PartType partType) {
        this.partType = partType;
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.addProperty("loader", this.loader.toString());
        if (gearType != null) {
            json.addProperty("gear_type", this.gearType.getName());
        }
        if (partType != null) {
            json.addProperty("part_type", this.partType.getName().toString());
        }
        return json;
    }
}
