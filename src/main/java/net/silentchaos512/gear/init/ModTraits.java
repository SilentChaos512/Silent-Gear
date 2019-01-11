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
import net.silentchaos512.gear.api.lib.ResourceOrigin;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.api.traits.TraitRegistry;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.trait.DurabilityTrait;
import net.silentchaos512.gear.trait.StatModifierTrait;
import net.silentchaos512.gear.trait.TraitRefractive;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

import javax.annotation.Nullable;
import java.io.File;

public final class ModTraits implements IPhasedInitializer {
    public static final ModTraits INSTANCE = new ModTraits();

    public static Trait ancient;
    public static Trait crude;
    public static Trait holy;
    public static Trait magmatic;
    public static Trait multiBreak;
    public static Trait speedBoostLight;
    public static Trait synergyBoost;
    public static final float ANCIENT_XP_BOOST = 0.25f;
    public static final float SYNERGY_BOOST_MULTI = 0.04f;

    private static final int COMMON_MAX_LEVEL = 4;

    private ModTraits() {}

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        TraitRegistry.register(new DurabilityTrait(path("malleable"), ResourceOrigin.BUILTIN_CORE));
        TraitRegistry.register(new DurabilityTrait(path("brittle"), ResourceOrigin.BUILTIN_CORE));

        multiBreak = TraitRegistry.register(new Trait(path("multi_break"), ResourceOrigin.BUILTIN_CORE));
        speedBoostLight = TraitRegistry.register(new Trait(path("speed_boost_light"), ResourceOrigin.BUILTIN_CORE));
        synergyBoost = TraitRegistry.register(new Trait(path("synergy_boost"), ResourceOrigin.BUILTIN_CORE));
        crude = TraitRegistry.register(new Trait(path("crude"), ResourceOrigin.BUILTIN_CORE));

        TraitRegistry.register(new TraitRefractive(path("refractive"), ResourceOrigin.BUILTIN_CORE));
        ancient = TraitRegistry.register(new Trait(path("ancient"), ResourceOrigin.BUILTIN_CORE));
        magmatic = TraitRegistry.register(new Trait(path("magmatic"), ResourceOrigin.BUILTIN_CORE));

        // TODO: JSON + add a DamageType trait that changes damage type?
        holy = TraitRegistry.register(new Trait(path("holy"), COMMON_MAX_LEVEL, TextFormatting.YELLOW, 0) {
            @Override
            public float onAttackEntity(@Nullable EntityPlayer player, EntityLivingBase target, int level, ItemStack gear, float baseValue) {
                if (!target.isEntityUndead()) return baseValue;
                return baseValue + 2 * level;
            }
        });

        TraitRegistry.register(new StatModifierTrait(path("bulky"), ResourceOrigin.BUILTIN_CORE) {
            @Override
            public float onGetStat(@Nullable EntityPlayer player, ItemStat stat, int level, ItemStack gear, float value, float damageRatio) {
                float result = super.onGetStat(player, stat, level, gear, value, damageRatio);
                if (stat == CommonItemStats.ATTACK_SPEED) {
                    // TODO: If part durability ever gets implemented, reduce speed lost as part is damaged.
                    // This trait will be used with a future upgrade item.
                    return result > -3.95f ? result : -3.9f;
                }
                return result;
            }
        });
        TraitRegistry.register(new StatModifierTrait(path("chipping"), ResourceOrigin.BUILTIN_CORE));
        TraitRegistry.register(new StatModifierTrait(path("eroded"), ResourceOrigin.BUILTIN_CORE));
        TraitRegistry.register(new StatModifierTrait(path("jagged"), ResourceOrigin.BUILTIN_CORE));
        TraitRegistry.register(new StatModifierTrait(path("soft"), ResourceOrigin.BUILTIN_CORE));

        UserDefined.loadUserTraits();
    }

    private static ResourceLocation path(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }

    private static final class UserDefined {
        static void loadUserTraits() {
            final File directory = new File(Config.INSTANCE.getDirectory(), "traits");
            final File[] files = directory.listFiles();

            if (!directory.isDirectory() || files == null) {
                SilentGear.log.warn("File \"{}\" is not a directory?", directory);
                return;
            }

            for (File file : files) {
                SilentGear.log.info("Trait file found: {}", file);
                String filename = file.getName().replace(".json", "");
                ResourceLocation name = path(filename);

                if (TraitRegistry.get(name.toString()) == null) {
                    // FIXME: For now, we just have stat modifier traits.
                    StatModifierTrait trait = new StatModifierTrait(name, ResourceOrigin.USER_DEFINED);
                    TraitRegistry.register(trait);
                }
            }
        }
    }
}
