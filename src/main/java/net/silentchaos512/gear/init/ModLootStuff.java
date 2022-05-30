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

import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;

import java.util.function.Supplier;

public final class ModLootStuff {
    // Conditions
    public static final RegistryObject<LootItemConditionType> HAS_TRAIT =
            registerCondition("has_trait", () -> new LootItemConditionType(HasTraitCondition.SERIALIZER));

    // Functions
    public static final RegistryObject<LootItemFunctionType> SELECT_TIER =
            registerFunction("select_tier", () -> new LootItemFunctionType(SelectGearTierLootFunction.SERIALIZER));
    public static final RegistryObject<LootItemFunctionType> SET_PARTS =
            registerFunction("set_parts", () -> new LootItemFunctionType(SetPartsFunction.SERIALIZER));

    // Global Loot Modifiers
    public static final RegistryObject<GlobalLootModifierSerializer<?>> BONUS_DROPS_TRAIT =
            registerGlobalModifier("bonus_drops_trait", BonusDropsTraitLootModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<?>> MAGMATIC_SMELTING =
            registerGlobalModifier("magmatic_smelting", MagmaticTraitLootModifier.Serializer::new);

    private ModLootStuff() {}

    public static void init() {}

    private static <T extends LootItemConditionType> RegistryObject<T> registerCondition(String name, Supplier<T> condition) {
        return Registration.LOOT_CONDITIONS.register(name, condition);
    }

    private static <T extends LootItemFunctionType> RegistryObject<T> registerFunction(String name, Supplier<T> condition) {
        return Registration.LOOT_FUNCTIONS.register(name, condition);
    }

    private static <T extends GlobalLootModifierSerializer<?>> RegistryObject<T> registerGlobalModifier(String name, Supplier<T> serializer) {
        return Registration.LOOT_MODIFIERS.register(name, serializer);
    }
}
