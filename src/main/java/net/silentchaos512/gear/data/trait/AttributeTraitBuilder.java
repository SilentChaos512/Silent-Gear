package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.traits.AttributeTrait;
import net.silentchaos512.gear.util.DataResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttributeTraitBuilder extends TraitBuilder {
    private final Map<String, List<AttributeTrait.ModifierData>> modifiers = new LinkedHashMap<>();

    public AttributeTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public AttributeTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, AttributeTrait.SERIALIZER);
    }

    public AttributeTraitBuilder addModifier(GearType gearType, EquipmentSlotType slot, Attribute attribute, AttributeModifier.Operation operation, float... values) {
        this.modifiers.computeIfAbsent(makeKey(gearType, slot), str -> new ArrayList<>())
                .add(AttributeTrait.ModifierData.of(attribute, operation, values));
        return this;
    }

    public AttributeTraitBuilder addModifiersEitherHand(GearType gearType, Attribute attribute, AttributeModifier.Operation operation, float... values) {
        addModifier(gearType, EquipmentSlotType.MAINHAND, attribute, operation, values);
        addModifier(gearType, EquipmentSlotType.OFFHAND, attribute, operation, values);
        return this;
    }

    private static String makeKey(GearType gearType, EquipmentSlotType slot) {
        return gearType.getName() + "/" + slot.getName();
    }

    @Override
    public JsonObject serialize() {
        if (this.modifiers.isEmpty()) {
            throw new IllegalStateException("Attribute trait '" + this.traitId + "' has no modifiers");
        }

        JsonObject json = super.serialize();

        JsonObject modsJson = new JsonObject();
        this.modifiers.forEach(((key, mods) -> {
            JsonArray array = new JsonArray();
            mods.forEach(e -> array.add(e.serialize()));
            modsJson.add(key, array);
        }));
        json.add("attribute_modifiers", modsJson);

        return json;
    }
}
