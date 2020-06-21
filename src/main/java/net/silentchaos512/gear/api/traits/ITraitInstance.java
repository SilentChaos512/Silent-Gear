package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.traits.TraitSerializers;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ITraitInstance {
    ResourceLocation getTraitId();

    @Nullable
    ITrait getTrait();

    int getLevel();

    Collection<ITraitCondition> getConditions();

    default boolean conditionsMatch(PartDataList parts, ItemStack gear) {
        // FIXME: Shouldn't be using trait reference?
        ITrait trait = getTrait();
        return trait == null || getConditions().stream().allMatch(c -> c.matches(gear, parts, trait));
    }

    default boolean conditionsMatch(Collection<MaterialInstance> materials, ItemStack gear) {
        // FIXME
        return true;
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
