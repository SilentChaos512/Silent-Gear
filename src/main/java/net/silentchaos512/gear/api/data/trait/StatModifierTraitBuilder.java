package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatModifierTraitBuilder extends TraitBuilder {
    private final Map<IItemStat, StatMod> mods = new LinkedHashMap<>();

    public StatModifierTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public StatModifierTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, ApiConst.STAT_MODIFIER_TRAIT_ID);
    }

    public StatModifierTraitBuilder addStatMod(IItemStat stat, float multi, boolean factorDamage, boolean factorValue) {
        this.mods.put(stat, new StatMod(multi, factorDamage, factorValue));
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.mods.isEmpty()) {
            throw new IllegalStateException("Stat modifier trait '" + this.getTraitId() + "' has no modifiers");
        }

        JsonObject json = super.serialize();

        JsonArray statsJson = new JsonArray();
        this.mods.forEach((stat, mod) -> statsJson.add(mod.serialize(stat)));
        json.add("stats", statsJson);

        return json;
    }

    public record StatMod(float multi, boolean factorDamage, boolean factorValue) {
        public JsonObject serialize(IItemStat stat) {
            JsonObject json = new JsonObject();
            json.addProperty("name", stat.getStatId().toString());
            json.addProperty("value", this.multi);
            json.addProperty("factor_damage", this.factorDamage);
            json.addProperty("factor_value", this.factorValue);
            return json;
        }
    }
}
