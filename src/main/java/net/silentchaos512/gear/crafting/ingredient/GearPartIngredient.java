/*
 * Silent Gear -- GearPartIngredient
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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public final class GearPartIngredient extends Ingredient implements IGearIngredient {
    public static final Codec<GearPartIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PartType.CODEC.fieldOf("part_type").forGetter(GearPartIngredient::getPartType)
    ).apply(instance, GearPartIngredient::new));

    private final PartType type;

    private GearPartIngredient(PartType type) {
        // Note: gear parts are NOT loaded at this time!
        super(Stream.of());
        this.type = type;
    }

    public static GearPartIngredient of(PartType type) {
        return new GearPartIngredient(type);
    }

    @Override
    public PartType getPartType() {
        return type;
    }

    @Override
    public Optional<Component> getJeiHint() {
        MutableComponent typeText = this.type.getDisplayName(0);
        MutableComponent text = TextUtil.withColor(typeText, Color.GOLD);
        return Optional.of(TextUtil.translate("jei", "partType", text));
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        PartData part = PartData.from(stack);
        return part != null && part.getType().equals(type);
    }

    @Override
    public ItemStack[] getItems() {
        // Although gear parts are not available when the ingredient is constructed,
        // they are available later on
        Collection<IGearPart> parts = PartManager.getPartsOfType(this.type);
        if (!parts.isEmpty()) {
            return parts.stream()
                    .flatMap(part -> Stream.of(part.getIngredient().getItems()))
                    .filter(stack -> !stack.isEmpty())
                    .toArray(ItemStack[]::new);
        }
        return super.getItems();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
