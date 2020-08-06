package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.traits.EnchantmentTrait;
import net.silentchaos512.gear.util.DataResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentTraitBuilder extends TraitBuilder {
    private final Map<GearType, List<EnchantmentTrait.EnchantmentData>> enchantments = new LinkedHashMap<>();

    public EnchantmentTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public EnchantmentTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, EnchantmentTrait.SERIALIZER);
    }

    public EnchantmentTraitBuilder addEnchantments(GearType gearType, Enchantment enchantment, int... levels) {
        this.enchantments.computeIfAbsent(gearType, gt -> new ArrayList<>())
                .add(EnchantmentTrait.EnchantmentData.of(enchantment, levels));
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.enchantments.isEmpty()) {
            throw new IllegalStateException("Enchantment trait '" + this.traitId + "' has no enchantments");
        }

        JsonObject json = super.serialize();

        JsonObject enchantmentsJson = new JsonObject();
        this.enchantments.forEach(((gearType, enchants) -> {
            JsonArray array = new JsonArray();
            enchants.forEach(e -> array.add(e.serialize()));
            enchantmentsJson.add(gearType.getName(), array);
        }));
        json.add("enchantments", enchantmentsJson);

        return json;
    }
}
