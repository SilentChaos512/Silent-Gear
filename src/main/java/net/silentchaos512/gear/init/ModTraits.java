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

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.Trait;
import net.silentchaos512.gear.api.traits.TraitRegistry;
import net.silentchaos512.gear.trait.DurabilityTrait;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

public final class ModTraits implements IPhasedInitializer {
    public static final ModTraits INSTANCE = new ModTraits();

    private static final float DURABILITY_EFFECT_CHANCE = 0.5f; // 0.1

    private ModTraits() {}

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        Trait malleable = TraitRegistry.register(new DurabilityTrait(path("malleable"), 3, TextFormatting.WHITE,
                DURABILITY_EFFECT_CHANCE, -1));
        Trait brittle = TraitRegistry.register(new DurabilityTrait(path("brittle"), 3, TextFormatting.DARK_GRAY,
                DURABILITY_EFFECT_CHANCE, 1));

        Trait.setCancelsWith(malleable, brittle);
    }

    private static ResourceLocation path(String name) {
        return new ResourceLocation(SilentGear.MOD_ID, name);
    }
}
