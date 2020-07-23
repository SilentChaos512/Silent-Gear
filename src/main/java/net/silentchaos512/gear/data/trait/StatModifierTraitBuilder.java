package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.stats.IItemStat;
import net.silentchaos512.gear.traits.StatModifierTrait;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatModifierTraitBuilder extends TraitBuilder {
    private final Map<IItemStat, StatModifierTrait.StatMod> mods = new LinkedHashMap<>();

    public StatModifierTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, StatModifierTrait.SERIALIZER);
    }

    public StatModifierTraitBuilder addStatMod(IItemStat stat, float multi, boolean factorDamage, boolean factorValue) {
        this.mods.put(stat, StatModifierTrait.StatMod.of(multi, factorDamage, factorValue));
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.mods.isEmpty()) {
            throw new IllegalStateException("Stat modifier trait '" + this.traitId + "' has no modifiers");
        }

        JsonObject json = super.serialize();

        JsonArray statsJson = new JsonArray();
        this.mods.forEach((stat, mod) -> statsJson.add(mod.serialize(stat)));
        json.add("stats", statsJson);

        return json;
    }
}
