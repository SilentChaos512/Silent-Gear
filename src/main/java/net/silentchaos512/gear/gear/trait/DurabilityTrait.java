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

package net.silentchaos512.gear.gear.trait;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.utils.MathUtils;

import java.util.Collection;

/**
 * A Trait that modifies durability damage taken by gear, similar to the unbreaking enchantment (or
 * the reverse in some cases). Chance of modification increases with trait level. Effect scale does
 * not change with level. A negative scale reduces damage taken, positive increases it.
 */
public final class DurabilityTrait extends SimpleTrait {
    public static final ResourceLocation TRIGGER_BRITTLE = SilentGear.getId("brittle_proc");

    public static final ITraitSerializer<DurabilityTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("durability_trait"),
            DurabilityTrait::new,
            (trait, json) -> {
                trait.activationChance = GsonHelper.getAsFloat(json, "activation_chance", 1);
                trait.effectScale = GsonHelper.getAsInt(json, "effect_scale", 0);
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
        Player player = context.getPlayer();
        if (damageTaken != 0 && shouldActivate(context.getTraitLevel())) {
            if (effectScale > 0 && player instanceof ServerPlayer) {
                LibTriggers.GENERIC_INT.trigger((ServerPlayer) player, TRIGGER_BRITTLE, 1);
            }
            return Math.round(damageTaken + effectScale);
        }

        return super.onDurabilityDamage(context, damageTaken);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        int chancePercent = (int) (100 * activationChance);
        String line = String.format("  - %.1f damage with a %d%% chance per level", effectScale, chancePercent);
        ret.add(line);
        return ret;
    }

    private boolean shouldActivate(int level) {
        return MathUtils.tryPercentage(activationChance * level);
    }

    @Override
    public ITraitSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
