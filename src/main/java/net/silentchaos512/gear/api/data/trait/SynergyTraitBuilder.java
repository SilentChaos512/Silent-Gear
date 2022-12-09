package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;

import java.util.Optional;

public class SynergyTraitBuilder extends TraitBuilder {
    private final float synergyMulti;
    private float rangeMin = 0f;
    private float rangeMax = Float.MAX_VALUE;

    public SynergyTraitBuilder(DataResource<ITrait> trait, int maxLevel, float synergyMulti) {
        this(trait.getId(), maxLevel, synergyMulti);
    }

    public SynergyTraitBuilder(ResourceLocation traitId, int maxLevel, float synergyMulti) {
        super(traitId, maxLevel, ApiConst.SYNERGY_TRAIT_ID);
        this.synergyMulti = synergyMulti;
    }

    public SynergyTraitBuilder setRangeMin(float min) {
        this.rangeMin = min;
        return this;
    }

    public SynergyTraitBuilder setRangeMax(float max) {
        this.rangeMax = max;
        return this;
    }

    public SynergyTraitBuilder setRange(float min, float max) {
        this.rangeMin = min;
        this.rangeMax = max;
        return this;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.addProperty("synergy_multi", this.synergyMulti);
        serializeRange().ifPresent(j -> json.add("applicable_range", j));
        return json;
    }

    private Optional<JsonObject> serializeRange() {
        if (this.rangeMin > 0f || this.rangeMax < Float.MAX_VALUE) {
            JsonObject json = new JsonObject();

            if (this.rangeMin > 0f) {
                json.addProperty("min", this.rangeMin);
            }
            if (this.rangeMax < Float.MAX_VALUE) {
                json.addProperty("max", this.rangeMax);
            }

            return Optional.of(json);
        }

        return Optional.empty();
    }
}
