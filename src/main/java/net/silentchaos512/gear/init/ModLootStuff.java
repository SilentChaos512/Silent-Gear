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

import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.silentchaos512.gear.loot.condition.HasPartCondition;
import net.silentchaos512.gear.loot.condition.HasTraitCondition;
import net.silentchaos512.gear.loot.function.SelectGearTierLootFunction;
import net.silentchaos512.gear.loot.function.SetPartsFunction;

public final class ModLootStuff {
    private ModLootStuff() {}

    public static void init() {
        LootConditionManager.registerCondition(HasPartCondition.SERIALIZER);
        LootConditionManager.registerCondition(HasTraitCondition.SERIALIZER);
        LootFunctionManager.registerFunction(SelectGearTierLootFunction.SERIALIZER);
        LootFunctionManager.registerFunction(SetPartsFunction.SERIALIZER);

        MinecraftForge.EVENT_BUS.addListener(ModLootStuff::onLootTableLoad);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
    }
}
