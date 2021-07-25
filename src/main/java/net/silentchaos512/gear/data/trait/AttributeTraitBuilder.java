package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.AttributeTrait;
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

    public AttributeTraitBuilder addModifier(GearType gearType, EquipmentSlot slot, Attribute attribute, AttributeModifier.Operation operation, float... values) {
        return addModifier(gearType, slot.getName(), attribute, operation, values);
    }

    public AttributeTraitBuilder addModifier(GearType gearType, String slot, Attribute attribute, AttributeModifier.Operation operation, float... values) {
        this.modifiers.computeIfAbsent(makeKey(gearType, slot), str -> new ArrayList<>())
                .add(AttributeTrait.ModifierData.of(attribute, operation, values));
        return this;
    }

    public AttributeTraitBuilder addArmorModifier(Attribute attribute, AttributeModifier.Operation operation, float... values) {
        addModifier(GearType.ARMOR, EquipmentSlot.HEAD, attribute, operation, values);
        addModifier(GearType.ARMOR, EquipmentSlot.CHEST, attribute, operation, values);
        addModifier(GearType.ARMOR, EquipmentSlot.LEGS, attribute, operation, values);
        addModifier(GearType.ARMOR, EquipmentSlot.FEET, attribute, operation, values);
        return this;
    }

    public AttributeTraitBuilder addModifiersEitherHand(GearType gearType, Attribute attribute, AttributeModifier.Operation operation, float... values) {
        addModifier(gearType, EquipmentSlot.MAINHAND, attribute, operation, values);
        addModifier(gearType, EquipmentSlot.OFFHAND, attribute, operation, values);
        return this;
    }

    public AttributeTraitBuilder addModifierAnySlot(GearType gearType, Attribute attribute, AttributeModifier.Operation operation, float... values) {
        return addModifier(gearType, "", attribute, operation, values);
    }

    private static String makeKey(GearType gearType, String slot) {
        if (slot.isEmpty()) {
            return gearType.getName();
        }
        return gearType.getName() + "/" + slot;
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
