package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.lib.util.NameUtils;

import java.util.*;

public class EnchantmentTraitBuilder extends TraitBuilder {
    private final Map<GearType, List<Data>> enchantments = new LinkedHashMap<>();

    public EnchantmentTraitBuilder(DataResource<ITrait> trait, int maxLevel) {
        this(trait.getId(), maxLevel);
    }

    public EnchantmentTraitBuilder(ResourceLocation traitId, int maxLevel) {
        super(traitId, maxLevel, ApiConst.ENCHANTMENT_TRAIT_ID);
    }

    public EnchantmentTraitBuilder addEnchantments(GearType gearType, Enchantment enchantment, int... levels) {
        this.enchantments.computeIfAbsent(gearType, gt -> new ArrayList<>())
                .add(new Data(NameUtils.fromEnchantment(enchantment), levels));
        return this;
    }

    @Override
    public JsonObject serialize() {
        if (this.enchantments.isEmpty()) {
            throw new IllegalStateException("Enchantment trait '" + this.getTraitId() + "' has no enchantments");
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

    public record Data(ResourceLocation enchantmentId, int... levels) {
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", this.enchantmentId.toString());

            JsonArray levelsJson = new JsonArray();
            Arrays.stream(this.levels).forEach(levelsJson::add);
            json.add("level", levelsJson);
            return json;
        }
    }
}
