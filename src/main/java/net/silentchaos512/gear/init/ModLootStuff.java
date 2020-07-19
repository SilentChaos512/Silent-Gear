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

import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
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
    public static final LootConditionType HAS_PART = new LootConditionType(HasPartCondition.SERIALIZER);
    public static final LootConditionType HAS_TRAIT = new LootConditionType(HasTraitCondition.SERIALIZER);
    public static final LootFunctionType SELECT_TIER = new LootFunctionType(SelectGearTierLootFunction.SERIALIZER);
    public static final LootFunctionType SET_PARTS = new LootFunctionType(SetPartsFunction.SERIALIZER);

    private ModLootStuff() {}

    public static void init() {
        Registry.register(Registry.field_239704_ba_, SilentGear.getId("has_part"), HAS_PART);
        Registry.register(Registry.field_239704_ba_, SilentGear.getId("has_trait"), HAS_TRAIT);
        Registry.register(Registry.field_239694_aZ_, SilentGear.getId("select_tier"), SELECT_TIER);
        Registry.register(Registry.field_239694_aZ_, SilentGear.getId("set_parts"), SET_PARTS);
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
