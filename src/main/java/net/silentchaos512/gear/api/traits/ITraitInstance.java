package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.trait.TraitSerializers;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface ITraitInstance {
    ResourceLocation getTraitId();

    @Nullable
    ITrait getTrait();

    int getLevel();

    Collection<ITraitCondition> getConditions();

    default boolean conditionsMatch(GearType gearType, PartDataList parts, ItemStack gear) {
        ITrait trait = getTrait();
        return trait == null || getConditions().stream().allMatch(c -> c.matches(gear, gearType, parts, trait));
    }

    default boolean conditionsMatch(List<MaterialInstance> materials, GearType gearType, PartType partType, ItemStack gear) {
        ITrait trait = getTrait();
        return trait == null || getConditions().stream().allMatch(c -> c.matches(gear, gearType, partType, materials, trait));
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

    default IFormattableTextComponent getConditionsText() {
        // Basically the same as AndTraitCondition
        return getConditions().stream()
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "and")).append(t2))
                .orElseGet(() -> new StringTextComponent(""));
    }
}
