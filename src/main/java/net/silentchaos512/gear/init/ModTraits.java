/*
 * Silent Gear -- ModTraits
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

package net.silentchaos512.gear.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.api.traits.TraitRegistry;
import net.silentchaos512.gear.trait.DurabilityTrait;
import net.silentchaos512.gear.trait.StatModifierTrait;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

import javax.annotation.Nullable;

public final class ModTraits implements IPhasedInitializer {
    public static final ModTraits INSTANCE = new ModTraits();

    public static Trait synergyBoost;
    public static final float SYNERGY_BOOST_MULTI = 0.1f;

    private static final float DURABILITY_EFFECT_CHANCE = 0.1f;
    private static final float SOFT_MULTI = 0.2f;

    private ModTraits() {}

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        Trait malleable = TraitRegistry.register(new DurabilityTrait(path("malleable"), 3, TextFormatting.WHITE,
                DURABILITY_EFFECT_CHANCE, -1));
        Trait brittle = TraitRegistry.register(new DurabilityTrait(path("brittle"), 3, TextFormatting.GRAY,
                DURABILITY_EFFECT_CHANCE, 1));
        Trait.setCancelsWith(malleable, brittle);

        synergyBoost = TraitRegistry.register(new Trait(path("synergy_boost"), 3, TextFormatting.DARK_GREEN, 0));

        TraitRegistry.register(new StatModifierTrait(path("soft"), 3, TextFormatting.YELLOW) {
            @Override
            public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value) {
                if (stat == CommonItemStats.HARVEST_SPEED) {
                    float damageRatio = (float) gear.getItemDamage() / (float) gear.getMaxDamage();
                    return value - SOFT_MULTI * level * value * damageRatio;
                }
                return value;
            }
        });
    }

    private static ResourceLocation path(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}
