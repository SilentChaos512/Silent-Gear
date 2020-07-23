package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.traits.DurabilityTrait;

public class DurabilityTraitBuilder extends TraitBuilder {
    private final int effectScale;
    private final float activationChance;

    public DurabilityTraitBuilder(ResourceLocation traitId, int maxLevel, int effectScale, float activationChance) {
        super(traitId, maxLevel, DurabilityTrait.SERIALIZER);
        this.effectScale = effectScale;
        this.activationChance = activationChance;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.addProperty("effect_scale", this.effectScale);
        json.addProperty("activation_chance", this.activationChance);
        return json;
    }
}
