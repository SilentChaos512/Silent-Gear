/*
 * Silent Gear -- ModLootStuff
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
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasPartCondition;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;

public final class ModLootStuff {
    private ModLootStuff() {}

    public static void init() {
        LootConditionManager.registerCondition(HasPartCondition.SERIALIZER);
        LootConditionManager.registerCondition(HasTraitCondition.SERIALIZER);
        LootFunctionManager.registerFunction(SelectGearTierLootFunction.SERIALIZER);
        LootFunctionManager.registerFunction(SetPartsFunction.SERIALIZER);
    }

    public static void registerGlobalModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        register("magmatic_smelting", new MagmaticTraitLootModifier.Serializer());
    }

    private static <T extends GlobalLootModifierSerializer<?>> void register(String name, T serializer) {
        ResourceLocation id = SilentGear.getId(name);
        serializer.setRegistryName(id);
        ForgeRegistries.LOOT_MODIFIER_SERIALIZERS.register(serializer);
    }
}
