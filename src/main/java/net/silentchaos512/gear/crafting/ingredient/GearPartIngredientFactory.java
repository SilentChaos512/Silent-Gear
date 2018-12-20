/*
 * Silent Gear -- GearPartIngredientFactory
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

package net.silentchaos512.gear.crafting.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.silentchaos512.gear.api.parts.PartType;

import javax.annotation.Nonnull;

public class GearPartIngredientFactory implements IIngredientFactory {
    @Nonnull
    @Override
    public Ingredient parse(JsonContext context, JsonObject json) {
        String typeName = JsonUtils.getString(json, "part_type", "");
        if (typeName.isEmpty())
            throw new JsonSyntaxException("'part_type' is missing");

        PartType type = PartType.get(typeName);
        if (type == null)
            throw new JsonSyntaxException("part_type " + typeName + " does not exist");

        return new GearPartIngredient(type);
    }
}
