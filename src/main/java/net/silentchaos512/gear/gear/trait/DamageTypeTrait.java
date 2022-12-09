package net.silentchaos512.gear.gear.trait;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;

public final class DamageTypeTrait extends SimpleTrait {
    public static final ITraitSerializer<DamageTypeTrait> SERIALIZER = new Serializer<>(
            ApiConst.DAMAGE_TYPE_TRAIT_ID,
            DamageTypeTrait::new,
            (trait, json) -> {
                trait.damageType = GsonHelper.getAsString(json, "damage_type", trait.getId().getPath());
                trait.damageBonus = GsonHelper.getAsFloat(json, "damage_bonus", 0);
            },
            (trait, buffer) -> {
                trait.damageType = buffer.readUtf();
                trait.damageBonus = buffer.readFloat();
            },
            (trait, buffer) -> {
                buffer.writeUtf(trait.damageType);
                buffer.writeFloat(trait.damageBonus);
            }
    );

    private String damageType;
    private float damageBonus;

    private DamageTypeTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public float onAttackEntity(TraitActionContext context, LivingEntity target, float baseValue) {
        if (target.isInvertedHealAndHarm() && "holy".equals(damageType)
                || target.fireImmune() && "chilled".equals(damageType)) {
            return baseValue + damageBonus * context.getTraitLevel();
        }

        return baseValue;
    }
}
