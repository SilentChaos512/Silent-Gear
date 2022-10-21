/*
 * Silent Gear -- SelectGearTierLootFunction
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

package net.silentchaos512.gear.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.SgLoot;
import net.silentchaos512.gear.util.GearGenerator;

public final class SelectGearTierLootFunction extends LootItemConditionalFunction {
    public static final Serializer SERIALIZER = new Serializer();
    private final int tier;

    private SelectGearTierLootFunction(LootItemCondition[] conditions, int tier) {
        super(conditions);
        this.tier = tier;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof ICoreItem)) return stack;
        return GearGenerator.create((ICoreItem) stack.getItem(), this.tier);
    }

    public static LootItemConditionalFunction.Builder<?> builder(int tier) {
        return simpleBuilder(conditions -> new SelectGearTierLootFunction(conditions, tier));
    }

    @Override
    public LootItemFunctionType getType() {
        return SgLoot.SELECT_TIER.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SelectGearTierLootFunction> {
        @Override
        public void serialize(JsonObject object, SelectGearTierLootFunction functionClazz, JsonSerializationContext serializationContext) {
            object.addProperty("tier", functionClazz.tier);
        }

        @Override
        public SelectGearTierLootFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            int tier = GsonHelper.getAsInt(object, "tier", 2);
            return new SelectGearTierLootFunction(conditionsIn, tier);
        }
    }
}
