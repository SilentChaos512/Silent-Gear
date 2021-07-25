/*
 * Silent Gear -- UpgradePart
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

package net.silentchaos512.gear.gear.part;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearTypeMatcher;
import net.silentchaos512.gear.api.part.IPartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.utils.Color;

import java.util.List;
import java.util.function.Function;

public class UpgradePart extends AbstractGearPart {
    private GearTypeMatcher gearTypes = GearTypeMatcher.ALL;

    public UpgradePart(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public PartType getType() {
        return PartType.MISC_UPGRADE;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartSerializers.UPGRADE_PART;
    }

    @Override
    public int getColor(PartData part, ItemStack gear, int layer, int animationFrame) {
        return Color.VALUE_WHITE;
    }

    @Override
    public void addInformation(PartData part, ItemStack gear, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, part.getDisplayName(gear));
    }

    @Override
    public boolean canAddToGear(ItemStack gear, PartData part) {
        return this.gearTypes.test(GearHelper.getType(gear));
    }

    public static class Serializer extends AbstractGearPart.Serializer<UpgradePart> {
        public Serializer(ResourceLocation serializerId, Function<ResourceLocation, UpgradePart> function) {
            super(serializerId, function);
        }

        @Override
        public UpgradePart read(ResourceLocation id, JsonObject json) {
            UpgradePart part = super.read(id, json, false);
            JsonElement gearTypesJson = json.get("gear_types");
            if (gearTypesJson != null && gearTypesJson.isJsonObject()) {
                part.gearTypes = GearTypeMatcher.deserialize(gearTypesJson.getAsJsonObject());
            }
            return part;
        }

        @Override
        public UpgradePart read(ResourceLocation id, FriendlyByteBuf buffer) {
            UpgradePart part = super.read(id, buffer);
            part.gearTypes = GearTypeMatcher.read(buffer);
            return part;
        }

        @Override
        public void write(FriendlyByteBuf buffer, UpgradePart part) {
            super.write(buffer, part);
            part.gearTypes.write(buffer);
        }
    }
}
