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

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
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
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("durability_trait");
    static final ITraitSerializer<DurabilityTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            DurabilityTrait::new,
            DurabilityTrait::readJson
    );

    private float activationChance;
    private float effectScale;

    private DurabilityTrait(ResourceLocation id) {
        super(id);
    }

    @Override
    public float onDurabilityDamage(TraitActionContext context, int damageTaken) {
        EntityPlayer player = context.getPlayer();
        if (damageTaken != 0 && shouldActivate(context.getTraitLevel())) {
            if (effectScale > 0 && player instanceof EntityPlayerMP) {
                LibTriggers.GENERIC_INT.trigger((EntityPlayerMP) player, TRIGGER_BRITTLE, 1);
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

    private static void readJson(DurabilityTrait trait, JsonObject json) {
        trait.activationChance = JsonUtils.getFloat(json, "activation_chance", 1);
        trait.effectScale = JsonUtils.getInt(json, "effect_scale", 0);
    }
}
