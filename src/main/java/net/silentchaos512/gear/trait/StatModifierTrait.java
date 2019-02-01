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

package net.silentchaos512.gear.trait;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.lib.ResourceOrigin;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.traits.Trait;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class StatModifierTrait extends Trait {
    private final Map<ItemStat, StatMod> mods = new HashMap<>();

    public StatModifierTrait(ResourceLocation name, ResourceOrigin origin) {
        super(name, origin);
    }

    @Override
    protected void processExtraJson(JsonObject json) {
        if (!json.has("stats")) {
            SilentGear.LOGGER.error("JSON file for StatModifierTrait '{}' is missing the 'stats' array", this.getName());
            return;
        }

        for (JsonElement element : json.get("stats").getAsJsonArray()) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();

                String statName = JsonUtils.getString(obj, "name", "");
                ItemStat stat = ItemStat.ALL_STATS.get(statName);

                if (stat != null) {
                    mods.put(stat, StatMod.fromJson(obj));
                }
            }
        }
    }

    @Override
    public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
        StatMod mod = this.mods.get(stat);

        if (mod != null) {
            return mod.apply(level, value, damageRatio);
        }

        return value;
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

            mod.multi = JsonUtils.getFloat(json, "value", 0);
            mod.factorDamage = JsonUtils.getBoolean(json, "factor_damage", true);
            mod.factorValue = JsonUtils.getBoolean(json, "factor_value", true);

            return mod;
        }
    }
}
