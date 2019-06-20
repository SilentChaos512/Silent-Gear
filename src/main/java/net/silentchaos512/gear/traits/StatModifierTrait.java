/*
 * Silent Gear -- StatModifierTrait
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;

import java.util.HashMap;
import java.util.Map;

public final class StatModifierTrait extends SimpleTrait {
    static final ITraitSerializer<StatModifierTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("stat_modifier_trait"),
            StatModifierTrait::new,
            StatModifierTrait::readJson,
            StatModifierTrait::readBuffer,
            StatModifierTrait::writeBuffer
    );

    private final Map<ItemStat, StatMod> mods = new HashMap<>();

    private StatModifierTrait(ResourceLocation name) {
        super(name, SERIALIZER);
    }

    @Override
    public float onGetStat(TraitActionContext context, ItemStat stat, float value, float damageRatio) {
        StatMod mod = this.mods.get(stat);
        if (mod != null) {
            return mod.apply(context.getTraitLevel(), value, damageRatio);
        }
        return value;
    }

    private static void readJson(StatModifierTrait trait, JsonObject json) {
        if (!json.has("stats")) {
            SilentGear.LOGGER.error("JSON file for StatModifierTrait '{}' is missing the 'stats' array", trait.getId());
            return;
        }

        for (JsonElement element : json.get("stats").getAsJsonArray()) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                String statName = JSONUtils.getString(obj, "name", "");
                ItemStat stat = ItemStat.ALL_STATS.get(statName);

                if (stat != null) {
                    trait.mods.put(stat, StatMod.fromJson(obj));
                }
            }
        }
    }

    private static void readBuffer(StatModifierTrait trait, PacketBuffer buffer) {
        trait.mods.clear();
        int count = buffer.readByte();
        for (int i = 0; i < count; ++i) {
            ItemStat stat = ItemStat.ALL_STATS.get(buffer.readString());
            trait.mods.put(stat, StatMod.read(buffer));
        }
    }

    private static void writeBuffer(StatModifierTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.mods.size());
        trait.mods.forEach((stat, mod) -> {
            buffer.writeString(stat.getName().getPath());
            mod.write(buffer);
        });
    }

    private static class StatMod {
        private float multi;
        private boolean factorDamage;
        private boolean factorValue;

        private float apply(int level, float value, float damageRatio) {
            float f = multi * level;

            if (factorDamage)
                f *= damageRatio;
            if (factorValue)
                f *= value;

            return value + f;
        }

        private static StatMod fromJson(JsonObject json) {
            StatMod mod = new StatMod();
            mod.multi = JSONUtils.getFloat(json, "value", 0);
            mod.factorDamage = JSONUtils.getBoolean(json, "factor_damage", true);
            mod.factorValue = JSONUtils.getBoolean(json, "factor_value", true);
            return mod;
        }

        private static StatMod read(PacketBuffer buffer) {
            StatMod mod = new StatMod();
            mod.multi = buffer.readFloat();
            mod.factorDamage = buffer.readBoolean();
            mod.factorValue = buffer.readBoolean();
            return mod;
        }

        private void write(PacketBuffer buffer) {
            buffer.writeFloat(multi);
            buffer.writeBoolean(factorDamage);
            buffer.writeBoolean(factorValue);
        }
    }
}
