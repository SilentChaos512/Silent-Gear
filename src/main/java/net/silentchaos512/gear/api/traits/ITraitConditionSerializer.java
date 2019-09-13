package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Conditions for traits applied to parts. These affect when a trait given to a part will actually
 * apply to a gear item, such as required a specific gear type or a minimum number of parts. Heavily
 * inspired by Forge's recipe conditions (what was IConditionSerializer).
 */
@FunctionalInterface
public interface ITraitConditionSerializer {
    ITraitCondition parse(JsonObject json);

    ITraitConditionSerializer NOT = register(SilentGear.getId("not"), json -> {
        ITraitCondition child = getCondition(JSONUtils.getJsonObject(json, "value"));
        return (gear, parts, trait) -> !child.matches(gear, parts, trait);
    });

    ITraitConditionSerializer OR = register(SilentGear.getId("or"), json -> {
        JsonArray values = JSONUtils.getJsonArray(json, "values");
        Collection<ITraitCondition> children = new ArrayList<>();
        for (JsonElement j : values) {
            if (!j.isJsonObject())
                throw new JsonSyntaxException("Or condition values must be an array of JsonObjects");
            children.add(getCondition(j.getAsJsonObject()));
        }
        return (gear, parts, trait) -> children.stream().anyMatch(condition -> condition.matches(gear, parts, trait));
    });

    ITraitConditionSerializer AND = register(SilentGear.getId("and"), json -> {
        JsonArray values = JSONUtils.getJsonArray(json, "values");
        Collection<ITraitCondition> children = new ArrayList<>();
        for (JsonElement j : values) {
            if (!j.isJsonObject())
                throw new JsonSyntaxException("Or condition values must be an array of JsonObjects");
            children.add(getCondition(j.getAsJsonObject()));
        }
        return (gear, parts, trait) -> children.stream().allMatch(condition -> condition.matches(gear, parts, trait));
    });

    ITraitConditionSerializer GEAR_TYPE = register(SilentGear.getId("gear_type"), json -> {
        String gearType = JSONUtils.getString(json, "gear_type");
        return (gear, parts, trait) -> {
            if (gear.isEmpty() || !(gear.getItem() instanceof ICoreItem))
                return false;
            return ((ICoreItem) gear.getItem()).getGearType().matches(gearType);
        };
    });

    ITraitConditionSerializer MATERIAL_COUNT = register(SilentGear.getId("material_count"), json -> {
        int count = JSONUtils.getInt(json, "count");
        return (gear, parts, trait) -> {
            int partsWithTrait = parts.getPartsWithTrait(trait);
            return partsWithTrait >= count;
        };
    });

    ITraitConditionSerializer MATERIAL_RATIO = register(SilentGear.getId("material_ratio"), json -> {
        float ratio = JSONUtils.getFloat(json, "ratio");
        return (gear, parts, trait) -> {
            float ratioInGear = (float) parts.getPartsWithTrait(trait) / parts.getMains().size();
            return ratioInGear >= ratio;
        };
    });

    static ITraitConditionSerializer register(ResourceLocation id, ITraitConditionSerializer serializer) {
        if (Helper.CONDITIONS.containsKey(id))
            throw new IllegalArgumentException("Already have trait condition serializer with ID '" + id + "'");
        if (SilentGear.LOGGER.isDebugEnabled())
            SilentGear.LOGGER.debug("Register ITraitConditionSerializer '{}'", id);

        Helper.CONDITIONS.put(id, serializer);
        return serializer;
    }

    static ITraitCondition getCondition(JsonObject json) {
        ResourceLocation type = new ResourceLocation(JSONUtils.getString(json, "type"));
        ITraitConditionSerializer serializer = Helper.CONDITIONS.get(type);
        if (serializer == null)
            throw new JsonSyntaxException("Unknown trait condition type: " + type);
        return serializer.parse(json);
    }

    /**
     * Hides registered serializers to prevent tampering
     */
    final class Helper {
        private static final Map<ResourceLocation, ITraitConditionSerializer> CONDITIONS = new HashMap<>();

        private Helper() {}
    }
}
