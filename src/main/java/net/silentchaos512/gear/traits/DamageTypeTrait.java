package net.silentchaos512.gear.traits;

import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;

public final class DamageTypeTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("damage_type_trait");
    static final ITraitSerializer<DamageTypeTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            DamageTypeTrait::new,
            DamageTypeTrait::readJson
    );

    private String damageType;
    private float damageBonus;

    private DamageTypeTrait(ResourceLocation id) {
        super(id);
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        if ("holy".equals(damageType) && target.isEntityUndead()) {
            // TODO: We need to actually cancel the event and attack with a new damage source
            return baseValue + damageBonus * context.getTraitLevel();
        }

        return baseValue;
    }

    private static void readJson(DamageTypeTrait trait, JsonObject json) {
        trait.damageType = JSONUtils.getString(json, "damage_type", trait.getId().getPath());
        trait.damageBonus = JSONUtils.getFloat(json, "damage_bonus", 0);
    }
}
