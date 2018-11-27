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

import net.minecraft.entity.EntityLivingBase;
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
import net.silentchaos512.gear.trait.TraitRefractive;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

import javax.annotation.Nullable;

public final class ModTraits implements IPhasedInitializer {
    public static final ModTraits INSTANCE = new ModTraits();

    public static Trait multiBreak;
    public static Trait speedBoostLight;
    public static Trait synergyBoost;
    public static Trait crude;
    public static Trait holy;
    public static final float SYNERGY_BOOST_MULTI = 0.04f;

    private static final int COMMON_MAX_LEVEL = 4;
    private static final float DURABILITY_EFFECT_CHANCE = 0.1f;
    private static final float JAGGED_MULTI = (float) 1 / 6;
    private static final float SOFT_MULTI = 0.15f;

    private ModTraits() {}

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        Trait malleable = TraitRegistry.register(new DurabilityTrait(path("malleable"),
                COMMON_MAX_LEVEL + 1, TextFormatting.WHITE,
                DURABILITY_EFFECT_CHANCE, -1));
        Trait brittle = TraitRegistry.register(new DurabilityTrait(path("brittle"),
                COMMON_MAX_LEVEL + 1, TextFormatting.GRAY,
                DURABILITY_EFFECT_CHANCE, 1));
        Trait.setCancelsWith(malleable, brittle);

        multiBreak = TraitRegistry.register(new Trait(path("multi_break"),
                COMMON_MAX_LEVEL, TextFormatting.DARK_GREEN, 0));
        speedBoostLight = TraitRegistry.register(new Trait(path("speed_boost_light"),
                COMMON_MAX_LEVEL, TextFormatting.GOLD, 0));
        synergyBoost = TraitRegistry.register(new Trait(path("synergy_boost"),
                COMMON_MAX_LEVEL + 1, TextFormatting.DARK_GREEN, 0));
        crude = TraitRegistry.register(new Trait(path("crude"),
                COMMON_MAX_LEVEL + 1, TextFormatting.BOLD, 0));
        Trait.setCancelsWith(synergyBoost, crude);

        TraitRegistry.register(new TraitRefractive(path("refractive"), 1, TextFormatting.GOLD, 0));

        holy = TraitRegistry.register(new Trait(path("holy"), COMMON_MAX_LEVEL, TextFormatting.YELLOW, 0) {
            @Override
            public float onAttackEntity(@Nullable EntityPlayer player, EntityLivingBase target, int level, ItemStack gear, float baseValue) {
                if (!target.isEntityUndead()) return baseValue;
                return baseValue + 2 * level;
            }
        });

        TraitRegistry.register(new StatModifierTrait(path("bulky"), COMMON_MAX_LEVEL, TextFormatting.BOLD) {
            @Override
            public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
                if (stat == CommonItemStats.ATTACK_SPEED) {
                    // TODO: If part durability ever gets implemented, reduce speed lost as part is damaged.
                    // This trait will be used with a future upgrade item.
                    float result = value - 0.075f * level;
                    return result > -3.95f ? result : -3.9f;
                }
                return value;
            }
        });
        TraitRegistry.register(new StatModifierTrait(path("chipping"), COMMON_MAX_LEVEL, TextFormatting.DARK_BLUE) {
            @Override
            public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
                if (stat == CommonItemStats.ARMOR)
                    return value - 0.075f * level * value * damageRatio;
                if (stat == CommonItemStats.HARVEST_SPEED)
                    return value + 0.25f * level * value * damageRatio;
                return value;
            }
        });
        TraitRegistry.register(new StatModifierTrait(path("jagged"), COMMON_MAX_LEVEL, TextFormatting.DARK_RED) {
            @Override
            public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
                if (stat == CommonItemStats.MELEE_DAMAGE)
                    return value + JAGGED_MULTI * level * value * damageRatio;
                return value;
            }
        });
        TraitRegistry.register(new StatModifierTrait(path("soft"), COMMON_MAX_LEVEL, TextFormatting.YELLOW) {
            @Override
            public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
                if (stat == CommonItemStats.HARVEST_SPEED)
                    return value - SOFT_MULTI * level * value * damageRatio;
                return value;
            }
        });
    }

    private static ResourceLocation path(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}
