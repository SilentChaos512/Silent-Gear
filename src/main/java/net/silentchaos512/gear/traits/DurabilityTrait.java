/*
 * Silent Gear -- DurabilityTrait
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.traits;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.utils.MathUtils;

/**
 * A Trait that modifies durability damage taken by gear, similar to the unbreaking enchantment (or
 * the reverse in some cases). Chance of modification increases with trait level. Effect scale does
 * not change with level. A negative scale reduces damage taken, positive increases it.
 */
public final class DurabilityTrait extends SimpleTrait {
    private static final ResourceLocation TRIGGER_BRITTLE = SilentGear.getId("brittle_proc");

    static final ITraitSerializer<DurabilityTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("durability_trait"),
            DurabilityTrait::new,
            (trait, json) -> {
                trait.activationChance = JSONUtils.getFloat(json, "activation_chance", 1);
                trait.effectScale = JSONUtils.getInt(json, "effect_scale", 0);
            },
            (trait, buffer) -> {
                trait.activationChance = buffer.readFloat();
                trait.effectScale = buffer.readFloat();
            },
            (trait, buffer) -> {
                buffer.writeFloat(trait.activationChance);
                buffer.writeFloat(trait.effectScale);
            }
    );

    private float activationChance;
    private float effectScale;

    private DurabilityTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public float onDurabilityDamage(TraitActionContext context, int damageTaken) {
        PlayerEntity player = context.getPlayer();
        if (damageTaken != 0 && shouldActivate(context.getTraitLevel())) {
            if (effectScale > 0 && player instanceof ServerPlayerEntity) {
                LibTriggers.GENERIC_INT.trigger((ServerPlayerEntity) player, TRIGGER_BRITTLE, 1);
            }
            return Math.round(damageTaken + effectScale);
        }

        return super.onDurabilityDamage(context, damageTaken);
    }

    private boolean shouldActivate(int level) {
        return MathUtils.tryPercentage(activationChance * level);
    }

    @Override
    public ITraitSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
