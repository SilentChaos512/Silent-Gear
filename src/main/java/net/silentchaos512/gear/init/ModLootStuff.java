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

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;
import net.silentchaos512.gear.loot.modifier.BonusDropsTraitLootModifier;
import net.silentchaos512.gear.loot.modifier.MagmaticTraitLootModifier;

import java.util.function.Supplier;

public final class ModLootStuff {
    public static final LootItemConditionType HAS_TRAIT = new LootItemConditionType(HasTraitCondition.SERIALIZER);
    public static final LootItemFunctionType SELECT_TIER = new LootItemFunctionType(SelectGearTierLootFunction.SERIALIZER);
    public static final LootItemFunctionType SET_PARTS = new LootItemFunctionType(SetPartsFunction.SERIALIZER);

    public static final RegistryObject<GlobalLootModifierSerializer<?>> BONUS_DROPS_TRAIT = register("bonus_drops_trait", BonusDropsTraitLootModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<?>> MAGMATIC_SMELTING = register("magmatic_smelting", MagmaticTraitLootModifier.Serializer::new);

    private ModLootStuff() {}

    public static void init() {
        Registry.register(Registry.LOOT_CONDITION_TYPE, SilentGear.getId("has_trait"), HAS_TRAIT);
        Registry.register(Registry.LOOT_FUNCTION_TYPE, SilentGear.getId("select_tier"), SELECT_TIER);
        Registry.register(Registry.LOOT_FUNCTION_TYPE, SilentGear.getId("set_parts"), SET_PARTS);
    }

    private static <T extends GlobalLootModifierSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer) {
        return Registration.LOOT_MODIFIERS.register(name, serializer);
    }
}
