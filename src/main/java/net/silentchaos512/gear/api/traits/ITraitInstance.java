package net.silentchaos512.gear.api.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.silentchaos512.gear.api.util.IGearComponentInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
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

    default boolean conditionsMatch(PartGearKey key, ItemStack gear, List<? extends IGearComponentInstance<?>> components) {
        ITrait trait = getTrait();
        if (trait == null) return true;

        for (ITraitCondition condition : getConditions()) {
            if (!condition.matches(trait, key, gear, components)) {
                return false;
            }
        }

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

    default MutableComponent getConditionsText() {
        // Basically the same as AndTraitCondition
        return getConditions().stream()
                .map(ITraitCondition::getDisplayText)
                .reduce((t1, t2) -> t1.append(TextUtil.translate("trait.condition", "and")).append(t2))
                .orElseGet(() -> new TextComponent(""));
    }
}
