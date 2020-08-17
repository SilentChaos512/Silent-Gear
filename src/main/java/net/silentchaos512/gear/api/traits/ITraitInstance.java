package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.trait.TraitSerializers;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface ITraitInstance {
    ResourceLocation getTraitId();

    @Nullable
    ITrait getTrait();

    int getLevel();

    Collection<ITraitCondition> getConditions();

    default boolean conditionsMatch(PartDataList parts, ItemStack gear) {
        ITrait trait = getTrait();
        return trait == null || getConditions().stream().allMatch(c -> c.matches(gear, parts, trait));
    }

    default boolean conditionsMatch(List<MaterialInstance> materials, PartType partType, ItemStack gear) {
        ITrait trait = getTrait();
        return trait == null || getConditions().stream().allMatch(c -> c.matches(gear, partType, materials, trait));
    }

    default JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("name", getTraitId().toString());
        json.addProperty("level", getLevel());
        Collection<ITraitCondition> conditions = getConditions();
        if (!conditions.isEmpty()) {
            JsonArray array = new JsonArray();
            for (ITraitCondition condition : conditions) {
                array.add(TraitSerializers.serializeCondition(condition));
            }
            json.add("conditions", array);
        }
        return json;
    }
}
