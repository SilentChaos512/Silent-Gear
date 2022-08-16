/*
 * Silent Gear -- GearGenerator
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

package net.silentchaos512.gear.util;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.gear.part.PartManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class GearGenerator {
    private GearGenerator() {
        throw new IllegalAccessError("Utility class");
    }

    public static Optional<PartData> getRandomPart(GearType gearType, PartType type) {
        return getRandomPart(gearType, type, -1);
    }

    public static Optional<PartData> getRandomPart(GearType gearType, PartType partType, final int partTier) {
        Optional<PartData> optional = partType.getCompoundPartItem(gearType)
                .map(item -> PartManager.from(new ItemStack(item)))
                .map(part -> part.randomizeData(gearType, partTier));

        if (!optional.isPresent()) {
            // No compound part available? Try to find a simple part.
            List<IGearPart> partsOfTier = PartManager.getValues().stream()
                    .filter(part -> partTier == -1 || partTier == part.getTier())
                    .collect(Collectors.toList());

            if (!partsOfTier.isEmpty()) {
                IGearPart random = partsOfTier.get(SilentGear.RANDOM.nextInt(partsOfTier.size()));
                return Optional.of(random.randomizeData(gearType, partTier));
            } else if (partTier != -1) {
                return getRandomPart(gearType, partType, -1);
            }
            return Optional.empty();
        }

        return optional;
    }

    public static ItemStack create(ICoreItem item, int minTier, int maxTier) {
        if (minTier >= maxTier)
            return create(item, maxTier);
        return create(item, minTier + SilentGear.RANDOM.nextInt(maxTier - minTier));
    }

    public static ItemStack create(ICoreItem item, int tier) {
        return randomizeParts(new ItemStack(item), tier);
    }

    public static ItemStack randomizeParts(ItemStack stack, int tier) {
        if (!(stack.getItem() instanceof ICoreItem)) {
            throw new RuntimeException("Called GearGenerator.randomizeParts on non-gear");
        }
        ICoreItem item = (ICoreItem) stack.getItem();
        GearType gearType = item.getGearType();
        PartDataList parts = PartDataList.of();

        for (PartType partType : item.getRequiredParts()) {
            getRandomPart(gearType, partType, tier).ifPresent(parts::add);
        }

        if (parts.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.copy();
        GearData.writeConstructionParts(result, parts);

        // Apply some random upgrades?
        if (item instanceof ICoreTool && tier > 1 && SilentGear.RANDOM.nextFloat() < 0.2f * tier + 0.1f) {
            getRandomPart(gearType, PartType.TIP, tier).ifPresent(part ->
                    GearData.addUpgradePart(result, part));
        }

        GearData.recalculateStats(result, null);
        parts.forEach(p -> p.onAddToGear(result));

        return result;
    }
}
